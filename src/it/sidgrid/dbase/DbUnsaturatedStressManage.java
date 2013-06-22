package it.sidgrid.dbase;

import it.sidgrid.task.ProgressTask;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
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
 * GUI to manage the unsaturated model layer table on postgresql by gvSIG
 */

public class DbUnsaturatedStressManage extends javax.swing.JPanel implements IWindow{
	private JScrollPane jScrollPane1;
	private JComboBox jComboBoxDB;
	private JButton apply;
	private JButton load;
	private JButton show;
	private JButton reset;
	private NewModel modello;
	private JComboBox jComboBoxUnsatur;
	private JTable table;
	Statement stmt = null;
	ResultSet rs = null;
	private WindowInfo viewInfo = null;
	private JButton btnCloseTool;
	private SpringLayout springLayout;
	
	public DbUnsaturatedStressManage() {
		super();		    
		initGUI();	
	}

	private void initGUI() {
		ComboBoxModel jComboBoxLayersModel = new DefaultComboBoxModel(Utils.getDbase());
		springLayout = new SpringLayout();
		setLayout(springLayout);
		jComboBoxDB = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, jComboBoxDB, 22, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, jComboBoxDB, 32, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, jComboBoxDB, 44, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, jComboBoxDB, 204, SpringLayout.WEST, this);
		jComboBoxDB.setModel(jComboBoxLayersModel);
		jComboBoxDB.setSelectedIndex(0);
		jComboBoxDB.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent e) {
				setTable();
			}
		});
		this.add(jComboBoxDB);
		this.add(getAdd());
		this.add(getSave());
		this.add(getLoad());
		this.add(getReset());
		this.add(getComboBoxUnsatu());
		this.add(getJScrollPane1());
		this.add(getBtnCloseTool());
		this.setName(PluginServices.getText(this,"Modify_Unsaturated_Zone_Stress_Period"));
		this.setSize(685, 300);
		this.setVisible(true);

		JLabel db = new JLabel(PluginServices.getText(this,"GeoDB"));
		springLayout.putConstraint(SpringLayout.NORTH, db, 25, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, db, 218, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, db, 41, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, db, 279, SpringLayout.WEST, this);
		add(db);

		JLabel lblUnsatu = new JLabel(PluginServices.getText(this,"Unsaturated"));
		springLayout.putConstraint(SpringLayout.NORTH, lblUnsatu, 3, SpringLayout.NORTH, jComboBoxDB);
		springLayout.putConstraint(SpringLayout.WEST, lblUnsatu, 0, SpringLayout.WEST, getAdd());
		springLayout.putConstraint(SpringLayout.EAST, lblUnsatu, -10, SpringLayout.EAST, this);
		add(lblUnsatu);
	}

	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			//			model = new DefaultTableModel();
			modello = new NewModel();

			modello.addColumn("gid");
			modello.addColumn("row");
			modello.addColumn("col");

			table = new JTable(modello);
			table.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
			jScrollPane1 = new JScrollPane(table);
			springLayout.putConstraint(SpringLayout.WEST, getComboBoxUnsatu(), -172, SpringLayout.EAST, jScrollPane1);
			springLayout.putConstraint(SpringLayout.EAST, getComboBoxUnsatu(), 0, SpringLayout.EAST, jScrollPane1);
			springLayout.putConstraint(SpringLayout.EAST, jScrollPane1, -10, SpringLayout.WEST, getAdd());
			springLayout.putConstraint(SpringLayout.NORTH, getAdd(), -3, SpringLayout.NORTH, jScrollPane1);
			springLayout.putConstraint(SpringLayout.SOUTH, getReset(), 0, SpringLayout.SOUTH, jScrollPane1);
			springLayout.putConstraint(SpringLayout.NORTH, jScrollPane1, 68, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jScrollPane1, 32, SpringLayout.WEST, this);
			
		}
		return jScrollPane1;
	}

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Modify_Unsaturated_Zone_Stress_Period"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	private JButton getAdd() {
		if(show == null) {
			show = new JButton();
			springLayout.putConstraint(SpringLayout.WEST, show, -90, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.EAST, show, -10, SpringLayout.EAST, this);
			show.setText(PluginServices.getText(this,"Show"));
			show.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					
					try {
						
						String dbase = (String)jComboBoxDB.getSelectedItem();
						String dbUnsatur = (String)jComboBoxUnsatur.getSelectedItem();
						Connection conn = Utils.getConnectionToDatabase(dbase);
						String sql = "SELECT * FROM "+dbUnsatur+" ORDER BY gid";
//						Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+dbase,"postgres","postgres");
						stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);
						stmt.execute(sql);
						rs = stmt.getResultSet();

						ResultSetMetaData rsmd = rs.getMetaData();

						String[] fields = new String[rsmd.getColumnCount()];

						int j = 0;
						for (int i = 1; i < fields.length; i++)
						{
							if (rsmd.getColumnName(i).contains("sp"))
							{
								modello.addColumn(rsmd.getColumnName(i));																	
							}
							j++;
						}

						Object[] fieldsDef = new Object [modello.getColumnCount()];

						while (rs.next()) {
							Integer id = rs.getInt(1);
							fieldsDef[0] = id;
							Integer row = rs.getInt(modello.getColumnName(1));
							fieldsDef[1] = row;
							Integer col = rs.getInt(modello.getColumnName(2));
							fieldsDef[2] = col;


							for (int i = 3; i < fieldsDef.length; i++)
							{
								Double sp = rs.getDouble(modello.getColumnName(i));
								fieldsDef[i]=sp;
							}

							modello.addRow(fieldsDef);

						}

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					show.setEnabled(false);
					load.setEnabled(true);
				}
			});

		}
		return show;
	}

	private JButton getSave() {
		if(apply == null) {
			apply = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, apply, 6, SpringLayout.SOUTH, getAdd());
			springLayout.putConstraint(SpringLayout.WEST, apply, -90, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.EAST, apply, -10, SpringLayout.EAST, this);
			apply.setText(PluginServices.getText(this,"Apply"));
			apply.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
				 
					new Thread() {
						@Override
						public void run() {	
							ProgressTask test = new ProgressTask();
							test.setMax(table.getRowCount());
							Connection conn = null;
					try {
						
						String dbase = (String)jComboBoxDB.getSelectedItem();
						String dbUnsatur = (String)jComboBoxUnsatur.getSelectedItem();
						conn = Utils.getConnectionToDatabase(dbase);
//						ResultSet rs = null;
//						Statement stmt=conn.createStatement(ResultSet.CONCUR_UPDATABLE, ResultSet.TYPE_SCROLL_SENSITIVE);
																
						String[] fields = new String[table.getColumnCount()];
						
						for (int i = 0; i < table.getRowCount(); i++) {
							for (int j = 1; j < fields.length; j++)
							{                    			
								String campo = table.getColumnName(j);
								PreparedStatement ps = conn.prepareStatement("UPDATE "+dbUnsatur+" SET "+ campo+"=? WHERE gid="+i+"+1");
								ps.setDouble(1, Double.parseDouble(table.getValueAt(i, j).toString()));
								ps.execute();
								
							}
							
							test.setValue(i);
							test.repaint();
							
						}
						JOptionPane.showMessageDialog(null,
								PluginServices.getText(this, "Run_successfull"),
								"Message",
								JOptionPane.INFORMATION_MESSAGE);

					} 

					catch (SQLException e) {
						JOptionPane.showMessageDialog(null,
								e.getLocalizedMessage(),
								PluginServices.getText(this, "Error"),
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
					finally{
						try { if(conn!=null)conn.close(); } catch (SQLException e) {/* IGNORED */}
						test.dispose();
					}
					
				}

			}.start();
					
				}			
			});

		}
		return apply;
	}


	private JButton getLoad() {
		if(load == null) {
			load = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, load, 10, SpringLayout.SOUTH, getSave());
			springLayout.putConstraint(SpringLayout.WEST, load, -90, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.EAST, load, -10, SpringLayout.EAST, this);
			load.setText(PluginServices.getText(this,"Load"));
			load.setEnabled(false);
			load.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.addChoosableFileFilter(new XlsFilter());
					int n = fileChooser.showOpenDialog(new JPanel());

					if (n == JFileChooser.APPROVE_OPTION)
					{
						fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						File inputWorkbook = fileChooser.getSelectedFile();

						Workbook w;

						try {
							w = Workbook.getWorkbook(inputWorkbook);
							// Get the first sheet
							Sheet sheet = w.getSheet(0);
							// Loop over first 10 column and lines

							Object[] fieldsDef = new Object [modello.getColumnCount()-1];

							for (int i = 1; i < sheet.getRows(); i++) {
								for (int j = 1; j < fieldsDef.length; j++)
								{ 
									Cell a = sheet.getCell(j,i);
									String stringa1 = a.getContents();
									modello.setValueAt(stringa1, i-1, j);

								}					



								//  								Object[] stressxls = {stringa2.replace(",", "."), stringa3.replace(",", "."), stringa4.replace(",", "."), stringa5};
								//  								modello.addRow(fieldsDef);

							}

						} catch (BiffException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}


					}

				}
			});

		}
		return load;
	}

	private JButton getReset() {
		if(reset == null) {
			reset = new JButton();
			springLayout.putConstraint(SpringLayout.WEST, reset, -90, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.EAST, reset, -10, SpringLayout.EAST, this);
			reset.setText(PluginServices.getText(this,"Reset"));
			reset.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {			    	  

					System.out.println(table.getColumnCount());

					modello = new NewModel();

					modello.addColumn("Id");
					modello.addColumn("row");
					modello.addColumn("col");
					table.setModel(modello);

					show.setEnabled(true);
					load.setEnabled(false);
				}

			});

		}
		return reset;
	}

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(DbUnsaturatedStressManage.this);
						}
					});
			springLayout.putConstraint(SpringLayout.SOUTH, getJScrollPane1(), -10, SpringLayout.NORTH, btnCloseTool);
			springLayout.putConstraint(SpringLayout.WEST, btnCloseTool, 32, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, btnCloseTool, -10, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, btnCloseTool, 112, SpringLayout.WEST, this);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private JComboBox getComboBoxUnsatu() {
		if(jComboBoxUnsatur == null) {
			ComboBoxModel jComboModelTable = 
				new DefaultComboBoxModel();
			jComboBoxUnsatur = new JComboBox();
			springLayout.putConstraint(SpringLayout.NORTH, jComboBoxUnsatur, 0, SpringLayout.NORTH, jComboBoxDB);
			springLayout.putConstraint(SpringLayout.SOUTH, jComboBoxUnsatur, 44, SpringLayout.NORTH, this);
			jComboBoxUnsatur.setModel(jComboModelTable);

		}
		return jComboBoxUnsatur;
	}
	
	protected void setTable() {
		final ComboBoxModel jComboBoxFieldModelTable = new DefaultComboBoxModel(Utils.getTableUnsatur((String)jComboBoxDB.getSelectedItem()));
		jComboBoxUnsatur.setModel(jComboBoxFieldModelTable);

	}
	
	public class NewModel extends DefaultTableModel {
		@Override
		public boolean isCellEditable(int row, int col) {
			return (col != 0);
		}
	}

	abstract class SuffixAwareFilter extends javax.swing.filechooser.FileFilter {
		public String getSuffix(File f) {
			String s = f.getPath(), suffix = null;
			int i = s.lastIndexOf('.');

			if(i > 0 && i < s.length() - 1)
				suffix = s.substring(i+1).toLowerCase();

			return suffix;
		}
		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}
	}

	class XlsFilter extends SuffixAwareFilter {
		@Override
		public boolean accept(File f) {
			String suffix = getSuffix(f);

			if(suffix != null)
				return super.accept(f) || suffix.equals("xls");

			return false;
		}
		@Override
		public String getDescription() {
			return "Formato xls(*.xls)";
		}
	}

}
