package it.sidgrid.wrapper;

import java.util.ArrayList;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.Sfr2FileAlgorithm;
import it.sidgrid.utils.Utils;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.unex.sextante.core.ITaskMonitor;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvTable;
import es.unex.sextante.gvsig.core.gvVectorLayer;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
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
 * @author sid&grid
 * GUI
 * SID&GRID mandatory wrapper input file
 * SFR2 package
 * Sfr2FileAlgorithm SEXTANTE algorithm
 * Stream flow table
 * 
 */

public class StreamFlowFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox streamLayer;
	private JComboBox TableStreamFlow;
	private JComboBox modelCombo;
	private JCheckBox chckBoxCBC;
	private JLabel StreamTableStressLabel;
	private JFormattedTextField constValue;
	private JFormattedTextField dleakValue;
	private JLabel lblDleakToleranceLevel;
	private JFormattedTextField numtimValue;
	private JLabel lblNumtimValue;
	private JFormattedTextField weightValue;
	private JLabel lblWeightFactor;
	private JFormattedTextField streamflowtoleranceValue;
	private JLabel lblStreamTolerance;
	private final Action action = new SwingAction();
	private JLabel lblModelProject;
	private JButton btnCloseTool;
	
	public StreamFlowFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(null);
		this.setSize(509, 333);
	
		ComboBoxModel model = new DefaultComboBoxModel(getModel());
		modelCombo = new JComboBox();
		modelCombo.setBounds(19, 26, 195, 27);
		modelCombo.setModel(model);
		add(modelCombo);
		
		ComboBoxModel stream = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		streamLayer = new JComboBox();
		streamLayer.setBounds(19, 61, 195, 27);
		streamLayer.setModel(stream);
		streamLayer.setToolTipText("Stream Model layer");
		add(streamLayer);
		
		JLabel streamLabel = new JLabel(PluginServices.getText(this,"Stream_Model_Layer"));
		streamLabel.setBounds(260, 65, 168, 16);
		add(streamLabel);

		add(getJComboBoxTable());
		
		StreamTableStressLabel = new JLabel(PluginServices.getText(this,"Stream_Table_Stress_Period"));
		StreamTableStressLabel.setBounds(260, 104, 195, 16);
		add(StreamTableStressLabel);

		constValue = new JFormattedTextField(Double.class);
		constValue.setBounds(19, 139, 68, 27);
		constValue.setToolTipText("");
		constValue.setValue(new Double(0.0));
		add(constValue);
		
		JLabel lblConstant = new JLabel(PluginServices.getText(this,"Constant"));
		lblConstant.setBounds(99, 145, 88, 16);
		add(lblConstant);
		
		dleakValue = new JFormattedTextField(Double.class);
		dleakValue.setBounds(19, 178, 68, 27);
		dleakValue.setToolTipText("");
		dleakValue.setValue(new Double(0.0));
		add(dleakValue);
		
		lblDleakToleranceLevel = new JLabel(PluginServices.getText(this,"DLEAK_tolerance_level"));
		lblDleakToleranceLevel.setBounds(99, 184, 178, 16);
		add(lblDleakToleranceLevel);
		
		numtimValue = new JFormattedTextField(Double.class);
		numtimValue.setBounds(19, 221, 68, 27);
		numtimValue.setToolTipText("");
		numtimValue.setValue(new Double(0.0));
		add(numtimValue);
		
		lblNumtimValue = new JLabel(PluginServices.getText(this,"Num_of_sub_time_steps"));
		lblNumtimValue.setBounds(99, 227, 178, 16);
		add(lblNumtimValue);
		
		weightValue = new JFormattedTextField(Double.class);
		weightValue.setBounds(263, 139, 68, 27);
		weightValue.setToolTipText("");
		weightValue.setValue(new Double(0.0));
		add(weightValue);
		
		lblWeightFactor = new JLabel(PluginServices.getText(this,"Weight_factor"));
		lblWeightFactor.setBounds(343, 145, 112, 16);
		add(lblWeightFactor);
		
		streamflowtoleranceValue = new JFormattedTextField(Double.class);
		streamflowtoleranceValue.setBounds(263, 178, 68, 27);
		streamflowtoleranceValue.setToolTipText("");
		streamflowtoleranceValue.setValue(new Double(0.0));
		add(streamflowtoleranceValue);
		
		lblStreamTolerance = new JLabel(PluginServices.getText(this,"Stream_tolerance"));
		lblStreamTolerance.setBounds(343, 184, 142, 16);
		add(lblStreamTolerance);
		
		JButton btnRun = new JButton("Run");
		btnRun.setAction(action);
		btnRun.setBounds(387, 257, 68, 29);
		add(btnRun);
		
		lblModelProject = new JLabel(PluginServices.getText(this,"Model_project"));
		lblModelProject.setBounds(260, 30, 168, 16);
		add(lblModelProject);
		
		add(getBtnCloseTool());
		
		JSeparator separator = new JSeparator();
		separator.setBounds(260, 212, 195, 21);
		add(separator);
		
		chckBoxCBC = new JCheckBox("Write cell by cell budget");
		chckBoxCBC.setBounds(260, 222, 206, 23);
		add(chckBoxCBC);
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(StreamFlowFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(307, 257, 68, 29);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private JComboBox getJComboBoxTable() {
		if(TableStreamFlow == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getProjectTableNames());
			TableStreamFlow = new JComboBox();
			TableStreamFlow.setBounds(19, 100, 195, 27);
			TableStreamFlow.setModel(jComboBoxTableModel);
		}
		return TableStreamFlow;
	}
	
	private String[] getModel() {
		String[] solution = new String[]{};

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
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Sfr2_package"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Run"));
			putValue(SHORT_DESCRIPTION, "Run Wrapper");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			PluginServices.getMDIManager().closeWindow(StreamFlowFileWrapper.this);
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelCombo.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
					
					String selectedStreamLayer = (String)streamLayer.getSelectedItem();
					FLayers layer = mapCtrl.getMapContext().getLayers();										
					FLyrVect stream = (FLyrVect)layer.getLayer(selectedStreamLayer);
					gvVectorLayer layerStream = new gvVectorLayer();
					layerStream.create(stream);
					
					String stressp = (String) TableStreamFlow.getSelectedItem();
					ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
					gvTable tableStress = new gvTable();
					tableStress.create(stresstable);
					
					Sfr2FileAlgorithm alg = new Sfr2FileAlgorithm();
					ParametersSet params = alg.getParameters();
					ITaskMonitor waitingpanel = new DefaultTaskMonitor("Sfr2FileAlgorithm", true, null);
					
					try {
						int numModel = doc.getNumModel();
						int cbc = 0;
						int cbc1 = 0;
						
						if(chckBoxCBC.isSelected()==true){
							cbc = 90;
							cbc1 = 90;
							
							if(numModel!=0)
							{
								int prefix = numModel*100;
								cbc = prefix+cbc;
								cbc1 = prefix+cbc1;
							}														
						}
						
						params.getParameter(Sfr2FileAlgorithm.LAYER).setParameterValue(layerStream);
						params.getParameter(Sfr2FileAlgorithm.TABLESTREAM).setParameterValue(tableStress);
						params.getParameter(Sfr2FileAlgorithm.COUNT).setParameterValue(doc.getStress());
						params.getParameter(Sfr2FileAlgorithm.CONST).setParameterValue(constValue.getText());
						params.getParameter(Sfr2FileAlgorithm.DLEAK).setParameterValue(dleakValue.getText());
						params.getParameter(Sfr2FileAlgorithm.FLWTOL).setParameterValue(streamflowtoleranceValue.getText());
						params.getParameter(Sfr2FileAlgorithm.NUMTIM).setParameterValue(numtimValue.getText());
						params.getParameter(Sfr2FileAlgorithm.WEIGHT).setParameterValue(weightValue.getText());
						params.getParameter(Sfr2FileAlgorithm.ISTCB1).setParameterValue(cbc);
						params.getParameter(Sfr2FileAlgorithm.ISTCB2).setParameterValue(cbc1);
						params.getParameter(Sfr2FileAlgorithm.SFR).setParameterValue(filepath+".sfr");

						alg.execute(waitingpanel, m_OutputFactory);
						waitingpanel.close();
						JOptionPane.showMessageDialog(null,
								PluginServices.getText(this, "Run_successfull"),
								"Avviso",
								JOptionPane.INFORMATION_MESSAGE);

					} catch (WrongParameterIDException e) {
						JOptionPane.showMessageDialog(null,
								e.getLocalizedMessage(),
								PluginServices.getText(this, "Error"),
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} catch (GeoAlgorithmExecutionException e) {
						JOptionPane.showMessageDialog(null,
								e.getLocalizedMessage(),
								PluginServices.getText(this, "Error"),
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}finally{
						waitingpanel.close();
					}

				}					
			}
			.start();
		}
	}
}
