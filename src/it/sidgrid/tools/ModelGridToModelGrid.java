package it.sidgrid.tools;

import it.sidgrid.task.ProgressTask;
import it.sidgrid.utils.Utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.AbstractAction;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.layers.ReloadLayerException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.IVectorialJDBCDriver;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.exceptions.IteratorException;
import es.unex.sextante.gvsig.core.gvVectorLayer;

import java.awt.event.ActionEvent;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
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
 * 
 * GUI
 * intersect max three vector layer field to max three model layer fields
 * 
 */

public class ModelGridToModelGrid extends javax.swing.JPanel implements IWindow {
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox jComboModelGrid;
	private JSeparator jSeparator3;
	private JLabel jLabel3;
	private JLabel jLabel1;
	private JLabel jLabelTo1;
	private JSeparator jSeparator2;
	private AbstractAction abstractActionCalculate;
	private JButton jButtonRun;
	private JComboBox jComboVarX;
	private JComboBox jComboVarY;
	private JLabel jLabelModel;
	private JLabel jLabelParams;
	private JComboBox jComboModelZ;
	private JComboBox jComboModelY;
	private JComboBox jComboModelX;
	private JComboBox jComboVarZ;
	private JSeparator jSeparator1;
	private JComboBox jComboVectParams;
	private JButton btnCloseTool;

	public ModelGridToModelGrid(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();


	}

