package it.sidgrid.tools;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
/*
 * import javax.swing.JOptionPane;


import org.apache.log4j.Logger;
import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapControl;
 */
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.ConnectionFactory;
import com.iver.cit.gvsig.fmap.drivers.ConnectionJDBC;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGisDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
//import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;

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
 * Create a gvSIG layer from a SID&GRID data base table
 */

public class CaricaPostGis {
//    private static String selectedDriver;

//private static Logger log = Logger.getLogger(CaricaPostGis.class);

public CaricaPostGis() {
//        selectedDriver = "PostGIS JDBC Driver";
}


/**
 * 
 * @param host
 * @param port
 * @param dbase
 * @param user
 * @param pwd
 * @param layerName
 * @param tableName
 * @return
 * @throws SQLException
 */
public static FLayer getLayerPostGIS(String host, Integer port, String dbase, String user, String pwd, String layerName, String tableName) throws SQLException{
	
	// Check if Postgresql driver exists
	try {
		Class.forName("org.postgresql.Driver");
	} catch (ClassNotFoundException e1) {
		// Driver not found
		e1.printStackTrace();
	}
	
	//String whereClause = "";
	
	String dbURL = "jdbc:postgresql://"+host+":" + port + "/" + dbase ;
	IConnection conn;
	try {
		conn = ConnectionFactory.createConnection(dbURL, user, pwd);
		
		String fidField = "gid";
		String geomField = "the_geom";
		
		Statement st = ((ConnectionJDBC)conn).getConnection().createStatement();
		ResultSet rs = st.executeQuery("select * from " + tableName + " LIMIT 1");
		ResultSetMetaData rsmd = rs.getMetaData();
		String[] fields = new String[rsmd.getColumnCount()-1]; // We don't want to include the_geom field
		int j = 0;
		for (int i = 0; i < fields.length; i++) {
			if (!rsmd.getColumnName(i+1).equalsIgnoreCase(geomField))
			{
				fields[j++] = rsmd.getColumnName(i+1);
			}
		}

		rs.close();
		
		DBLayerDefinition lyrDef = new DBLayerDefinition();
		lyrDef.setName(layerName);
		lyrDef.setSchema("public");  // TODO: read from db.properties?
		lyrDef.setTableName(tableName);
		lyrDef.setWhereClause("");	// mandatory
		lyrDef.setFieldNames(fields);
		lyrDef.setFieldGeometry(geomField);
		lyrDef.setFieldID(fidField);
		
		lyrDef.setUser(user);
		lyrDef.setPassword(pwd);
		lyrDef.setHost(host);
		lyrDef.setPort(port);
				
		PostGisDriver pgd = new PostGisDriver();
		pgd.setData(conn, lyrDef);
		//  	connection open?

		return LayerFactory.createDBLayer(pgd, layerName, CRSFactory.getCRS("EPSG:"+pgd.getLyrDef().getSRID_EPSG()));
		
	} catch (DBException e) {
		e.printStackTrace();
		return null;
	}



	

}

}
