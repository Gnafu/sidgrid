package it.sidgrid.tools;

import it.sidgrid.sextante.tools.PolylineToCHDAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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

import javax.swing.JFormattedTextField;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.cresques.cts.IProjection;
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
 * create a CHD line layer from a polyline shape
 * request PolylineToModelCellsAlgorithm SEXTANTE algorithm
 * 
 */

public class LineToChd extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private DefaultTableModel model;
	private JComboBox comboBox;
	private final Action action_1 = new SwingAction();
	private JComboBox jComboBoxTable;
	private JFormattedTextField fromLayerText;
	private JFormattedTextField tolayersText;
	FLayer shape;
	private ComboBoxModel jComboDBModel;
	private JButton btnCloseTool;
	
	
	public LineToChd(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
	}
	
	private void initGUI() {

		this.setSize(365, 297);
		this.setVisible(true);
		this.setLayout(null);
		model = new DefaultTableModel();			
		model.addColumn("chd layer name");
		comboBox = new JComboBox(Utils.getVectLayers(mapCtrl));
		comboBox.setBounds(32, 52, 182, 27);
		add(comboBox);
		
		
		fromLayerText = new JFormattedTextField();
		fromLayerText.setToolTipText("Model Layer starting from");
		fromLayerText.setText("1");
		fromLayerText.setBounds(42, 129, 65, 28);
		add(fromLayerText);
		
		tolayersText = new JFormattedTextField();
		tolayersText.setToolTipText("Model Layer to end");
		tolayersText.setText("1");
		tolayersText.setBounds(42, 169, 65, 28);
		add(tolayersText);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(146, 130, -77, 12);
		add(separator);
		
		JLabel stressperiodLabel = new JLabel(PluginServices.getText(this,"Time_Table"));
		stressperiodLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		stressperiodLabel.setBounds(146, 91, 182, 16);
		add(stressperiodLabel);
		
		JLabel fromLayerLabel = new JLabel(PluginServices.getText(this,"From_model_layer"));
		fromLayerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		fromLayerLabel.setBounds(167, 135, 161, 16);
		add(fromLayerLabel);
		
		JLabel toLayerLabel = new JLabel(PluginServices.getText(this,"To_model_layer"));
		toLayerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		toLayerLabel.setBounds(195, 175, 133, 16);
		add(toLayerLabel);
		
		jComboDBModel =  new DefaultComboBoxModel(Utils.getDbase());
		JComboBox dbComboBox = new JComboBox(jComboDBModel);
		dbComboBox.setBounds(32, 20, 182, 27);
		add(dbComboBox);
		
		JLabel dbLabel = new JLabel(PluginServices.getText(this,"GeoDB"));
		dbLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		dbLabel.setBounds(226, 25, 102, 16);
		add(dbLabel);
		
		JLabel lineComboBox = new JLabel(PluginServices.getText(this,"Line_layer"));
		lineComboBox.setHorizontalAlignment(SwingConstants.RIGHT);
		lineComboBox.setBounds(236, 57, 92, 16);
		add(lineComboBox);
		
		JButton runButton = new JButton("Run");
		runButton.setAction(action_1);
		runButton.setBounds(226, 221, 117, 29);
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
			jComboBoxTable.setBounds(32, 87, 182, 27);
			jComboBoxTable.setModel(jComboBoxTableModel);
		}
		return jComboBoxTable;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Line_to_Time-Variant_Specified-Head"));
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
			putValue(SHORT_DESCRIPTION, "Run tool");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String line = (String)comboBox.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect lineGeomtry = (FLyrVect)layers.getLayer(line);
			final gvVectorLayer layerLine = new gvVectorLayer();
			layerLine.create(lineGeomtry);
			
			FLyrVect modelgrid = (FLyrVect)layers.getLayer("model_layer_1");
			final gvVectorLayer layerGrid = new gvVectorLayer();
			layerGrid.create(modelgrid);
			

			String stressp = (String) jComboBoxTable.getSelectedItem();
			ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
			final ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
			gvTable tableStress = new gvTable();
			tableStress.create(stresstable);
			
			PluginServices.getMDIManager().closeWindow(LineToChd.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {

					PolylineToCHDAlgorithm alg =  new PolylineToCHDAlgorithm();

					try {
						VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
						
						int nsp = stresstable.getModelo().getRowCount();
						int fromLay = Integer.parseInt(fromLayerText.getText());
						int toLay = Integer.parseInt(tolayersText.getText());
						
						ParametersSet params = alg.getParameters();
						params.getParameter(PolylineToCHDAlgorithm.LINES).setParameterValue(layerLine);
						params.getParameter(PolylineToCHDAlgorithm.GRID).setParameterValue(layerGrid);
						params.getParameter(PolylineToCHDAlgorithm.NSP).setParameterValue(nsp);
						params.getParameter(PolylineToCHDAlgorithm.FROM).setParameterValue(fromLay);
						params.getParameter(PolylineToCHDAlgorithm.TO).setParameterValue(toLay);
						
						OutputObjectsSet outputs = alg.getOutputObjects();
						Output out = outputs.getOutput(PolylineToCHDAlgorithm.RESULT);	
						out.setOutputChannel(new FileOutputChannel(".shp"));
						alg.execute(null, m_OutputFactory);

						String layerName = JOptionPane.showInputDialog("Layer name");
										
						IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
						shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
						mapCtrl.getMapContext().getLayers().addLayer(shape);
						FLayer line = mapCtrl.getMapContext().getLayers().getLayer(layerName);

						int save = JOptionPane.showConfirmDialog(null, "Save_to_database?");
						if (save==JOptionPane.YES_OPTION)
						{												 
							try {
							Utils.saveToPostGIS( (FLyrVect) line, (String) jComboDBModel.getSelectedItem());
							String dbase = (String) jComboDBModel.getSelectedItem();
							Properties props = Utils.getDBProperties(); 					
							String username = props.getProperty("db.user");  
					        String password = props.getProperty("db.passwd");
					        String port = props.getProperty("db.port");
					        String host = props.getProperty("db.host");
					        //CaricaPostGis load = new CaricaPostGis();
							
							mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());				
							mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS(host,Integer.valueOf(port),  dbase, username, password, layerName, layerName));
							
							} catch (NumberFormatException e) {
								// TODO Integer.valueOf catch block
								e.printStackTrace();
							} catch (SQLException e) {
								// TODO CaricaPostGis.getLayerPostGIS catch block
								e.printStackTrace();
							} catch (InitializeWriterException e) {
								// TODO saveToPostGIS catch block
								e.printStackTrace();
							} catch (VisitorException e) {
								// TODO saveToPostGIS catch block
								e.printStackTrace();
							} catch (DriverIOException e) {
								// TODO saveToPostGIS catch block
								e.printStackTrace();
							} catch (DriverException e) {
								// TODO saveToPostGIS catch block
								e.printStackTrace();
							} catch (DBException e) {
								// TODO saveToPostGIS catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO saveToPostGIS catch block
								e.printStackTrace();
							}

							
							
						}
						

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
					test.dispose();
					
					JOptionPane.showMessageDialog(null,
							PluginServices.getText(this, "Run_successfull"),
							"Avviso",
							JOptionPane.INFORMATION_MESSAGE);//NON ED


				}

			}.start();
			
		}
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(LineToChd.this);
						}
					});
			btnCloseTool.setBounds(32, 222, 117, 27);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
}