	private void initGUI() {

		this.setVisible(true);
		this.setLayout(null);
		this.setPreferredSize(new java.awt.Dimension(424, 266));
		this.add(getJComboVectParams());
		this.add(getJComboModelGrid());
		this.add(getJSeparator1());
		this.add(getJComboVarX());
		this.add(getJComboVarY());
		this.add(getJComboVarZ());
		this.add(getJComboModelX());
		this.add(getJComboModelY());
		this.add(getJComboModelZ());
		this.add(getJLabelParams());
		this.add(getJLabelModel());
		this.add(getJButtonRun());
		this.add(getJSeparator2());
		this.add(getJSeparator3());
		this.add(getJLabelTo1());
		this.add(getJLabel1());
		this.add(getJLabel3());
		add(getBtnCloseTool());

	}

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Copy_from_Model_layer"));
			viewInfo.setHeight(270);
			viewInfo.setWidth(450);

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public String[] getFieldLayerNumericFieldsGrid() {
		String[] fields;
		String selectedLayerGrid = (String)jComboModelGrid.getSelectedItem();
		FLayers layers = mapCtrl.getMapContext().getLayers();
		FLyrVect layer = (FLyrVect)layers.getLayer(selectedLayerGrid);
		ArrayList<String> list = new ArrayList<String>();
		try {
			SelectableDataSource recordset = layer.getRecordset();
			int numFields = recordset.getFieldCount();
			list.add("unselected");
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
				list.add("unselected");
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

	protected void setFields() {

		final ComboBoxModel jComboBoxFieldModelX = new DefaultComboBoxModel(getFieldLayerNumericFieldsGrid());
		jComboModelX.setModel(jComboBoxFieldModelX);

		final ComboBoxModel jComboBoxFieldModelY = new DefaultComboBoxModel(getFieldLayerNumericFieldsGrid());
		jComboModelY.setModel(jComboBoxFieldModelY);

		final ComboBoxModel jComboBoxFieldModelZ = new DefaultComboBoxModel(getFieldLayerNumericFieldsGrid());
		jComboModelZ.setModel(jComboBoxFieldModelZ);

		final ComboBoxModel jComboBoxFieldVarX = new DefaultComboBoxModel(getFieldLayerNumericFieldsParams());
		jComboVarX.setModel(jComboBoxFieldVarX);

		final ComboBoxModel jComboBoxFieldVarY = new DefaultComboBoxModel(getFieldLayerNumericFieldsParams());
		jComboVarY.setModel(jComboBoxFieldVarY);

		final ComboBoxModel jComboBoxFieldVarZ = new DefaultComboBoxModel(getFieldLayerNumericFieldsParams());
		jComboVarZ.setModel(jComboBoxFieldVarZ);

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

	private JComboBox getJComboModelGrid() {
		if(jComboModelGrid == null) {
			ComboBoxModel jComboModelGridModel = 
				new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboModelGrid = new JComboBox();
			jComboModelGrid.setModel(jComboModelGridModel);
			jComboModelGrid.setBounds(266, 40, 141, 22);
			jComboModelGrid.addItemListener(new java.awt.event.ItemListener() {
				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					setFields();
				}
			});
		}
		return jComboModelGrid;
	}

	private JSeparator getJSeparator1() {
		if(jSeparator1 == null) {
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(18, 121, 389, 14);
		}
		return jSeparator1;
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

	private JComboBox getJComboVarY() {
		if(jComboVarY == null) {
			ComboBoxModel jComboVarYModel = 
				new DefaultComboBoxModel(getFieldLayerNumericFieldsParams());
			jComboVarY = new JComboBox();
			jComboVarY.setModel(jComboVarYModel);
			jComboVarY.setBounds(18, 143, 145, 22);
		}
		return jComboVarY;
	}

	private JComboBox getJComboVarZ() {
		if(jComboVarZ == null) {
			ComboBoxModel jComboVarZModel = 
				new DefaultComboBoxModel(getFieldLayerNumericFieldsParams());
			jComboVarZ = new JComboBox();
			jComboVarZ.setModel(jComboVarZModel);
			jComboVarZ.setBounds(18, 195, 145, 22);
		}
		return jComboVarZ;
	}

	private JComboBox getJComboModelX() {
		if(jComboModelX == null) {
			ComboBoxModel jComboModelXModel = 
				new DefaultComboBoxModel(getFieldLayerNumericFieldsGrid());
			jComboModelX = new JComboBox();
			jComboModelX.setModel(jComboModelXModel);
			jComboModelX.setBounds(266, 89, 141, 22);
		}
		return jComboModelX;
	}

	private JComboBox getJComboModelY() {
		if(jComboModelY == null) {
			ComboBoxModel jComboModelYModel = 
				new DefaultComboBoxModel(getFieldLayerNumericFieldsGrid());
			jComboModelY = new JComboBox();
			jComboModelY.setModel(jComboModelYModel);
			jComboModelY.setBounds(266, 143, 141, 22);
		}
		return jComboModelY;
	}

	private JComboBox getJComboModelZ() {
		if(jComboModelZ == null) {
			ComboBoxModel jComboModelZModel = 
				new DefaultComboBoxModel(getFieldLayerNumericFieldsGrid());
			jComboModelZ = new JComboBox();
			jComboModelZ.setModel(jComboModelZModel);
			jComboModelZ.setBounds(266, 195, 141, 22);
		}
		return jComboModelZ;
	}

	private JLabel getJLabelParams() {
		if(jLabelParams == null) {
			jLabelParams = new JLabel();
			jLabelParams.setText(PluginServices.getText(this,"Model_layer"));
			jLabelParams.setBounds(18, 20, 145, 15);
		}
		return jLabelParams;
	}

	private JLabel getJLabelModel() {
		if(jLabelModel == null) {
			jLabelModel = new JLabel();
			jLabelModel.setText(PluginServices.getText(this,"Model_layer"));
			jLabelModel.setBounds(266, 20, 141, 15);
		}
		return jLabelModel;
	}

	private JButton getJButtonRun() {
		if(jButtonRun == null) {
			jButtonRun = new JButton();
			jButtonRun.setBounds(341, 229, 66, 22);
			jButtonRun.setAction(getAbstractActionCalculate());
		}
		return jButtonRun;
	}

	private AbstractAction getAbstractActionCalculate() {
		if(abstractActionCalculate == null) {
			abstractActionCalculate = new AbstractAction(PluginServices.getText(this, "Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PluginServices.getMDIManager().closeWindow(ModelGridToModelGrid.this);
					final ProgressTask test = new ProgressTask();

					new Thread() {
						@Override
						public void run() {


							String selectedLayerGriglia = (String)jComboModelGrid.getSelectedItem();
							FLayers layers = mapCtrl.getMapContext().getLayers();										
							FLyrVect griglia = (FLyrVect)layers.getLayer(selectedLayerGriglia);
							gvVectorLayer layerGriglia = new gvVectorLayer();
							layerGriglia.create(griglia);


							String selectedLayerVar = (String)jComboVectParams.getSelectedItem();
							FLayers layersvar = mapCtrl.getMapContext().getLayers();										
							FLyrVect Var = (FLyrVect)layersvar.getLayer(selectedLayerVar);
							gvVectorLayer layerVar = new gvVectorLayer();
							layerVar.create(Var);

							Connection conn = null;
							IVectorialJDBCDriver driver = null;
							
							int i = 1;
							test.setMax(layerGriglia.getShapesCount());
							
							try {
								/*layer griglia*/


								/*layer parametri*/
								SelectableDataSource data2 = Var.getRecordset();

								int field2x, field2y, field2z;

								if (jComboVarX.getSelectedItem().toString().contains("unselected") == false)
								{
									field2x = data2.getFieldIndexByName(jComboVarX.getSelectedItem().toString());
								}
								else
									field2x = -1;

								if (jComboVarY.getSelectedItem().toString().contains("unselected") == false)
								{
									field2y = data2.getFieldIndexByName(jComboVarY.getSelectedItem().toString());
								}
								else
									field2y = -1;

								if (jComboVarZ.getSelectedItem().toString().contains("unselected") == false)
								{
									field2z = data2.getFieldIndexByName(jComboVarZ.getSelectedItem().toString());
								}
								else
									field2z = -1;


								//int k = 0;

//								ResultSet rs = null;

//								String sql = "select * from "+selectedLayerGriglia+" order by gid asc";


//								Statement stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);

								SelectableDataSource data = griglia.getRecordset();			
								driver = (IVectorialJDBCDriver) data.getDriver();	
								String dbase = driver.getConnection().getCatalogName();
								conn = Utils.getConnectionToDatabase(dbase);
								ResultSet rs = null;				    
								String sql = "select * from "+ selectedLayerGriglia+" order by gid asc";				
								Statement stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);					    
								rs = stmt.executeQuery(sql);

//								rs = stmt.executeQuery(sql);

								String fieldGridX = jComboVarX.getSelectedItem().toString();
								String fieldGridY = jComboVarY.getSelectedItem().toString();
								String fieldGridZ = jComboVarZ.getSelectedItem().toString();

								final IFeatureIterator iter = layerVar.iterator();

								while (iter.hasNext()) {
									final IFeature feature = iter.next();							
									rs.next();
									test.setValue(i++);
									if (field2x != -1 && fieldGridX.contentEquals("unselected") != true)
									{
										String d= feature.getRecord().getValue(field2x).toString();										
										System.out.println(d);										
										if (d.contains(".")) {
										// d is an integer
											System.out.println("******DECIMALE");
											double valueX = (Double) feature.getRecord().getValue(field2x);
											rs.updateDouble(jComboModelX.getSelectedItem().toString(), new Double(valueX));
							
										}
										else
										{
										// d's not an integer -- do something different
											System.out.println("******INTERO");
											int valueX = (Integer) feature.getRecord().getValue(field2x);
											rs.updateInt(jComboModelX.getSelectedItem().toString(), new Integer(valueX));
										}

									}
									if (field2y != -1 && fieldGridY.contentEquals("unselected") != true)
									{
										String d= feature.getRecord().getValue(field2y).toString();										
																				
										if (d.contains(".")) {
										// d is an integer
											
											double valueY = (Double) feature.getRecord().getValue(field2y);
											rs.updateDouble(jComboModelY.getSelectedItem().toString(), new Double(valueY));
							
										}
										else
										{
										// d's not an integer -- do something different
											
											int valueY = (Integer) feature.getRecord().getValue(field2y);
											rs.updateInt(jComboModelY.getSelectedItem().toString(), new Integer(valueY));
										}

									}
									if (field2z != -1 && fieldGridZ.contentEquals("unselected") != true)
									{
										String d= feature.getRecord().getValue(field2z).toString();										
										
										if (d.contains(".")) {
										// d is an integer
											
											double valueZ = (Double) feature.getRecord().getValue(field2z);
											rs.updateDouble(jComboModelZ.getSelectedItem().toString(), new Double(valueZ));
							
										}
										else
										{
										// d's not an integer -- do something different
											
											int valueZ = (Integer) feature.getRecord().getValue(field2z);
											rs.updateInt(jComboModelZ.getSelectedItem().toString(), new Integer(valueZ));
										}


									}
									rs.updateRow();
								}     


							} catch (ReadDriverException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IteratorException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (DBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finally{
								if(driver!=null)driver.close();					    
								try {
									if(conn!=null)conn.close();
									griglia.reload();
								}
								catch (SQLException e) {/* IGNORED */}
								catch (ReloadLayerException e) {e.printStackTrace();}	

								test.dispose();

							}
							JOptionPane.showMessageDialog(null,
									PluginServices.getText(this, "Run_successfull"),
									"Message",
									JOptionPane.INFORMATION_MESSAGE);//NON EDT
						}

					}.start();

				}
			};
		}
		return abstractActionCalculate;
	}

	private JSeparator getJSeparator2() {
		if(jSeparator2 == null) {
			jSeparator2 = new JSeparator();
			jSeparator2.setBounds(18, 175, 389, 14);
		}
		return jSeparator2;
	}

	private JSeparator getJSeparator3() {
		if(jSeparator3 == null) {
			jSeparator3 = new JSeparator();
			jSeparator3.setBounds(18, 69, 389, 14);
		}
		return jSeparator3;
	}

	private JLabel getJLabelTo1() {
		if(jLabelTo1 == null) {
			jLabelTo1 = new JLabel();
			jLabelTo1.setText(PluginServices.getText(this,"To"));
			jLabelTo1.setBounds(202, 93, 49, 15);
		}
		return jLabelTo1;
	}

	private JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(PluginServices.getText(this,"To"));
			jLabel1.setBounds(202, 145, 49, 15);
		}
		return jLabel1;
	}

	private JLabel getJLabel3() {
		if(jLabel3 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText(PluginServices.getText(this,"To"));
			jLabel3.setBounds(202, 199, 49, 15);
		}
		return jLabel3;
	}


	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(ModelGridToModelGrid.this);
						}
					});
			btnCloseTool.setBounds(263, 229, 66, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	

}
