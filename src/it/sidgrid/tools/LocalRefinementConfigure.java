package it.sidgrid.tools;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import it.sidgrid.utils.Close;
import it.sidgrid.utils.NameChooser;
import it.sidgrid.utils.Utils;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
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
 * GUI to setup Local Grid Refinement parameters for LGR model package
 * read db.properites
 */
public class LocalRefinementConfigure extends javax.swing.JPanel implements IWindow{
	private JScrollPane jScrollTableLGR;
	private DefaultTableModel model;
	private JTable table;
	private JButton show;
	private WindowInfo viewInfo = null;
	Statement stmt = null;
	ResultSet rs = null;
	private JComboBox jComboBox1;
	private AbstractAction saveTable;
	private JButton apply;
	private JLabel lblGeodatabase;
	private SpringLayout springLayout;
	
	public LocalRefinementConfigure(){
		super();		    
		initGUI();	
	}
	
	
	private void initGUI(){
		//this.setName(PluginServices.getText(this,"Configure_LGR"));
		this.setSize(1020, 231);
		this.setVisible(true);
		springLayout = new SpringLayout();
		setLayout(springLayout);
		this.add(getjScrollTableLGR());
		this.add(getTable());
		this.add(getApply());
		ComboBoxModel jComboBoxLayersModel = new DefaultComboBoxModel(Utils.getDbase());
		jComboBox1 = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, jComboBox1, -250, SpringLayout.WEST, getTable());
		springLayout.putConstraint(SpringLayout.EAST, jComboBox1, -26, SpringLayout.WEST, getTable());
		springLayout.putConstraint(SpringLayout.NORTH, getjScrollTableLGR(), 14, SpringLayout.SOUTH, jComboBox1);
		springLayout.putConstraint(SpringLayout.NORTH, jComboBox1, 24, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, jComboBox1, 46, SpringLayout.NORTH, this);
		this.add(jComboBox1);
		jComboBox1.setModel(jComboBoxLayersModel);
		jComboBox1.setSelectedIndex(0);
		
