package it.sidgrid.wrapper;

import java.util.ArrayList;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.ChdFileAlgorithm;
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

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JSeparator;
import java.awt.Color;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

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
 * CHD package
 * ChdFileAlgorithm SEXTANTE algorithm
 * 
 */

public class ChdFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox chdLayer;
	private JComboBox sheadCombobox;
	private JComboBox modelCombobox;
	private JLabel labelModel;
	private JSeparator separator;
	private final Action action = new SwingAction();
	
	public ChdFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(null);
		this.setSize(222, 284);

		ComboBoxModel chdlayer = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		chdLayer = new JComboBox();
		chdLayer.setBounds(19, 106, 168, 27);
		chdLayer.setModel(chdlayer);
		chdLayer.setToolTipText("Chd Model layer");
		chdLayer.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent e) {
				setFields();

			}
		});
		add(chdLayer);
		
		
		
		JLabel chdLabel = new JLabel(PluginServices.getText(this,"Chd_Model_Layer"));
		chdLabel.setHorizontalAlignment(SwingConstants.LEFT);
		chdLabel.setBounds(29, 84, 139, 16);
		add(chdLabel);
		
		sheadCombobox = new JComboBox();
		sheadCombobox.setToolTipText(PluginServices.getText(this,"Shead_first_field"));
		sheadCombobox.setBounds(19, 171, 168, 27);
		setFields();
		add(sheadCombobox);
		
		JLabel sheadLabel = new JLabel(PluginServices.getText(this,"First_Shead_field"));
		sheadLabel.setBounds(29, 155, 139, 16);
		add(sheadLabel);
		
		modelCombobox = new JComboBox(getModel());
		modelCombobox.setToolTipText("Model Document");
		modelCombobox.setBounds(19, 45, 168, 27);
		add(modelCombobox);
		
		labelModel = new JLabel(PluginServices.getText(this,"Model_project"));
		labelModel.setBounds(29, 22, 149, 16);
		add(labelModel);
		
		JButton btnRunChd = new JButton();
		btnRunChd.setAction(action);
		btnRunChd.setBounds(116, 225, 80, 22);
		add(btnRunChd);
		
		separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(19, 210, 168, 16);
		add(separator);
		
		add(getBtnCloseTool());

	}
	
	protected void setFields() {

		// final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(getFieldLayerNumericFields());
		final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(Utils.getFieldLayerNumericFields(mapCtrl, (String)chdLayer.getSelectedItem()));
		sheadCombobox.setModel(jComboBoxFieldModel);

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
			viewInfo.setTitle(PluginServices.getText(this, "Chd_package"));

		}
		return viewInfo;
	}
	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	private JButton btnCloseTool;

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(ChdFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(19, 225, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Run"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String Chd = (String)chdLayer.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect chdgeometry = (FLyrVect)layers.getLayer(Chd);
			final gvVectorLayer layerChd = new gvVectorLayer();
			layerChd.create(chdgeometry);
			
			PluginServices.getMDIManager().closeWindow(ChdFileWrapper.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelCombobox.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
					
					ChdFileAlgorithm alg =  new ChdFileAlgorithm();

					try {
						
						ParametersSet params = alg.getParameters();
						params.getParameter(ChdFileAlgorithm.LAYER).setParameterValue(layerChd);
						params.getParameter(ChdFileAlgorithm.COUNT).setParameterValue(doc.getStress());
						params.getParameter(ChdFileAlgorithm.SHEAD).setParameterValue(sheadCombobox.getSelectedIndex());
						params.getParameter(ChdFileAlgorithm.CHD).setParameterValue(filepath+".chd");						
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
						// TODO serve?
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

}
