package it.sidgrid.tools;

import it.sidgrid.sextante.tools.PolygonToZoneBudgetAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongOutputIDException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.Action;

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
 * SID&GRID tool to create the budget zone layer from a model layer
 * request PolygonToZoneBudgetAlgorithm SEXTANTE algorithm
 * Menu: SG_Analysis/Create_Zone_Budget
 * 
 */

public class CreateZoneBudget extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private ComboBoxModel jComboDBModel;
	private JComboBox comboBox;
	private JFormattedTextField numLayer;
	private final Action action = new SwingAction();
	FLayer shape;
	private JButton btnCloseTool;
	
	public CreateZoneBudget(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
	}
	
	private void initGUI() {
		
		// the title is set by the getwindowInfo()
		this.setSize(355, 204);
		this.setVisible(true);
		this.setLayout(null);
		this.add(getNumLayer());
		jComboDBModel =  new DefaultComboBoxModel(Utils.getDbase());
		JComboBox dbComboBox = new JComboBox(jComboDBModel);
		dbComboBox.setBounds(32, 20, 182, 27);
		add(dbComboBox);
		
		JLabel geoDBLabel = new JLabel(PluginServices.getText(this,"GeoDB"));
		geoDBLabel.setHorizontalAlignment(SwingConstants.LEFT);
		geoDBLabel.setBounds(224, 25, 124, 16);
		add(geoDBLabel);
		
		comboBox = new JComboBox(Utils.getVectLayers(mapCtrl));
		comboBox.setBounds(32, 52, 182, 27);
		add(comboBox);
		
		JLabel layerLabel = new JLabel(PluginServices.getText(this,"Model_layer"));
		layerLabel.setHorizontalAlignment(SwingConstants.LEFT);
		layerLabel.setBounds(224, 57, 124, 16);
		add(layerLabel);
		
		
		JButton runButton = new JButton("");
		runButton.setAction(action);
		runButton.setBounds(215, 136, 117, 29);
		add(runButton);
		
		JLabel numberLabel = new JLabel(PluginServices.getText(this,"Total_layer"));
		numberLabel.setHorizontalAlignment(SwingConstants.LEFT);
		numberLabel.setBounds(224, 93, 124, 16);
		add(numberLabel);
		
		add(getBtnCloseTool());
		
	}

	private JFormattedTextField getNumLayer() {
		if(numLayer == null) {
			numLayer = new JFormattedTextField(int.class);
			numLayer.setValue(0);
			numLayer.setBounds(66, 91, 142, 21);
		}
		return numLayer;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {			
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Create_Zone_Budget"));
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
		@Override
		public void actionPerformed(ActionEvent e) {
			String zone = (String)comboBox.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect zoneGeomtry = (FLyrVect)layers.getLayer(zone);
			final gvVectorLayer layerZone = new gvVectorLayer();
			layerZone.create(zoneGeomtry);

			
			PluginServices.getMDIManager().closeWindow(CreateZoneBudget.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {

					PolygonToZoneBudgetAlgorithm alg =  new PolygonToZoneBudgetAlgorithm();

					try {
						VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");		
						int numberLayer = Integer.parseInt(numLayer.getText());
						ParametersSet params = alg.getParameters();
						params.getParameter(PolygonToZoneBudgetAlgorithm.POLYGON).setParameterValue(layerZone);
						params.getParameter(PolygonToZoneBudgetAlgorithm.NL).setParameterValue(numberLayer);
						
						OutputObjectsSet outputs = alg.getOutputObjects();
						Output out = outputs.getOutput(PolygonToZoneBudgetAlgorithm.RESULT);	
						out.setOutputChannel(new FileOutputChannel(".shp"));
						alg.execute(null, m_OutputFactory);

						String layerName_tmp = JOptionPane.showInputDialog("Layer name");
						String layerName = layerName_tmp+"_bzone";
						IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
						shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
						mapCtrl.getMapContext().getLayers().addLayer(shape);
						FLayer line = mapCtrl.getMapContext().getLayers().getLayer(layerName);

						int save = JOptionPane.showConfirmDialog(null, PluginServices.getText(this, "Save_to_database?"));

						if (save==JOptionPane.YES_OPTION)
						{
							Utils.saveToPostGIS( (FLyrVect) line, (String) jComboDBModel.getSelectedItem());
							//CaricaPostGis load = new CaricaPostGis();
							String dbase = (String) jComboDBModel.getSelectedItem();
							Properties props = Utils.getDBProperties();
							String host = props.getProperty("db.host");  
					        String username = props.getProperty("db.user");  
					        String password = props.getProperty("db.passwd");
					        String port = props.getProperty("db.port");
					        
							mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());
							mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS( host, Integer.valueOf(port), dbase,  username, password, layerName, layerName));

						}
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
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(CreateZoneBudget.this);
						}
					});
			btnCloseTool.setBounds(88, 136, 117, 29);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
}
