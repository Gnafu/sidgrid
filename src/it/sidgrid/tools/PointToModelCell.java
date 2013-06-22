package it.sidgrid.tools;

import it.sidgrid.sextante.tools.PointToModelCellAlgorithm;

import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import com.hardcode.driverManager.DriverLoadException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
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
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;

import javax.swing.JLabel;

import org.cresques.cts.IProjection;

import java.awt.Dimension;

/*Copyright (C) 2013  SID&GRID Project

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
 * @author sid&grid
 * GUI
 * convert point layer to a model cells layer with row and column value
 * requests for River, Drain, Ghb package
 * 
 */

public class PointToModelCell extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private JComboBox jComboPoint;
	private JComboBox jComboGrid;
	private JButton jButton1;
	private AbstractAction abstractAction1;
	FLayer shape;
	
	public PointToModelCell(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
		
	}
	
	private void initGUI() {

		
		//this.setName(PluginServices.getText(this,"Point_to_Model_cell"));
		
		this.setVisible(true);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(352, 208));
		this.add(getJComboVectPoint());
		this.add(getJComboGrid());
		this.add(getRunButton());
		add(getBtnCloseTool());
		
		JLabel lblNewLabel = new JLabel(PluginServices.getText(this,"Point_layer"));
		lblNewLabel.setBounds(181, 42, 131, 16);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel(PluginServices.getText(this,"Model_layer"));
		lblNewLabel_1.setBounds(181, 90, 131, 16);
		add(lblNewLabel_1);

}
	
	
	private JComboBox getJComboVectPoint() {
		if(jComboPoint == null) {
			ComboBoxModel jComboVectParamsModel = 
				new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboPoint = new JComboBox();
			jComboPoint.setModel(jComboVectParamsModel);
			jComboPoint.setBounds(21, 40, 148, 22);
		}
		return jComboPoint;
	}
	
	private JComboBox getJComboGrid(){
		if(jComboGrid == null){
			ComboBoxModel jComboGridParamsModel = 
					new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboGrid = new JComboBox();
			jComboGrid.setModel(jComboGridParamsModel);
			jComboGrid.setBounds(21, 86, 148, 27);
		}
		return jComboGrid;
		
	}
	
//	private JComboBox getJComboGrid() {
//		if(jComboGrid == null) {
//			ComboBoxModel jComboGridParamsModel = 
//				new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
//			jComboGrid = new JComboBox();
//			jComboGrid.setModel(jComboGridParamsModel);
//			jComboGrid.setBounds(21, 86, 148, 27);
//		}
//		return jComboPoint;
//	}
	
	
	private JButton getRunButton() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setBounds(252, 148, 80, 22);
			jButton1.setAction(getRunAction());
		}
		return jButton1;
	}
	
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	public static AnalysisExtent m_AnalysisExtent = new AnalysisExtent();
	private JButton btnCloseTool;
	
	private AbstractAction getRunAction() {
		if(abstractAction1 == null) {
			abstractAction1 = new AbstractAction(PluginServices.getText(this, "Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {

					String point = (String)jComboPoint.getSelectedItem();
					FLayers layers = mapCtrl.getMapContext().getLayers();										
					FLyrVect pointGeomtry = (FLyrVect)layers.getLayer(point);
					final gvVectorLayer layerPoint = new gvVectorLayer();
					layerPoint.create(pointGeomtry);

					String grid = (String)jComboGrid.getSelectedItem();
					FLayers modelLayer = mapCtrl.getMapContext().getLayers();										
					FLyrVect gridGeomtry = (FLyrVect)modelLayer.getLayer(grid);
					final gvVectorLayer layergrid = new gvVectorLayer();
					layergrid.create(gridGeomtry);
					
					
					PluginServices.getMDIManager().closeWindow(PointToModelCell.this);
//					final WaitingPanel test = new WaitingPanel();

										
					new Thread() {
						@Override
						public void run() {
							
							PointToModelCellAlgorithm alg =  new PointToModelCellAlgorithm();
							ITaskMonitor test_waitingpanel = new DefaultTaskMonitor("PointToModelCellAlgorithm", true, null);
							try {
																
								VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
		
								ParametersSet params = alg.getParameters();
								params.getParameter(PointToModelCellAlgorithm.POINT).setParameterValue(layerPoint);
								params.getParameter(PointToModelCellAlgorithm.GRID).setParameterValue(layergrid);
								
								OutputObjectsSet outputs = alg.getOutputObjects();
								Output out = outputs.getOutput(PointToModelCellAlgorithm.RESULT);	
								out.setOutputChannel(new FileOutputChannel(".shp"));
								
//								alg.setAnalysisExtent(m_AnalysisExtent);	
								alg.execute(test_waitingpanel, m_OutputFactory);
								test_waitingpanel.close();
								String layerName = "point_cell_tmp";
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
						PluginServices.getMDIManager().closeWindow(PointToModelCell.this);
					}
				});
		btnCloseTool.setBounds(152, 148, 80, 22);
		btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
	}
	return btnCloseTool;
}

	
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Point_to_Model_cell"));
			viewInfo.setHeight(208);
			viewInfo.setWidth(352);
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
