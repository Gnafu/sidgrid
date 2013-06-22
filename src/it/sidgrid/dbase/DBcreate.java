package it.sidgrid.dbase;

import it.sidgrid.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;

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
 * Create data base schema for sidgrid model. The connection is base on a simple postgres
 * read db.properites
 * user: postgres
 * psw: postgres 
 */
public class DBcreate extends Extension{

	@Override
	public void execute(String actionCommand) {
		
		String inputname = JOptionPane.showInputDialog(null, "DB Name", PluginServices.getText(this,"Create_DB"), JOptionPane.QUESTION_MESSAGE);
		if(inputname==null || inputname.isEmpty())
			return;
		// Postgres  uses lowercase database names
		String nome = inputname.toLowerCase();
			
		if(!Pattern.matches("[0-9a-zA-Z$_]+", nome))
		{
			JOptionPane.showMessageDialog(null, PluginServices.getText(this, "invalid_db_name"));
			return;
		}
		
		Properties props = Utils.getDBProperties(); 
		
		String jdbc = props.getProperty("jdbc", "postgresql");  
        String username = props.getProperty("db.user", "postgres");  
        String password = props.getProperty("db.passwd", "postgres");
        String port = props.getProperty("db.port", "5432");
        String template = props.getProperty("db.template", "postgistemplate");
		if(!Pattern.matches("[0-9a-zA-Z$_]+", template))
		{
			JOptionPane.showMessageDialog(null, PluginServices.getText(this, "invalid_template_name"));
			return;
		}

        String host = props.getProperty("db.host", "localhost");
 		
		String firstDb = "template1";
        String url = "jdbc:"+ jdbc + "://"+host+":" + port;
			
        Boolean problem = false;  // Exception handling
        
		// Connect to DB server to create DB
        Connection conn = null;
		Statement st = null;
        try {			
			conn = DriverManager.getConnection(url+ "/" + firstDb, username, password);
			st = conn.createStatement();
			
			// TODO: rischio di SQLInjection, usare PreparedStatement?
			// "TABLESPACE pg_default" removed , tablespace is inherited by TEMPLATE
			String sql ="CREATE DATABASE "+ nome + " OWNER "+username+" TEMPLATE "+ template;
			st.execute(sql);
			
		} catch (SQLException e1) {
			problem = true;
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "Connection problems creating DB:\n"+e1.getMessage());
		} finally {
			if(st != null){
				try { st.close(); } catch (SQLException e) { /* ignored */ }
			}
			if(conn != null){
				try { conn.close(); } catch (SQLException e) { /* ignored */ }
			}
			if(problem)return;
		}
		
		// Connect to created DB to create tables using a Transaction
		Connection conn2 = null;
		Statement st2 = null;
		try {
			String url2 = url + "/" + nome ;  // connect to the newly created database
			conn2 = DriverManager.getConnection(url2, username, password);
			// Transaction
			conn2.setAutoCommit(false);
			
			st2 = conn2.createStatement();
			String sql2 = "CREATE table stressperiod (id integer NOT NULL, lenght numeric(40,15) NOT NULL, time_steps integer NOT NULL, multiplier numeric(40,15) NOT NULL, state character(10) NOT NULL, CONSTRAINT StressPeriod_pkey PRIMARY KEY (id))";
			String sql3 = "CREATE table lpf (id integer NOT NULL, Model_layer character(50) NOT NULL, Layer_type character(50) NOT NULL, Layer_average character(50) NOT NULL, Anisotropia character(10) NOT NULL, Value_anisotropia numeric(50,20) NOT NULL, Layer_VKA integer NOT NULL, Layer_Wet character(10) NOT NULL, CONSTRAINT LPF_pkey PRIMARY KEY (id))";
			String sql4 = "CREATE TABLE streamflow (id integer NOT NULL, sp integer NOT NULL, nseg integer NOT NULL, icalc integer NOT NULL, outseg integer NOT NULL, iupseg integer NOT NULL, iprior numeric(40,15) NOT NULL, flow numeric(40,15) NOT NULL, runoff numeric(40,15) NOT NULL, etsw numeric(40,15) NOT NULL, pptsw numeric(40,15) NOT NULL, roughch numeric(40,15) NOT NULL, hcond1 numeric(40,15) NOT NULL, thickm1 numeric(40,15) NOT NULL, elevup numeric(40,15) NOT NULL, width1 numeric(40,15) NOT NULL, thts1 numeric(40,15) NOT NULL, thti1 numeric(40,15) NOT NULL, eps1 numeric(40,15) NOT NULL, uhc1 numeric(40,15) NOT NULL, hcond2 numeric(40,15) NOT NULL, thickm2 numeric(40,15) NOT NULL, elevdn numeric(40,15) NOT NULL, width2 numeric(40,15) NOT NULL, thts2 numeric(40,15) NOT NULL, thti2 numeric(40,15) NOT NULL, eps2 numeric(40,15) NOT NULL, uhc2 numeric(40,15) NOT NULL, CONSTRAINT streamflow_pkey PRIMARY KEY (id))";
			String sql5 = "CREATE TABLE soiltype(id integer NOT NULL,model_layers character(50) NOT NULL,soil_lay_type character(50) NOT NULL,alpha numeric(40,15),vgn numeric(40,15),rsat numeric(40,15),effp numeric(40,15),isc integer NOT NULL, CONSTRAINT soiltype_pkey PRIMARY KEY (id))";
			String sql6 = "CREATE TABLE gridrefine(id SERIAL NOT NULL,name_file text,ishflg integer NOT NULL DEFAULT 1,ibflg integer NOT NULL DEFAULT 1,iter integer NOT NULL DEFAULT 20,relaxh numeric(40,15) NOT NULL DEFAULT 0.5,relaxf numeric(40,15) NOT NULL DEFAULT 0.5,hcloser numeric(40,15) NOT NULL DEFAULT 0.05,fcloser numeric(40,15) NOT NULL DEFAULT 0.05,row_start integer NOT NULL,col_start integer NOT NULL,row_end integer NOT NULL,col_end integer NOT NULL,lay_end integer NOT NULL,ncpp integer NOT NULL,ncppl integer NOT NULL,CONSTRAINT gridrefine_pkey PRIMARY KEY (id))";
			
			st2.execute(sql2);
			st2.execute(sql3);
			st2.execute(sql4);
			st2.execute(sql5);
			st2.execute(sql6);
			conn2.commit();
			
			st2.close();
			conn2.close();

		} catch (SQLException e) {	
			problem = true;
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Connection problems:\n"+e.getMessage());
		} finally {
			if(st2 != null){
				try { st2.close(); } catch (SQLException e) { /* ignored */ }
			}
			if(conn2 != null){
				try { conn2.close(); } catch (SQLException e) { /* ignored */ }
			}
			if(problem)return;
		}
		
