package it.sidgrid.tools;

import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.AbstractAction;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.layers.ReloadLayerException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.IVectorialJDBCDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.IteratorException;
import es.unex.sextante.exceptions.WrongOutputIDException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvRasterLayer;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.vectorTools.gridStatisticsInPolygons.GridStatisticsInPolygonsAlgorithm;
import java.awt.event.ActionEvent;
import java.io.File;

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
 * SID&GRID tool to intersect raster value to vector model layer
 * & grid stat INTERPOLATOR from SEXTANTE algorithm
 */

public class RasterToModLayerInterpolator extends javax.swing.JPanel implements IWindow{

	private MapControl mapCtrl;
	private JComboBox rasterLayer;
	private JComboBox layerGriglia;
	private JButton jButtonRun;
	private JLabel jLabel2;
	private JLabel jLabelModelLayer;
	private JComboBox jComboBoxModelLayer;
	private JLabel jLabel1;
	private AbstractAction abstractActionRun;
	private WindowInfo viewInfo = null;
	private JButton btnCloseTool;
	
	public RasterToModLayerInterpolator(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
		
		
	}
	
	private void initGUI() {
		
		this.setVisible(true);
		this.setLayout(null);
		this.setPreferredSize(new java.awt.Dimension(382, 250));
		this.add(getLayerGriglia());
		this.add(getLayerRaster());
		this.add(getJButtonRun());
		this.add(getJLabel1());
		this.add(getJLabel2());
		this.add(getJComboBoxModelLayer());
		this.add(getJLabelModelLayer());
		add(getBtnCloseTool());

}
	
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"From_Raster_-_Interpolator"));
			viewInfo.setHeight(250);
			viewInfo.setWidth(382);
			
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	private JComboBox getLayerGriglia() {
		if(layerGriglia == null) {
			ComboBoxModel layerGrigliaModel = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			layerGriglia = new JComboBox();
			layerGriglia.setModel(layerGrigliaModel);
			layerGriglia.setBounds(49, 103, 259, 22);
			layerGriglia.addItemListener(new java.awt.event.ItemListener() {
                @Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
                   setFields();
                }
             });
           }
		return layerGriglia;
	}
	
	protected void setFields() {

	      final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(Utils.getFieldLayerNumericFields(mapCtrl, (String)layerGriglia.getSelectedItem()));
	      jComboBoxModelLayer.setModel(jComboBoxFieldModel);
	      
	      
	   }
	
	private JComboBox getLayerRaster() {
		if(rasterLayer == null) {
			ComboBoxModel LayerVarModel = new DefaultComboBoxModel(Utils.getRasterLayers(mapCtrl));
			rasterLayer = new JComboBox();
			rasterLayer.setModel(LayerVarModel);
			rasterLayer.setBounds(49, 41, 259, 22);
			rasterLayer.addItemListener(new java.awt.event.ItemListener() {
                @Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
                                   
                }
             });
		}
		return rasterLayer;
	}
	
	private JButton getJButtonRun() {
		if(jButtonRun == null) {
			jButtonRun = new JButton();
			jButtonRun.setBounds(240, 203, 66, 22);
			jButtonRun.setAction(getAbstractActionRun());
		}
		return jButtonRun;
	}
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private AbstractAction getAbstractActionRun() {
		if(abstractActionRun == null) {
			abstractActionRun = new AbstractAction(PluginServices.getText(this, "Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					
					PluginServices.getMDIManager().closeWindow(RasterToModLayerInterpolator.this);
					final WaitingPanel test = new WaitingPanel();
										
					new Thread() {
						@Override
						public void run() {
					
					String selectedLayerGriglia = (String)layerGriglia.getSelectedItem();
					FLayers layers = mapCtrl.getMapContext().getLayers();										
					FLyrVect griglia = (FLyrVect)layers.getLayer(selectedLayerGriglia);
					gvVectorLayer layerGriglia = new gvVectorLayer();
					layerGriglia.create(griglia);
					
					String selectedRasterLayer = (String)rasterLayer.getSelectedItem();
					FLayers layersras = mapCtrl.getMapContext().getLayers();										
					FLyrRasterSE Ras = (FLyrRasterSE)layersras.getLayer(selectedRasterLayer);
					gvRasterLayer layerRast = new gvRasterLayer();
					layerRast.create(Ras);
					
					
					GridStatisticsInPolygonsAlgorithm alg = new GridStatisticsInPolygonsAlgorithm();
					final ArrayList<gvRasterLayer> rasterLayers = new ArrayList<gvRasterLayer>();
					rasterLayers.add(layerRast);
					
					Connection conn = null;
					IVectorialJDBCDriver driver = null;
					try {
						
						SelectableDataSource data = griglia.getRecordset();			
						driver = (IVectorialJDBCDriver) data.getDriver();	
						String dbase = driver.getConnection().getCatalogName();
						conn = Utils.getConnectionToDatabase(dbase);
					    ResultSet rs = null;				    
					    String sql = "select * from "+ selectedLayerGriglia+" order by gid asc";				
					    Statement stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);					    
					    rs = stmt.executeQuery(sql);										


					    VectorialFileDriver driverVector = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
					    ParametersSet params = alg.getParameters();
					    params.getParameter(GridStatisticsInPolygonsAlgorithm.LAYER).setParameterValue(layerGriglia);
					    params.getParameter(GridStatisticsInPolygonsAlgorithm.GRIDS).setParameterValue(rasterLayers);
			    
					    OutputObjectsSet outputs = alg.getOutputObjects();
						Output out = outputs.getOutput(GridStatisticsInPolygonsAlgorithm.RESULT);	
						out.setOutputChannel(new FileOutputChannel(".shp"));
						alg.execute(null, m_OutputFactory);						
						
						IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
						FLayer shape = LayerFactory.createLayer("Grid_stat", driverVector, new File (".shp"), viewProj);
						
						mapCtrl.getMapContext().getLayers().addLayer(shape);
						FLayers gridStat = mapCtrl.getMapContext().getLayers();
						FLyrVect grigliaStat = (FLyrVect)gridStat.getLayer("Grid_stat");
						gvVectorLayer layerStat = new gvVectorLayer();
						layerStat.create(grigliaStat);
												
						final IFeatureIterator iter = layerStat.iterator();
//						IFeatureIterator iter2 = layerGriglia.iterator();
//
						while (iter.hasNext()) {
							final IFeature feature = iter.next();							
							rs.next();
							rs.updateDouble(jComboBoxModelLayer.getSelectedItem().toString(), Double.parseDouble(feature.getRecord().getValue(17).toString()));
							rs.updateRow();
						}     

					    mapCtrl.getMapContext().getLayers().removeLayer(shape);
											    
						JOptionPane.showMessageDialog(null,
								PluginServices.getText(this, "Run_successfull"),
							    "Message",
							    JOptionPane.INFORMATION_MESSAGE);
					    
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IteratorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
					}finally{
						
						if(driver!=null)driver.close();					    
					    try {
					    	if(conn!=null)conn.close();
						} catch (SQLException e) { /* IGNORED */ }
					    try {
							griglia.reload();
						} catch (ReloadLayerException e) {
							e.printStackTrace();
						}
					    test.dispose();

						
					}
					
						}
					}.start();

				}
			};
		}
		return abstractActionRun;
	}
	
	private JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(PluginServices.getText(this,"Raster_Data_Grid"));
			jLabel1.setBounds(49, 21, 259, 15);
		}
		return jLabel1;
	}
	
	private JLabel getJLabel2() {
		if(jLabel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText(PluginServices.getText(this,"Model_layer"));
			jLabel2.setBounds(49, 83, 259, 15);
		}
		return jLabel2;
	}
	
	private JComboBox getJComboBoxModelLayer() {
		if(jComboBoxModelLayer == null) {
			ComboBoxModel jComboBoxModelLayerModel = 
					new DefaultComboBoxModel(Utils.getFieldLayerNumericFields(mapCtrl, (String)layerGriglia.getSelectedItem()));
			jComboBoxModelLayer = new JComboBox();
			jComboBoxModelLayer.setModel(jComboBoxModelLayerModel);
			jComboBoxModelLayer.setBounds(49, 159, 160, 22);
		}
		return jComboBoxModelLayer;
	}
	
	private JLabel getJLabelModelLayer() {
		if(jLabelModelLayer == null) {
			jLabelModelLayer = new JLabel();
			jLabelModelLayer.setText(PluginServices.getText(this,"Field"));
			jLabelModelLayer.setBounds(49, 139, 90, 15);
		}
		return jLabelModelLayer;
	}
	
//	public String[] getFieldLayerNumericFields() {
//		String[] fields = new String[]{};
//		String selectedLayerVar = (String)layerGriglia.getSelectedItem();
//		FLayers layers = mapCtrl.getMapContext().getLayers();
//		FLyrVect layer = (FLyrVect)layers.getLayer(selectedLayerVar);
//		ArrayList<String> list = new ArrayList<String>();
//		try {
//			SelectableDataSource recordset = layer.getRecordset();
//			int numFields = recordset.getFieldCount();
//			
//			for (int i = 0; i < numFields; i++) {												
//					list.add(recordset.getFieldName(i));
//				
//			}
//		} catch (ReadDriverException e) {
//			return null;
//		}
//		fields = new String[list.size()];
//		list.toArray(fields);
//		return fields;
//	}

private JButton getBtnCloseTool() {
	if (btnCloseTool == null) {
		btnCloseTool = new JButton(
				new AbstractAction(PluginServices.getText(this, "Close")) {
					@Override
					public void actionPerformed(ActionEvent evt) {
						PluginServices.getMDIManager().closeWindow(RasterToModLayerInterpolator.this);
					}
				});
		btnCloseTool.setBounds(162, 203, 66, 22);
		btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
	}
	return btnCloseTool;
}

	
}
