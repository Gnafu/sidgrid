package it.sidgrid.tools;

import it.sidgrid.sextante.tools.CreateModelLayerAlgorithm;
import it.sidgrid.utils.ColumnHeaderToolTips;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
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
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;

import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import org.cresques.cts.IProjection;
import javax.swing.SpringLayout;

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
 * create the layer property flow table and groundwater model layers
 * from Top model layer
 */

public class CreateModelLayers extends javax.swing.JPanel implements IWindow{
	private AbstractAction testAction1;
	private JButton Export;
	private JScrollPane jScrollPane1;
	private JLabel jLabelDB;
	private JComboBox dbase;
	private JButton remove;
	private JButton add;
	private DefaultTableModel model;
	private JTable table;
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private Icon plus;
	private Icon delete;
	private Icon play;
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private FLyrVect targetLayer = null;
	private JButton btnCloseTool;
	private SpringLayout springLayout;


	public CreateModelLayers(MapControl mc) {
		super();
		this.mapCtrl = mc;	    

		// Qui viene recuperato il layer su cui basarsi per creare tutti gli altri
		FLayer[] layers = mapCtrl.getMapContext().getLayers().getActives();	
		for(FLayer current: layers)
			try {
				if(current instanceof FLyrVect && ((FLyrVect) current).getRecordset().getFieldIndexByName("TOP") >= 0){
					targetLayer=(FLyrVect) current;
					// Mi fermo al primo che trovo (come il vecchio "layers[0]")
					break;
				}
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
			if(targetLayer==null){
				JOptionPane.showMessageDialog(null,
						"Non trovo nessun Model Layer",
						"CreateModelLayers",
						JOptionPane.WARNING_MESSAGE);
				PluginServices.getMDIManager().closeWindow(this);
				return;
			}

				initGUI();


	}

	private void initGUI() {
		plus = new ImageIcon(getClass().getResource("/images/add.gif"));
		delete = new ImageIcon(getClass().getResource("/images/delete.gif"));
		play = new ImageIcon(getClass().getResource("/images/application_go.png"));

		ComboBoxModel jComboBoxDBaseModel = new DefaultComboBoxModel(Utils.getDbase());
		springLayout = new SpringLayout();
		setLayout(springLayout);
		dbase = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, dbase, 23, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, dbase, -280, SpringLayout.EAST, this);
		dbase.setModel(jComboBoxDBaseModel);
		this.add(dbase);				
		this.add(getJScrollPane1());
		this.add(getExport());
		this.add(getAdd());
		this.add(getRemove());
		this.add(getJLabelDB());

		this.setName(PluginServices.getText(this,"Create_model_layers"));
		this.setSize(960, 270);
		this.setVisible(true);
		add(getBtnCloseTool());


	}

	private JButton getExport() {
		if(Export == null) {
			Export = new JButton();
			springLayout.putConstraint(SpringLayout.SOUTH, Export, -10, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.SOUTH, getJScrollPane1(), -6, SpringLayout.NORTH, Export);
			springLayout.putConstraint(SpringLayout.WEST, Export, -110, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.EAST, Export, -10, SpringLayout.EAST, this);
			Export.setAction(getTestAction1());
		}
		return Export;
	}


