package it.sidgrid.dbase;

import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.table.TableColumn;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

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
 * GUI to manage the layer property file table on postgres by gvSIG
 * read db.properites
 */
public class DbLPFmanage extends javax.swing.JPanel implements IWindow{
	private JComboBox jComboBoxDB;
	private JComboBox jComboBoxLPF;
	private AbstractAction actionApply;


	private JScrollPane jScrollPane;

	private JButton apply;
	private JLabel jLabelDB;
	private JLabel jLabelLPF;
	private JButton show;

	private DefaultTableModel model;
	private JTable table;
	Statement stmt = null;
	ResultSet rs = null;
	private WindowInfo viewInfo = null;
	private JButton btnCloseTool;
	private SpringLayout springLayout;

	/**
	 * Auto-generated main method to display this JFrame
	 * @throws IOException 
	 * @throws SQLException 
	 */



	public DbLPFmanage() throws IOException {
		super();		    
		initGUI();	
	}

	private void initGUI() {
		ComboBoxModel jComboBoxLayersModel = new DefaultComboBoxModel(Utils.getDbase());
		springLayout = new SpringLayout();
		setLayout(springLayout);
		jComboBoxDB = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, jComboBoxDB, 23, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, jComboBoxDB, 90, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, jComboBoxDB, 45, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, jComboBoxDB, 262, SpringLayout.WEST, this);
		this.add(jComboBoxDB);
		this.add(getJScrollPane());

