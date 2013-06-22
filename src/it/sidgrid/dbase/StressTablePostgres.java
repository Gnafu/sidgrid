package it.sidgrid.dbase;

import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

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
 * GUI to create simulation time. Insert into stressperiod table
 */

public class StressTablePostgres extends javax.swing.JPanel implements IWindow{
	private JComboBox jComboBox1;
	private AbstractAction applyAction;
	private AbstractAction loadAction;
	private JButton load;

	private JScrollPane jScrollPane1;

	private JButton apply;
	private JLabel jLabelDB;

	private JButton remove;
	private JButton add;

	private DefaultTableModel model;
	private JTable table;

	private WindowInfo viewInfo = null;
	private JButton btnCloseTool;
	private SpringLayout springLayout;

	/**
	 * Auto-generated main method to display this JFrame
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public StressTablePostgres() {
		super();		    
		initGUI();	
	}


	private void initGUI() {
		ComboBoxModel jComboBoxLayersModel = new DefaultComboBoxModel(Utils.getDbase());
		springLayout = new SpringLayout();
		setLayout(springLayout);
		jComboBox1 = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, jComboBox1, 23, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, jComboBox1, -280, SpringLayout.EAST, this);
		this.add(jComboBox1);
		this.add(getJScrollPane1());				
		this.add(getAdd());
		this.add(getRemove());								
		this.add(getJLabelDB());
		this.add(getApply());
		this.add(getLoad());
		jComboBox1.setModel(jComboBoxLayersModel);
		if(jComboBox1.getItemCount()>0)
			jComboBox1.setSelectedIndex(0);
		this.add(getBtnCloseTool());

		this.setSize(690, 300);
		this.setVisible(true);

	}


	private AbstractAction getApplyAction() {
		if(applyAction == null) {
			applyAction = new AbstractAction(PluginServices.getText(this,"Apply"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PluginServices.getMDIManager().closeWindow(StressTablePostgres.this);
					try {
						String dbase = (String)jComboBox1.getSelectedItem();
						Connection conn = Utils.getConnectionToDatabase(dbase);
						PreparedStatement ps = conn.prepareStatement("INSERT INTO stressperiod(id, lenght, time_steps, multiplier, state)VALUES (?, ?, ?, ?, ?)");

						for (int i=0; i < table.getRowCount(); i++)
						{
							ps.setInt(1, i+1);
							ps.setDouble(2, Double.parseDouble(table.getValueAt(i, 1).toString()));
							ps.setDouble(3, Integer.parseInt(table.getValueAt(i, 2).toString()));
							ps.setDouble(4, Double.parseDouble(table.getValueAt(i, 3).toString()));
							ps.setString(5, table.getValueAt(i, 4).toString());
							ps.execute();
						}
						conn.close();
						
//						ProjectExtension ext3 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
//						ProjectTable time = (ProjectTable) ext3.getProject().getProjectDocumentByName("stressperiod", ProjectTableFactory.registerName);
//						time.getModelo().getRecordset().reload();
						
						ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
                        ProjectTable pt = (ProjectTable) ext.getProject().getProjectDocumentByName("stressperiod", ProjectTableFactory.registerName);
                        pt.setModel(pt.getModelo());
						
					} 

					catch (SQLException e) {
						JOptionPane.showMessageDialog(null, PluginServices.getText(null, "Error_The_Time_Table_is_not_empty"), "SQL Warnig", JOptionPane.WARNING_MESSAGE);
						
					}

				}

			};

		}

		return applyAction;
	}



	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			model = new DefaultTableModel();


			model.addColumn("ID");
			model.addColumn("Lenght");
			model.addColumn("Time Steps");
			model.addColumn("Multiplier");
			model.addColumn("State");

			//		    Object[] defaultdata = { new Double (0), new Integer(0), new Double (0), "st" };
			//		    model.addRow(defaultdata);

			table = new JTable(model);

			setUpSimulColumn(table.getColumnModel().getColumn(4));

			jScrollPane1 = new JScrollPane(table);
			springLayout.putConstraint(SpringLayout.NORTH, jScrollPane1, 51, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jComboBox1, -6, SpringLayout.NORTH, jScrollPane1);
			springLayout.putConstraint(SpringLayout.EAST, jComboBox1, 0, SpringLayout.EAST, jScrollPane1);
			springLayout.putConstraint(SpringLayout.WEST, jScrollPane1, 32, SpringLayout.WEST, this);

		}
		return jScrollPane1;
	}



	private JButton getAdd() {
		if(add == null) {
			add = new JButton();
			springLayout.putConstraint(SpringLayout.WEST, add, -90, SpringLayout.EAST, this);
			add.setText(PluginServices.getText(this,"Add"));
			add.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					Object[] stressdata = {new Integer(model.getRowCount()+1), new Double (0), new Integer(0), new Double (0), "SS" };
					model.addRow(stressdata);
				}
			});
		}
		return add;
	}

	private JButton getRemove() {
		if(remove == null) {
			remove = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, remove, 10, SpringLayout.SOUTH, getAdd());
			springLayout.putConstraint(SpringLayout.WEST, remove, -90, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.EAST, remove, -10, SpringLayout.EAST, this);
			remove.setText(PluginServices.getText(this,"Remove"));
			remove.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					
					if(table.getSelectedRow() != -1){
						model.removeRow(table.getSelectedRow());
					}
					for(int i=0;i<model.getRowCount();i++)
						model.setValueAt(i+1, i, 0);
					
				}
			});
		}
		return remove;
	}



	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Define_stress_period"));

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
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
			springLayout.putConstraint(SpringLayout.WEST, jLabelDB, -60, SpringLayout.WEST, jComboBox1);
			springLayout.putConstraint(SpringLayout.SOUTH, jLabelDB, 42, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jLabelDB, -6, SpringLayout.WEST, jComboBox1);
			jLabelDB.setText(PluginServices.getText(this,"GeoDB"));
		}
		return jLabelDB;
	}

	private JButton getApply() {
		if(apply == null) {
			apply = new JButton();
			springLayout.putConstraint(SpringLayout.EAST, getJScrollPane1(), -9, SpringLayout.WEST, apply);
			springLayout.putConstraint(SpringLayout.EAST, getAdd(), 0, SpringLayout.EAST, apply);
			springLayout.putConstraint(SpringLayout.WEST, apply, -90, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, apply, 0, SpringLayout.SOUTH, getJScrollPane1());
			springLayout.putConstraint(SpringLayout.EAST, apply, -10, SpringLayout.EAST, this);
			apply.setText(PluginServices.getText(this,"Apply"));
			apply.setAction(getApplyAction());
		}
		return apply;
	}

	private JButton getLoad() {
		if(load == null) {
			load = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, getAdd(), 10, SpringLayout.SOUTH, load);
			springLayout.putConstraint(SpringLayout.WEST, load, -90, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.NORTH, load, -3, SpringLayout.NORTH, getJScrollPane1());
			springLayout.putConstraint(SpringLayout.EAST, load, 0, SpringLayout.EAST, getApply());
			load.setAction(getLoadAction());
		}
		return load;
	}

	private AbstractAction getLoadAction() {
		if(loadAction == null) {
			loadAction = new AbstractAction(PluginServices.getText(this,"Load"), null) {
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

							for (int i = 1; i < sheet.getRows(); i++) {

								Cell a2 = sheet.getCell(1,i);
								Cell a3 = sheet.getCell(2,i);
								Cell a4 = sheet.getCell(3,i);
								Cell a5 = sheet.getCell(4,i);


								String stringa2 = a2.getContents();
								String stringa3 = a3.getContents();
								String stringa4 = a4.getContents();
								String stringa5 = a5.getContents();

								Object[] stressxls = {i, stringa2.replace(",", "."), stringa3.replace(",", "."), stringa4.replace(",", "."), stringa5};
								model.addRow(stressxls);

							}

						} catch (BiffException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}


					}


				}
			};
		}
		return loadAction;
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(StressTablePostgres.this);
						}
					});
			springLayout.putConstraint(SpringLayout.SOUTH, getJScrollPane1(), -6, SpringLayout.NORTH, btnCloseTool);
			springLayout.putConstraint(SpringLayout.WEST, btnCloseTool, 32, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, btnCloseTool, 112, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, btnCloseTool, -10, SpringLayout.SOUTH, this);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
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






