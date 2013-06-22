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
import javax.swing.JLabel;
//import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
 * GUI to manage the Stress Period table on postgres by gvSIG
 *
 */

public class DbStressPeriodManage extends javax.swing.JPanel implements IWindow{
	private JComboBox jComboBox1;
	private AbstractAction testAction1;

	private JScrollPane jScrollPane1;

	private JButton apply;
	private JLabel jLabelDB;

	private JButton show;
	private JButton Add;
	private NewModel modello;
	private JTable table;
	Statement stmt = null;
	ResultSet rs = null;
	private WindowInfo viewInfo = null;
	private int newsp;
	private int record;
	private JButton btnCloseTool;
	private SpringLayout springLayout;
	/**
	 * Auto-generated main method to display this JFrame
	 * @throws IOException 
	 * @throws SQLException 
	 */



	public DbStressPeriodManage() {
		super();		    
		initGUI();	
	}

	private void initGUI() {
		ComboBoxModel jComboBoxLayersModel = new DefaultComboBoxModel(Utils.getDbase());
		springLayout = new SpringLayout();
		setLayout(springLayout);
		jComboBox1 = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, jComboBox1, 23, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, jComboBox1, 45, SpringLayout.NORTH, this);
		jComboBox1.setModel(jComboBoxLayersModel);
		jComboBox1.setSelectedIndex(0);
		this.add(jComboBox1);
		this.add(getJScrollPane1());				
		this.add(getShow());
		this.add(getAdd());
		this.add(getJLabelDB());
		this.add(getApply());


