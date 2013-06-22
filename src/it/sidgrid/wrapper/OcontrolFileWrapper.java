package it.sidgrid.wrapper;
import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.BasFileAlgorithm;
import it.sidgrid.sextante.wrapperTools.DisFileAlgorithm;
import it.sidgrid.sextante.wrapperTools.LpfFileAlgorithm;
import it.sidgrid.sextante.wrapperTools.OcFileAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.vividsolutions.jts.geom.Geometry;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.dataObjects.IFeature;
import es.unex.sextante.dataObjects.IFeatureIterator;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvTable;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.math.simpleStats.SimpleStats;

import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JFormattedTextField;
import javax.swing.JSeparator;
import java.awt.Color;

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
 * SID&GRID mandatory wrapper input file
 * BAS6 BasFileAlgorithm sextante algorithm
 * DIS	DisFileAlgorithm sextante algorithm
 * GEO
 * LPF	LpfFileAlgorithm sextante algorithm
 * OC	OcFileAlgorithm sextante algorithm
 * 
 * Menu: SG_Model_packages/Domain_parameters
 * Extension: it.sidgrid.wrapper.extensiones.WrapperExt
 * Command: bas
 */

public class OcontrolFileWrapper extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	// TODO: rendere statici i valori dell'interfaccia da mantenere tra le sessioni
	// Stored GUI values, MUST be initialized to default values.
	// panel
	private static String layerGriglia_value = null;
	private static String ModelLayer_value = null;
	private static boolean jCheckBoxConstant_status = false;
	// panel_2
	private static String jComboBoxTable_value = null;
	// panel_3
	private static String jComboBoxLpf_value = null;
	private static String hdry_value = "0.0";
	private static double formattedWetfct_value = 1.0;
	private static int formattedIHdwet_value = 1;
	private static int formattedIwetit_value = 1;
	private static boolean chckBoxCBC_status = false;
	// panel_4
	private static int formattedMxIterField_value = 20;
	private static int formattedInnerIterField_value = 10;
	private static int comboNpCondBox_index = 0;
	private static double formattedHcloseField_value = 0.001;
	private static double formattedRclosetField_value = 0.001;
	private static double formattedRelaxField_value = 1.0;
	private static int formattedNbPoltField_value = 1;
	private static int formattedIprPcgField_value = 999;
	private static String comboMutPcgBox_value = null;
	private static double formattedDumptField_value = 1;
		
	private AbstractAction actionRun;
	private JCheckBox jCheckBoxConstant;
	private JLabel jLabelTime;
	private JLabel jLabelDis;
	private JComboBox jComboBoxTable;
	private JComboBox jComboBoxLpf;
	private JLabel basLabel;
	private JLabel mainLabel;
	private JLabel jLabel1;
	private WindowInfo viewInfo = null;
	private JComboBox layerGriglia;
	private JComboBox Ch;
	private JButton jBtnRun;
	private JLabel field;
	private JLabel toplayer;
	private JComboBox ModelLayer;
	private JLabel lblLayerPropertiesFile;
	private JLabel lblLpfTable;
	private JFormattedTextField hdry;
	private JLabel lblHdryParameters;
	private JFormattedTextField formattedMxIterField;
	private JFormattedTextField formattedInnerIterField;
	private JComboBox comboNpCondBox;
	private JFormattedTextField formattedHcloseField;
	private JFormattedTextField formattedRclosetField;
	private JCheckBox chckBoxCBC;
	private JFormattedTextField formattedWetfct;
	private JFormattedTextField formattedIwetit;
	private JFormattedTextField formattedIHdwet;
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private JFormattedTextField formattedRelaxField;
	private JLabel lblRelax;
	private JFormattedTextField formattedNbPoltField;
	private JLabel lblNbpol;
	private JFormattedTextField formattedIprPcgField;
	private JLabel lblIprpcg;
	private JComboBox comboMutPcgBox;
	private JFormattedTextField formattedDumptField;
	private JLabel lblDump;
	private JLabel lblMutpcg;
	private JLabel lblIwetit;
	private JLabel lblIhdwet;
	private JButton btnCloseTool;
	
	public OcontrolFileWrapper(MapControl mc) {
		
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
        setLayout(null);
        
        this.setName(PluginServices.getText(this,"Fundamental_packages"));
		this.setSize(452, 368);
		this.setVisible(true);
		this.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(6, 6, 417, 288);
        add(tabbedPane);
        
        JPanel panel = new JPanel();
        tabbedPane.addTab("Global", null, panel, null);
        panel.setLayout(null);
        panel.add(getLayerGriglia());
        panel.add(getToplayer());
        panel.add(getModelLayer());
        panel.add(getJLabel1());
        panel.add(getMainLabel());
        panel.add(getJCheckBoxConstant());
        
        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Bas", null, panel_1, null);
        panel_1.setLayout(null);
        panel_1.add(getBasLabel());
        panel_1.add(getField());
        panel_1.add(getCh());
        
        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Dis", null, panel_2, null);
        panel_2.setLayout(null);
        panel_2.add(getJLabelDis());
        panel_2.add(getJLabelTime());
        panel_2.add(getJComboBoxTable());
        add(getJBtnRun());
        
        JPanel panel_3 = new JPanel();
        tabbedPane.addTab("Lpf", null, panel_3, null);
        panel_3.setLayout(null);
        panel_3.add(getLblLayerPropertiesFile());
        panel_3.add(getJComboBoxLpf());
        panel_3.add(getLblLpfTable());
        panel_3.add(getFormattedTextField());
        panel_3.add(getLblHdryParameters());
        panel_3.add(getFormattedTextField_Wetfct());
        panel_3.add(getFormattedTextField_Iwetit());
        panel_3.add(getFormattedTextField_IHdwet());
        chckBoxCBC = new JCheckBox("Write cell by cell budget");
		chckBoxCBC.setBounds(118, 202, 206, 23);
		chckBoxCBC.setSelected(chckBoxCBC_status);
		panel_3.add(chckBoxCBC);
		
		JLabel lblWetfct = new JLabel("WETFCT");
		lblWetfct.setBounds(6, 154, 71, 16);
		panel_3.add(lblWetfct);
		panel_3.add(getLblIwetit());
		panel_3.add(getLblIhdwet());
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(6, 184, 375, 16);
		panel_3.add(separator);
        
        JPanel panel_4 = new JPanel();
        tabbedPane.addTab("Pcg", null, panel_4, null);
        panel_4.setLayout(null);
        
        formattedMxIterField = new JFormattedTextField(Integer.class);
        formattedMxIterField.setValue(formattedMxIterField_value);
        formattedMxIterField.setToolTipText("is the maximum number of outer iterations");
        formattedMxIterField.setBounds(28, 21, 44, 28);
        panel_4.add(formattedMxIterField);
        
        JLabel lblOuterIterations = new JLabel(PluginServices.getText(this,"Outer_Iterations"));
        lblOuterIterations.setBounds(77, 27, 115, 16);
        panel_4.add(lblOuterIterations);
        
        formattedInnerIterField = new JFormattedTextField(Integer.class);
        formattedInnerIterField.setToolTipText("is the number of inner iterations");
        formattedInnerIterField.setValue(formattedInnerIterField_value);
        formattedInnerIterField.setBounds(219, 21, 44, 28);
        panel_4.add(formattedInnerIterField);
        
        JLabel lblInnerIterations = new JLabel(PluginServices.getText(this,"Inner_Iterations"));
        lblInnerIterations.setBounds(275, 27, 115, 16);
        panel_4.add(lblInnerIterations);
        
        comboNpCondBox = new JComboBox();
        comboNpCondBox.setToolTipText("matrix conditioning method");
        comboNpCondBox.setModel(new DefaultComboBoxModel(new String[] {"Modified Incomplete Cholesky", "Polynomial"}));
        comboNpCondBox.setBounds(65, 61, 247, 27);
        comboNpCondBox.setSelectedIndex(comboNpCondBox_index); //set from previous choice
        comboNpCondBox.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent e) {
				if (comboNpCondBox.getSelectedIndex()==0)
				{
					getFormattedRelaxField().setEditable(true);
					getFormattedNbPoltField().setEditable(false);
				}				
				else if (comboNpCondBox.getSelectedIndex()==1)
					{					
					getFormattedRelaxField().setEditable(false);
					getFormattedNbPoltField().setEditable(true);
					}				
			}
		});
        panel_4.add(comboNpCondBox);
        
        formattedHcloseField = new JFormattedTextField(double.class);
        formattedHcloseField.setValue(formattedHcloseField_value);
        formattedHcloseField.setToolTipText("is the head change criterion for convergence, in units of length");
        formattedHcloseField.setBounds(28, 100, 44, 28);
        panel_4.add(formattedHcloseField);
        
        JLabel lblHCLOSELabel = new JLabel("HCLOSE");
        lblHCLOSELabel.setBounds(78, 106, 61, 16);
        panel_4.add(lblHCLOSELabel);
        
        formattedRclosetField = new JFormattedTextField(double.class);
        formattedRclosetField.setValue(formattedRclosetField_value);
        formattedRclosetField.setToolTipText("in units of cubic length per time");
        formattedRclosetField.setBounds(28, 140, 44, 28);
        panel_4.add(formattedRclosetField);
        
        JLabel lblRclose = new JLabel("RCLOSE");
        lblRclose.setBounds(78, 146, 61, 16);
        panel_4.add(lblRclose);
        panel_4.add(getFormattedRelaxField());
        panel_4.add(getLblRelax());
        panel_4.add(getFormattedNbPoltField());
        panel_4.add(getLblNbpol());
        panel_4.add(getFormattedIprPcgField());
        panel_4.add(getLblIprpcg());
        panel_4.add(getComboMutPcgBox());
        panel_4.add(getFormattedDumptField());
        panel_4.add(getLblDump());
        panel_4.add(getLblMutpcg());
        
        add(getBtnCloseTool());
	}
       