		JButton btnCloseButton = new JButton(PluginServices.getText(this,"Close"));
		springLayout.putConstraint(SpringLayout.SOUTH, btnCloseButton, -14, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnCloseButton, -16, SpringLayout.WEST, getApply());
		btnCloseButton.addActionListener(new Close(this));
		add(btnCloseButton);
		add(getLblGeodatabase());
		springLayout.putConstraint(SpringLayout.SOUTH, getjScrollTableLGR(), -14, SpringLayout.NORTH, btnCloseButton);
	}
	
	private JScrollPane getjScrollTableLGR() {
		if(jScrollTableLGR == null) {
			model = new DefaultTableModel()
			{
				private static final long serialVersionUID = 1L;
				@Override
				public boolean isCellEditable(int row, int column)
				{
					if (column == 0 || column == 9 || column == 10 || column == 11 || column == 12 || column == 13
							 || column == 14 || column == 15) 
						return false;
					return true;
				}
			}; 
			model.addColumn("id");
			model.addColumn("Name file");
			model.addColumn("ishflg");
			model.addColumn("ibflg");
			model.addColumn("Iter");
			model.addColumn("RelaxH");
			model.addColumn("RelaxF");
			model.addColumn("Hcloser");
			model.addColumn("Fcloser");
			model.addColumn("row start");
			model.addColumn("col start");
			model.addColumn("row end");
			model.addColumn("col end");
			model.addColumn("lay end");
			model.addColumn("ncpp");
			model.addColumn("ncppl");
			

			table = new JTable(model);	
			setUpTypeColumnNameFIle(table.getColumnModel().getColumn(1));
			jScrollTableLGR = new JScrollPane(table);
			springLayout.putConstraint(SpringLayout.WEST, jScrollTableLGR, 17, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, jScrollTableLGR, -16, SpringLayout.EAST, this);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			
		
		}
		return jScrollTableLGR;
	}
	
	public void setUpTypeColumnNameFIle(TableColumn typeColumn) {
		//Set up the editor for the simulation cells.
		NameChooser name = new NameChooser();
		
		typeColumn.setCellEditor(name);

		//Set up tool tips for the simulation cells.
		
	}
	
	private JButton getTable() {
		if(show == null) {
			show = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, show, 24, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, show, -26, SpringLayout.EAST, this);
			show.setText(PluginServices.getText(this,"Show"));
			show.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					String dbase = (String)jComboBox1.getSelectedItem();
					try {
						
						String sql = "SELECT id,name_file,ishflg,ibflg,iter,relaxh,relaxf,hcloser,fcloser,row_start,col_start,row_end,col_end,lay_end,ncpp,ncppl FROM gridrefine";
						Connection conn = Utils.getConnectionToDatabase(dbase);
						stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);
						stmt.execute(sql);
						rs = stmt.getResultSet();

						while (rs.next()) {

							String id = rs.getString(1);
							String name = rs.getString(2);
							Integer ishflg = rs.getInt(3);
							Integer ibflg = rs.getInt(4);
							Integer Iter = rs.getInt(5);
							Double RelaxH = rs.getDouble(6);
							Double RelaxF = rs.getDouble(7);
							Double Hcloser = rs.getDouble(8);
							Double Fcloser = rs.getDouble(9);
							Integer row_s = rs.getInt(10);
							Integer col_s = rs.getInt(11);
							Integer row_e = rs.getInt(12);
							Integer col_e = rs.getInt(13);
							Integer lay = rs.getInt(14);
							Integer ncpp = rs.getInt(15);
							Integer ncppl = rs.getInt(16);
																					
							Object[] row = {id, name, ishflg, ibflg, Iter, RelaxH, RelaxF, Hcloser,
									Fcloser, row_s, col_s, row_e, col_e, lay, ncpp, ncppl};

							model.addRow(row);

						}
						conn.close();

					} catch (SQLException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Table or data base error");
					}
					show.setEnabled(false);
				}
			});
		}
		return show;
	}
	
	private AbstractAction getSaveTable() {
		if(saveTable == null) {
			saveTable = new AbstractAction(PluginServices.getText(this,"Apply"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PluginServices.getMDIManager().closeWindow(LocalRefinementConfigure.this);
					try {

						String dbase = (String)jComboBox1.getSelectedItem();
						Connection conn = Utils.getConnectionToDatabase(dbase);
//						ResultSet rs = null;
//						Statement stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);

						for (int i = 0; i < table.getRowCount(); i++) {
							PreparedStatement ps = conn.prepareStatement("UPDATE gridrefine SET name_file=?,ishflg=?,ibflg=?,iter=?,relaxh=?,relaxf=?,hcloser=?,fcloser=?,row_start=?,col_start=?,row_end=?,col_end=?,lay_end=?,ncpp=?,ncppl=? WHERE id="+i+"+1");
					
							ps.setString(1, table.getValueAt(i, 1).toString());
							ps.setInt(2, Integer.parseInt(table.getValueAt(i, 2).toString()));
							ps.setInt(3, Integer.parseInt(table.getValueAt(i, 3).toString()));
							ps.setInt(4, Integer.parseInt(table.getValueAt(i, 4).toString()));
							ps.setDouble(5, Double.parseDouble(table.getValueAt(i, 5).toString()));
							ps.setDouble(6, Double.parseDouble(table.getValueAt(i, 6).toString()));
							ps.setDouble(7, Double.parseDouble(table.getValueAt(i, 7).toString()));
							ps.setDouble(8, Double.parseDouble(table.getValueAt(i, 8).toString()));
							ps.setInt(9, Integer.parseInt(table.getValueAt(i, 9).toString()));
							ps.setInt(10, Integer.parseInt(table.getValueAt(i, 10).toString()));
							ps.setInt(11, Integer.parseInt(table.getValueAt(i, 11).toString()));
							ps.setInt(12, Integer.parseInt(table.getValueAt(i, 12).toString()));
							ps.setInt(13, Integer.parseInt(table.getValueAt(i, 13).toString()));
							ps.setInt(14, Integer.parseInt(table.getValueAt(i, 14).toString()));
							ps.setInt(15, Integer.parseInt(table.getValueAt(i, 15).toString()));							
														
							ps.execute();

						}
						conn.close();

					} 

					catch (SQLException e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(null, "Table or data base error");
					}

				}

			};

		}

		return saveTable;
	}
	
	private JButton getApply() {
		if(apply == null) {
			apply = new JButton();
			springLayout.putConstraint(SpringLayout.SOUTH, apply, -14, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, apply, -26, SpringLayout.EAST, this);
			apply.setText(PluginServices.getText(this,"Apply"));
			apply.setAction(getSaveTable());
		}
		return apply;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "LGR_table"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	private JLabel getLblGeodatabase() {
		if (lblGeodatabase == null) {
			lblGeodatabase = new JLabel(PluginServices.getText(this,"GeoDataBase"));
			springLayout.putConstraint(SpringLayout.NORTH, lblGeodatabase, 28, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, lblGeodatabase, -226, SpringLayout.WEST, jComboBox1);
			springLayout.putConstraint(SpringLayout.EAST, lblGeodatabase, -26, SpringLayout.WEST, jComboBox1);
			lblGeodatabase.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return lblGeodatabase;
	}
}