		this.setName(PluginServices.getText(this,"Modify_Stress_Period"));
		this.setSize(680, 233);
		this.setVisible(true);
		this.add(getBtnCloseTool());
	}


	private AbstractAction getTestAction1() {
		if(testAction1 == null) {
			testAction1 = new AbstractAction(PluginServices.getText(this,"Apply"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PluginServices.getMDIManager().closeWindow(DbStressPeriodManage.this);
					try {
						Class.forName("org.postgresql.Driver");
						
						String dbase = (String)jComboBox1.getSelectedItem();
						Connection conn = Utils.getConnectionToDatabase(dbase);
//						Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbase,"postgres","postgres");						
						
						conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);
						//Statement stmt = conn.createStatement();
						for (int i = 0; i < getRecord(); i++) {
							PreparedStatement ps = conn.prepareStatement("UPDATE stressperiod SET lenght=?, time_steps=?, multiplier=?, state=? WHERE id="+i+"+1");

							ps.setDouble(1, Double.parseDouble(table.getValueAt(i, 1).toString()));
							ps.setDouble(2, Integer.parseInt(table.getValueAt(i, 2).toString()));
							ps.setDouble(3, Double.parseDouble(table.getValueAt(i, 3).toString()));
							ps.setString(4, table.getValueAt(i, 4).toString());

							ps.execute();

						}

						if (getNewSP() != 0)
						{
//							Connection conn2 = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbase,"postgres","postgres");
							PreparedStatement ps1 = conn.prepareStatement("INSERT INTO stressperiod(id, lenght, time_steps, multiplier, state)VALUES (?, ?, ?, ?, ?)");
							for (int k=getRecord(); k < (getRecord()+getNewSP()); k++)
							{
								ps1.setInt(1, k+1);
								ps1.setDouble(2, Double.parseDouble(table.getValueAt(k, 1).toString()));
								ps1.setDouble(3, Integer.parseInt(table.getValueAt(k, 2).toString()));
								ps1.setDouble(4, Double.parseDouble(table.getValueAt(k, 3).toString()));
								ps1.setString(5, table.getValueAt(k, 4).toString());
								ps1.execute();
//								stmt.execute("ALTER TABLE well_model ADD COLUMN sp_"+(k+1)+" double precision");
							}
						
														
						}
												
						conn.close();
//						ProjectExtension ext3 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
//						ProjectTable time = (ProjectTable) ext3.getProject().getProjectDocumentByName("stressperiod", ProjectTableFactory.registerName);
////					    time.getModelo().getRecordset().reload();
//					    time.getAssociatedTable().getRecordset().reload();
					    
					    ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
                        ProjectTable pt = (ProjectTable) ext.getProject().getProjectDocumentByName("stressperiod", ProjectTableFactory.registerName);
                        pt.setModel(pt.getModelo());
						
					} 

					catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			};

		}

		return testAction1;
	}



	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			//			model = new DefaultTableModel();
			modello = new NewModel();

			modello.addColumn("Id");
			modello.addColumn("Lenght");
			modello.addColumn("Time Steps");
			modello.addColumn("Multiplier");
			modello.addColumn("State");

			table = new JTable(modello);

			setUpSimulColumn(table.getColumnModel().getColumn(4));

			jScrollPane1 = new JScrollPane(table);
			springLayout.putConstraint(SpringLayout.WEST, jComboBox1, -170, SpringLayout.EAST, jScrollPane1);
			springLayout.putConstraint(SpringLayout.NORTH, jScrollPane1, 23, SpringLayout.SOUTH, jComboBox1);
			springLayout.putConstraint(SpringLayout.EAST, jComboBox1, 0, SpringLayout.EAST, jScrollPane1);
			springLayout.putConstraint(SpringLayout.WEST, jScrollPane1, 32, SpringLayout.WEST, this);

		}
		return jScrollPane1;
	}



	private JButton getShow() {
		if(show == null) {
			show = new JButton();
			springLayout.putConstraint(SpringLayout.EAST, getJScrollPane1(), -6, SpringLayout.WEST, show);
			springLayout.putConstraint(SpringLayout.NORTH, show, 68, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, show, -86, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, show, 90, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, show, -10, SpringLayout.EAST, this);
			show.setText(PluginServices.getText(this,"Show"));
			show.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					String dbase = (String)jComboBox1.getSelectedItem();
					try {
						String sql = "SELECT id, lenght, time_steps, multiplier, state FROM stressperiod";
						Connection conn = Utils.getConnectionToDatabase(dbase);
						stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);
						stmt.execute(sql);
						rs = stmt.getResultSet();
						record=0;
						while (rs.next()) {
							Integer id = rs.getInt(1);
							Double sp = rs.getDouble(2);
							Integer ts = (int) rs.getShort(3);
							Double mlt = rs.getDouble(4);
							String state = rs.getString(5);

							Object[] row = {id, sp, ts, mlt, state};

							modello.addRow(row);
							record++;
							System.out.println(getRecord());
						}


					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}
		return show;
	}

	
	private JButton getAdd() {
		if(Add == null) {
			newsp=0;
			Add = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, Add, 6, SpringLayout.SOUTH, getShow());
			springLayout.putConstraint(SpringLayout.WEST, Add, -86, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, Add, 26, SpringLayout.SOUTH, getShow());
			springLayout.putConstraint(SpringLayout.EAST, Add, -10, SpringLayout.EAST, this);
			Add.setText("Add");
			Add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					Object[] stressdata = { new Integer(0), new Double (0), new Integer(0), new Double (0), "SS" };
					modello.addRow(stressdata);
					newsp++;
				}
			});
		}
		return Add;
		
	}

	private int getNewSP(){
		return newsp;		
	}
	
	private int getRecord(){
		return record;		
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Modify_Stress_Period"));

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
							PluginServices.getMDIManager().closeWindow(DbStressPeriodManage.this);
						}
					});
			springLayout.putConstraint(SpringLayout.SOUTH, getJScrollPane1(), -6, SpringLayout.NORTH, btnCloseTool);
			springLayout.putConstraint(SpringLayout.NORTH, btnCloseTool, -26, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.SOUTH, btnCloseTool, -6, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, btnCloseTool, 112, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.WEST, btnCloseTool, 32, SpringLayout.WEST, this);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}


	public void setUpSimulColumn(TableColumn sportColumn) {
		//Set up the editor for the simulation cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("SS");
		comboBox.addItem("TR");  			
		sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

		//Set up tool tips for the simulation cells.
		DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		sportColumn.setCellRenderer(renderer);
	}



	private JLabel getJLabelDB() {
		if(jLabelDB == null) {
			jLabelDB = new JLabel();
			springLayout.putConstraint(SpringLayout.NORTH, jLabelDB, 4, SpringLayout.NORTH, jComboBox1);
			springLayout.putConstraint(SpringLayout.WEST, jLabelDB, -56, SpringLayout.WEST, jComboBox1);
			springLayout.putConstraint(SpringLayout.EAST, jLabelDB, -6, SpringLayout.WEST, jComboBox1);
			jLabelDB.setText(PluginServices.getText(this,"GeoDB"));
		}
		return jLabelDB;
	}

	private JButton getApply() {
		if(apply == null) {
			apply = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, apply, -26, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.SOUTH, apply, -6, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.WEST, apply, -86, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.EAST, apply, -11, SpringLayout.EAST, this);
			apply.setAction(getTestAction1());
		}
		return apply;
	}

	public class NewModel extends DefaultTableModel {
		@Override
		public boolean isCellEditable(int row, int col) {
			return (col != 0);
		}
	}


}







