package it.sidgrid.tools;

// v 0.5

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.tools.MultiProfileVectorAlgorithm;
import it.sidgrid.utils.Utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.ProgressMonitor;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.ProjectDocument;

import es.unex.sextante.core.ITaskMonitor;
import es.unex.sextante.core.ObjectAndDescription;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongOutputIDException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gui.additionalResults.AdditionalResults;
import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.Output;
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

/**
 * @author sid&grid
 * GUI
 * create a profile graph of the model
 * request a polyline shape as cross section
 * model layer array (model properties)
 * request MultiProfileVectorAlgorithm SEXTANTE algorithm
 */
public class MPVectorAlg extends javax.swing.JPanel implements IWindow {

	private static final long serialVersionUID = 8775849706036161922L;
	private MapControl mapCtrl;
	private JComboBox jComboProfilo;
	//	private JComboBox jComboDB;
	private JButton jButton1;
	private AbstractAction abstractAction1;
	private WindowInfo viewInfo = null;
	FLayer shape;
	//	private JLabel jLabelDB;
	private JLabel jLabelPoint;
	//	private JComboBox cBox_Campo;
	private JList listLayers;
	private JButton btnCloseTool;


	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"2D_Cross_section"));
			viewInfo.setHeight(230);
			viewInfo.setWidth(260);

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public MPVectorAlg(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}

	private void initGUI() {

		this.setName(PluginServices.getText(this,"Multi_Profile_Vector_Algorithm"));
		this.setVisible(true);
		this.setLayout(null);
//		this.setPreferredSize(new Dimension(215, 258));

		JLabel lblGroundLayersSelezionati = new JLabel(PluginServices.getText(this,"Models_layers"));
		lblGroundLayersSelezionati.setBounds(23, 66, 165, 14);
		add(lblGroundLayersSelezionati);

		listLayers = new JList();
		listLayers.setAlignmentY(Component.TOP_ALIGNMENT);
		listLayers.setAlignmentX(Component.LEFT_ALIGNMENT);
		listLayers.setBounds(23, 91, 164, 113);
		// *** Popolo Jlist con la lista degli altri layer del modello ***
		// Recupero il theDocument
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		String[] progetto = getMultiLayer();
		// Devo istanziarlo come array di oggetti perchè DefaultComboBoxModel non prende array di stringhe
		Object[] modelLayers = {};
		if(progetto.length > 0){
			System.out.println("progetto[0] = "+progetto[0]);
			if(progetto.length > 1){ System.out.println("Ci sono piu' theDocument nel progetto: " + progetto[1]);}
 
			HydrologicalModel theDoc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto[0], HydrologicalModelFactory.registerName);
			ArrayList<gvVectorLayer> temp = theDoc.getLayers();
			System.out.println("temp.size() = "+temp.size());  // debug
			modelLayers = new Object[temp.size()];
			for(int k=0; k<temp.size(); k++){
				modelLayers[k] = temp.get(k).getName();
				System.out.println("temp["+k+"] = "+modelLayers[k]); // debug

			}
			ListModel jList2Model = new DefaultComboBoxModel(modelLayers);
			listLayers.setModel(jList2Model);
			//listLayers.validate();
		}else
		{
			System.out.println("Non ci sono modelli nel progetto!");
		}

		add(listLayers);

		this.add(getJComboPoint());

		// Imposto la ComboBox per il layer di linee
		String[] temp = Utils.getVectLayers(mapCtrl);
		ArrayList<String> vectLayersLista = new ArrayList<String>();
		for(int i=0; i<temp.length; i++)
			vectLayersLista.add(temp[i]);
		ArrayList<String> comboLista = new ArrayList<String>();
		boolean trovato = false;
		System.out.println("vectLayersLista: "+vectLayersLista.toString());
		System.out.println("comboLista: "+comboLista.toString());
		for(int i=0; i<vectLayersLista.size(); i++){
			trovato = false;
			for(int j=0; j<modelLayers.length; j++)
				if(vectLayersLista.get(i).equalsIgnoreCase((String) modelLayers[j]))
					trovato = true;
			if(!trovato){
				System.out.println("Aggiungo "+vectLayersLista.get(i));
				comboLista.add(vectLayersLista.get(i));
			}
		}
		ComboBoxModel jComboPointModel = new DefaultComboBoxModel(comboLista.toArray());
		jComboProfilo.setModel(jComboPointModel);
		System.out.println("jCompoPoint Modello contiene "+jComboProfilo.getModel().getSize());
		jComboProfilo.validate();


	
		this.add(getJButton1());
		this.add(getJLabelPoint());
		//		this.add(getJLabelDB());

		//		JLabel lblCampo = new JLabel("Campo");
		//		lblCampo.setBounds(218, 12, 114, 14);
		//		add(lblCampo);
		//
		//		JLabel lblNewLabel = new JLabel("(per salvare i risultati)");
		//		lblNewLabel.setBounds(218, 104, 114, 19);
		//		add(lblNewLabel);
		this.add(getBtnCloseTool());

	}

	private JComboBox getJComboPoint() {		
		if(jComboProfilo == null) {
			ComboBoxModel jComboPointModel = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboProfilo = new JComboBox();
			jComboProfilo.setModel(jComboPointModel);
			jComboProfilo.setBounds(23, 33, 165, 22);
		}
		return jComboProfilo;
	}

	// Metodo di supporto
	private String[] getMultiLayer() {
		String[] solution = null;

		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		ArrayList<ProjectDocument> lista = ext.getProject().getDocuments();
		ArrayList<String> list = new ArrayList<String>();
		for (int i=0; i<lista.size(); i++)
		{
			ProjectDocument doc = lista.get(i);
			if(doc instanceof HydrologicalModel)
			{
				list.add(doc.getName());

			}
		}
		solution = new String[list.size()];
		list.toArray(solution);	    
		return solution;

	}



	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Run");
			jButton1.setBounds(124, 216, 64, 22);
			jButton1.setAction(getRunAction());
		}
		return jButton1;
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();

	private AbstractAction getRunAction() {

		if (abstractAction1 == null) {
			abstractAction1 = new AbstractAction(PluginServices.getText(this,"Run"), null) {
				private static final long serialVersionUID = -2820577575926149548L;

				@Override
				public void actionPerformed(ActionEvent evt) {

					// Imposto lo shape di linee per ROUTE
					String linesLyrSelected = (String) jComboProfilo.getSelectedItem();
					FLayers layers = mapCtrl.getMapContext().getLayers();
					FLyrVect pointgeometry = (FLyrVect) layers.getLayer(linesLyrSelected);
					// Il layer esiste perchè nella jComboProfilo ci metto quelli esistenti.
					// *** ROUTE ***
					final gvVectorLayer linesLayer = new gvVectorLayer();
					linesLayer.create(pointgeometry);					


					// Imposto la lista di layer del modello DEM e LAYERS
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					String[] progetto = getMultiLayer();
					// TODO : Prendo il primo progetto che capita
					ProjectDocument doc = ext.getProject().getProjectDocumentByName(progetto[0], HydrologicalModelFactory.registerName);
					// Compatibilità garantita dal nome del ProjectDocument
					// Non si garantisce doc != null 
					HydrologicalModel theDoc = (HydrologicalModel) doc;
					ArrayList<gvVectorLayer> temp = theDoc.getLayers();
					if(temp.size()==0)
						return;
					//					FLyrVect pointgeometry = theDoc.getLayers().get(pointLyrSelected); // TODO : Cercare con il NOME pointLyrSelected
					//					final gvVectorLayer layerPoint = new gvVectorLayer();
					//					layerPoint.create(pointgeometry);
					//					FLyrVect pointgeometry = null;
					// *** DEM ***
					final gvVectorLayer firstLayer = temp.get(0);
					// *** LAYERS ***
					final ArrayList<gvVectorLayer> otherLayers = new ArrayList<gvVectorLayer>();
					for(int j=1; j<temp.size(); j++)
						otherLayers.add(temp.get(j));
					//					FLyrVect modelgrid = (FLyrVect) layers.getLayer("model_layer_1");
					//					final gvVectorLayer layerGrid = new gvVectorLayer();
					//					layerGrid.create(modelgrid);

					PluginServices.getMDIManager().closeWindow(MPVectorAlg.this);

					// Inutile pannello di waiting, non viene mai usato e l'algoritmo ha il suo.
					//final WaitingPanel test_waitingpanel = new WaitingPanel();

					new Thread() {
						@Override
						public void run() {

							MultiProfileVectorAlgorithm alg = new MultiProfileVectorAlgorithm();
							ITaskMonitor test_waitingpanel = new DefaultTaskMonitor("MultiProfileVectorAlgorithm", true, null);

							try {
								//VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");

								ParametersSet params = alg.getParameters();
								params.getParameter(MultiProfileVectorAlgorithm.ROUTE).setParameterValue(linesLayer);

								params.getParameter(MultiProfileVectorAlgorithm.DEM).setParameterValue(firstLayer);
								if(otherLayers.size()>0)
									params.getParameter(MultiProfileVectorAlgorithm.LAYERS).setParameterValue(otherLayers);

								// *** FIELD **
								//params.getParameter(MultiProfileVectorAlgorithm.FIELD).setParameterValue(cBox_Campo.getSelectedIndex());					

								OutputObjectsSet outputs = alg.getOutputObjects();
								//Output out = outputs.getOutput(MultiProfileVectorAlgorithm.PROFILEPOINTS);
								//out.setOutputChannel(new FileOutputChannel(".shp"));
								// Provo senza output del grafico
								//								Output out2 = outputs.getOutput(MultiProfileVectorAlgorithm.GRAPH);
								//								out2.getOutputObject();
								boolean all_fine = alg.execute(test_waitingpanel, m_OutputFactory);

								test_waitingpanel.close();
								// AddResults()
								{

									final Output out1 = outputs.getOutput(MultiProfileVectorAlgorithm.GRAPH);
									String sDescription = out1.getDescription();
									final Object object = out1.getOutputObject();
									//									         if (object instanceof IVectorLayer) {
									//									            layer = (FLayer) ((IVectorLayer) object).getBaseDataObject();
									//									            if (layer != null) {
									//									               layer.setName(sDescription);
									//									               m_MapContext.getLayers().addLayer(layer);
									//									               bInvalidate = true;
									//									            }
									//									         }
									//									         else
									//									        	 if (object instanceof ITable) {
									//									            try {
									//									               final ProjectTable table = (ProjectTable) ((ITable) object).getBaseDataObject();
									//									               if (table != null) {
									//									                  ((ProjectExtension) PluginServices.getExtension(ProjectExtension.class)).getProject().addDocument(table);
									//									               }
									//									            }
									//									            catch (final Exception e) {
									//									               e.printStackTrace();
									//									            }
									//									         }
									//									         else
									//									        	 if (object instanceof IRasterLayer) {
									//									            final IRasterLayer rasterLayer = (IRasterLayer) object;
									//									            layer = (FLayer) rasterLayer.getBaseDataObject();
									//									            if (layer != null) {
									//									               ((FLyrRasterSE) layer).setNoDataValue(rasterLayer.getNoDataValue());
									//									               layer.setName(sDescription);
									//									               m_MapContext.getLayers().addLayer(layer);
									//									               bInvalidate = true;
									//									            }
									//									         }
									//									         else
									if (object instanceof String) {
										JTextPane jTextPane;
										JScrollPane jScrollPane;
										jTextPane = new JTextPane();
										jTextPane.setEditable(false);
										jTextPane.setContentType("text/html");
										jTextPane.setText((String) object);
										jScrollPane = new JScrollPane();
										jScrollPane.setViewportView(jTextPane);
										jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
										jTextPane.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
										AdditionalResults.addComponent(new ObjectAndDescription(sDescription, jScrollPane));
									}
									else if (object instanceof Component) {
										AdditionalResults.addComponent(new ObjectAndDescription(sDescription, object));
									}

									if(all_fine)
										JOptionPane.showMessageDialog(null, "Procedura eseguita con successo", "Avviso", JOptionPane.INFORMATION_MESSAGE);
									else
										JOptionPane.showMessageDialog(null, "Procedura interrotta dall'utente", "Avviso", JOptionPane.INFORMATION_MESSAGE);


									AdditionalResults.showPanel();

								}


								//String layerName = "MPVector_result";

								//shape = LayerFactory.createLayer(layerName, driver, new File(".shp"), CRSFactory.getCRS("EPSG:3003"));
								//mapCtrl.getMapContext().getLayers().addLayer(shape);
								//FLayer point = mapCtrl.getMapContext().getLayers().getLayer(layerName);

								//								int save = JOptionPane.showConfirmDialog(null, "Save in GeoDB?");
								//
								//								if (save == JOptionPane.YES_OPTION) {
								//									saveToPostGIS(mapCtrl.getMapContext(), (FLyrVect) point);
								//									//test_waitingpanel.dispose();
								//
								//								} else {
								//									//test_waitingpanel.dispose();
								//								}

							} catch (WrongParameterIDException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (WrongOutputIDException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (GeoAlgorithmExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// ED

							//							CaricaPostGis test = new CaricaPostGis();
							//							String dbase = (String) jComboDB.getSelectedItem();
							//							FLyrVect postgis = (FLyrVect) shape;
							//							try {
							//								mapCtrl.getMapContext().getLayers().addLayer(test.getLayerPostGIS("jdbc:postgresql://localhost:5432/" + dbase, "localhost", "postgres", "postgres", "MPVector_result", "MPVector_result", postgis.getShapeType()));
							//								mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());
							//							} catch (ReadDriverException e) {
							//								// TODO Auto-generated catch block
							//								e.printStackTrace();
							//							} catch (SQLException e) {
							//								// TODO Auto-generated catch block
							//								e.printStackTrace();
							//							}

						}

					}.start();

				}
			};
		}
		return abstractAction1;
	}

	//	public void saveToPostGIS(MapContext mapContext, FLyrVect layer) throws DriverIOException, ReadDriverException, InitializeWriterException, VisitorException, DriverException, SQLException {
	//		try {
	//			String tableName = "MPVector_result";
	//
	//			tableName = tableName.toLowerCase();
	//
	////			String dbase = (String) jComboDB.getSelectedItem();
	//
	//			IConnection conex = ConnectionFactory.createConnection("jdbc:postgresql://localhost:5432/" + dbase, "postgres", "postgres");
	//
	//			DBLayerDefinition originalDef = null;
	//			if (layer.getSource().getDriver() instanceof IVectorialDatabaseDriver) {
	//				originalDef = ((IVectorialDatabaseDriver) layer.getSource().getDriver()).getLyrDef();
	//			}
	//
	//			DBLayerDefinition dbLayerDef = new DBLayerDefinition();
	//
	//			dbLayerDef.setCatalogName("");
	//
	//			dbLayerDef.setSchema("public");
	//
	//			dbLayerDef.setTableName(tableName);
	//			dbLayerDef.setName(tableName);
	//			dbLayerDef.setShapeType(layer.getShapeType());
	//			SelectableDataSource sds = layer.getRecordset();
	//
	//			FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
	//			dbLayerDef.setFieldsDesc(fieldsDescrip);
	//
	//			if (originalDef != null) {
	//				dbLayerDef.setFieldID(originalDef.getFieldID());
	//				dbLayerDef.setFieldGeometry(originalDef.getFieldGeometry());
	//
	//			} else {
	//				// Search for id field name
	//				int index = 0;
	//				String fieldName = "gid";
	//				while (findFileByName(fieldsDescrip, fieldName) != -1) {
	//					index++;
	//					fieldName = "gid" + index;
	//				}
	//				dbLayerDef.setFieldID(fieldName);
	//
	//				// search for geom field name
	//				index = 0;
	//				fieldName = "the_geom";
	//				while (findFileByName(fieldsDescrip, fieldName) != -1) {
	//					index++;
	//					fieldName = "the_geom" + index;
	//				}
	//				dbLayerDef.setFieldGeometry(fieldName);
	//
	//			}
	//			// if id field dosen't exist we add it
	//			if (findFileByName(fieldsDescrip, dbLayerDef.getFieldID()) == -1) {
	//				int numFieldsAnt = fieldsDescrip.length;
	//				FieldDescription[] newFields = new FieldDescription[dbLayerDef.getFieldsDesc().length + 1];
	//				for (int i = 0; i < numFieldsAnt; i++) {
	//					newFields[i] = fieldsDescrip[i];
	//				}
	//				newFields[numFieldsAnt] = new FieldDescription();
	//				newFields[numFieldsAnt].setFieldDecimalCount(0);
	//				newFields[numFieldsAnt].setFieldType(Types.INTEGER);
	//				newFields[numFieldsAnt].setFieldLength(7);
	//				newFields[numFieldsAnt].setFieldName(dbLayerDef.getFieldID());
	//				dbLayerDef.setFieldsDesc(newFields);
	//			}
	//
	//			dbLayerDef.setWhereClause("");
	//			String strSRID = layer.getProjection().getAbrev().substring(5);
	//			dbLayerDef.setSRID_EPSG(strSRID);
	//			dbLayerDef.setConnection(conex);
	//
	//			PostGISWriter writer = (PostGISWriter) LayerFactory.getWM().getWriter("PostGIS Writer");
	//			writer.setWriteAll(true);
	//			writer.setCreateTable(true);
	//			writer.initialize(dbLayerDef);
	//			PostGisDriver postGISDriver = new PostGisDriver();
	//			postGISDriver.setLyrDef(dbLayerDef);
	//			postGISDriver.open();
	//			PostProcessSupport.clearList();
	//			Object[] params = new Object[2];
	//			params[0] = conex;
	//			params[1] = dbLayerDef;
	//			PostProcessSupport.addToPostProcess(postGISDriver, "setData", params, 1);
	//
	//			writeFeaturesNoThread(layer, writer);
	//
	//		} catch (DriverLoadException e) {
	//
	//		} catch (DBException e) {
	//
	//		}
	//
	//	}

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
	public void writeFeaturesNoThread(FLyrVect layer, IWriter writer) throws DriverIOException, com.hardcode.gdbms.engine.data.driver.DriverException, ReadDriverException, VisitorException, DriverLoadException, DBException, SQLException {
		ReadableVectorial va = layer.getSource();
		SelectableDataSource sds = layer.getRecordset();

		writer.preProcess();

		int rowCount;
		FBitSet bitSet = layer.getRecordset().getSelection();

		if (bitSet.cardinality() == 0)
			rowCount = va.getShapeCount();
		else
			rowCount = bitSet.cardinality();

		ProgressMonitor progress = new ProgressMonitor((JComponent) PluginServices.getMDIManager().getActiveWindow(), PluginServices.getText(this, "exportando_features"), PluginServices.getText(this, ""), 0, rowCount);

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
					DefaultRowEdited edRow = new DefaultRowEdited(feat, IRowEdited.STATUS_ADDED, i);
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
					DefaultRowEdited edRow = new DefaultRowEdited(feat, IRowEdited.STATUS_ADDED, i);

					writer.process(edRow);
				}
			}

		}

		writer.postProcess();
		progress.close();

	}

	//	private int findFileByName(FieldDescription[] fields, String fieldName) {
	//		for (int i = 0; i < fields.length; i++)
	//
	//		{
	//			FieldDescription f = fields[i];
	//			if (f.getFieldName().equalsIgnoreCase(fieldName)) {
	//				return i;
	//			}
	//		}
	//
	//		return -1;
	//
	//	}

	private JLabel getJLabelPoint() {
		if (jLabelPoint == null) {
			jLabelPoint = new JLabel();
			jLabelPoint.setText(PluginServices.getText(this,"Cross_section_layer"));
			jLabelPoint.setBounds(23, 12, 159, 15);
		}
		return jLabelPoint;
	}

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(MPVectorAlg.this);
						}
					});
			btnCloseTool.setBounds(23, 216, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	//	private JLabel getJLabelDB() {
	//		if (jLabelDB == null) {
	//			jLabelDB = new JLabel();
	//			jLabelDB.setText("Geo DB");
	//			jLabelDB.setBounds(218, 91, 114, 14);
	//		}
	//		return jLabelDB;
	//	}
	//	protected JComboBox get_Campo() {
	//		return cBox_Campo;
	//	}
	public JList getListLayers() {
		return listLayers;
	}
}