/*        
	private String[] getLayers() {
		String[] solution = new String[]{};
		//		View v = (View) PluginServices.getMDIManager().getActiveWindow();
		//		MapControl mapCtrl = v.getMapControl();
		final FLayers layers = mapCtrl.getMapContext().getLayers();
		int numLayers = layers.getLayersCount();
		if(layers != null && numLayers > 0)
		{
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < numLayers; i++)
			{
				FLayer layer = layers.getLayer(i);
				if(layer instanceof FLyrVect)
				{
					list.add(layer.getName());
				}
			}
			solution = new String[list.size()];
			list.toArray(solution);
		}
		return solution;

	}
*/
	private JComboBox getLayerGriglia() {
		if(layerGriglia == null) {
			String[] vLayerList = Utils.getVectLayers(mapCtrl);
			ComboBoxModel layerGrigliaModel = new DefaultComboBoxModel(vLayerList); // getLayers()
			layerGriglia = new JComboBox();
			layerGriglia.setModel(layerGrigliaModel);
			layerGriglia.setBounds(138, 60, 139, 22);
			layerGriglia.addItemListener(new java.awt.event.ItemListener() {
				@Override
				public void itemStateChanged(final java.awt.event.ItemEvent e) {
					setFields();
				}
			});
			// Must be AFTER the itemlistener creation to properly set related values
			if(layerGriglia_value != null &&
					Arrays.asList(vLayerList).contains(layerGriglia_value) ){
				layerGriglia.setSelectedItem(layerGriglia_value);
			}

		}
		return layerGriglia;
	}
	
	protected void setFields() {

		final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(getFieldLayerNumericFields());
		getCh().setModel(jComboBoxFieldModel);

	}
	
	private JComboBox getCh() {
		if(Ch == null) {
			// There's no need to store a value for ch, it's set up by getFieldLayerNumericFields()
			ComboBoxModel ChModel = new DefaultComboBoxModel(getFieldLayerNumericFields());
			Ch = new JComboBox();
			Ch.setBounds(111, 74, 171, 22);
			Ch.setModel(ChModel);
		}
		return Ch;
	}
	
	public String[] getFieldLayerNumericFields() {
		String[] fields;
		String selectedLayerVar = (String)getLayerGriglia().getSelectedItem();
		FLayers layers = mapCtrl.getMapContext().getLayers();
		FLyrVect layer = (FLyrVect)layers.getLayer(selectedLayerVar);
		ArrayList<String> list = new ArrayList<String>();
		if(layer!=null)
			try {
				SelectableDataSource recordset = layer.getRecordset();
				int numFields = recordset.getFieldCount();
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
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(OcontrolFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(259, 306, 77, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Fundamental_packages"));
			
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	private JButton getJBtnRun() {
		if(jBtnRun == null) {
			jBtnRun = new JButton();
			jBtnRun.setBounds(346, 306, 77, 22);
			jBtnRun.setAction(getActionRun());
		}
		return jBtnRun;
	}
	
	private JLabel getToplayer() {
		if(toplayer == null) {
			toplayer = new JLabel();
			toplayer.setText(PluginServices.getText(this,"Top_Model_Layer"));
			toplayer.setBounds(27, 64, 110, 15);
			toplayer.setFont(new java.awt.Font("Dialog",0,12));
		}
		return toplayer;
	}
	
	private JLabel getField() {
		if(field == null) {
			field = new JLabel();
			field.setHorizontalAlignment(SwingConstants.CENTER);
			field.setBounds(111, 40, 171, 22);
			field.setText(PluginServices.getText(this,"Initial_(starting)_head"));
			field.setFont(new java.awt.Font("Dialog",0,12));
		}
		return field;
	}
/*	
	private String[] getMultiLayer() {
		String[] solution = null;

		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		ArrayList<ProjectDocument> lista = ext.getProject().getDocuments();
		ArrayList<String> list = new ArrayList<String>();
		for (int i=0; i<lista.size(); i++)
		{
			ProjectDocument doc = lista.get(i);
			if(doc instanceof HydrologicalModel)
			{
				list.add(doc.getName());

			}
		}
		solution = new String[list.size()];
		list.toArray(solution);	    
		return solution;

	}
*/	
	private JComboBox getModelLayer() {
		if(ModelLayer == null) {
			String[] modelNames = Utils.getModelsNames();
			ComboBoxModel ModelLayerModel = 
				new DefaultComboBoxModel(modelNames); // getMultiLayer()
			ModelLayer = new JComboBox();
			ModelLayer.setModel(ModelLayerModel);
			ModelLayer.setBounds(138, 136, 139, 22);
			// If I have a saved value, check if exists, and select it
			if(ModelLayer_value != null &&
					Arrays.asList(modelNames).contains(ModelLayer_value) ){
				ModelLayer.setSelectedItem(ModelLayer_value);
			}
		}
		return ModelLayer;
	}
	
	
	private JLabel getJLabel1() {
		if(jLabel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(PluginServices.getText(this,"Model_project"));
			jLabel1.setBounds(27, 140, 111, 15);
			jLabel1.setFont(new java.awt.Font("Dialog",0,12));
		}
		return jLabel1;
	}
	
	
	private JComboBox getJComboBoxTable() {
		if(jComboBoxTable == null) {
			String[] tableNames = Utils.getProjectTableNames();
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(tableNames);
			
			jComboBoxTable = new JComboBox();
			jComboBoxTable.setBounds(83, 70, 258, 27);
			jComboBoxTable.setModel(jComboBoxTableModel);
			// If I have a saved value, check if exists, and select it
			if(jComboBoxTable_value != null &&
					Arrays.asList(tableNames).contains(jComboBoxTable_value) ){
				jComboBoxTable.setSelectedItem(jComboBoxTable_value);
			}
		}
		return jComboBoxTable;
	}
	
	private JComboBox getJComboBoxLpf() {
		if(jComboBoxLpf == null) {
			String[] tableNames = Utils.getProjectTableNames();
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(tableNames);
			
			jComboBoxLpf = new JComboBox();
			jComboBoxLpf.setBounds(118, 41, 258, 27);
			jComboBoxLpf.setModel(jComboBoxTableModel);
			// If I have a saved value, check if exists, and select it
			if(jComboBoxLpf_value != null &&
					Arrays.asList(tableNames).contains(jComboBoxLpf_value) ){
				jComboBoxLpf.setSelectedItem(jComboBoxLpf_value);
			}
					
		}
		return jComboBoxLpf;
	}
	
	private JLabel getMainLabel() {
		if(mainLabel == null) {
			mainLabel = new JLabel();
			mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
			mainLabel.setText(PluginServices.getText(this,"Mandatory"));
			mainLabel.setBounds(27, 6, 375, 15);
			mainLabel.setFont(new java.awt.Font("Dialog",0,12));
		}
		return mainLabel;
	}
	
	private JLabel getBasLabel() {
		if(basLabel == null) {
			basLabel = new JLabel();
			basLabel.setHorizontalAlignment(SwingConstants.CENTER);
			basLabel.setVerticalAlignment(SwingConstants.TOP);
			basLabel.setBounds(10, 6, 392, 22);
			basLabel.setText(PluginServices.getText(this,"Basic_Input_File"));
			basLabel.setFont(new java.awt.Font("Dialog",0,12));
		}
		return basLabel;
	}
	
	private JLabel getJLabelDis() {
		if(jLabelDis == null) {
			jLabelDis = new JLabel();
			jLabelDis.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelDis.setBounds(10, 6, 392, 15);
			jLabelDis.setText(PluginServices.getText(this,"Discretization_Input_FIle"));
			jLabelDis.setFont(new java.awt.Font("Dialog",0,12));
		}
		return jLabelDis;
	}
	
	private JLabel getJLabelTime() {
		if(jLabelTime == null) {
			jLabelTime = new JLabel();
			jLabelTime.setHorizontalAlignment(SwingConstants.CENTER);
			jLabelTime.setBounds(83, 43, 258, 15);
			jLabelTime.setText(PluginServices.getText(this,"Time_Table"));
			jLabelTime.setFont(new java.awt.Font("Dialog",0,12));
		}
		return jLabelTime;
	}
	
	private JCheckBox getJCheckBoxConstant() {
		if(jCheckBoxConstant == null) {
			jCheckBoxConstant = new JCheckBox();
			jCheckBoxConstant.setText("Constant surface");
			jCheckBoxConstant.setBounds(138, 204, 152, 19);
			jCheckBoxConstant.setSelected(jCheckBoxConstant_status);
		}
		return jCheckBoxConstant;
	}
	

	private AbstractAction getActionRun(){
		if(actionRun == null) {
			actionRun = new AbstractAction(PluginServices.getText(this,"Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {

					// Save input data for future GUI uses
					comboNpCondBox_index = comboNpCondBox.getSelectedIndex();
					formattedMxIterField_value = (Integer) formattedMxIterField.getValue();
					formattedInnerIterField_value = (Integer) formattedInnerIterField.getValue();
					formattedHcloseField_value = (Double) formattedHcloseField.getValue();
					formattedRclosetField_value = (Double) formattedRclosetField.getValue();
					chckBoxCBC_status = chckBoxCBC.isSelected();
					jCheckBoxConstant_status = jCheckBoxConstant.isSelected();
					formattedWetfct_value = (Double) formattedWetfct.getValue();
					formattedIwetit_value = (Integer) formattedIwetit.getValue();
					formattedIHdwet_value = (Integer) formattedIHdwet.getValue();
					jComboBoxLpf_value = (String) jComboBoxLpf.getSelectedItem();
					jComboBoxTable_value = (String) jComboBoxTable.getSelectedItem();
					layerGriglia_value = (String) layerGriglia.getSelectedItem();
					ModelLayer_value = (String) ModelLayer.getSelectedItem();
					hdry_value  = hdry.getText();  // MAYBE it's better to store just the Double?
					formattedRelaxField_value = (Double) formattedRelaxField.getValue();
					formattedNbPoltField_value = (Integer) formattedNbPoltField.getValue();
					formattedIprPcgField_value = (Integer) formattedIprPcgField.getValue();
					comboMutPcgBox_value = (String) comboMutPcgBox.getSelectedItem();
					formattedDumptField_value = (Double) formattedDumptField.getValue();


					
					PluginServices.getMDIManager().closeWindow(OcontrolFileWrapper.this);
					final WaitingPanel test = new WaitingPanel();

					new Thread() {
						@Override
						public void run() {

							String progetto = (String) ModelLayer.getSelectedItem();
							ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
							HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
							
							String selectedLayerGriglia = (String)layerGriglia.getSelectedItem();
							FLayers layer = mapCtrl.getMapContext().getLayers();										
							FLyrVect griglia = (FLyrVect)layer.getLayer(selectedLayerGriglia);
							gvVectorLayer layerGriglia = new gvVectorLayer();
							layerGriglia.create(griglia);

							String stressp = (String) jComboBoxTable.getSelectedItem();
							ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
							ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
							gvTable tableStress = new gvTable();
							tableStress.create(stresstable);
							
							String lpf = (String) jComboBoxLpf.getSelectedItem();
							ProjectExtension ext3 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
							ProjectTable lpftable = (ProjectTable) ext3.getProject().getProjectDocumentByName(lpf, ProjectTableFactory.registerName);
							gvTable tableLpf = new gvTable();
							tableLpf.create(lpftable);
							
							String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();

							final ArrayList<gvVectorLayer> layers = doc.getLayers();
							System.out.println(layers.size());

							try {

								/*Scrivi file .geo****************************************************************/

								IFeatureIterator iter = layerGriglia.iterator();
								final int nrow = layerGriglia.getFieldIndexByName("ROW");
								final int ncol = layerGriglia.getFieldIndexByName("COL");
								final SimpleStats statsx = new SimpleStats();
								final SimpleStats statsy = new SimpleStats();
								while (iter.hasNext()) {
									final IFeature feature = iter.next();
									try {
										int xmax = Integer.parseInt(feature.getRecord().getValue(nrow).toString());
										int ymax = Integer.parseInt(feature.getRecord().getValue(ncol).toString());

										statsx.addValue(xmax);
										statsy.addValue(ymax);
									}
									catch (final Exception e) {}

								}
								iter.close();
								int imax = (int) statsx.getMax();
								int jmax =  (int) statsy.getMax();	
								
								final IFeatureIterator iter1 = layerGriglia.iterator();
								File file = new File(doc.getWorkingDirectory() +"/"+ doc.getName()+".geo");	
								FileWriter fw = new FileWriter(file);
								while (iter1.hasNext()) {
									final IFeature feature1 = iter1.next();
									
									if (Integer.parseInt(feature1.getRecord().getValue(nrow).toString())==imax && Integer.parseInt(feature1.getRecord().getValue(ncol).toString())==1)
									{
										Geometry geom1 = feature1.getGeometry();
										geom1.getCentroid().getX();
										geom1.getCentroid().getY();
										double fattore = Math.pow(10, 1);
										int delr = (int) (Math.ceil((geom1.getLength()/4) * fattore)/fattore);
										int delc = delr;
										  
										fw.write("NCOLS "+jmax + "\n");
										fw.write("NROWS "+imax + "\n");
										fw.write("CELLSIZE " + delc+"\n");
										fw.write("XLLCENTER "+ geom1.getCentroid().getX() + "\n");
										fw.write("YLLCENTER "+ geom1.getCentroid().getY() + "\n");
										fw.write("NODATA_VALUE -99999.00000");
										
									}
									
								}
								fw.flush();
								fw.close();
								iter1.close();
										
								

								

								/*Scrivi file .bas************************************************************/	
								BasFileAlgorithm alg = new BasFileAlgorithm();
								ParametersSet params = alg.getParameters();

								params.getParameter(BasFileAlgorithm.LAYER).setParameterValue(layerGriglia);
								params.getParameter(BasFileAlgorithm.LAYERS).setParameterValue(layers);
								params.getParameter(BasFileAlgorithm.CH).setParameterValue(Ch.getSelectedIndex());
								params.getParameter(BasFileAlgorithm.BAS).setParameterValue(filepath+".bas");


								alg.execute(null, m_OutputFactory);

								/*Scrivi file .dis***************************************************************/
							
								
								DisFileAlgorithm alg2 = new DisFileAlgorithm();
								ParametersSet params2 = alg2.getParameters();
								params2.getParameter(DisFileAlgorithm.LAYER).setParameterValue(layerGriglia);
								params2.getParameter(DisFileAlgorithm.LAYERS).setParameterValue(layers);
								params2.getParameter(DisFileAlgorithm.ITMUNI).setParameterValue(doc.getTimeCode());
								params2.getParameter(DisFileAlgorithm.LEMUNI).setParameterValue(doc.getSpaceCode());
								params2.getParameter(DisFileAlgorithm.GEO).setParameterValue(jCheckBoxConstant.isSelected());
								params2.getParameter(DisFileAlgorithm.DIS).setParameterValue(filepath+".dis");
								params2.getParameter(DisFileAlgorithm.TABLE).setParameterValue(tableStress);
								params2.getParameter(DisFileAlgorithm.COUNT).setParameterValue(doc.getStress());
								alg2.execute(null, m_OutputFactory);

								/*Scrivi file .lpf***************************************************************/
								int numModelLpf = doc.getNumModel();
								int cbc = 0;
								if(chckBoxCBC.isSelected()==true){
									cbc = 90;
									if(numModelLpf!=0)
									{
										int prefix = numModelLpf*100;
										cbc = prefix+cbc;								
									}														
								}
								double hdryValue = Double.parseDouble(hdry.getText());
								double wetfct = Double.parseDouble(formattedWetfct.getText());
								int iwetit = Integer.parseInt(formattedIwetit.getText());
								int ihdwet = Integer.parseInt(formattedIHdwet.getText());
								
								LpfFileAlgorithm alg3 = new LpfFileAlgorithm();
								ParametersSet params3 = alg3.getParameters();
								params3.getParameter(LpfFileAlgorithm.LAYERS).setParameterValue(layers);
								params3.getParameter(LpfFileAlgorithm.TABLE).setParameterValue(tableLpf);
								params3.getParameter(LpfFileAlgorithm.TABLETIME).setParameterValue(tableStress);
								params3.getParameter(LpfFileAlgorithm.HDRY).setParameterValue(hdryValue);
								params3.getParameter(LpfFileAlgorithm.ILPFCB).setParameterValue(cbc);
								params3.getParameter(LpfFileAlgorithm.WETFTC).setParameterValue(wetfct);
								params3.getParameter(LpfFileAlgorithm.IWETIT).setParameterValue(iwetit);
								params3.getParameter(LpfFileAlgorithm.IHDWET).setParameterValue(ihdwet);
								params3.getParameter(LpfFileAlgorithm.LPF).setParameterValue(filepath+".lpf");
								alg3.execute(null, m_OutputFactory);
								
								/*Scrivi file .oc***************************************************************/			
								int numModel = doc.getNumModel();
								OcFileAlgorithm alg4 = new OcFileAlgorithm();
								ParametersSet params4 = alg4.getParameters();
								params4.getParameter(OcFileAlgorithm.TABLE).setParameterValue(tableStress);
								
								int fhd_unit = 101;
								int fdn_unit = 102;
								
								if(numModel!=0)
								{
									int prefix = numModel*1000;
									fhd_unit = prefix+fhd_unit;
									fdn_unit = prefix+fdn_unit;
								}

								params4.getParameter(OcFileAlgorithm.FDN).setParameterValue(fdn_unit);
								params4.getParameter(OcFileAlgorithm.FHD).setParameterValue(fhd_unit);				
								params4.getParameter(OcFileAlgorithm.OC).setParameterValue(filepath+".oc");
								alg4.execute(null, m_OutputFactory);
								
								/*Scrivi file .pcg***************************************************************/
								File filePcg = new File(doc.getWorkingDirectory() +"/"+ doc.getName()+".pcg");	
								FileWriter fwPcg = new FileWriter(filePcg);
								fwPcg.write("# PCG Solver Package"+ "\n");
								 /*MXITER, ITER1, NPCOND*/
								int mxiter = (Integer) formattedMxIterField.getValue();
								int itermx = (Integer) formattedInnerIterField.getValue();
								int npcond;
								if (comboNpCondBox.getSelectedIndex()==0)								
									npcond = 1;								
								else
									npcond = 2;
								
								fwPcg.write("   "+mxiter+"   "+itermx+"   "+npcond+"     # MXITER, ITER1, NPCOND"+ "\n");
								
								/*HCLOSE, RCLOSE, RELAX, NBPOL, IPRPCG, MUTPCG, DAMPPCG*/
								double hclose = (Double) formattedHcloseField.getValue();
								double rclose = (Double) formattedRclosetField.getValue();
								double relax = (Double) formattedRelaxField.getValue();
								int nppol = (Integer) formattedNbPoltField.getValue();
								int iprpcg = (Integer) formattedIprPcgField.getValue();
								int mutpcg;
								switch(comboMutPcgBox.getSelectedIndex())
								{
								case 0: mutpcg=0; break;
								case 1: mutpcg=1; break;
								case 2: mutpcg=2; break;
								case 3: mutpcg=3; break;
								default: mutpcg=0; break;
								}
								double dump = (Double) formattedDumptField.getValue();
								fwPcg.write("   "+hclose+"    "+rclose+"    "+relax+"    "+nppol+"    "+iprpcg+"    "+mutpcg+"    "+dump+"   # HCLOSE, RCLOSE, RELAX, NBPOL, IPRPCG, MUTPCG, DAMPPCG"+ "\n");
								fwPcg.flush();
								fwPcg.close();
								
								test.dispose();
								
								JOptionPane.showMessageDialog(null,
										PluginServices.getText(this, "Run_successfull"),
										"Message",
										JOptionPane.INFORMATION_MESSAGE);

								
							} catch (GeoAlgorithmExecutionException e) {
								JOptionPane.showMessageDialog(null,
										e.getLocalizedMessage(),
										PluginServices.getText(this, "Error"),
										JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							} catch (IOException e) {
								JOptionPane.showMessageDialog(null,
										e.getLocalizedMessage(),
										PluginServices.getText(this, "Error"),
										JOptionPane.ERROR_MESSAGE);
								e.printStackTrace();
							}
							finally{
								if(test.isDisplayable())
									test.dispose();								
							}
						}

					}
					.start();

				}
			};
		}

		return actionRun;
	}

	
	private JLabel getLblLayerPropertiesFile() {
		if (lblLayerPropertiesFile == null) {
			lblLayerPropertiesFile = new JLabel(PluginServices.getText(this,"Layer_Properties_File_Input"));
			lblLayerPropertiesFile.setHorizontalAlignment(SwingConstants.CENTER);
			lblLayerPropertiesFile.setFont(new Font("Dialog", Font.PLAIN, 12));
			lblLayerPropertiesFile.setBounds(6, 6, 396, 16);
		}
		return lblLayerPropertiesFile;
	}
	private JLabel getLblLpfTable() {
		if (lblLpfTable == null) {
			lblLpfTable = new JLabel(PluginServices.getText(this,"LPF_table"));
			lblLpfTable.setFont(new Font("Dialog", Font.PLAIN, 12));
			lblLpfTable.setBounds(6, 45, 127, 16);
		}
		return lblLpfTable;
	}
	
	/*FORMATO NUMERICO SENZA SEPARATORE DELLE MIGLIAIA*/
	private JFormattedTextField getFormattedTextField() {
		if (hdry == null) {
			DecimalFormatSymbols decimalSymbols =
				    new DecimalFormatSymbols(java.util.Locale.ITALIAN);
				decimalSymbols.setDecimalSeparator('.');
				String type = "####.##";
				DecimalFormat formatter = 
				               new DecimalFormat(type, decimalSymbols);			
			hdry = new JFormattedTextField(formatter);
			hdry.setBounds(126, 87, 113, 28);
			hdry.setText(hdry_value);
		}
		return hdry;
	}
	private JLabel getLblHdryParameters() {
		if (lblHdryParameters == null) {
			lblHdryParameters = new JLabel("HDRY Parameters");
			lblHdryParameters.setHorizontalAlignment(SwingConstants.LEFT);
			lblHdryParameters.setBounds(6, 93, 186, 16);
		}
		return lblHdryParameters;
	}
	
	private JFormattedTextField getFormattedRelaxField() {
		if (formattedRelaxField == null) {
			formattedRelaxField = new JFormattedTextField(double.class);
			formattedRelaxField.setToolTipText("is the relaxation parameter used with NPCOND = 1");
			formattedRelaxField.setValue(formattedRelaxField_value); //1.0			
			formattedRelaxField.setBounds(28, 174, 44, 28);
		}
		return formattedRelaxField;
	}
	private JLabel getLblRelax() {
		if (lblRelax == null) {
			lblRelax = new JLabel("RELAX");
			lblRelax.setBounds(78, 180, 61, 16);
		}
		return lblRelax;
	}
	private JFormattedTextField getFormattedNbPoltField() {
		if (formattedNbPoltField == null) {
			formattedNbPoltField = new JFormattedTextField(int.class);
			formattedNbPoltField.setValue(formattedNbPoltField_value); //1
			formattedNbPoltField.setToolTipText("is used when NPCOND = 2 to indicate whether the estimate of the upper bound on the maximum eigenvalue is 2.0, or whether the estimate will be calculated");
			formattedNbPoltField.setBounds(28, 208, 44, 28);
			formattedNbPoltField.setEditable(false);
		}
		return formattedNbPoltField;
	}
	private JLabel getLblNbpol() {
		if (lblNbpol == null) {
			lblNbpol = new JLabel("NBPOL");
			lblNbpol.setBounds(77, 214, 92, 16);
		}
		return lblNbpol;
	}
	private JFormattedTextField getFormattedIprPcgField() {
		if (formattedIprPcgField == null) {
			formattedIprPcgField = new JFormattedTextField(int.class);
			formattedIprPcgField.setValue(formattedIprPcgField_value); //999
			formattedIprPcgField.setToolTipText("is the printout interval for PCG");
			formattedIprPcgField.setBounds(219, 100, 44, 28);
		}
		return formattedIprPcgField;
	}
	private JLabel getLblIprpcg() {
		if (lblIprpcg == null) {
			lblIprpcg = new JLabel("IPRPCG");
			lblIprpcg.setBounds(275, 106, 61, 16);
		}
		return lblIprpcg;
	}
	private JComboBox getComboMutPcgBox() {
		if (comboMutPcgBox == null) {
			String[] values = {"printing tables of maximum head change and residual each iteration", "printing only the total number of iterations", "no printing", "printing only if convergence fails"};
			comboMutPcgBox = new JComboBox();
			comboMutPcgBox.setToolTipText("controls printing of convergence information from the solver");
			comboMutPcgBox.setModel(new DefaultComboBoxModel( values ));
			comboMutPcgBox.setBounds(152, 190, 160, 27);
			// If I have a saved value, check if exists, and select it
			if(comboMutPcgBox_value != null &&
					Arrays.asList(values).contains(comboMutPcgBox_value) ){
				comboMutPcgBox.setSelectedItem(comboMutPcgBox_value);
			}

		}
		return comboMutPcgBox;
	}
	private JFormattedTextField getFormattedDumptField() {
		if (formattedDumptField == null) {
			formattedDumptField = new JFormattedTextField(double.class);
			formattedDumptField.setToolTipText("damping factor");
			formattedDumptField.setValue(formattedDumptField_value); //1.0
			formattedDumptField.setBounds(219, 140, 40, 28);
		}
		return formattedDumptField;
	}
	private JLabel getLblDump() {
		if (lblDump == null) {
			lblDump = new JLabel("DUMP");
			lblDump.setBounds(275, 146, 61, 16);
		}
		return lblDump;
	}
	private JLabel getLblMutpcg() {
		if (lblMutpcg == null) {
			lblMutpcg = new JLabel("MUTPCG");
			lblMutpcg.setBounds(324, 195, 61, 16);
		}
		return lblMutpcg;
	}
	private JFormattedTextField getFormattedTextField_Wetfct() {
		if (formattedWetfct == null) {
			formattedWetfct = new JFormattedTextField(double.class);
			formattedWetfct.setValue(formattedWetfct_value);			
			formattedWetfct.setBounds(73, 148, 44, 28);
		}
		return formattedWetfct;
	}
	private JFormattedTextField getFormattedTextField_Iwetit() {
		if (formattedIwetit == null) {
			formattedIwetit = new JFormattedTextField(int.class);
			formattedIwetit.setValue(formattedIwetit_value);			
			formattedIwetit.setBounds(205, 148, 44, 28);
		}
		return formattedIwetit;
	}
	private JFormattedTextField getFormattedTextField_IHdwet() {
		if (formattedIHdwet == null) {
			formattedIHdwet = new JFormattedTextField(int.class);
			formattedIHdwet.setValue(formattedIHdwet_value);			
			formattedIHdwet.setBounds(332, 148, 44, 28);
		}
		return formattedIHdwet;
	}
	
	private JLabel getLblIwetit() {
		if (lblIwetit == null) {
			lblIwetit = new JLabel("IWETIT");
			lblIwetit.setBounds(143, 154, 61, 16);
		}
		return lblIwetit;
	}
	private JLabel getLblIhdwet() {
		if (lblIhdwet == null) {
			lblIhdwet = new JLabel("IHDWET");
			lblIhdwet.setBounds(280, 154, 61, 16);
		}
		return lblIhdwet;
	}
}
