package it.sidgrid.wrapper;

import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.RchFileAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.utiles.swing.JComboBox;

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;

import javax.swing.JLabel;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JCheckBox;

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
 * RCH package
 * RchFileAlgorithm SEXTANTE algorithm
 * 
 */


public class RchFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox comboBox;
	private JComboBox rech;
	private JComboBox modelCombobox;
	private JComboBox rChopComboBox;
	private JCheckBox chckBoxCBC;
	private final Action action = new SwingAction();
	private JButton btnCloseTool;
	
	public RchFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(null);
		this.setSize(360, 364);
		ComboBoxModel rch = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		comboBox = new JComboBox();
		comboBox.setBounds(43, 94, 167, 27);
		comboBox.setModel(rch);
		comboBox.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent e) {
				setFields();

			}
		});
		add(comboBox);
		this.add(getRech());
		
		JLabel GridLabel = new JLabel(PluginServices.getText(this,"Rch_Layer_grid"));
		GridLabel.setBounds(216, 99, 172, 16);
		add(GridLabel);
		
		JLabel RechLabel = new JLabel(PluginServices.getText(this,"RECH_Field"));
		RechLabel.setBounds(248, 196, 140, 16);
		add(RechLabel);
		
		rChopComboBox = new JComboBox();
		rChopComboBox.setModel(new DefaultComboBoxModel(new String[] {"Recharge only top grid layer", "Verical distribution in layer variable IRCH", "Recharge to the hightest active cell in each vertical column"}));
		rChopComboBox.setBounds(43, 133, 278, 27);
	
		add(rChopComboBox);
		
		modelCombobox = new JComboBox(getModel());
		modelCombobox.setToolTipText("Model Document");
		modelCombobox.setBounds(43, 39, 168, 27);
		add(modelCombobox);
		
		JLabel modelLabel = new JLabel(PluginServices.getText(this,"Model_project"));
		modelLabel.setBounds(216, 44, 172, 16);
		add(modelLabel);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(43, 172, 278, 16);
		add(separator);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(Color.BLACK);
		separator_1.setBounds(43, 71, 278, 16);
		add(separator_1);
		
		JButton runButton = new JButton("");
		runButton.setAction(action);
		runButton.setBounds(261, 293, 80, 22);
		add(runButton);
		
		add(getBtnCloseTool());
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setForeground(Color.BLACK);
		separator_2.setBounds(43, 230, 278, 16);
		add(separator_2);

		chckBoxCBC = new JCheckBox("Write cell by cell budget");
		chckBoxCBC.setBounds(74, 258, 206, 23);
		add(chckBoxCBC);
		
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setWidth(360);
			viewInfo.setHeight(320);
			viewInfo.setTitle(PluginServices.getText(this, "Rch_package"));

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
							PluginServices.getMDIManager().closeWindow(RchFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(150, 293, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	protected void setFields() {

		// final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(getFieldLayerNumericFields());
		final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(Utils.getFieldLayerNumericFields(mapCtrl, (String)comboBox.getSelectedItem()));
		rech.setModel(jComboBoxFieldModel);
		
		
	}
	
	private JComboBox getRech() {
		if(rech == null) {
			rech = new JComboBox();
			rech.setBounds(43, 191, 167, 27);
			setFields();
		}
		return rech;
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
	
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Run"));			
		}
		@Override
		public void actionPerformed(ActionEvent e) {			
			String Rch = (String)comboBox.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();
			FLyrVect rchgeometry = (FLyrVect)layers.getLayer(Rch);
			final gvVectorLayer layerRch = new gvVectorLayer();
			layerRch.create(rchgeometry);
			
			PluginServices.getMDIManager().closeWindow(RchFileWrapper.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelCombobox.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
					
					RchFileAlgorithm alg =  new RchFileAlgorithm();

					try {
						int numModel = doc.getNumModel();
						int cbc = 0;
						if(chckBoxCBC.isSelected()==true){
							cbc = 90;
							if(numModel!=0)
							{
								int prefix = numModel*100;
								cbc = prefix+cbc;								
							}														
						}
						
						ParametersSet params = alg.getParameters();
						params.getParameter(RchFileAlgorithm.LAYER).setParameterValue(layerRch);
						params.getParameter(RchFileAlgorithm.INRECH).setParameterValue(rech.getSelectedIndex());
						params.getParameter(RchFileAlgorithm.COUNT).setParameterValue(doc.getStress());
						params.getParameter(RchFileAlgorithm.NRCHOP).setParameterValue(rChopComboBox.getSelectedIndex());
						params.getParameter(RchFileAlgorithm.IRCHCB).setParameterValue(cbc);
						params.getParameter(RchFileAlgorithm.RCH).setParameterValue(filepath+".rch");						
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

				}

			}.start();
			
		}
	}

}
