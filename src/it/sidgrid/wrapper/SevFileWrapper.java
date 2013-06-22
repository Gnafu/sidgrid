package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.SevFileAlgorithm;
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
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;

import javax.swing.JLabel;

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
 * SID&GRID wrapper input file
 * SEV for VSF
 */
public class SevFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox jComboBoxLayer;
	private JComboBox modelCombobox;
	private JFormattedTextField formattedAthmosferic;
	private JFormattedTextField formattedSurfResist;
	private JFormattedTextField formattedEvapotrLayer;
	private JLabel lblAthmosferic;
	private JLabel lblSurfResist;
	private JLabel lblEvapotraLayer;
	private final Action action = new SwingAction();
	private JButton btnCloseTool;
	
	public SevFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(null);
		this.setSize(364, 286);
		this.add(getJComboVsfLayer());
		
		JLabel lblVsfLayer = new JLabel(PluginServices.getText(this,"VSF_Model_Layer"));
		lblVsfLayer.setBounds(179, 60, 151, 16);
		add(lblVsfLayer);
		
		formattedAthmosferic = new JFormattedTextField(double.class);
		formattedAthmosferic.setValue(0.0);
		formattedAthmosferic.setToolTipText("");
		formattedAthmosferic.setBounds(85, 94, 83, 28);
        this.add(formattedAthmosferic);
        
        lblAthmosferic = new JLabel(PluginServices.getText(this,"Athmosferic_potential_pressure"));
        lblAthmosferic.setBounds(180, 100, 170, 16);
        add(lblAthmosferic);
        
        formattedSurfResist = new JFormattedTextField(double.class);
        formattedSurfResist.setValue(0.0);
        formattedSurfResist.setToolTipText("");
        formattedSurfResist.setBounds(85, 134, 83, 28);
        this.add(formattedSurfResist);
        
        lblSurfResist = new JLabel(PluginServices.getText(this,"Surface_resistance_factor"));
        lblSurfResist.setBounds(180, 140, 170, 16);
        add(lblSurfResist);
        
        formattedEvapotrLayer = new JFormattedTextField(int.class);
        formattedEvapotrLayer.setValue(0);
        formattedEvapotrLayer.setToolTipText("");
        formattedEvapotrLayer.setBounds(84, 174, 83, 28);
        this.add(formattedEvapotrLayer);
        
        lblEvapotraLayer = new JLabel(PluginServices.getText(this,"Evapotranspiration_Layer"));
        lblEvapotraLayer.setBounds(180, 180, 170, 16);
        add(lblEvapotraLayer);
        
        modelCombobox = new JComboBox(Utils.getModelsNames());
		modelCombobox.setToolTipText("Model Document");
		modelCombobox.setBounds(16, 16, 151, 27);
		add(modelCombobox);
		
		lblModelProject = new JLabel(PluginServices.getText(this,"Model_project"));
		lblModelProject.setBounds(179, 21, 139, 16);
		add(lblModelProject);
		
		JButton btnRunRef = new JButton();
		btnRunRef.setAction(action);
		btnRunRef.setBounds(238, 223, 80, 22);
		add(btnRunRef);
		
		add(getBtnCloseTool());
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(SevFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(135, 223, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private JComboBox getJComboVsfLayer() {
		if(jComboBoxLayer == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getVectLayers(mapCtrl));
			jComboBoxLayer = new JComboBox();
			jComboBoxLayer.setBounds(16, 55, 151, 27);
			jComboBoxLayer.setModel(jComboBoxTableModel);
		}
		return jComboBoxLayer;
	}
	
	
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private JLabel lblModelProject;
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Run"));

		}
		@Override
		public void actionPerformed(ActionEvent e) {			
			PluginServices.getMDIManager().closeWindow(SevFileWrapper.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelCombobox.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
					
					String vsfLayerName = (String)jComboBoxLayer.getSelectedItem();
					FLayers layersUn = mapCtrl.getMapContext().getLayers();										
					FLyrVect vsfLayer = (FLyrVect)layersUn.getLayer(vsfLayerName);
					final gvVectorLayer vsfGvLayer = new gvVectorLayer();
					vsfGvLayer.create(vsfLayer);
					
					SevFileAlgorithm alg =  new SevFileAlgorithm();

					try {
						
						ParametersSet params = alg.getParameters();
						params.getParameter(SevFileAlgorithm.SEV).setParameterValue(filepath+".sev");													
						params.getParameter(SevFileAlgorithm.VSF).setParameterValue(vsfGvLayer);
						params.getParameter(SevFileAlgorithm.HA).setParameterValue(formattedAthmosferic.getText());						
						params.getParameter(SevFileAlgorithm.SRES).setParameterValue(formattedSurfResist.getText());
						params.getParameter(SevFileAlgorithm.SEL).setParameterValue(formattedEvapotrLayer.getText());
						params.getParameter(SevFileAlgorithm.COUNT).setParameterValue(doc.getStress());
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
					/*} catch (WrongOutputIDException e) {
						// TODO Serve?
						e.printStackTrace();*/
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

				}

			}.start();
		}
	}
		
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Sev"));
			
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
}