		// Looking for a postgres data source driver
		String driverInfo="";
		String [] driverList = LayerFactory.getDM().getDriverNames();
		for (int i=0; i<driverList.length; i++)
		{
			if (driverList[i].toLowerCase().contains("postgresql"))
				driverInfo = driverList[i].toString();
			//System.out.println(driverList[i]);  // debug
		}
				
		try{
			
			String[] tablestoload = {"streamflow", "soiltype", "gridrefine", "stressperiod", "lpf"};
			ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class ); 
			for(String tbName: tablestoload){
				LayerFactory.getDataSourceFactory().addDBDataSourceByTable(tbName, host, Integer.parseInt(port), username, password, nome, tbName, driverInfo); 
				DataSource dataSource = LayerFactory.getDataSourceFactory ().createRandomDataSource(tbName,DataSourceFactory.AUTOMATIC_OPENING);
				SelectableDataSource sds = new SelectableDataSource(dataSource);
				EditableAdapter auxea = new EditableAdapter();
				auxea.setOriginalDataSource(sds);
				ProjectTable projectTable = ProjectFactory.createTable(tbName,auxea);
				ext.getProject().addDocument(projectTable);
				sds.stop();
			}
				
////			Load streamflow table
//			String table1 = "streamflow";
//			LayerFactory.getDataSourceFactory().addDBDataSourceByTable(table1, host, Integer.parseInt(port), username, password, nome, "streamflow", driverInfo); 
//			DataSource dataSource = LayerFactory.getDataSourceFactory ().createRandomDataSource("streamflow",DataSourceFactory.AUTOMATIC_OPENING);
//			SelectableDataSource sds = new SelectableDataSource(dataSource);
//			EditableAdapter auxea = new EditableAdapter();
//			auxea.setOriginalDataSource(sds);
//			ProjectTable projectTable = ProjectFactory.createTable("streamflow",auxea);
//			projectTable.setProjectDocumentFactory(new ProjectTableFactory());  // ARGH!!
//			ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class ); 
//			ext.getProject().addDocument(projectTable);
//			sds.stop();
////			Load soiltable table
//			String table2 = "soiltable";
//			LayerFactory.getDataSourceFactory().addDBDataSourceByTable(table2, host, Integer.parseInt(port), username, password, nome, "streamflow", driverInfo); 
//			DataSource dataSource2 = LayerFactory.getDataSourceFactory ().createRandomDataSource("streamflow",DataSourceFactory.AUTOMATIC_OPENING);
//			SelectableDataSource sds2 = new SelectableDataSource(dataSource2);
//			EditableAdapter auxea2 = new EditableAdapter();
//			auxea2.setOriginalDataSource(sds2);
//			ProjectTable projectTable2 = ProjectFactory.createTable("soiltable",auxea2);
//			projectTable.setProjectDocumentFactory(new ProjectTableFactory());  // ARGH!!
//			ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class ); 
//			ext2.getProject().addDocument(projectTable2);
//			sds2.stop();
//			
////			Load gridrefine table
//			String table3 = "gridrefine";
//			LayerFactory.getDataSourceFactory().addDBDataSourceByTable(table3, host, Integer.parseInt(port), username, password, nome, "gridrefine", driverInfo); 
//			DataSource dataSource3 = LayerFactory.getDataSourceFactory ().createRandomDataSource("gridrefine",DataSourceFactory.AUTOMATIC_OPENING);
//			SelectableDataSource sds3 = new SelectableDataSource(dataSource3);
//			EditableAdapter auxea3 = new EditableAdapter();
//			auxea3.setOriginalDataSource(sds3);
//			ProjectTable projectTable3 = ProjectFactory.createTable("gridrefine",auxea3);
//			projectTable.setProjectDocumentFactory(new ProjectTableFactory());  // ARGH!!
//			ProjectExtension ext3 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class ); 
//			ext3.getProject().addDocument(projectTable3);
//			sds3.stop();

			JOptionPane.showMessageDialog(null, PluginServices.getText(this, "db_created_ok"));

		} catch (DriverLoadException e) {
			e.printStackTrace();
		} catch (NoSuchTableException e) {
			e.printStackTrace();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}

		
	}

	@Override
	public void initialize() {
		
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

}
