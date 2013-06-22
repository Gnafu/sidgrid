package it.sidgrid.tools;

import java.awt.event.ActionEvent;
import java.io.File;

import it.sidgrid.sextante.tools.PolygonToWeatherAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.cresques.cts.IProjection;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongOutputIDException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvTable;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
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
 * Create Weather model layer by gvSIG gui
 * request PolygonToWeatherAlgorithm SEXTANTE algorithm
 */
public class CreateWeatherLayer extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private ComboBoxModel jComboDBModel;
	private JComboBox comboBox;
	private JComboBox jComboBoxTable;
	private final Action action = new SwingAction();
	FLayer shape;
	private JButton btnCloseTool;
	
	
	public CreateWeatherLayer(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
	}
	
	private void initGUI() {
		
		this.setSize(355, 204);
		this.setVisible(true);
		this.setLayout(null);

		jComboDBModel =  new DefaultComboBoxModel(Utils.getDbase());
		JComboBox dbComboBox = new JComboBox(jComboDBModel);
		dbComboBox.setBounds(10, 11, 182, 27);
		add(dbComboBox);
		
		JLabel geoDBLabel = new JLabel(PluginServices.getText(this,"GeoDB"));
		geoDBLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		geoDBLabel.setBounds(186, 11, 124, 16);
		add(geoDBLabel);
		
		comboBox = new JComboBox(Utils.getVectLayers(mapCtrl));
		comboBox.setBounds(10, 43, 182, 27);
		add(comboBox);
		
		JLabel layerLabel = new JLabel(PluginServices.getText(this,"Model_layer"));
		layerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		layerLabel.setBounds(186, 43, 124, 16);
		add(layerLabel);
		
		
		
		JLabel stressPeriodLabel = new JLabel(PluginServices.getText(this,"Time_Table"));
		stressPeriodLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stressPeriodLabel.setBounds(186, 83, 124, 16);
		add(stressPeriodLabel);
		
		JButton runButton = new JButton("");
		runButton.setAction(action);
		runButton.setBounds(193, 131, 117, 29);
		add(runButton);
		this.add(getJComboBoxTable());
		this.add(getBtnCloseTool());
		
	}
	
	private JComboBox getJComboBoxTable() {
		if(jComboBoxTable == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getProjectTableNames());
			jComboBoxTable = new JComboBox();
			jComboBoxTable.setBounds(10, 78, 182, 27);
			jComboBoxTable.setModel(jComboBoxTableModel);
		}
		return jComboBoxTable;
	}
	
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {			
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Create_Weather_layer"));

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this, "Run"));

		}
		public void actionPerformed(ActionEvent e) {
			String zone = (String)comboBox.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect zoneGeomtry = (FLyrVect)layers.getLayer(zone);
			final gvVectorLayer layerZone = new gvVectorLayer();
			layerZone.create(zoneGeomtry);
			
			String stressp = (String) jComboBoxTable.getSelectedItem();
			ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
			final ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
			gvTable tableStress = new gvTable();
			tableStress.create(stresstable);
			
			PluginServices.getMDIManager().closeWindow(CreateWeatherLayer.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				public void run() {

					PolygonToWeatherAlgorithm alg =  new PolygonToWeatherAlgorithm();

					try {
						VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
//						int dSp = Integer.parseInt(stressperiodText.getText());
						int nsp = stresstable.getModelo().getRowCount();
						ParametersSet params = alg.getParameters();
						params.getParameter(PolygonToWeatherAlgorithm.POLYGON).setParameterValue(layerZone);
						params.getParameter(PolygonToWeatherAlgorithm.NSP).setParameterValue(nsp);						
						
						OutputObjectsSet outputs = alg.getOutputObjects();
						Output out = outputs.getOutput(PolygonToWeatherAlgorithm.RESULT);	
						out.setOutputChannel(new FileOutputChannel(".shp"));
						alg.execute(null, m_OutputFactory);

						String layerName_tmp = JOptionPane.showInputDialog("Layer name");
						String layerName = layerName_tmp+"_weather";
						IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
						shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
						mapCtrl.getMapContext().getLayers().addLayer(shape);

						/*DON'T PUT INTO DATABASE - JYTHON SCRIPT REQUEST FOR THIS MODEL DATA OBJECT
						PUT INTO DATABASE ONLY AFTER JYTHON SCRIPT FOR RUNOFF WAS APPLIED*/
//						FLayer line = mapCtrl.getMapContext().getLayers().getLayer(layerName);
						
//						int save = JOptionPane.showConfirmDialog(null, PluginServices.getText(this, "Save_to_database?"));
//
//						if (save==JOptionPane.YES_OPTION)
//						{
//							Utils.saveToPostGIS( (FLyrVect) line, (String) jComboDBModel.getSelectedItem());
//							//CaricaPostGis load = new CaricaPostGis();
//							String dbase = (String) jComboDBModel.getSelectedItem();
//							Properties props = Utils.getDBProperties(); 					
//							String username = props.getProperty("db.user");  
//					        String password = props.getProperty("db.passwd");
//					        String port = props.getProperty("db.port");
//					        String host = props.getProperty("db.host");
//					        
//							mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());
//							mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS( host, Integer.valueOf(port), dbase, username, password, layerName, layerName));
//							test.dispose();
//
//						}
//						else
//						{
//							test.dispose();
//						}
						JOptionPane.showMessageDialog(null,
								PluginServices.getText(this, "Run_successfull"),
								"Avviso",
								JOptionPane.INFORMATION_MESSAGE);//NON ED
						
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
					}
					finally{
						test.dispose();
					}
				}

			}.start();
			
			
		}
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(CreateWeatherLayer.this);
						}
					});
			btnCloseTool.setBounds(10, 131, 117, 29);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	
}