	private AbstractAction getTestAction1() {
		if(testAction1 == null) {
			testAction1 = new AbstractAction(PluginServices.getText(this, "Run"), play) {
				@Override
				public void actionPerformed(ActionEvent evt) {

					// Controllo che non abbia celle selezionate, altrimenti crea layers sbagliati
					int continua = JOptionPane.YES_OPTION;
					ReadableVectorial rv = targetLayer.getSource();
					try {
						rv.start();
						FBitSet selection = rv.getRecordset().getSelection();
						//System.out.println(selection.isEmpty()?"\n\n Selection VUOTA \n\n":"\n\n Selection = "+selection.cardinality()+"\n\n");

						if(!selection.isEmpty())
						{
							continua = JOptionPane.showConfirmDialog(null, 
									"!! ATTENZIONE !!\n" +
									"Sul Top Model Layer è attiva una selezione\n" +
									"I layer creati conterranno solo "+selection.cardinality()+" celle\n" +
									"Vuoi continuare?", "CreateModelLayers", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						}

						rv.stop();
					} catch (InitializeDriverException e1) {
						e1.printStackTrace();
					} catch (ReadDriverException e1) {
						e1.printStackTrace();
					}


					if(continua != JOptionPane.YES_OPTION)
					{
						//PluginServices.getMDIManager().closeWindow(CreateModelLayers.this);
						return;
					}
					
					PluginServices.getMDIManager().closeWindow(CreateModelLayers.this);
//					final WaitingPanel test = new WaitingPanel();
					final DefaultTaskMonitor progress = new DefaultTaskMonitor(
							PluginServices.getText(this, "creating_ground_layers"),
							true, null
					);

					new Thread() {
						@Override
						public void run() {

							CreateModelLayerAlgorithm prova = new CreateModelLayerAlgorithm();
							gvVectorLayer gvsiglayer = new gvVectorLayer();
							gvsiglayer.create(targetLayer);

							try {
								
								String database = (String)dbase.getSelectedItem();
								Properties props = Utils.getDBProperties(); 					
								//String jdbc = props.getProperty("jdbc", "postgres");  
						        String host = props.getProperty("db.host", "localhost");
						        String username = props.getProperty("db.user", "postgres");  
						        String password = props.getProperty("db.passwd", "postgres");
						        String port = props.getProperty("db.port", "5432");
						        //String url = "jdbc:"+ jdbc + "://" + host + ":" + port + "/" +database;
						        
						        
						        //Connection conn = Utils.getConnectionToDatabase(database);
//						        Connection conn = DriverManager.getConnection(url, username, password);
//								Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+database,"postgres","postgres");

								VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
								for (int i = 1; i < table.getRowCount(); i++)
								{
									double dBottom = Double.parseDouble((String) table.getValueAt(i, 0));
									double dTop = Double.parseDouble((String) table.getValueAt(i, 1));							
									ParametersSet params = prova.getParameters();
									params.getParameter(CreateModelLayerAlgorithm.INPUT).setParameterValue(gvsiglayer);
									params.getParameter(CreateModelLayerAlgorithm.TOP).setParameterValue(dBottom);
									params.getParameter(CreateModelLayerAlgorithm.BOTTOM).setParameterValue(dTop);
									params.getParameter(CreateModelLayerAlgorithm.NUMBERL).setParameterValue(table.getValueAt(i, 2).toString());
									OutputObjectsSet outputs = prova.getOutputObjects();
									Output out = outputs.getOutput(CreateModelLayerAlgorithm.RESULT);
									out.setOutputChannel(new FileOutputChannel(".shp"));
									prova.execute(null, m_OutputFactory);

									
									
									IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
									String layerName = table.getValueAt(i, 2).toString();
									FLyrVect shape = (FLyrVect) LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
									//mapCtrl.getMapContext().getLayers().addLayer(shape);
									Utils.saveToPostGIS( shape, database);
									//saveToPostGIS(mapCtrl.getMapContext(), (FLyrVect) shape, (String)dbase.getSelectedItem());
									//CaricaPostGis test = new CaricaPostGis();
									//FLyrVect postgis = (FLyrVect) shape;
									//mapCtrl.getMapContext().getLayers().removeLayer(shape.getName());
									//mapCtrl.getMapContext().getLayers().addLayer(test.getLayerPostGIS("jdbc:postgresql://localhost:5432/"+database, "localhost", "postgres", "postgres", layerName, layerName, postgis.getShapeType()));
									mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS(host,Integer.valueOf(port), database, username, password, layerName, layerName));

								}
								//conn.close();
								//ResultSet rs = null;
								//Statement stmt=conn.createStatement(rs.CONCUR_UPDATABLE, rs.TYPE_SCROLL_SENSITIVE);
								//Connection connTable = DriverManager.getConnection(url, username, password);
								Connection connTable = Utils.getConnectionToDatabase(database);
								PreparedStatement ps = connTable.prepareStatement("INSERT INTO lpf(id, model_layer, layer_type, layer_average, anisotropia, value_anisotropia, layer_vka, layer_wet) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

								// TODO: controllare se l'id di lpf esiste?
								for (int i=0; i < table.getRowCount(); i++)
								{
									ps.setInt(1, i+1);
									ps.setString(2, table.getValueAt(i, 2).toString());
									ps.setString(3, table.getValueAt(i, 3).toString());
									ps.setString(4, table.getValueAt(i, 4).toString());
									ps.setString(5, table.getValueAt(i, 5).toString());
									ps.setDouble(6, Double.parseDouble(table.getValueAt(i, 6).toString()));
									ps.setInt(7, Integer.parseInt(table.getValueAt(i, 7).toString()));
									ps.setString(8, table.getValueAt(i, 8).toString());

									ps.execute();

								}
								connTable.close();

								progress.close();


							} catch (GeoAlgorithmExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (DriverLoadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SecurityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (SQLException e) {
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
							} catch (DBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} finally{
								progress.close();
							}

							JOptionPane.showMessageDialog(null,
									PluginServices.getText(this, "Run_successfull"),
									"Message",
									JOptionPane.INFORMATION_MESSAGE);

						}
					}.start();
				}
			};

		}

		return testAction1;
	}


	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			model = new DefaultTableModel()
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column)
				{

					if (column == 6) {
						return false;
					}
					return true;
				}

			}; 

			model.addColumn("Top");
			model.addColumn("Bottom");
			model.addColumn("Model layer");
			model.addColumn("Layer type");
			model.addColumn("Layer avg");
			model.addColumn("Constant Anisotropy");
			model.addColumn("Anisotropy value");
			model.addColumn("Layer vka");
			model.addColumn("Layer wet");


			model.addTableModelListener(new TableModelListener() {

				@Override
				public void tableChanged(TableModelEvent e) {
					if(e.getType() == TableModelEvent.UPDATE && e.getColumn() == 5) {
						int row = e.getFirstRow();
						int col = e.getColumn();
						if(model.getValueAt(row, col) == "si")
						{
							String Valore = JOptionPane.showInputDialog(new JFormattedTextField(double.class), "inserisci", 0);                    	                    
							try
							{
								if(Valore != null){
									double inserimento = Double.parseDouble(Valore);
									model.setValueAt(inserimento, row, col+1);
								}
								else // Annullato
									model.setValueAt("no", row, col);

							}
							catch(NumberFormatException ex)
							{
								JOptionPane.showMessageDialog(null, "Bad input. Only numeric values");
								model.setValueAt("no", row, col);
							}
						}
						else
						{
							model.setValueAt(0.0, row, col+1);
						}
					}
				}
			}
			);

			try {


				int filedTop = targetLayer.getRecordset().getFieldIndexByName("TOP");
				int filedBottom = targetLayer.getRecordset().getFieldIndexByName("BOTTOM");

				long idRec = 1;
				String nameLayer = targetLayer.getName();
				String Top = targetLayer.getRecordset().getFieldValue(idRec, filedTop).toString();
				String Bottom = targetLayer.getRecordset().getFieldValue(idRec, filedBottom).toString();

				Object[] toplayer = { Top, Bottom, nameLayer, 
						"confined", "harmonic", "no", new Double (0), new Integer (0), "inactive" 
				};

				model.addRow(toplayer);
				table = new JTable(model);
				setUpTypeColumn(table.getColumnModel().getColumn(3));
				setUpTypeColumnLayAvg(table.getColumnModel().getColumn(4));
				setUpTypeColumnLayWet(table.getColumnModel().getColumn(8));
				setUpTypeColumnLayChani(table.getColumnModel().getColumn(5));

				JTableHeader header = table.getTableHeader();

				ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
				tips.setToolTip(table.getColumnModel().getColumn(0), "Top of the layer model");
				tips.setToolTip(table.getColumnModel().getColumn(1), "Bottom of the layer model");
				tips.setToolTip(table.getColumnModel().getColumn(2), "Model layer name");
				tips.setToolTip(table.getColumnModel().getColumn(3), "Specifies the model layer type");
				tips.setToolTip(table.getColumnModel().getColumn(4), "Defines the method of calculating interblock transmissivity");
				tips.setToolTip(table.getColumnModel().getColumn(5), "Contains a value for each layer that is a flag or the horizontal anisotropy");
				tips.setToolTip(table.getColumnModel().getColumn(6), "The value for the horizontal anisotropy");
				tips.setToolTip(table.getColumnModel().getColumn(7), "<html>Contains a flag for each layer that indicates whether variable VKA <br> is vertical hydraulic conductivity or the ratio of horizontal to vertical hydraulic conductivity</br><br>0Ñindicates VKA is vertical hydraulic conductivity</br><br>not 0Ñindicates VKA is the ratio of horizontal to vertical hydraulic conductivity</br></html>");
				tips.setToolTip(table.getColumnModel().getColumn(8), "Contains a flag for each layer that indicates if wetting is active");
				header.addMouseMotionListener(tips);

				jScrollPane1 = new JScrollPane(table);
				springLayout.putConstraint(SpringLayout.NORTH, jScrollPane1, 60, SpringLayout.NORTH, this);
				springLayout.putConstraint(SpringLayout.SOUTH, dbase, -15, SpringLayout.NORTH, jScrollPane1);
				springLayout.putConstraint(SpringLayout.EAST, dbase, 0, SpringLayout.EAST, jScrollPane1);
				springLayout.putConstraint(SpringLayout.WEST, jScrollPane1, 32, SpringLayout.WEST, this);
				springLayout.putConstraint(SpringLayout.EAST, jScrollPane1, -10, SpringLayout.EAST, this);




			} catch (ReadDriverException e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
			}

		}
		return jScrollPane1;
	}


	private JButton getAdd() {
		if(add == null) {
			add = new JButton();
			springLayout.putConstraint(SpringLayout.WEST, add, 32, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, add, -10, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, add, 132, SpringLayout.WEST, this);
			add.setText(PluginServices.getText(this,"Add"));
			add.setIcon(plus);
			add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {

					if(table.isEditing())
						table.getCellEditor().cancelCellEditing();
					String Top = table.getValueAt(table.getRowCount()-1, 1).toString();

					String layer = "model_layer_" + (table.getRowCount() +1);

					Object[] defaultinput = { Top, "0", layer, 
							"confined", "harmonic", "no", "0", "0", "inactive"};
					model.addRow(defaultinput);


				}
			});
		}
		return add;
	}

	private JButton getRemove() {
		if(remove == null) {
			remove = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, remove, 0, SpringLayout.NORTH, getExport());
			springLayout.putConstraint(SpringLayout.WEST, remove, 32, SpringLayout.EAST, getAdd());
			remove.setText(PluginServices.getText(this,"remove"));
			remove.setIcon(delete);
			remove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if(table.getSelectedRow()>-1)
						model.removeRow(table.getSelectedRow());
				}
			});
		}
		return remove;
	}

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Model_layers"));

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public void setUpTypeColumn(TableColumn typeColumn) {
		//Set up the editor for the simulation cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("confined");
		comboBox.addItem("convertible");  			
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));

		//Set up tool tips for the simulation cells.
		DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		typeColumn.setCellRenderer(renderer);
	}

	public void setUpTypeColumnLayAvg(TableColumn typeColumn) {
		//Set up the editor for the simulation cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("harmonic");
		comboBox.addItem("logarithmic"); 
		comboBox.addItem("arithmetic"); 
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));

		//Set up tool tips for the simulation cells.
		DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		typeColumn.setCellRenderer(renderer);
	}

	public void setUpTypeColumnLayWet(TableColumn typeColumn) {
		//Set up the editor for the simulation cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("inactive");
		comboBox.addItem("active"); 
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));

		//Set up tool tips for the simulation cells.
		DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		typeColumn.setCellRenderer(renderer);
	}

	public void setUpTypeColumnLayChani(TableColumn typeColumn) {
		//Set up the editor for the simulation cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("si");
		comboBox.addItem("no"); 
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));

		//Set up tool tips for the simulation cells.
		DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		typeColumn.setCellRenderer(renderer);
	}

	private JLabel getJLabelDB() {
		if(jLabelDB == null) {
			jLabelDB = new JLabel();
			springLayout.putConstraint(SpringLayout.NORTH, jLabelDB, 4, SpringLayout.NORTH, dbase);
			springLayout.putConstraint(SpringLayout.WEST, jLabelDB, -133, SpringLayout.WEST, dbase);
			springLayout.putConstraint(SpringLayout.EAST, jLabelDB, -10, SpringLayout.WEST, dbase);
			jLabelDB.setText(PluginServices.getText(this,"GeoDB"));
		}
		return jLabelDB;
	}

private JButton getBtnCloseTool() {
	if (btnCloseTool == null) {
		btnCloseTool = new JButton(
				new AbstractAction(PluginServices.getText(this, "Close")) {
					@Override
					public void actionPerformed(ActionEvent evt) {
						PluginServices.getMDIManager().closeWindow(CreateModelLayers.this);
					}
				});
		springLayout.putConstraint(SpringLayout.EAST, getRemove(), -480, SpringLayout.WEST, btnCloseTool);
		springLayout.putConstraint(SpringLayout.WEST, btnCloseTool, -106, SpringLayout.WEST, getExport());
		springLayout.putConstraint(SpringLayout.SOUTH, btnCloseTool, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnCloseTool, -26, SpringLayout.WEST, getExport());
		btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
	}
	return btnCloseTool;
}
}

