package it.sidgrid.tools;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.tools.CreateTopModelAlgorithm;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.cresques.cts.IProjection;

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
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import es.unex.sextante.core.OutputFactory;

import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import java.awt.Dimension;

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
 * create a top model layer
 * request CreateTopModelAlgorithm SEXTANTE algorithm
 * Menu: SG_Configure/Domain/Create_Top_Model_Layer
 * 
 */
public class CreateTopModelLayer extends javax.swing.JPanel implements IWindow {
	private JComboBox jComboBox1;
	private AbstractAction testAction1;
	private JButton Export;
	private JLabel TopLabel;
	private JLabel BottomLabel;
	private JLabel AngleLabel;
	private JLabel RisolutionXLabel;
	private JLabel RisolutionYLabel;
	private JLabel ModelName;
	private JFormattedTextField Bottom;
	private JFormattedTextField Top;
	private JComboBox jComboBoxDB;
	private JComboBox jComboBoxModel;
	private JFormattedTextField Angle;
	private JFormattedTextField RisolutionY;
	private JFormattedTextField RisolutionX;
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private JLabel DataBaseLabel;
	private JLabel LayerDomain;
	FLayer shape;
	private JButton btnCloseTool;

	public CreateTopModelLayer(MapControl mc){
		super();
		this.mapCtrl = mc;
		initGUI();
	}

