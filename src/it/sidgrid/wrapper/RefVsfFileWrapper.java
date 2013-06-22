package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.RefFileAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvTable;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

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
 * REF file for VSF package
 * This GUI allows the soil type definition for each model layers
 * 
 */
public class RefVsfFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox jComboBoxTable;
	private JComboBox modelCombobox;
	private JFormattedTextField formattedPTABA;
	private JFormattedTextField formattedPTABB;
	private JLabel lblPabba;
	private JLabel lblTheUpperBounds;
	private final Action action = new SwingAction();
	private JButton btnCloseTool;
	
	public RefVsfFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(null);
		this.setSize(358, 264);
		this.add(getJComboBoxTable());
		
		JLabel lblSoilTypeTable = new JLabel(PluginServices.getText(this,"Soil_Type_Table"));
		lblSoilTypeTable.setHorizontalAlignment(SwingConstants.RIGHT);
		lblSoilTypeTable.setBounds(179, 79, 151, 16);
		add(lblSoilTypeTable);
		
		formattedPTABA = new JFormattedTextField(double.class);
		formattedPTABA.setValue(-0.001);
		formattedPTABA.setToolTipText("");
		formattedPTABA.setBounds(26, 114, 83, 28);
        this.add(formattedPTABA);
        
        lblPabba = new JLabel(PluginServices.getText(this,"Pore_pressure_lower_bound"));
        lblPabba.setHorizontalAlignment(SwingConstants.RIGHT);
        lblPabba.setBounds(121, 120, 210, 16);
        add(lblPabba);
        
        formattedPTABB = new JFormattedTextField(double.class);
        formattedPTABB.setValue(-100);
        formattedPTABB.setToolTipText("");
        formattedPTABB.setBounds(26, 154, 83, 28);
        this.add(formattedPTABB);
        
        lblTheUpperBounds = new JLabel(PluginServices.getText(this,"Pore_pressure_upper_bound"));
        lblTheUpperBounds.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTheUpperBounds.setBounds(99, 160, 231, 16);
        add(lblTheUpperBounds);
        
        modelCombobox = new JComboBox(Utils.getModelsNames());
		modelCombobox.setToolTipText(PluginServices.getText(this,"Model_Document"));
		modelCombobox.setBounds(16, 36, 151, 27);
		add(modelCombobox);
		
		lblModelProject = new JLabel(PluginServices.getText(this,"Model_project"));
		lblModelProject.setHorizontalAlignment(SwingConstants.RIGHT);
		lblModelProject.setBounds(200, 40, 130, 16);
		add(lblModelProject);
		
		JButton btnRunRef = new JButton();
		btnRunRef.setAction(action);
		btnRunRef.setBounds(252, 208, 80, 22);
		add(btnRunRef);
		
		add(getBtnCloseTool());
		}
	
	
	private JComboBox getJComboBoxTable() {
		if(jComboBoxTable == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getProjectTableNames());
			jComboBoxTable = new JComboBox();
			jComboBoxTable.setBounds(16, 75, 151, 27);
			jComboBoxTable.setModel(jComboBoxTableModel);
		}
		return jComboBoxTable;
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(RefVsfFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(155, 208, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private JLabel lblModelProject;
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Run"));

		}
		@Override
		public void actionPerformed(ActionEvent e) {			
			PluginServices.getMDIManager().closeWindow(RefVsfFileWrapper.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelCombobox.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
					
					String soilTable = (String) jComboBoxTable.getSelectedItem();
					ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					ProjectTable table = (ProjectTable) ext2.getProject().getProjectDocumentByName(soilTable, ProjectTableFactory.registerName);
					gvTable tableStress = new gvTable();
					tableStress.create(table);
					
					RefFileAlgorithm alg =  new RefFileAlgorithm();

					try {
						
						ParametersSet params = alg.getParameters();
						params.getParameter(RefFileAlgorithm.REF).setParameterValue(filepath+".ref");													
						params.getParameter(RefFileAlgorithm.PTABA).setParameterValue(formattedPTABA.getText());
						params.getParameter(RefFileAlgorithm.PTABB).setParameterValue(formattedPTABB.getText());						
						params.getParameter(RefFileAlgorithm.TABLESOIL).setParameterValue(tableStress);						
						alg.execute(null, m_OutputFactory);						
						
						test.dispose();
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
						if(test.isDisplayable())
							test.dispose(); 
					}

					test.dispose();

				}

			}.start();
		}
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Ref"));		
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
