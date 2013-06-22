package it.sidgrid.tools;

import it.sidgrid.sextante.tools.CreateSurfaceLayerAlgorithm;
import it.sidgrid.utils.Utils;

import javax.swing.JComboBox;

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

import es.unex.sextante.core.ITaskMonitor;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongOutputIDException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
 * create the surface model layer from a exist model layer
 * request CreateSurfaceLayerAlgorithm SEXTANTE algorithm
 * 
 */

public class SurfaceModelLayer extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private JComboBox comboBox;
	private ComboBoxModel jComboDBModel;
	private final Action action = new SwingAction();
	FLayer shape;
	private JButton btnCloseTool;

	public SurfaceModelLayer(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
	}
	
private void initGUI() {
		
		this.setSize(365, 180);
		this.setVisible(true);
		this.setLayout(null);
		
		comboBox = new JComboBox(Utils.getVectLayers(mapCtrl));
		comboBox.setBounds(20, 53, 182, 27);
		add(comboBox);
		
		jComboDBModel =  new DefaultComboBoxModel(Utils.getDbase());
		JComboBox dbComboBox = new JComboBox(jComboDBModel);
		dbComboBox.setBounds(20, 21, 182, 27);
		add(dbComboBox);
		
		JLabel lblNewLabel = new JLabel(PluginServices.getText(this,"Model_layer"));
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(202, 58, 124, 16);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel(PluginServices.getText(this,"GeoDB"));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(231, 26, 95, 16);
		add(lblNewLabel_1);
		
		JButton btnNewButton = new JButton("Run");
		btnNewButton.setAction(action);
		btnNewButton.setBounds(231, 107, 107, 27);
		add(btnNewButton);
		add(getBtnCloseTool());
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {			
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "surface_grid"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(SurfaceModelLayer.this);
						}
					});
			btnCloseTool.setBounds(20, 107, 107, 27);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Run"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String top = (String)comboBox.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect topGeomtry = (FLyrVect)layers.getLayer(top);
			final gvVectorLayer layerSurface = new gvVectorLayer();
			layerSurface.create(topGeomtry);
			
			PluginServices.getMDIManager().closeWindow(SurfaceModelLayer.this);
			
			new Thread() {
				@Override
				public void run() {

					CreateSurfaceLayerAlgorithm alg =  new CreateSurfaceLayerAlgorithm();
					ITaskMonitor test_waitingpanel = new DefaultTaskMonitor("CreateSurfaceLayerAlgorithm", true, null);
					try {
						VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
						
						ParametersSet params = alg.getParameters();
						params.getParameter(CreateSurfaceLayerAlgorithm.INPUT).setParameterValue(layerSurface);
												
						OutputObjectsSet outputs = alg.getOutputObjects();
						Output out = outputs.getOutput(CreateSurfaceLayerAlgorithm.RESULT);	
						out.setOutputChannel(new FileOutputChannel(".shp"));
						alg.execute(test_waitingpanel, m_OutputFactory);
						test_waitingpanel.close();

						String layerName = JOptionPane.showInputDialog("Layer name");
						IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
						shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
						mapCtrl.getMapContext().getLayers().addLayer(shape);
						FLayer surface = mapCtrl.getMapContext().getLayers().getLayer(layerName);

						int save = JOptionPane.showConfirmDialog(null, PluginServices.getText(this, "Save_to_database?"));

						if (save==JOptionPane.YES_OPTION)
						{
							Utils.saveToPostGIS( (FLyrVect) surface, (String) jComboDBModel.getSelectedItem());				

							//CaricaPostGis load = new CaricaPostGis();
							String dbase = (String) jComboDBModel.getSelectedItem();
							Properties props = Utils.getDBProperties(); 					
							
							String username = props.getProperty("db.user");  
					        String password = props.getProperty("db.passwd");
					        String port = props.getProperty("db.port");
					        String host = props.getProperty("db.host");
							
							mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());
							mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS(host,Integer.valueOf(port),  dbase,  username, password, layerName, layerName));

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
					JOptionPane.showMessageDialog(null,
							PluginServices.getText(this, "Run_successfull"),
							"Avviso",
							JOptionPane.INFORMATION_MESSAGE);//NON ED


				}

			}.start();
			
		}
	}
}
