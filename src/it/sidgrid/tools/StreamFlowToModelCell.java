package it.sidgrid.tools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import it.sidgrid.sextante.tools.StreamPolylineToModelGridAlgorithm;
import it.sidgrid.utils.Utils;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import com.hardcode.driverManager.DriverLoadException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

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
import es.unex.sextante.gvsig.core.gvTable;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.Action;

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
 * Create Stream Segment and dataset 5, 6a, 6b, 6c with stress period table
 * Table will be write to database
 * This tool works for one segment
 * 
 */


public class StreamFlowToModelCell extends javax.swing.JPanel implements IWindow{
	private JScrollPane jScrollPane1;
	private WindowInfo viewInfo = null;
	private DefaultTableModel model;
	private JTable table;
	private MapControl mapCtrl;
	private JComboBox jComboVectParams;
	private JComboBox jComboBoxDataBase;
	private JLabel lblStressPeriodTable;
	private JFormattedTextField formattedLayerNumber;
	private JFormattedTextField formattedSegment;
	private JLabel lblLayerActiveNumber;
	private JLabel lblSegmentNumber;
	private final Action apply = new SwingAction();
	FLayer shape;
	private JTextPane warningMessage;
	private JButton btnApply;
	private JButton btnCloseTool;	
	
	public StreamFlowToModelCell(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
			  
	}
	private void initGUI() {
		springLayout = new SpringLayout();
		setLayout(springLayout);
			
		this.add(getJComboVectParams());
		this.add(getJComboBoxDataBase());
		this.setName(PluginServices.getText(this,"Stream_to_model_cells"));
		this.setSize(685, 321);
		this.setVisible(true);

		String stressp = "stressperiod";
		ProjectExtension ext1 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		final ProjectTable stresstable = (ProjectTable) ext1.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);

		this.add(getJScrollPane1());
		this.add(getBtnApply());

		if(stresstable == null){
			this.add(getWarningMessagePane());
			getBtnApply().setEnabled(false);
		}
		
		JLabel lblStreamSegmentShape = new JLabel(PluginServices.getText(this,"Stream_segment_shape"));
		springLayout.putConstraint(SpringLayout.NORTH, lblStreamSegmentShape, 41, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblStreamSegmentShape, 186, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblStreamSegmentShape, 57, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, lblStreamSegmentShape, 369, SpringLayout.WEST, this);
		add(lblStreamSegmentShape);
		add(getLblStressPeriodTable());
		
		formattedLayerNumber = new JFormattedTextField(Integer.class);
		springLayout.putConstraint(SpringLayout.NORTH, formattedLayerNumber, 37, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, formattedLayerNumber, 381, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, formattedLayerNumber, 65, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, formattedLayerNumber, 452, SpringLayout.WEST, this);
		formattedLayerNumber.setText("0");
		add(formattedLayerNumber);
		formattedLayerNumber.setColumns(10);
		
		formattedSegment = new JFormattedTextField(Integer.class);
		springLayout.putConstraint(SpringLayout.NORTH, formattedSegment, 63, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, formattedSegment, 381, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, formattedSegment, 91, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, formattedSegment, 452, SpringLayout.WEST, this);
		formattedSegment.setText("0");
		add(formattedSegment);
		formattedSegment.setColumns(10);
		add(getLblLayerActiveNumber());
		add(getLblSegmentNumber());
		add(getBtnCloseTool());
	}
	
	public JTextPane getWarningMessagePane() {
		if (warningMessage == null) {
		
			warningMessage = new JTextPane();
			warningMessage.setText(PluginServices.getText(this, "stressperiod_table_missing"));
			warningMessage.setBounds(17, 104, 649, 141);
		}
		return warningMessage;
		
	}
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Stream_flow_segment"));

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
							PluginServices.getMDIManager().closeWindow(StreamFlowToModelCell.this);
						}
					});
			springLayout.putConstraint(SpringLayout.NORTH, btnCloseTool, -45, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.WEST, btnCloseTool, -96, SpringLayout.WEST, getBtnApply());
			springLayout.putConstraint(SpringLayout.SOUTH, btnCloseTool, -16, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, btnCloseTool, -16, SpringLayout.WEST, getBtnApply());
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private JComboBox getJComboVectParams() {
		if(jComboVectParams == null) {
			ComboBoxModel jComboVectParamsModel = 
				new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboVectParams = new JComboBox();
			springLayout.putConstraint(SpringLayout.NORTH, jComboVectParams, 37, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jComboVectParams, 17, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jComboVectParams, 64, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jComboVectParams, 174, SpringLayout.WEST, this);
			jComboVectParams.setModel(jComboVectParamsModel);

		}
		return jComboVectParams;
	}
	
	/*	Select geodb for stream flow table*/
	private JComboBox getJComboBoxDataBase() {
		if(jComboBoxDataBase == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getDbase());
			jComboBoxDataBase = new JComboBox();
			springLayout.putConstraint(SpringLayout.NORTH, jComboBoxDataBase, 65, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jComboBoxDataBase, 17, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jComboBoxDataBase, 92, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jComboBoxDataBase, 174, SpringLayout.WEST, this);
			jComboBoxDataBase.setModel(jComboBoxTableModel);
		}
		return jComboBoxDataBase;
	}

	