		this.add(getShow());								
		this.add(getJLabelDB());
		this.add(getApply());
		jComboBoxDB.setModel(jComboBoxLayersModel);
		jComboBoxDB.setSelectedIndex(0);
		jComboBoxDB.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent e) {
				setTable();
			}
		});

		this.add(getComboBoxLPF());
		
		this.setName(PluginServices.getText(this,"Modify_LPF"));
		this.setSize(695, 216);
		this.setVisible(true);
		this.add(getJLabelLPF());
		this.add(getBtnCloseTool());
	}

	private JComboBox getComboBoxLPF() {
		if(jComboBoxLPF == null) {
			ComboBoxModel jComboModelTable = 
				new DefaultComboBoxModel();
			jComboBoxLPF = new JComboBox();
			springLayout.putConstraint(SpringLayout.NORTH, jComboBoxLPF, 23, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jComboBoxLPF, 419, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jComboBoxLPF, 45, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jComboBoxLPF, 591, SpringLayout.WEST, this);
			jComboBoxLPF.setModel(jComboModelTable);
			jComboBoxLPF.addItemListener(new java.awt.event.ItemListener() {
				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					getTableView().setEnabled(false);
					getShow().setEnabled(true);
				}
			});

		}
		return jComboBoxLPF;
	}
	
	private AbstractAction getActionApply() {
		if(actionApply == null) {
			actionApply = new AbstractAction(PluginServices.getText(this,"Apply"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PluginServices.getMDIManager().closeWindow(DbLPFmanage.this);
					try {

						String dbase = (String)jComboBoxDB.getSelectedItem();
						Connection conn = Utils.getConnectionToDatabase(dbase);
//						ResultSet rs = null;
//						Statement stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);

						for (int i = 0; i < table.getRowCount(); i++) {
							PreparedStatement ps = conn.prepareStatement("UPDATE "+(String)jComboBoxLPF.getSelectedItem()+" SET model_layer=?, layer_type=?, layer_average=?, anisotropia=?, value_anisotropia=?, layer_vka=?, layer_wet=? WHERE id="+i+"+1");

							ps.setString(1, table.getValueAt(i, 0).toString());
							ps.setString(2, table.getValueAt(i, 1).toString());
							ps.setString(3, table.getValueAt(i, 2).toString());
							ps.setString(4, table.getValueAt(i, 3).toString());
							ps.setDouble(5, Double.parseDouble(table.getValueAt(i, 4).toString()));
							ps.setInt(6, Integer.parseInt(table.getValueAt(i, 5).toString()));
							ps.setString(7, table.getValueAt(i, 6).toString());
							
							ps.execute();

						}
						conn.close();
						ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
						ProjectTable pt = (ProjectTable) ext.getProject().getProjectDocumentByName((String) getComboBoxLPF().getSelectedItem(), ProjectTableFactory.registerName);
						pt.setModel(pt.getModelo());
					} 

					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			};

		}

		return actionApply;
	}

	private JTable getTableView() {
		if(table == null) {
			model = new DefaultTableModel()
			{

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column)
				{

					if (column == 4) 
						return false;
					return true;
				}

			}; 

			//model.addColumn("Top");
			//model.addColumn("Bottom");
			model.addColumn("Model Layer");
			model.addColumn("Layer type");
			model.addColumn("Layer avg");
			model.addColumn("Constant Anisotropy");
			model.addColumn("Anisotropy value");
			model.addColumn("Layer vka");
			model.addColumn("Layer wet");
		

			model.addTableModelListener(new TableModelListener() {

				@Override
				public void tableChanged(TableModelEvent e) {
					if(e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
						//TableModel source = (TableModel)e.getSource();
						int row = e.getFirstRow();
						int col = e.getColumn();
						if(model.getValueAt(row, col) == "si")
						{
							String Valore = JOptionPane.showInputDialog(new JFormattedTextField(double.class), "inserisci", 0);                    	                    
							try
							{
								double inserimento = Double.parseDouble(Valore);

								model.setValueAt(inserimento, row, col+1);
							}
							catch(NumberFormatException ex)
							{
								JOptionPane.showMessageDialog(null, "Bad input. Only numeric values");
							}
						}
						else
						{
							model.setValueAt(0.0, row, col+1);
						}
					}
				}
			});

			table = new JTable(model);

			setUpTypeColumn(table.getColumnModel().getColumn(1));
			setUpTypeColumnLayAvg(table.getColumnModel().getColumn(2));
			setUpTypeColumnLayWet(table.getColumnModel().getColumn(6));
			setUpTypeColumnLayChani(table.getColumnModel().getColumn(3));		}
		return table;
	}

	private JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new JScrollPane(getTableView());
			springLayout.putConstraint(SpringLayout.NORTH, jScrollPane, 68, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jScrollPane, 32, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, jScrollPane, 32, SpringLayout.WEST, getApply());
		}
		return jScrollPane;
	}



	private JButton getShow() {
		if(show == null) {
			show = new JButton();
			springLayout.putConstraint(SpringLayout.EAST, getJScrollPane(), -6, SpringLayout.WEST, show);
			springLayout.putConstraint(SpringLayout.NORTH, show, 65, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.SOUTH, show, -58, SpringLayout.NORTH, getApply());
			springLayout.putConstraint(SpringLayout.EAST, show, -10, SpringLayout.EAST, this);
			show.setText(PluginServices.getText(this,"Show"));
			show.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					String dbase = (String)jComboBoxDB.getSelectedItem();
					try {
						
						String sql = "SELECT id, model_layer, layer_type, layer_average, anisotropia, value_anisotropia, layer_vka, layer_wet FROM "+(String)jComboBoxLPF.getSelectedItem()+";";
						Connection conn = Utils.getConnectionToDatabase(dbase);
						stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);
						stmt.execute(sql);
						rs = stmt.getResultSet();

						// clean the table before update
						DefaultTableModel tmod= (DefaultTableModel)getTableView().getModel();
						while(tmod.getRowCount()>0)
							tmod.removeRow(0);
						
						while (rs.next()) {

							String modelL = rs.getString(2);
							String type = rs.getString(3);
							String average = rs.getString(4);
							String ani = rs.getString(5);
							Double chani = rs.getDouble(6);
							Integer vka = rs.getInt(7);
							String wet = rs.getString(8); 
							

							Object[] row = {modelL, type, average, ani, chani, vka, wet };

							tmod.addRow(row);

						}

						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					show.setEnabled(false);
					getTableView().setEnabled(true);
				}
			});
			show.setBounds(597, 68, 59, 22);
		}
		return show;
	}


	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Modify_LPF"));
			viewInfo.setHeight(220);
			viewInfo.setWidth(695);
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
							PluginServices.getMDIManager().closeWindow(DbLPFmanage.this);
						}
					});
			springLayout.putConstraint(SpringLayout.SOUTH, getJScrollPane(), -6, SpringLayout.NORTH, btnCloseTool);
			springLayout.putConstraint(SpringLayout.WEST, btnCloseTool, 32, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, btnCloseTool, -21, SpringLayout.SOUTH, this);
			btnCloseTool.setSize(80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
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
			springLayout.putConstraint(SpringLayout.NORTH, jLabelDB, 26, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jLabelDB, 32, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jLabelDB, 41, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jLabelDB, 76, SpringLayout.WEST, this);
			jLabelDB.setText(PluginServices.getText(this,"GeoDB"));
		}
		return jLabelDB;
	}
	
	private JLabel getJLabelLPF() {
		if(jLabelLPF == null) {
			jLabelLPF = new JLabel();
			springLayout.putConstraint(SpringLayout.NORTH, jLabelLPF, 26, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jLabelLPF, 341, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jLabelLPF, 41, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jLabelLPF, 421, SpringLayout.WEST, this);
			jLabelLPF.setText(PluginServices.getText(this,"LPF_table"));
		}
		return jLabelLPF;
	}

	private JButton getApply() {
		if(apply == null) {
			apply = new JButton();
			springLayout.putConstraint(SpringLayout.EAST, apply, -10, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.NORTH, apply, 145, SpringLayout.NORTH, this);
			apply.setAction(getActionApply());
		}
		return apply;
	}

	protected void setTable() {
		final ComboBoxModel jComboBoxFieldModelTable = new DefaultComboBoxModel(Utils.getTableLPF((String)jComboBoxDB.getSelectedItem()));
		jComboBoxLPF.setModel(jComboBoxFieldModelTable);

	}
	
}
