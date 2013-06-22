package it.sidgrid.tools;

import it.sidgrid.sextante.tools.PointToWellAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.unex.sextante.core.ITaskMonitor;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongOutputIDException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvTable;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.cresques.cts.IProjection;

import java.awt.Dimension;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.Color;

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
 * create a well model layer from a well shape
 * request PointToWellAlgorithm SEXTANTE algorithm
 * 
 */
public class PointToWell extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private JComboBox jComboPoint;
	private JComboBox jComboGrid;
	private JComboBox jComboDB;
	private JButton jButton1;
	private AbstractAction abstractAction1;
	private WindowInfo viewInfo = null;
	FLayer shape;
	private JLabel jLabelDB;
	private JLabel jLabelSP;
	private JLabel jLabelPoint;
	private JLabel jLabelGrid;
	private JLabel jLabelModel;
	private JComboBox jComboBoxTable;
	private JComboBox jComboBoxModel;

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Point_to_Well"));
			viewInfo.setHeight(260);
			viewInfo.setWidth(360);
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public PointToWell(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();		
	}

	private void initGUI(){

		this.setName(PluginServices.getText(this,"Point_to_Well"));		
		this.setVisible(true);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(357, 262));
		this.add(getJComboPoint());
		this.add(getJComboDB());
		this.add(getJButton1());
		this.add(getJLabelPoint());
		this.add(getJLabelDB());
		this.add(getJLabelSP());
		this.add(getJComboBoxTable());
		this.add(getJComboGrid());
		this.add(getJLabelGrid());
		this.add(getJLabelModel());
		this.add(getJComboModel());
		add(getBtnCloseTool());
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(24, 46, 308, 15);
		add(separator);
				
	}

	private JComboBox getJComboBoxTable() {
		if(jComboBoxTable == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getProjectTableNames());
			jComboBoxTable = new JComboBox();
			jComboBoxTable.setBounds(169, 100, 165, 27);
			jComboBoxTable.setModel(jComboBoxTableModel);
		}
		return jComboBoxTable;
	}
	
	
	
	private JComboBox getJComboPoint() {
		if(jComboPoint == null) {
			ComboBoxModel jComboPointModel = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboPoint = new JComboBox();
			jComboPoint.setModel(jComboPointModel);
			jComboPoint.setBounds(169, 139, 165, 22);
		}
		return jComboPoint;
	}
	
	private JComboBox getJComboModel() {
		if(jComboBoxModel == null) {
			ComboBoxModel jComboPointModel = new DefaultComboBoxModel(Utils.getModelsNames());
			jComboBoxModel = new JComboBox();
			jComboBoxModel.setModel(jComboPointModel);
			jComboBoxModel.setBounds(169, 16, 165, 22);
		}
		return jComboBoxModel;
	}
	
	private JComboBox getJComboGrid() {
		if(jComboGrid == null) {
			ComboBoxModel jComboPointModel = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboGrid = new JComboBox();
			jComboGrid.setModel(jComboPointModel);
			jComboGrid.setBounds(169, 173, 165, 22);
		}
		return jComboGrid;
	}

	private JComboBox getJComboDB(){
		if(jComboDB == null) {
			ComboBoxModel jComboDBModel =  new DefaultComboBoxModel(Utils.getDbase());
			jComboDB = new JComboBox();
			jComboDB.setModel(jComboDBModel);
			jComboDB.setBounds(24, 102, 130, 22);
		}
		return jComboDB;
	}

	private JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText(PluginServices.getText(this, "Run"));
			jButton1.setBounds(254, 218, 80, 22);
			jButton1.setAction(getAbstractAction1());
		}
		return jButton1;
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private JButton btnCloseTool;

	private AbstractAction getAbstractAction1() {
		if(abstractAction1 == null) {
			abstractAction1 = new AbstractAction(PluginServices.getText(this, "Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {

					String Point = (String)jComboPoint.getSelectedItem();
					FLayers layers = mapCtrl.getMapContext().getLayers();										
					FLyrVect pointgeometry = (FLyrVect)layers.getLayer(Point);
					final gvVectorLayer layerPoint = new gvVectorLayer();
					layerPoint.create(pointgeometry);

					final String Model = (String)jComboBoxModel.getSelectedItem();
					
					final String Grid = (String)jComboGrid.getSelectedItem();					
//					FLyrVect modelgrid = (FLyrVect)layers.getLayer(Grid);
//					final gvVectorLayer layerGrid = new gvVectorLayer();
//					layerGrid.create(modelgrid);

					String stressp = (String) jComboBoxTable.getSelectedItem();
					ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					final ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
					gvTable tableStress = new gvTable();
					tableStress.create(stresstable);
					
					
					PluginServices.getMDIManager().closeWindow(PointToWell.this);
//					final WaitingPanel test = new WaitingPanel();

					
					new Thread() {
						@Override
						public void run() {

							PointToWellAlgorithm alg =  new PointToWellAlgorithm();
							ITaskMonitor test_waitingpanel = new DefaultTaskMonitor("PointToWellAlgorithm", true, null);
							Connection conn = null;
							WaitingPanel test = null;
							try {
								int nsp = stresstable.getModelo().getRowCount();
								
								VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
								

								ParametersSet params = alg.getParameters();
								params.getParameter(PointToWellAlgorithm.LAYER).setParameterValue(layerPoint);
//								params.getParameter(PointToWellAlgorithm.GRID).setParameterValue(layerGrid);
								params.getParameter(PointToWellAlgorithm.NSP).setParameterValue(nsp);
								OutputObjectsSet outputs = alg.getOutputObjects();
								Output out = outputs.getOutput(PointToWellAlgorithm.RESULT);	
								out.setOutputChannel(new FileOutputChannel(".shp"));
								alg.execute(test_waitingpanel, m_OutputFactory);
								test_waitingpanel.close();
								String layerName = Model+"_well";
								IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
								shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
								mapCtrl.getMapContext().getLayers().addLayer(shape);
								FLayer point = mapCtrl.getMapContext().getLayers().getLayer(layerName);
								
								
								/******
								 * Link Model Data Object to 
								 * GeoDataBase by SQL
								 * ************/		
								int save = JOptionPane.showConfirmDialog(null, PluginServices.getText(this, "Save_to_database?"));

								if (save==JOptionPane.YES_OPTION)
								{
									Utils.saveToPostGIS( (FLyrVect) point, (String) jComboDB.getSelectedItem());
								}

								test = new WaitingPanel();
								
								String dbase = (String) jComboDB.getSelectedItem();
								Properties props = Utils.getDBProperties(); 					
								//String jdbc = props.getProperty("jdbc");  
								String host = props.getProperty("db.host");  
						        String username = props.getProperty("db.user");  
						        String password = props.getProperty("db.passwd");
						        String port = props.getProperty("db.port");
						        //String url = "jdbc:" + jdbc + "://"+host+ ":" + port + "/" +dbase;
						        conn = Utils.getConnectionToDatabase(dbase);
						        //Connection conn = DriverManager.getConnection(url, username, password);
								String sql = "update "+layerName+" set \"ROW\" = "+Grid+".\"ROW\", \"COL\" = "+Grid+".\"COL\" from "+Grid+" where ST_Intersects("+layerName+".the_geom, "+Grid+".the_geom)";
								Statement stmt=conn.createStatement();
								stmt.executeUpdate(sql);

								//CaricaPostGis load = new CaricaPostGis();
								mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());
								mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS(host, Integer.valueOf(port), dbase, username, password, layerName, layerName));
								
								test.dispose();
								
								JOptionPane.showMessageDialog(null,
										PluginServices.getText(this, "Run_successfull"),
										"Avviso",
										JOptionPane.INFORMATION_MESSAGE);//NON ED
								
								/******
								 * END sub
								 * ************/
								
							} catch (DriverLoadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (WrongParameterIDException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (WrongOutputIDException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (GeoAlgorithmExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (ReadDriverException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InitializeWriterException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (VisitorException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (DriverIOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (DriverException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (DBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}finally{
								try {
									if(conn!=null)conn.close();
								} catch (SQLException e) {
									/* IGNORED */
								}
								if(test!=null)
									if(test.isDisplayable())
										test.dispose();

							}

						}

					}.start();

				}
			};
		}
		return abstractAction1;
	}

	private JLabel getJLabelPoint() {
		if(jLabelPoint == null) {
			jLabelPoint = new JLabel();
			jLabelPoint.setText(PluginServices.getText(this,"Point_layer"));
			jLabelPoint.setBounds(24, 142, 159, 15);
		}
		return jLabelPoint;
	}
	
	private JLabel getJLabelModel() {
		if(jLabelModel == null) {
			jLabelModel = new JLabel();
			jLabelModel.setText(PluginServices.getText(this,"Model_project"));
			jLabelModel.setBounds(24, 19, 159, 15);
		}
		return jLabelModel;
	}
	
	private JLabel getJLabelGrid() {
		if(jLabelGrid == null) {
			jLabelGrid = new JLabel();
			jLabelGrid.setText(PluginServices.getText(this,"Model_layer"));
			jLabelGrid.setBounds(24, 173, 159, 15);
		}
		return jLabelGrid;
	}

	private JLabel getJLabelDB() {
		if(jLabelDB == null) {
			jLabelDB = new JLabel();
			jLabelDB.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelDB.setText(PluginServices.getText(this,"Geo_DB"));
			jLabelDB.setBounds(40, 75, 96, 15);
		}
		return jLabelDB;
	}

	private JLabel getJLabelSP() {
		if(jLabelSP == null) {
			jLabelSP = new JLabel();
			jLabelSP.setHorizontalAlignment(SwingConstants.RIGHT);
			jLabelSP.setText(PluginServices.getText(this,"Time_Table"));
			jLabelSP.setBounds(194, 73, 130, 15);
		}
		return jLabelSP;
	}

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(PointToWell.this);
						}
					});
			btnCloseTool.setBounds(149, 218, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}

}