	private void initGUI() {
		ComboBoxModel jComboBoxLayersModel= new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		jComboBox1 = new JComboBox();

		ComboBoxModel jComboBoxDBaseModel = new DefaultComboBoxModel(Utils.getDbase());
		jComboBoxDB = new JComboBox();
		this.add(jComboBoxDB);

		ComboBoxModel modelCombobox = new DefaultComboBoxModel(Utils.getModelsNames());	
		jComboBoxModel = new JComboBox();
		jComboBoxModel.setModel(modelCombobox);		
		jComboBoxModel.setBounds(181, 238, 201, 27);
		this.add(jComboBoxModel);
		
		this.add(jComboBox1);
//		this.add(getJLabel1());
//		this.add(getPath());
		this.add(getExport());
		this.add(getRisolutionXLabel());
		this.add(getRisolutionYLabel());
		this.add(getAngleLabel());
		this.add(getTopLabel());
		this.add(getBottomLabel());
		this.add(getRisolutionX());
		this.add(getRisolutionY());
		this.add(getAngle());
		this.add(getTop());
		this.add(getBottom());
		this.add(getDataBaseLabel());
		this.add(getLayerDomain());
		this.add(getBtnCloseTool());
		this.add(getModelLabel());

		jComboBox1.setBounds(181, 24, 201, 22);
		jComboBox1.setModel(jComboBoxLayersModel);
		jComboBox1.setSelectedIndex(0);

		jComboBoxDB.setModel(jComboBoxDBaseModel);
		jComboBoxDB.setSelectedIndex(0);
		jComboBoxDB.setBounds(181, 204, 201, 22);
		

		this.setName(PluginServices.getText(this,"Create_Grid"));
		this.setSize(410, 330);
		this.setVisible(true);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(430, 339));

	}

	private JButton getExport() {
		if(Export == null) {
			Export = new JButton();

			Export.setBounds(302, 285, 80, 22);
			Export.setAction(getTestAction1());
		}
		return Export;
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();

	private AbstractAction getTestAction1(){
		if(testAction1 == null) {
			testAction1 = new AbstractAction(PluginServices.getText(this,"Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {

					PluginServices.getMDIManager().closeWindow(CreateTopModelLayer.this);
//					final WaitingPanel test = new WaitingPanel();

					final DefaultTaskMonitor progress = new DefaultTaskMonitor(
							PluginServices.getText(this, "creating_features"),
							true, null
					);

				
					/* TODO:
					 * 		Il layer esiste già in quel db? sovrascriverlo?
					 */
					
					new Thread() {
						@Override
						public void run() {
							String progetto = (String) jComboBoxModel.getSelectedItem();
							ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
							HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
							
							
							CreateTopModelAlgorithm prova =  new CreateTopModelAlgorithm();
							String selectedLayer = (String)jComboBox1.getSelectedItem();
							FLayers layers = mapCtrl.getMapContext().getLayers();																					
							FLyrVect solution = (FLyrVect)layers.getLayer(selectedLayer);
							gvVectorLayer gvsiglayer = new gvVectorLayer();
							gvsiglayer.create(solution);

							int save = JOptionPane.NO_OPTION;

							try {
								VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");

								double dTop = Double.parseDouble(Top.getText());
								double dBottom = Double.parseDouble(Bottom.getText());
								double dRisolutionX = Double.parseDouble(RisolutionX.getText());
								double dRisolutionY = Double.parseDouble(RisolutionY.getText());
								double dAngle = Double.parseDouble(Angle.getText());
								
								doc.setAngle(dAngle);
								
								ParametersSet params = prova.getParameters();
								params.getParameter(CreateTopModelAlgorithm.LAYER).setParameterValue(gvsiglayer);
								params.getParameter(CreateTopModelAlgorithm.TOP).setParameterValue(dTop);
								params.getParameter(CreateTopModelAlgorithm.BOTTOM).setParameterValue(dBottom);
								params.getParameter(CreateTopModelAlgorithm.INTERVALX).setParameterValue(dRisolutionX);
								params.getParameter(CreateTopModelAlgorithm.INTERVALY).setParameterValue(dRisolutionY);
								params.getParameter(CreateTopModelAlgorithm.ANGLE).setParameterValue(dAngle);
								OutputObjectsSet outputs = prova.getOutputObjects();
								Output out = outputs.getOutput(CreateTopModelAlgorithm.GRATICULE);	
								out.setOutputChannel(new FileOutputChannel(".shp"));
								prova.execute(progress, m_OutputFactory);

								String layerName = "model_layer_1";

								IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
								shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
								mapCtrl.getMapContext().getLayers().addLayer(shape);
								save = JOptionPane.showConfirmDialog(null, PluginServices.getText(this, "Save_to_database?"));

								if (save==JOptionPane.YES_OPTION)
								{
									Utils.saveToPostGIS( (FLyrVect) shape, (String) jComboBoxDB.getSelectedItem());
									String dbase = (String) jComboBoxDB.getSelectedItem();					
									Properties props = Utils.getDBProperties(); 					
									
									String username = props.getProperty("db.user");  
							        String password = props.getProperty("db.passwd");
							        String port = props.getProperty("db.port");
							        String host = props.getProperty("db.host");
	
							        mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());
									mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS(host, Integer.valueOf(port), dbase, username, password, shape.getName(), shape.getName() ));
								}
								
								progress.close();
								
								// da catturare l'eccezione per comunicare all'utente se file creato verra sovrascritto o meno
							} catch (GeoAlgorithmExecutionException e) {
								e.printStackTrace();
							} catch (DriverLoadException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
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
								e.printStackTrace();    // <- Ti interessa questa
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
			};
		}

		return testAction1;
	}


	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton("");
			btnCloseTool.setAction(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(CreateTopModelLayer.this);
						}
					}
					);
			btnCloseTool.setBounds(212, 285, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}

	@Override

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Create_Grid"));
			
		}
		return viewInfo;
	}


	@Override


	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	private JLabel getRisolutionXLabel() {
		if(RisolutionXLabel == null) {
			RisolutionXLabel = new JLabel();
			RisolutionXLabel.setText(PluginServices.getText(this,"Resolution_X"));
			RisolutionXLabel.setBounds(32, 52, 182, 16);
		}
		return RisolutionXLabel;
	}

	private JLabel getRisolutionYLabel() {
		if(RisolutionYLabel == null) {
			RisolutionYLabel = new JLabel();
			RisolutionYLabel.setText(PluginServices.getText(this,"Resolution_Y"));
			RisolutionYLabel.setBounds(32, 80, 183, 16);
		}
		return RisolutionYLabel;
	}

	private JLabel getAngleLabel() {
		if(AngleLabel == null) {
			AngleLabel = new JLabel();
			AngleLabel.setText(PluginServices.getText(this,"Angle"));
			AngleLabel.setBounds(32, 176, 141, 16);
		}
		return AngleLabel;
	}

	private JLabel getTopLabel() {
		if(TopLabel == null) {
			TopLabel = new JLabel();
			TopLabel.setText(PluginServices.getText(this,"Layer_elevation_(Top)"));
			TopLabel.setBounds(32, 116, 182, 16);
		}
		return TopLabel;
	}

	private JLabel getBottomLabel() {
		if(BottomLabel == null) {
			BottomLabel = new JLabel();
			BottomLabel.setText(PluginServices.getText(this,"Layer_elevation_(Bottom)"));
			BottomLabel.setBounds(32, 144, 182, 16);
		}
		return BottomLabel;
	}

	private JFormattedTextField getRisolutionX() {
		if(RisolutionX == null) {
			RisolutionX = new JFormattedTextField(double.class);
			RisolutionX.setValue(0.0);
			RisolutionX.setBounds(245, 50, 137, 21);
		}
		return RisolutionX;
	}

	private JFormattedTextField getRisolutionY() {
		if(RisolutionY == null) {
			RisolutionY = new JFormattedTextField(Double.class);
			RisolutionY.setValue(0.0);
			RisolutionY.setBounds(245, 78, 137, 21);
		}
		return RisolutionY;
	}

	private JFormattedTextField getAngle() {
		if(Angle == null) {
			Angle = new JFormattedTextField(Double.class);
			Angle.setValue(0.0);
			Angle.setBounds(245, 174, 137, 21);
		}
		return Angle;
	}

	private JFormattedTextField getTop() {
		if(Top == null) {
			Top = new JFormattedTextField(Double.class);
			Top.setValue(0.0);
			Top.setBounds(245, 114, 137, 21);
		}
		return Top;
	}

	private JFormattedTextField getBottom() {
		if(Bottom == null) {
			Bottom = new JFormattedTextField(Double.class);
			Bottom.setValue(0.0);
			Bottom.setBounds(245, 141, 137, 21);

		}
		return Bottom;
	}

	private JLabel getDataBaseLabel() {
		if(DataBaseLabel == null) {
			DataBaseLabel = new JLabel();
			DataBaseLabel.setText(PluginServices.getText(this,"Data_Base"));
			DataBaseLabel.setBounds(32, 206, 182, 16);
		}
		return DataBaseLabel;
	}
	
	private JLabel getModelLabel() {
		if(ModelName == null) {
			ModelName = new JLabel();
			ModelName.setText(PluginServices.getText(this,"Model_project"));
			ModelName.setBounds(32, 242, 182, 16);
		}
		return ModelName;
	}
	
	private JLabel getLayerDomain() {
		if(LayerDomain == null) {
			LayerDomain = new JLabel();
			LayerDomain.setText(PluginServices.getText(this,"Model_domain"));
			LayerDomain.setBounds(32, 26, 182, 16);
		}
		return LayerDomain;
	}
}

