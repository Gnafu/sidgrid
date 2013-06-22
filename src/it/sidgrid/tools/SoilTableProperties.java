package it.sidgrid.tools;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import it.sidgrid.utils.ColumnHeaderToolTips;
import it.sidgrid.utils.Utils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.unex.sextante.gvsig.core.gvTable;
import javax.swing.JLabel;
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
 * Create the soil table layers properties to use Variably Saturated Flow
 * VSF
 * the table will be archived into geodb
 * 
 */
public class SoilTableProperties extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private JScrollPane ScrollPanelSoilTable;
	private DefaultTableModel model;
	private JTable table;
	private JComboBox jComboBoxDataBase;
	private final Action apply = new SwingAction();
	private JTextPane warningMessage;
	private JButton btnApply;
	private JButton btnCloseTool;
	private JComboBox jComboBoxLPF;
	private JLabel jLabelLPF;
	private Action action;
	private SpringLayout springLayout;
	
	public SoilTableProperties(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();		
	}
	
	private void initGUI() {
		
		//this.setName(PluginServices.getText(this,"Create_soil_table"));
		this.setSize(665, 256);
		this.setVisible(true);
		springLayout = new SpringLayout();
		setLayout(springLayout);
		this.add(getComboBoxLPF());
		this.add(getJLabelLPF());
		this.add(getBtnApply());
//		String lpf_table = "lpf";
//		ProjectExtension ext1 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
//		final ProjectTable layertable = (ProjectTable) ext1.getProject().getProjectDocumentByName(lpf_table, ProjectTableFactory.registerName);
//		if(layertable == null)
//			this.add(getWarningMessagePane());
//		else
//		{
			this.add(getJScrollPaneSoil());
//			this.add(getBtnApply());
//		}

		this.add(getJComboBoxDataBase());
		
		JLabel lblGeodb = new JLabel(PluginServices.getText(this,"GeoDB"));
		springLayout.putConstraint(SpringLayout.NORTH, lblGeodb, 33, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblGeodb, 17, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblGeodb, 49, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, lblGeodb, 149, SpringLayout.WEST, this);
		add(lblGeodb);
				
		add(getBtnCloseTool());
		
		JButton btnNewButton = new JButton("");
		springLayout.putConstraint(SpringLayout.EAST, getJScrollPaneSoil(), -16, SpringLayout.WEST, btnNewButton);
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 63, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton, -94, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton, 92, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton, -14, SpringLayout.EAST, this);
		btnNewButton.setAction(getShowAction());
		add(btnNewButton);

		
		springLayout.putConstraint(SpringLayout.NORTH, btnCloseTool, -43, SpringLayout.NORTH, getBtnApply());
		springLayout.putConstraint(SpringLayout.WEST, btnCloseTool, -94, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnCloseTool, -14, SpringLayout.NORTH, getBtnApply());
		springLayout.putConstraint(SpringLayout.EAST, btnCloseTool, -14, SpringLayout.EAST, this);

	}
	
	private JComboBox getComboBoxLPF() {
		if(jComboBoxLPF == null) {
			ComboBoxModel jComboModelTable = 
				new DefaultComboBoxModel();
			jComboBoxLPF = new JComboBox();
			springLayout.putConstraint(SpringLayout.NORTH, jComboBoxLPF, 29, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jComboBoxLPF, 397, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jComboBoxLPF, 56, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jComboBoxLPF, 549, SpringLayout.WEST, this);
			jComboBoxLPF.setModel(jComboModelTable);

		}
		return jComboBoxLPF;
	}
	
	private JLabel getJLabelLPF() {
		if(jLabelLPF == null) {
			jLabelLPF = new JLabel();
			springLayout.putConstraint(SpringLayout.NORTH, jLabelLPF, 28, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jLabelLPF, 318, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jLabelLPF, 55, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jLabelLPF, 470, SpringLayout.WEST, this);
			jLabelLPF.setText(PluginServices.getText(this,"LPF_table"));
		}
		return jLabelLPF;
	}
	
	protected void setTable() {
		final ComboBoxModel jComboBoxFieldModelTable = new DefaultComboBoxModel(Utils.getTableLPF((String)jComboBoxDataBase.getSelectedItem()));
		jComboBoxLPF.setModel(jComboBoxFieldModelTable);

	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(SoilTableProperties.this);
						}
					});
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private JButton getBtnApply() {
		if(btnApply == null){
			btnApply =new JButton(PluginServices.getText(this, "Apply_window"));
			springLayout.putConstraint(SpringLayout.NORTH, btnApply, -55, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.WEST, btnApply, -94, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, btnApply, -26, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, btnApply, -14, SpringLayout.EAST, this);
			btnApply.setAction(apply);
		}
		return btnApply;
	}

	/*	Panel with table to define ALPHA, VGN, RSAT and EFFP soil parameters value*/
	private JScrollPane getJScrollPaneSoil() {
		if(ScrollPanelSoilTable == null) {
						
			model = new DefaultTableModel();
			model.addColumn("Model layer");	// string
			model.addColumn("soil type");	// string
			model.addColumn("alpha");	// double
			model.addColumn("vgn");	// double
			model.addColumn("rsat");	// double
			model.addColumn("effp");	// double
			model.addColumn("isc");	// integer
			
			
			model.addTableModelListener(new TableModelListener() {

				@Override
				public void tableChanged(TableModelEvent e) {
					if(e.getType() == TableModelEvent.UPDATE && e.getColumn() == 1) {
						//TableModel source = (TableModel)e.getSource();
						int row = e.getFirstRow();
						int col = e.getColumn();
						if(model.getValueAt(row, col) == "sand")
						{                    	                    
							model.setValueAt(14.500, row, col+1);
							model.setValueAt(2.680, row, col+2);
							model.setValueAt(0.045, row, col+3);
							model.setValueAt(0.0430, row, col+4);
						}
						if(model.getValueAt(row, col) == "loamy sand")
						{
							model.setValueAt(12.400, row, col+1);
							model.setValueAt(2.280, row, col+2);
							model.setValueAt(0.057, row, col+3);
							model.setValueAt(0.410, row, col+4);
						}
						if(model.getValueAt(row, col) == "sandy loam")
						{
							model.setValueAt(7.500, row, col+1);
							model.setValueAt(1.890, row, col+2);
							model.setValueAt(0.065, row, col+3);
							model.setValueAt(0.410, row, col+4);
						}
						if(model.getValueAt(row, col) == "loam")
						{
							model.setValueAt(3.600, row, col+1);
							model.setValueAt(1.560, row, col+2);
							model.setValueAt(0.078, row, col+3);
							model.setValueAt(0.430, row, col+4);
						}
						if(model.getValueAt(row, col) == "silt")
						{
							model.setValueAt(1.600, row, col+1);
							model.setValueAt(1.370, row, col+2);
							model.setValueAt(0.034, row, col+3);
							model.setValueAt(0.460, row, col+4);
						}
						if(model.getValueAt(row, col) == "silt loam")
						{
							model.setValueAt(2.000, row, col+1);
							model.setValueAt(1.410, row, col+2);
							model.setValueAt(0.067, row, col+3);
							model.setValueAt(0.450, row, col+4);
						}
						if(model.getValueAt(row, col) == "sandy clay loam")
						{
							model.setValueAt(5.900, row, col+1);
							model.setValueAt(1.480, row, col+2);
							model.setValueAt(0.100, row, col+3);
							model.setValueAt(0.390, row, col+4);
						}
						if(model.getValueAt(row, col) == "clay loam")
						{
							model.setValueAt(1.900, row, col+1);
							model.setValueAt(1.310, row, col+2);
							model.setValueAt(0.095, row, col+3);
							model.setValueAt(0.410, row, col+4);
						}
						if(model.getValueAt(row, col) == "silty clay loam")
						{
							model.setValueAt(1.000, row, col+1);
							model.setValueAt(1.230, row, col+2);
							model.setValueAt(0.089, row, col+3);
							model.setValueAt(0.430, row, col+4);
						}
						if(model.getValueAt(row, col) == "sandy clay")
						{
							model.setValueAt(2.700, row, col+1);
							model.setValueAt(1.230, row, col+2);
							model.setValueAt(0.100, row, col+3);
							model.setValueAt(0.380, row, col+4);
						}
						if(model.getValueAt(row, col) == "silty clay")
						{
							model.setValueAt(0.500, row, col+1);
							model.setValueAt(1.090, row, col+2);
							model.setValueAt(0.070, row, col+3);
							model.setValueAt(0.360, row, col+4);
						}
						if(model.getValueAt(row, col) == "clay")
						{
							model.setValueAt(0.800, row, col+1);
							model.setValueAt(1.090, row, col+2);
							model.setValueAt(0.068, row, col+3);
							model.setValueAt(0.380, row, col+4);
						}
					}
				}
			}
			);
			
			
			

			
			table = new JTable(model);
			setUpTypeColumn(table, table.getColumnModel().getColumn(1));
			setUpGenuchtenPar(table, table.getColumnModel().getColumn(6));
			table.setAutoResizeMode (JTable.AUTO_RESIZE_ALL_COLUMNS);
			
			JTableHeader header = table.getTableHeader();

			ColumnHeaderToolTips tips = new ColumnHeaderToolTips();
			tips.setToolTip(table.getColumnModel().getColumn(0), "Model layers");
			tips.setToolTip(table.getColumnModel().getColumn(1), "Soil layer type");
			tips.setToolTip(table.getColumnModel().getColumn(2), "Alpha value - change unit it if you don't use meter");
			tips.setToolTip(table.getColumnModel().getColumn(3), "VGN value for VSF");
			tips.setToolTip(table.getColumnModel().getColumn(4), "RSAT value for VSF");
			tips.setToolTip(table.getColumnModel().getColumn(5), "EFFP value for VSF");
			tips.setToolTip(table.getColumnModel().getColumn(6), "Singular or dual set parameters in van Genuchten");
			
			header.addMouseMotionListener(tips);
			
			ScrollPanelSoilTable = new JScrollPane(table);
			springLayout.putConstraint(SpringLayout.NORTH, ScrollPanelSoilTable, 8, SpringLayout.SOUTH, getComboBoxLPF());
			springLayout.putConstraint(SpringLayout.WEST, ScrollPanelSoilTable, 17, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, ScrollPanelSoilTable, -16, SpringLayout.SOUTH, this);
			
			}		
		return ScrollPanelSoilTable;
	}

	
	
	public JTextPane getWarningMessagePane() {
		if (warningMessage == null) {
		
			warningMessage = new JTextPane();
			warningMessage.setText(PluginServices.getText(this, "lpf_table_missing"));
			warningMessage.setBounds(17, 67, 532, 141);
		}
		return warningMessage;
		
	}

	public void setUpTypeColumn(JTable table, TableColumn typeColumn) {
		//Set up the editor for the simulation cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("sand");
		comboBox.addItem("loamy sand");
		comboBox.addItem("sandy loam");
		comboBox.addItem("loam");
		comboBox.addItem("silt");
		comboBox.addItem("silt loam");
		comboBox.addItem("sandy clay loam");
		comboBox.addItem("clay loam");
		comboBox.addItem("silty clay loam");
		comboBox.addItem("sandy clay");
		comboBox.addItem("silty clay");
		comboBox.addItem("clay");
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
  
		//Set up tool tips for the soil type.
		DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		typeColumn.setCellRenderer(renderer);
	}
	
	public void setUpGenuchtenPar(JTable table, TableColumn typeColumn) {
		//Set up the editor for the simulation cells.
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("1");
		comboBox.addItem("2");
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
  
		//Set up tool tips for the soil type.
		DefaultTableCellRenderer renderer =
			new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		typeColumn.setCellRenderer(renderer);
	}
	
	/*	Select geodb for stream flow table*/
	private JComboBox getJComboBoxDataBase() {
		if(jComboBoxDataBase == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getDbase());
			jComboBoxDataBase = new JComboBox();
			springLayout.putConstraint(SpringLayout.NORTH, jComboBoxDataBase, 29, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jComboBoxDataBase, 119, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jComboBoxDataBase, 56, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jComboBoxDataBase, 276, SpringLayout.WEST, this);
			jComboBoxDataBase.setModel(jComboBoxTableModel);
			jComboBoxDataBase.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					setTable();
				}
			});
		}
		return jComboBoxDataBase;
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Apply"));
			putValue(SHORT_DESCRIPTION, "Apply soil type");
		}
	@Override
	public void actionPerformed(ActionEvent e) {

		
		final String dbase = (String)jComboBoxDataBase.getSelectedItem();
		
		PluginServices.getMDIManager().closeWindow(SoilTableProperties.this);
		
		new Thread() {
			@Override
			public void run() {
				
				
				try {

					//Connection connSelect = Utils.getConnectionToDatabase(dbase);
			        //Statement st = connSelect.createStatement();
			        
			        /*Select VSF Soil Table****************/
//			        ResultSet rs = st.executeQuery("SELECT count (id) FROM soiltype");
//			        int s = 0;
//			        while (rs.next()) {
//			            // Get the data from the row using the column index
//			            s = rs.getInt(1);
//			            
//			        }				       
//			        connSelect.close();
			       
			        /*Insert new soil type for model layers*******************/
			        Connection conn = Utils.getConnectionToDatabase(dbase);
			        PreparedStatement ps = conn.prepareStatement("INSERT INTO soiltype(id, model_layers, soil_lay_type, alpha, vgn, rsat, effp, isc)" +
			        		"VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			        for (int i=0; i < table.getRowCount(); i++)
					{		
			        	
						ps.setInt(1, (i+1)); // id
						
						ps.setString(2, table.getValueAt(i, 0).toString()); // model layer
						ps.setString(3, table.getValueAt(i, 1).toString()); // soil type
						ps.setDouble(4, Double.parseDouble(table.getValueAt(i, 2).toString())); // alpha
						ps.setDouble(5, Double.parseDouble(table.getValueAt(i, 3).toString())); //vgn
						ps.setDouble(6, Double.parseDouble(table.getValueAt(i, 4).toString())); // rsat
						ps.setDouble(7, Double.parseDouble(table.getValueAt(i, 5).toString())); // effp						
						ps.setInt(8, Integer.parseInt(table.getValueAt(i, 6).toString())); // isc						
						
						ps.execute();
						
					
					}
					conn.close();
					ProjectExtension ext3 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					ProjectTable soil = (ProjectTable) ext3.getProject().getProjectDocumentByName("soiltype", ProjectTableFactory.registerName);
					soil.getModelo().getRecordset().reload();
        
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ReloadDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ReadDriverException e) {
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
}
	
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Soil_type"));

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	private class Show_table extends AbstractAction {
		public Show_table() {
			putValue(NAME, PluginServices.getText(this,"Show"));

		}
		public void actionPerformed(ActionEvent e) {
			String lpf_table = (String)jComboBoxLPF.getSelectedItem();
			ProjectExtension ext1 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
			final ProjectTable layertable = (ProjectTable) ext1.getProject().getProjectDocumentByName(lpf_table, ProjectTableFactory.registerName);
			final gvTable tableSoil = new gvTable();
			tableSoil.create(layertable);
			int records = (int) tableSoil.getRecordCount();
			
			
			for (int i=0; i < records; i++){
				Object[] soil = {
						new String("layer_"+(i+1)), new String(""), new Double(0.000), new Double(0.0), new Double(0.0), new Double(0.0), new Integer(1)
				};
				model.addRow(soil);
			}
		}
	}
	private Action getShowAction() {
		if (action == null) {
			action = new Show_table();
		}
		return action;
	}
}