/*	Panel with table to define dataset 5, 6a, 6b, 6c, 6d for each 
	stress period of sgment to create*/
	private JScrollPane getJScrollPane1() {
		if(jScrollPane1 == null) {
			
			
			model = new DefaultTableModel();
			model.addColumn("sp");	// int
			model.addColumn("nseg");	// int
			model.addColumn("icalc");	// int
			model.addColumn("outseg");	// int
			model.addColumn("iupseg");	// int
			model.addColumn("iprior");	// double
			model.addColumn("flow");	// double
			model.addColumn("runoff");	// double
			model.addColumn("etsw");	// double
			model.addColumn("pptsw");	// double
			model.addColumn("roughch");	// double
			model.addColumn("hcond1");	// double
			model.addColumn("thickm1");	// double
			model.addColumn("elevup");	// double
			model.addColumn("width1");	// double
			model.addColumn("thts1");	// double
			model.addColumn("thti1");	// double
			model.addColumn("eps1");	// double
			model.addColumn("uhc1");	// double
			model.addColumn("hcond2");	// double
			model.addColumn("thickm2");	// double
			model.addColumn("elevdn");	// double
			model.addColumn("width2");	// double
			model.addColumn("thts2");	// double
			model.addColumn("thti2");	// double
			model.addColumn("eps2");	// double
			model.addColumn("uhc2");	// double
			
			String stressp = "stressperiod";
			ProjectExtension ext1 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
			final ProjectTable stresstable = (ProjectTable) ext1.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
			if(stresstable == null)
			{
				// TODO: Questa condizione non dovrebbe verificarsi, questo metodo viene 
				// 		 chiamato solo se il controllo di esistenza della tabella è andato a buon fine
			}
			final gvTable tableStress = new gvTable();
			tableStress.create(stresstable);
			int records = (int) tableStress.getRecordCount();
			
			
			for (int i=0; i < records; i++){
				Object[] segment = {new Integer(0), new Integer (0), new Integer (0), new Integer (0), new Integer (0), new Double (0),new Double (0),
						new Double (0), new Double (0),new Double (0),new Double (0),new Double (0),new Double (0),new Double (0),
						new Double (0),new Double (0),new Double (0),new Double (0),new Double (0),new Double (0),new Double (0),
						new Double (0),new Double (0),new Double (0),new Double (0),new Double (0),new Double (0)
				};
				model.addRow(segment);
			}
			
			
			table = new JTable(model);
			table.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
			jScrollPane1 = new JScrollPane(table);
			springLayout.putConstraint(SpringLayout.NORTH, jScrollPane1, 8, SpringLayout.SOUTH, getJComboBoxDataBase());
			springLayout.putConstraint(SpringLayout.WEST, jScrollPane1, 17, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, jScrollPane1, -16, SpringLayout.EAST, this);
			
			}		
		return jScrollPane1;
	}
	private JLabel getLblStressPeriodTable() {
		if (lblStressPeriodTable == null) {
			lblStressPeriodTable = new JLabel(PluginServices.getText(this,"GeoDB"));
			springLayout.putConstraint(SpringLayout.NORTH, lblStressPeriodTable, 69, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, lblStressPeriodTable, 186, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, lblStressPeriodTable, 85, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, lblStressPeriodTable, 329, SpringLayout.WEST, this);
		}
		return lblStressPeriodTable;
	}
	private JLabel getLblLayerActiveNumber() {
		if (lblLayerActiveNumber == null) {
			lblLayerActiveNumber = new JLabel(PluginServices.getText(this,"Layer_number"));
			springLayout.putConstraint(SpringLayout.NORTH, lblLayerActiveNumber, 41, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, lblLayerActiveNumber, 476, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, lblLayerActiveNumber, 57, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, lblLayerActiveNumber, 633, SpringLayout.WEST, this);
		}
		return lblLayerActiveNumber;
	}
	private JLabel getLblSegmentNumber() {
		if (lblSegmentNumber == null) {
			lblSegmentNumber = new JLabel(PluginServices.getText(this,"Segment_number"));
			springLayout.putConstraint(SpringLayout.NORTH, lblSegmentNumber, 69, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, lblSegmentNumber, 475, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, lblSegmentNumber, 85, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, lblSegmentNumber, 604, SpringLayout.WEST, this);
		}
		return lblSegmentNumber;
	}
	
	
	private JButton getBtnApply() {
		if(btnApply == null){
			btnApply =new JButton(PluginServices.getText(this,"Apply"));
			springLayout.putConstraint(SpringLayout.SOUTH, getJScrollPane1(), -16, SpringLayout.NORTH, btnApply);
			springLayout.putConstraint(SpringLayout.NORTH, btnApply, -45, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.WEST, btnApply, -96, SpringLayout.EAST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, btnApply, -16, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, btnApply, -16, SpringLayout.EAST, this);
			btnApply.setAction(apply);
		}
		return btnApply;
	}



	/*Call Sextante GeoAlgorithm to calculate stream segment 
	and insert value into streamflow table */
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	public static AnalysisExtent m_AnalysisExtent = new AnalysisExtent();
	private SpringLayout springLayout;
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Apply"));
			putValue(SHORT_DESCRIPTION, "Apply stream flow segment");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			String line = (String)jComboVectParams.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect lineGeomtry = (FLyrVect)layers.getLayer(line);
			final gvVectorLayer layerLine = new gvVectorLayer();
			layerLine.create(lineGeomtry);
			
			String grid = "model_layer_1";
			FLayers Modellayers = mapCtrl.getMapContext().getLayers();										
			FLyrVect gridGeomtry = (FLyrVect)Modellayers.getLayer(grid);
			final gvVectorLayer layerGrid = new gvVectorLayer();
			layerGrid.create(gridGeomtry);
			
			/* TODO: da verificare l'effettivo utilizzo
			String stressp = "stressperiod";
			ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
			final ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
			final gvTable tableStress = new gvTable();
			tableStress.create(stresstable);
			*/
			final String dbase = (String)jComboBoxDataBase.getSelectedItem();
			
			PluginServices.getMDIManager().closeWindow(StreamFlowToModelCell.this);
			
			new Thread() {
				@Override
				public void run() {
					
					StreamPolylineToModelGridAlgorithm alg =  new StreamPolylineToModelGridAlgorithm();
					ITaskMonitor test_waitingpanel = new DefaultTaskMonitor("StreamPolylineToModelGridAlgorithm", true, null);
					try {
						
						
						VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
						
						
						int layer = Integer.parseInt(formattedLayerNumber.getText());
						int segment = Integer.parseInt(formattedSegment.getText());
						
						ParametersSet params = alg.getParameters();
						params.getParameter(StreamPolylineToModelGridAlgorithm.LINES).setParameterValue(layerLine);
						params.getParameter(StreamPolylineToModelGridAlgorithm.GRID).setParameterValue(layerGrid);
						params.getParameter(StreamPolylineToModelGridAlgorithm.LAYER).setParameterValue(layer);
						params.getParameter(StreamPolylineToModelGridAlgorithm.SEGMENT).setParameterValue(segment);
						OutputObjectsSet outputs = alg.getOutputObjects();
						Output out = outputs.getOutput(StreamPolylineToModelGridAlgorithm.RESULT);	
						out.setOutputChannel(new FileOutputChannel(".shp"));
						
//						alg.setAnalysisExtent(m_AnalysisExtent);	
						alg.execute(test_waitingpanel, m_OutputFactory);
						test_waitingpanel.close();
						String layerName = "stream_segment_"+segment;
						IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
						shape = LayerFactory.createLayer(layerName, driver, new File (".shp"), viewProj);							
						mapCtrl.getMapContext().getLayers().addLayer(shape);
						
						Connection connSelect = Utils.getConnectionToDatabase(dbase);
				        Statement st = connSelect.createStatement();
				        
				        /*Count record to append the new segment****************/
				        ResultSet rs = st.executeQuery("SELECT count (id) FROM streamflow");
				        int s = 0;
				        while (rs.next()) {
				            // Get the data from the row using the column index
				            s = rs.getInt(1);
				            
				        }				       
				        // TODO: Perchè chiudere e riaprire subito dopo?
				        connSelect.close();
				       
				        /*Insert new stress period and segment record to the streamflow table*******************/
				        Connection conn = Utils.getConnectionToDatabase(dbase);
				        PreparedStatement ps = conn.prepareStatement("INSERT INTO streamflow(id, sp, nseg, icalc, outseg, iupseg, iprior, flow, runoff, etsw, pptsw, roughch, hcond1, thickm1, elevup, width1, thts1, thti1, eps1, uhc1, hcond2, thickm2, elevdn, width2, thts2, thti2, eps2, uhc2)" +
				        		"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				        
				        for (int i=0, k = s+1; i < table.getRowCount(); i++, k++)
						{
				        	System.out.println("*****************"+k);
							ps.setInt(1, k);
							ps.setInt(2, Integer.parseInt(table.getValueAt(i, 0).toString()));
							ps.setInt(3, Integer.parseInt(table.getValueAt(i, 1).toString()));
							ps.setInt(4, Integer.parseInt(table.getValueAt(i, 2).toString()));
							ps.setInt(5, Integer.parseInt(table.getValueAt(i, 3).toString()));
							ps.setInt(6, Integer.parseInt(table.getValueAt(i, 4).toString()));
							ps.setDouble(7, Double.parseDouble(table.getValueAt(i, 5).toString()));
							ps.setDouble(8, Double.parseDouble(table.getValueAt(i, 6).toString()));
							ps.setDouble(9, Double.parseDouble(table.getValueAt(i, 7).toString()));
							ps.setDouble(10, Double.parseDouble(table.getValueAt(i, 8).toString()));
							ps.setDouble(11, Double.parseDouble(table.getValueAt(i, 9).toString()));
							ps.setDouble(12, Double.parseDouble(table.getValueAt(i, 10).toString()));
							ps.setDouble(13, Double.parseDouble(table.getValueAt(i, 11).toString()));
							ps.setDouble(14, Double.parseDouble(table.getValueAt(i, 12).toString()));
							ps.setDouble(15, Double.parseDouble(table.getValueAt(i, 13).toString()));
							ps.setDouble(16, Double.parseDouble(table.getValueAt(i, 14).toString()));
							ps.setDouble(17, Double.parseDouble(table.getValueAt(i, 15).toString()));
							ps.setDouble(18, Double.parseDouble(table.getValueAt(i, 16).toString()));
							ps.setDouble(19, Double.parseDouble(table.getValueAt(i, 17).toString()));
							ps.setDouble(20, Double.parseDouble(table.getValueAt(i, 18).toString()));
							ps.setDouble(21, Double.parseDouble(table.getValueAt(i, 19).toString()));
							ps.setDouble(22, Double.parseDouble(table.getValueAt(i, 20).toString()));
							ps.setDouble(23, Double.parseDouble(table.getValueAt(i, 21).toString()));
							ps.setDouble(24, Double.parseDouble(table.getValueAt(i, 22).toString()));
							ps.setDouble(25, Double.parseDouble(table.getValueAt(i, 23).toString()));
							ps.setDouble(26, Double.parseDouble(table.getValueAt(i, 24).toString()));
							ps.setDouble(27, Double.parseDouble(table.getValueAt(i, 25).toString()));
							ps.setDouble(28, Double.parseDouble(table.getValueAt(i, 26).toString()));
							ps.execute();
							
						}
						conn.close();
//						ProjectExtension ext3 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
//						ProjectTable streamflow = (ProjectTable) ext3.getProject().getProjectDocumentByName("streamflow", ProjectTableFactory.registerName);
//						streamflow.getModelo().getRecordset().reload();
				        
						ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
                        ProjectTable pt = (ProjectTable) ext.getProject().getProjectDocumentByName("streamflow", ProjectTableFactory.registerName);
                        pt.setModel(pt.getModelo());
						
				        
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
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					JOptionPane.showMessageDialog(null,
							PluginServices.getText(this, "Run_successfull"),
							"Avviso",
							JOptionPane.INFORMATION_MESSAGE);

				}

			}.start();
			
		}
	}
}
