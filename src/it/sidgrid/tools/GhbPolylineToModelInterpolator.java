package it.sidgrid.tools;

import it.sidgrid.sextante.tools.GhbPolylineToModelGridAlgorithm;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;

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
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.unex.sextante.core.AnalysisExtent;
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

import javax.swing.JLabel;

import org.cresques.cts.IProjection;

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
 * linear interpolator for GHB package. 
 * Extract points from line with interpolated values
 * Request a dbf table with "from" and "to" to interpolate values
 * request GhbPolylineToModelGridAlgorithm SEXTANTE algorithm
 */
public class GhbPolylineToModelInterpolator extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private JComboBox jComboVectParams;
	private JComboBox jComboVarX;
	private JComboBox jComboBoxTable;
	private JFormattedTextField cellDimension;
	private JFormattedTextField cellLayer;
	private JFormattedTextField xyzValue;
	private JFormattedTextField bleValue;
	private JButton jButton1;
	private AbstractAction abstractAction1;
	FLayer shape;

	public GhbPolylineToModelInterpolator(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();


	}

	private void initGUI() {


		//this.setName(PluginServices.getText(this,"Ghb_Line_to_Model_Interpolator"));

		this.setVisible(true);
		this.setLayout(null);
		this.setSize(new Dimension(400, 236));
		this.add(getJComboVectParams());
		this.add(getJComboVarX());
		this.add(getJComboBoxTable());
		this.add(getJButton1());
		add(getBtnCloseTool());
		cellDimension = new JFormattedTextField();
		cellDimension.setToolTipText("Cell dimension");
		cellDimension.setText("1");
		cellDimension.setBounds(24, 135, 65, 28);
		add(cellDimension);

		cellLayer = new JFormattedTextField();
		cellLayer.setToolTipText("Ghb layer");
		cellLayer.setText("1");
		cellLayer.setBounds(24, 173, 65, 28);
		add(cellLayer);

		xyzValue = new JFormattedTextField();
		xyzValue.setToolTipText("");
		xyzValue.setText("1");
		xyzValue.setBounds(192, 85, 65, 28);
		add(xyzValue);

		bleValue = new JFormattedTextField();
		bleValue.setToolTipText("");
		bleValue.setText("1");
		bleValue.setBounds(192, 133, 65, 28);
		add(bleValue);

		JLabel lblHcellDim = new JLabel(PluginServices.getText(this,"Cell_dim"));
		lblHcellDim.setBounds(101, 141, 89, 16);
		add(lblHcellDim);

		JLabel lblLayerNum = new JLabel(PluginServices.getText(this,"Layer_num"));
		lblLayerNum.setBounds(101, 179, 89, 16);
		add(lblLayerNum);

		JLabel lblxyzPar = new JLabel(PluginServices.getText(this,"[xyz]_Par"));
		lblxyzPar.setBounds(272, 91, 61, 16);
		add(lblxyzPar);

		JLabel blePar = new JLabel(PluginServices.getText(this,"Boundary_dist"));
		blePar.setBounds(269, 141, 104, 16);
		add(blePar);

		JLabel lblNewLabel = new JLabel(PluginServices.getText(this,"Ghb_(shp)"));
		lblNewLabel.setBounds(28, 19, 135, 16);
		add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel(PluginServices.getText(this,"Lenght"));
		lblNewLabel_1.setBounds(28, 70, 135, 16);
		add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel(PluginServices.getText(this,"Parameter_(dbf)"));
		lblNewLabel_2.setBounds(204, 19, 147, 16);
		add(lblNewLabel_2);
	}

	public String[] getFieldLayerNumericFieldsParams() {
		String[] fields;
		String selectedLayerGrid = (String)jComboVectParams.getSelectedItem();
		FLayers layers = mapCtrl.getMapContext().getLayers();
		FLyrVect layer = (FLyrVect)layers.getLayer(selectedLayerGrid);
		ArrayList<String> list = new ArrayList<String>();
		if(layer!=null)
			try {
				SelectableDataSource recordset = layer.getRecordset();
				int numFields = recordset.getFieldCount();

				for (int i = 0; i < numFields; i++) {												
					list.add(recordset.getFieldName(i));

				}
			} catch (ReadDriverException e) {
				return null;
			}
			fields = new String[list.size()];
			list.toArray(fields);
			return fields;
	}

	private JComboBox getJComboVectParams() {
		if(jComboVectParams == null) {
			ComboBoxModel jComboVectParamsModel = 
				new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboVectParams = new JComboBox();
			jComboVectParams.setModel(jComboVectParamsModel);
			jComboVectParams.setBounds(18, 40, 145, 22);
			jComboVectParams.addItemListener(new java.awt.event.ItemListener() {
				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					setFields();
				}
			});
		}
		return jComboVectParams;
	}

	protected void setFields() {

		final ComboBoxModel jComboBoxFieldModelX = new DefaultComboBoxModel(getFieldLayerNumericFieldsParams());
		jComboVarX.setModel(jComboBoxFieldModelX);

	}

	private JComboBox getJComboVarX() {
		if(jComboVarX == null) {
			ComboBoxModel jComboVarXModel = 
				new DefaultComboBoxModel(getFieldLayerNumericFieldsParams());
			jComboVarX = new JComboBox();
			jComboVarX.setModel(jComboVarXModel);
			jComboVarX.setBounds(18, 89, 145, 22);
		}
		return jComboVarX;
	}

	private JComboBox getJComboBoxTable() {
		if(jComboBoxTable == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getProjectTableNames());
			jComboBoxTable = new JComboBox();
			jComboBoxTable.setBounds(192, 38, 159, 27);
			jComboBoxTable.setModel(jComboBoxTableModel);
		}
		return jComboBoxTable;
	}

	private JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText(PluginServices.getText(this, "Run"));
			jButton1.setBounds(291, 173, 80, 22);
			jButton1.setAction(getAbstractAction1());
		}
		return jButton1;
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	public static AnalysisExtent m_AnalysisExtent = new AnalysisExtent();
	private JButton btnCloseTool;

	private AbstractAction getAbstractAction1() {
		if(abstractAction1 == null) {
			abstractAction1 = new AbstractAction(PluginServices.getText(this, "Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {

					String line = (String)jComboVectParams.getSelectedItem();
					FLayers layers = mapCtrl.getMapContext().getLayers();										
					FLyrVect lineGeomtry = (FLyrVect)layers.getLayer(line);
					final gvVectorLayer layerLine = new gvVectorLayer();
					layerLine.create(lineGeomtry);


					String stressp = (String) jComboBoxTable.getSelectedItem();
					ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					final ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
					final gvTable tableStress = new gvTable();
					tableStress.create(stresstable);


					PluginServices.getMDIManager().closeWindow(GhbPolylineToModelInterpolator.this);
//					final WaitingPanel test = new WaitingPanel();

					new Thread() {
						@Override
						public void run() {

							GhbPolylineToModelGridAlgorithm alg =  new GhbPolylineToModelGridAlgorithm();
							ITaskMonitor test_waitingpanel = new DefaultTaskMonitor("GhbPolylineToModelGridAlgorithm", true, null);
							try {

								VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");

								int cell = Integer.parseInt(cellDimension.getText());
								int layer = Integer.parseInt(cellLayer.getText());
								int xyz = Integer.parseInt(xyzValue.getText());
								int ble = Integer.parseInt(bleValue.getText());

								ParametersSet params = alg.getParameters();
								params.getParameter(GhbPolylineToModelGridAlgorithm.LINES).setParameterValue(layerLine);
								params.getParameter(GhbPolylineToModelGridAlgorithm.LENGHT).setParameterValue(jComboVarX.getSelectedIndex());
								params.getParameter(GhbPolylineToModelGridAlgorithm.TABLE).setParameterValue(tableStress);
								params.getParameter(GhbPolylineToModelGridAlgorithm.DISTANCE).setParameterValue(cell);
								params.getParameter(GhbPolylineToModelGridAlgorithm.FROM).setParameterValue(layer);
								params.getParameter(GhbPolylineToModelGridAlgorithm.TO).setParameterValue(layer);
								params.getParameter(GhbPolylineToModelGridAlgorithm.XYZ).setParameterValue(xyz);
								params.getParameter(GhbPolylineToModelGridAlgorithm.BLE).setParameterValue(ble);
								OutputObjectsSet outputs = alg.getOutputObjects();
								Output out = outputs.getOutput(GhbPolylineToModelGridAlgorithm.RESULT);	
								out.setOutputChannel(new FileOutputChannel(".shp"));

//								alg.setAnalysisExtent(m_AnalysisExtent);	
								alg.execute(test_waitingpanel, m_OutputFactory);
								test_waitingpanel.close();
								String layerName = "ghb_point_tmp";
								IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
								shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
								mapCtrl.getMapContext().getLayers().addLayer(shape);

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
		return abstractAction1;
	}

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(GhbPolylineToModelInterpolator.this);
						}
					});
			btnCloseTool.setBounds(202, 173, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Ghb_line"));		
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
