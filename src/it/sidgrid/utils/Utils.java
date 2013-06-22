package it.sidgrid.utils;

/*Copyright (C) 2013  SID&GRID Project

Regione Toscana
Universita' degli Studi di Firenze - Dept. of Mathematics and Computer Science
Scuola Superiore S.Anna
CNR-ISTI

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.*/

/**
 * @author 
 * Claudio Schifani
 * Lorenzo Pini
 * Iacopo Borsi
 * Rudy Rossetto
 */

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.buffer.BufferFactory;
import org.gvsig.raster.buffer.WriterBufferServer;
import org.gvsig.raster.dataset.GeoRasterWriter;
import org.gvsig.raster.dataset.IBuffer;
import org.gvsig.raster.dataset.InvalidSetViewException;
import org.gvsig.raster.dataset.NotSupportedExtensionException;
import org.gvsig.raster.dataset.RasterDataset;
import org.gvsig.raster.dataset.io.RasterDriverException;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.ConnectionFactory;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGISWriter;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGisDriver;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.utiles.PostProcessSupport;

import es.unex.sextante.gvsig.core.gvVectorLayer;
/**
 * @author sid&grid
 * SID&GRID model document utils tools
 * 
 */
public class Utils {

	private static Properties dbprops = null;
	/**
	 * Return a list of all FLyrVect layer names in the current view
	 * @param MapControl mapCtrl
	 * @return String[]
	 */
	public static String[] getVectLayers(MapControl mapCtrl) {
		String[] vectLayers = new String[]{};
		final FLayers mapCtrlLayers = mapCtrl.getMapContext().getLayers();

		int numLayers = mapCtrlLayers.getLayersCount();
		if (numLayers > 0) {
			ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < numLayers; i++)
			{
				FLayer layer = mapCtrlLayers.getLayer(i);
				//System.out.println("Checking: "+layer.getName()+" ("+layer.getClass().toString()+")");  // DEBUG
				if (layer instanceof FLyrVect) {
					list.add(layer.getName());
					//System.out.println("           -> FLyrVect");  // DEBUG
				}
			}
			vectLayers = new String[list.size()];
			list.toArray(vectLayers);
		}
/*		// Code to get nested vector layers
		ArrayList<String> list = new ArrayList<String>();
		LayersIterator it = new LayersIterator(mapCtrlLayers) {
			@Override
			public boolean evaluate(FLayer layer) {
				return (layer instanceof FLyrVect);
			}

		};
		while (it.hasNext())
		{
			list.add(((FLyrVect)it.next()).getName());
		}
		vectLayers = new String[list.size()];
		list.toArray(vectLayers);
*/
		return vectLayers;
	}

	/**
	 * Return a list of all FLyrRasterSE layer names in the current view
	 * @param MapControl mapCtrl
	 * @return String[]
	 */
	public static String[] getRasterLayers(MapControl mapCtrl) {
		String[] rasterLayer = new String[]{};
		final FLayers mapCtrlLayers = mapCtrl.getMapContext().getLayers();
		int numLayers = mapCtrlLayers.getLayersCount();
		if(numLayers > 0)
		{
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < numLayers; i++)
			{
				FLayer layer = mapCtrlLayers.getLayer(i);
				if(layer instanceof FLyrRasterSE)
				{
					list.add(layer.getName());
				}
			}
			rasterLayer = new String[list.size()];
			list.toArray(rasterLayer);
		}
		return rasterLayer;
	}
	
	/**
	 * Load database properties from file
	 * @return Properties 
	 */
	public static Properties getDBProperties(){   						
		if(dbprops==null || dbprops.size()==0){
			dbprops = new Properties();
			// Defaults
			dbprops.put("jdbc","postgresql");
			dbprops.put("db.host", "localhost");
			dbprops.put("db.port","5432");
			dbprops.put("db.user", "postgres");  
			dbprops.put("db.passwd", "postgres");
			dbprops.put("db.template","postgistemplate");

		    InputStream fileinput = Utils.class.getResourceAsStream("/db.properties");
			if(fileinput!=null){
				try {
					dbprops.load(fileinput);
					fileinput.close();
				} catch (IOException e) {
					System.err.println(e.getLocalizedMessage());
					JOptionPane.showMessageDialog(null,
							"Check your db.properties file",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
				}				
			}else{
				System.out.println("db.properties file not found, defaults used");
			}
			
		}
		return dbprops;
	}
	
	/**
	 * 
	 * @return String[] A list of available database names
	 */
	public static String[] getDbase() {
		String[] solution = new String[]{};
		
		ArrayList<String> list = new ArrayList<String>();
		try {

			Properties props = getDBProperties();  
			
		    String jdbc = props.getProperty("jdbc","postgresql");
			String host = props.getProperty("db.host", "localhost");
			String port = props.getProperty("db.port","5432");
		    String username = props.getProperty("db.user", "postgres");  
		    String password = props.getProperty("db.passwd", "postgres");
		    
		    String url = "jdbc:"+ jdbc + "://"+ host+ ":"+ port+ "/template1";

			Connection conn = DriverManager.getConnection(url, username, password);
			ResultSet res = conn.getMetaData().getCatalogs();
			while (res.next())
			{
				String catalog = res.getString("TABLE_CAT");
				if(!catalog.contains("template")){
					list.add(catalog);
				}
				
				
			}
			// testing, sembra che la close non serva
			conn.close();
		} catch (SQLException e) {
			// TODO segnalare all'utente che non si è potuto aprire il database?
			e.printStackTrace();
		}	
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;
	}

	/**
	 * Return a list of available ProjectTable names
	 * @return String[]
	 */
	public static String[] getProjectTableNames() {
		String[] tableList = null;

		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		ArrayList<ProjectDocument> lista = ext.getProject().getDocuments();
		ArrayList<String> list = new ArrayList<String>();
		for (int i=0; i<lista.size(); i++)
		{
			ProjectDocument table = lista.get(i);
			if(table instanceof ProjectTable)
			{
				list.add(table.getName());

			}
		}
		tableList = new String[list.size()];
		list.toArray(tableList);	    
		return tableList;

	}
	
	
	public static String[] getTableLPF(String dbase) {
		String[] solution = new String[]{};
		
		ArrayList<String> list = new ArrayList<String>();
		try {

			Connection conn = Utils.getConnectionToDatabase(dbase);
			ResultSet res = conn.getMetaData().getTables(null, null, null, null);
			while (res.next())
			{
				String catalog = res.getString("TABLE_NAME");
				if(catalog.startsWith("lpf") && catalog.endsWith("key")==false)
				list.add(catalog);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;
	}
	
	public static String[] getTableWell(String dbase) {
		String[] solution = new String[]{};
		
		ArrayList<String> list = new ArrayList<String>();
		try {

			Connection conn = Utils.getConnectionToDatabase(dbase);
			ResultSet res = conn.getMetaData().getTables(null, null, null, null);
			while (res.next())
			{
				String catalog = res.getString("TABLE_NAME");
				if(catalog.endsWith("well") && catalog.endsWith("key")==false)
				list.add(catalog);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;
	}	
	
	/**
	 * Return a list of tables ending with "rech" in the given database
	 * @param dbase 
	 * @return
	 * @throws IOException
	 */
	public static String[] getTableRecharge(String dbase) {
		String[] solution = new String[]{};
		
		ArrayList<String> list = new ArrayList<String>();
		try {

			Connection conn = Utils.getConnectionToDatabase(dbase);
			ResultSet res = conn.getMetaData().getTables(null, null, null, null);
			while (res.next())
			{
				String catalog = res.getString("TABLE_NAME");
				if(catalog.endsWith("rech") && catalog.endsWith("key")==false)
				list.add(catalog);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;
	}	
	
	public static String[] getTableUnsatur(String dbase) {
		String[] solution = new String[]{};
		
		ArrayList<String> list = new ArrayList<String>();
		try {

			Connection conn = Utils.getConnectionToDatabase(dbase);
			ResultSet res = conn.getMetaData().getTables(null, null, null, null);
			while (res.next())
			{
				String catalog = res.getString("TABLE_NAME");
				if(catalog.endsWith("unsatur") && catalog.endsWith("key")==false)
				list.add(catalog);
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;
	}
	
	public static String[] getFieldLayerNumericFields(MapControl mapCtrl, String selectedLayerVar) {
		String[] fields = new String[]{};
		if(selectedLayerVar== null)
			return fields;
		FLayers layers = mapCtrl.getMapContext().getLayers();
		System.out.println("layers: "+layers);
		System.out.println("stringa :"+selectedLayerVar);
		FLyrVect layer = (FLyrVect)layers.getLayer(selectedLayerVar);
		ArrayList<String> list = new ArrayList<String>();
		if(layer!=null)
			try {
				SelectableDataSource recordset = layer.getRecordset();
				int numFields = recordset.getFieldCount();
				for (int i = 0; i < numFields; i++) {												

					list.add(recordset.getFieldName(i));

				}
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
			fields = new String[list.size()];
			list.toArray(fields);
			return fields;
	}

	public static String[] getModelsNames() {
		String[] solution = null;

		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		ArrayList<ProjectDocument> lista = ext.getProject().getDocumentsByType(HydrologicalModelFactory.registerName);
		ArrayList<String> list = new ArrayList<String>();
		for (int i=0; i<lista.size(); i++)
		{
			ProjectDocument doc = lista.get(i);
			list.add(doc.getName());
		}
		solution = new String[list.size()];
		list.toArray(solution);	    
		return solution;

	}

	public static void createChild(String name) {
		int numModel=HydrologicalModel.NUMS.get(HydrologicalModelFactory.registerName).intValue(); 
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		HydrologicalModel child = new HydrologicalModel(); 
		child.setName(name);	       
		child.setCreationDate(DateFormat.getInstance().format(new Date()));
		numModel++;
	    System.out.println(child.getName()); 
	    child.setProject(ext.getProject(), 0);
	    child.setProjectDocumentFactory(new HydrologicalModelFactory());
	    System.out.println(child.getProject());
	    ext.getProject().addDocument(child);

	}
	
	
	public static ArrayList<gvVectorLayer> getModelLayers(String nomeModello) {
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(nomeModello, HydrologicalModelFactory.registerName);
		return doc.getLayers();

	}

	public static String[] getModelLayersNames(String nomeModello) {
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(nomeModello, HydrologicalModelFactory.registerName);
		ArrayList<String> list = new ArrayList<String>();
		for(gvVectorLayer g: doc.getLayers())
			list.add(g.getName());
		String[] solution = new String[list.size()];
		list.toArray(solution);	    
		return solution;
	}
	
	/**
	 * Connects to database and returns a Connection to specified dbase
	 * @param dbase
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Connection getConnectionToDatabase(String dbase) throws SQLException {
		Properties props = getDBProperties();   						
						
		String jdbc = props.getProperty("jdbc","postgresql");
		String host = props.getProperty("db.host", "localhost");
		String port = props.getProperty("db.port","5432");
	    String username = props.getProperty("db.user", "postgres");  
	    String password = props.getProperty("db.passwd", "postgres");
	    
	    String url = "jdbc:"+ jdbc + "://"+ host+ ":"+ port+ "/"+ dbase;
	    
		Connection conn = DriverManager.getConnection(url,username,password);
		return conn;
		
	}
	
	/**
	 * Manage PostGIS
	 * @throws DBException 
	 * @throws DriverLoadException 
	 * @throws IOException 
	 * 
	 */
	
	public static String getUserdb() throws IOException{
		Properties props = getDBProperties();
        String username = props.getProperty("db.user");  
		return username;
	}
	
	
	/**
	 * Copy layer into PostGIS
	 * @throws DBException 
	 * @throws DriverLoadException 
	 * @throws IOException 
	 * 
	 */
	public static void saveToPostGIS(FLyrVect layer, String dbase, String tbName) throws  DriverIOException, ReadDriverException, InitializeWriterException, VisitorException, DriverException, SQLException, DBException, DriverLoadException, IOException {

		String tableName = tbName;
		tableName = tableName.toLowerCase();

		// Mi connetto con postgresql
		Properties props = getDBProperties();
		String host = props.getProperty("db.host");
		String port = props.getProperty("db.port");
		String username = props.getProperty("db.user");  
        String password = props.getProperty("db.passwd");
        IConnection conex = ConnectionFactory.createConnection("jdbc:postgresql://"+host+":"+port+"/"+dbase, username, password);

		DBLayerDefinition originalDef = null;
		//System.out.println("** POSTGIS: istanceof "+ layer.getSource().getDriver().toString());
		if (layer.getSource().getDriver() instanceof IVectorialDatabaseDriver) {
			// Codice morto, "saveToPostGis" viene attualmente usato per inserire shp in db
			// sono tutti com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver
			System.out.println("** POSTGIS: Ho trovato un IVectorialDatabaseDriver !!!");
			originalDef=((IVectorialDatabaseDriver) layer.getSource().getDriver()).getLyrDef();
		}

		// Essendo un DBLayer userà PostGIS e non Postgres
		DBLayerDefinition dbLayerDef = new DBLayerDefinition();

		dbLayerDef.setCatalogName("");

		dbLayerDef.setSchema("public");

		dbLayerDef.setTableName(tableName);
		dbLayerDef.setName(tableName);
		dbLayerDef.setShapeType(layer.getShapeType());

		SelectableDataSource sds = layer.getRecordset();


		// TODO: getFieldDescription non considera gli alias.
		FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
		dbLayerDef.setFieldsDesc(fieldsDescrip);    // imposto i nomi dei campi come quelli del layer input

		if (originalDef != null){
			// sto importando un layer postgis, copio i campi geometrici
			dbLayerDef.setFieldID(originalDef.getFieldID()); 
			dbLayerDef.setFieldGeometry(originalDef.getFieldGeometry());
		}else{
			// Search for id field name
			int index=0;
			String fieldName="gid";
			// E' tutto commentato per forzare gid a campo chiave
//			while (findFieldIdByName(fieldsDescrip,fieldName) != -1){
//				System.out.println("** POSTGIS: Sono nel while del gid");
//				index++;
//				fieldName="gid"+index;
//			}
//			System.out.println("** POSTGIS: gid="+fieldName);
			dbLayerDef.setFieldID(fieldName);

			// search for geom field name
			index=0;
			fieldName="the_geom";
			// Teoricamente andrebbe commentato anche questo ma the_geom non viene letto come 
			// attributo durante una conversione db -> shp
//			while (findFieldIdByName(fieldsDescrip,fieldName) != -1){
			while (sds.getFieldIndexByName(fieldName) != -1){
//				System.out.println("** POSTGIS: Sono nel while di the_geom");
				index++;
				fieldName="the_geom"+index;
			}			
//			System.out.println("** POSTGIS: the_geom = "+fieldName);
			dbLayerDef.setFieldGeometry(fieldName);

		}
		// if id field dosen't exist we add it
//		if (findFieldIdByName(fieldsDescrip,dbLayerDef.getFieldID()) == -1)
		// sds è del layer in input, dbLayerDef è in output
		if (sds.getFieldIndexByName(dbLayerDef.getFieldID()) == -1)
		{
//			System.out.println("** POSTGIS: Sono nel if (gid non esiste)");
			int numFieldsAnt = fieldsDescrip.length;
			FieldDescription[] newFields = new FieldDescription[dbLayerDef.getFieldsDesc().length + 1];
			for (int i=0; i < numFieldsAnt; i++)
			{
//				System.out.println("** POSTGIS: Sono nel FOR field description");
				newFields[i] = fieldsDescrip[i];
			}
			newFields[numFieldsAnt] = new FieldDescription();
			newFields[numFieldsAnt].setFieldDecimalCount(0);
			newFields[numFieldsAnt].setFieldType(Types.INTEGER);
			newFields[numFieldsAnt].setFieldLength(7);
			newFields[numFieldsAnt].setFieldName(dbLayerDef.getFieldID());
			dbLayerDef.setFieldsDesc(newFields);
		}


		dbLayerDef.setWhereClause("");
		String strSRID = layer.getProjection().getAbrev().substring(5);
		dbLayerDef.setSRID_EPSG(strSRID);
		dbLayerDef.setConnection(conex);

		PostGISWriter writer=(PostGISWriter)LayerFactory.getWM().getWriter("PostGIS Writer");
		writer.setWriteAll(true);
		writer.setCreateTable(true);
		writer.initialize(dbLayerDef);
		PostGisDriver postGISDriver=new PostGisDriver();
		postGISDriver.setLyrDef(dbLayerDef);
		postGISDriver.open();
		PostProcessSupport.clearList();
		Object[] params = new Object[2];
		params[0] = conex;
		params[1] = dbLayerDef;
		PostProcessSupport.addToPostProcess(postGISDriver, "setData", params, 1);

		writeFeaturesNoThread(layer, writer);

	}

	/**
	 * Write layer into db, uses layer.getname() as table name.
	 * @param layer
	 * @param dbase
	 * @throws DriverIOException
	 * @throws ReadDriverException
	 * @throws InitializeWriterException
	 * @throws VisitorException
	 * @throws DriverException
	 * @throws SQLException
	 * @throws DBException
	 * @throws DriverLoadException
	 * @throws IOException
	 */
	public static void saveToPostGIS(FLyrVect layer, String dbase) throws  DriverIOException, ReadDriverException, InitializeWriterException, VisitorException, DriverException, SQLException, DBException, DriverLoadException, IOException {
		saveToPostGIS(layer, dbase, layer.getName());
	}

	/**
	 * @param layer
	 *            FLyrVect to obtain features. If selection, only selected
	 *            features will be precessed.
	 * @param writer
	 *            (Must be already initialized)
	 * @throws EditionException
	 * @throws DriverException
	 * @throws DriverIOException
	 * @throws com.hardcode.gdbms.engine.data.driver.DriverException
	 * @throws ReadDriverException 
	 * @throws VisitorException 
	 * @throws SQLException 
	 * @throws DBException 
	 * @throws DriverLoadException 
	 */
	public static void writeFeaturesNoThread(FLyrVect layer, IWriter writer)
	throws  DriverIOException,
	com.hardcode.gdbms.engine.data.driver.DriverException, ReadDriverException, VisitorException, DriverLoadException, DBException, SQLException {

		ReadableVectorial va = layer.getSource();
		SelectableDataSource sds = layer.getRecordset();

		writer.preProcess();

		int rowCount;
		FBitSet bitSet = layer.getRecordset().getSelection();

		if (bitSet.cardinality() == 0)
			rowCount = va.getShapeCount();
		else
			rowCount = bitSet.cardinality();

		ProgressMonitor progress = new ProgressMonitor(
				(JComponent) PluginServices.getMDIManager().getActiveWindow(),
				PluginServices.getText(null, "exportando_features"),
				PluginServices.getText(null, ""), 0,
				rowCount);

		progress.setMillisToDecideToPopup(200);
		progress.setMillisToPopup(500);

		if (bitSet.cardinality() == 0) {
			rowCount = va.getShapeCount();
			for (int i = 0; i < rowCount; i++) {
				IGeometry geom = va.getShape(i);

				progress.setProgress(i);
				if (progress.isCanceled())
					break;

				if (geom != null) {
					Value[] values = sds.getRow(i);
					IFeature feat = new DefaultFeature(geom, values, "" + i);
					DefaultRowEdited edRow = new DefaultRowEdited(feat,  DefaultRowEdited.STATUS_ADDED, i);
					writer.process(edRow);
				}
			}
		} else {
			int counter = 0;
			for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
				IGeometry geom = va.getShape(i);

				progress.setProgress(counter++);
				if (progress.isCanceled())
					break;

				if (geom != null) {
					Value[] values = sds.getRow(i);
					IFeature feat = new DefaultFeature(geom, values, "" + i);
					DefaultRowEdited edRow = new DefaultRowEdited(feat,	DefaultRowEdited.STATUS_ADDED, i);
					writer.process(edRow);
				}
			}

		}

		writer.postProcess();
		progress.close();

	}

	public static void convertASCtoGeoTIFF(String pathAsc, String model, IProjection viewProj) throws NotSupportedExtensionException, RasterDriverException, IOException, InterruptedException{
		IBuffer buf = null;
		RasterDataset f = null;
		f = RasterDataset.open(null, pathAsc);
		BufferFactory ds = new BufferFactory(f);
		ds.setReadOnly(true);
		int[] drawableBands = {0, 1, 2};
		ds.setDrawableBands(drawableBands);
		try {
			ds.setAreaOfInterest(0, 0, f.getWidth(), f.getHeight());
		} catch (InvalidSetViewException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (RasterDriverException e) {
			e.printStackTrace();
		}
		buf = ds.getRasterBuf();
		WriterBufferServer writerBufferServer = new WriterBufferServer(buf);
		String pathTif = pathAsc.substring(0, pathAsc.lastIndexOf(".asc"))+".tif";
		String pathRmf = pathAsc.substring(0, pathAsc.lastIndexOf(".asc"))+".rmf";
		File old = new File(pathRmf);
		old.delete();
		
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(model, HydrologicalModelFactory.registerName);
		
		
		/* Modifica per rotazione griglia*/
		if(doc.getAngle() == 0.0){
			
			GeoRasterWriter grw = GeoRasterWriter.getWriter(writerBufferServer, pathTif,
					buf.getBandCount(), f.getAffineTransform(), buf.getWidth(),
					buf.getHeight(), buf.getDataType(), GeoRasterWriter.getWriter(pathTif).getParams(), viewProj);
					
			grw.dataWrite();
			
			grw.writeClose();
			
						
		}
		
		else{
			//double[] m = {0,0,0,0,0,0};  // DEBUG
			AffineTransform transform = new AffineTransform();
			double pixelX = f.getPixelSizeX();
			double pixelY = f.getPixelSizeY();
			// double fWidth = f.getExtent().width();
			double fHeight = f.getExtent().height();
			
			/*  // DEBUG
			transform.getMatrix(m);
			System.out.println("\nMatrice transform: "+m[0]+" "+m[1]+" "+m[2]+" "+m[3]+" "+m[4]+" "+m[5]);
			
			f.getOwnTransformation().getMatrix(m);
			System.out.println("Matrice OWN: "+m[0]+" "+m[1]+" "+m[2]+" "+m[3]+" "+m[4]+" "+m[5]);
			*/
			
			Point2D pt = new Point2D() {
				
				double x = 0, y = 0;
				
				@Override
				public void setLocation(double arg0, double arg1) {
					x=arg0;
					y=arg1;
				}
				
				@Override
				public double getY() {
					return x;
				}
				
				@Override
				public double getX() {
					return y;
				}
			};
			
			// when setting the location the coordinates are reversed ( y , x )
			pt.setLocation( f.getHeight()-0.5, 0.5);
			Point2D anchor=f.rasterToWorld(pt);

			transform.preConcatenate(AffineTransform.getScaleInstance(pixelX, pixelY)); 
			// asc files use negative Y axis
			transform.preConcatenate(AffineTransform.getTranslateInstance((-pixelX/2), fHeight-(- pixelY/2)));
			// rotating at the lower left centroid
			transform.preConcatenate(AffineTransform.getRotateInstance(Math.toRadians(doc.getAngle())));
			// moving to anchor point
			transform.preConcatenate(AffineTransform.getTranslateInstance(anchor.getX(), anchor.getY()));
			
			/*  // DEBUG
			System.out.println("\n   RES : "+anchor.getX()+ " "+ anchor.getY());
			
			transform.getMatrix(m);
			System.out.println("\nMatrice transform: "+m[0]+" "+m[1]+" "+m[2]+" "+m[3]+" "+m[4]+" "+m[5]);
			System.out.println("**********TRANSFORM\n Scala:\nx "+transform.getScaleX()+"y "+transform.getScaleY());
			System.out.println(" Trasla:\nx "+transform.getTranslateX()+"y "+transform.getTranslateY());
			*/			
			GeoRasterWriter grw = GeoRasterWriter.getWriter(writerBufferServer, pathTif,
					buf.getBandCount(), transform , buf.getWidth(),
					buf.getHeight(), buf.getDataType(), GeoRasterWriter.getWriter(pathTif).getParams(), viewProj);
					
			grw.dataWrite();
			
			grw.writeClose();
		}
		
		
		//String metadata = pathTif.replaceAll(".tif", ".rmf");
		
	}


	
	/**
	 * Retrieve styles from a Geoserver server.
	 * The server data must be specified in db.properties {geo.resturl, geo.restuser, geo.restpassw}
	 * @return String[] of found styles or null if Geoserver not esists
	 * @throws MalformedURLException if geo.resturl is malformed
	 */
	public static String[] getGeoStyle() throws MalformedURLException {

		String[] styles = new String[]{};
		Properties props = Utils.getDBProperties(); 					
		String resturl = props.getProperty("geo.resturl");
		String restuser = props.getProperty("geo.restuser");
		String restpassw = props.getProperty("geo.restpassw");
		
		GeoServerRESTReader reader = new GeoServerRESTReader(resturl, restuser, restpassw);
		
		if(!reader.existGeoserver())
			return null;
		
		ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i< reader.getStyles().size(); i++)
			{
				String styleName = reader.getStyles().getNames().get(i);
				list.add(styleName);
				
			}
			styles = new String[list.size()];
			list.toArray(styles);
		
		return styles;
	}

	/**
	 * Retrieve styles from a Geoserver server using a separate Thread.
	 * The server data must be specified in db.properties {geo.resturl, geo.restuser, geo.restpassw}
	 * The Thread set the styleCBox with the retrived styles and the statusLabel with a message about the status of the server
	 * @param jComboBox styleCBox
	 * @param jLabel statusLabel
	 * @return void
	 */
	public static void getGeoStyleByThread(final JComboBox styleCBox, final JLabel statusLabel) {

		new Thread() {
			@Override
			public void run() {
				String[] styles = null;
					
				Properties props = Utils.getDBProperties(); 					
				String resturl = props.getProperty("geo.resturl");
				String restuser = props.getProperty("geo.restuser");
				String restpassw = props.getProperty("geo.restpassw");
				
				try {

					GeoServerRESTReader reader = new GeoServerRESTReader(resturl, restuser, restpassw);

					if(reader.existGeoserver()){
						
						styles = new String[]{};
						ArrayList<String> list = new ArrayList<String>();
							for (int i = 0; i< reader.getStyles().size(); i++)
							{
								String styleName = reader.getStyles().getNames().get(i);
								list.add(styleName);
								
							}
							styles = new String[list.size()];
							list.toArray(styles);
					}
					
				} catch (MalformedURLException e) {
					statusLabel.setText(PluginServices.getText(this, "Bad_URL"));
				}
				// null means geoserver not exists
				if(styles == null)
					statusLabel.setText(PluginServices.getText(this, "NOT_Working"));
				else
				{
					statusLabel.setText(PluginServices.getText(this, "Working"));
					ComboBoxModel jComboVectParamsModel = 
						new DefaultComboBoxModel(styles);
					styleCBox.setModel(jComboVectParamsModel);
				}
			}

		}.start();
		
	}

	/**
	 * Retrieve Geoserver status using a separate Thread.
	 * The server data must be specified in db.properties {geo.resturl, geo.restuser, geo.restpassw}
	 * The Thread set the statusLabel with a message about the status of the server
	 * @param jLabel statusLabel
	 * @return void
	 */
	public static void getGeoserveStatusByThread(final JLabel statusLabel) {

		new Thread() {
			@Override
			public void run() {
					
				Properties props = Utils.getDBProperties(); 					
				String resturl = props.getProperty("geo.resturl");
				String restuser = props.getProperty("geo.restuser");
				String restpassw = props.getProperty("geo.restpassw");
				
				try {

					GeoServerRESTReader reader = new GeoServerRESTReader(resturl, restuser, restpassw);

					if(reader.existGeoserver())
						statusLabel.setText(PluginServices.getText(this, "Working"));
					else
						statusLabel.setText(PluginServices.getText(this, "NOT_Working"));
							
				} catch (MalformedURLException e) {
					statusLabel.setText(PluginServices.getText(this, "Bad_URL"));
				}
			}

		}.start();
		
	}
	
	
}
