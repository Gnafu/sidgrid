package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.GhbFileAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.util.ArrayList;

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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;

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
 * GHB package
 * GhbFileAlgorithm SEXTANTE algorithm
 * 
 */

public class GhbFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox Sp;
	private JComboBox comboBox;
	private JComboBox comboModel;
	private JButton btnCloseTool;
//	private final Action action = new SwingAction();

	public GhbFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();

	private void initGUI() {
		this.setLayout(null);
		this.setSize(255, 360);
		// La vista DEVE avere dei layer puntuali (shapetype == 1)
		ComboBoxModel well = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		comboBox = new JComboBox();
		comboBox.setBounds(43, 104, 167, 27);
		comboBox.setModel(well);
		comboBox.addItemListener(new java.awt.event.ItemListener() {
			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent e) {
				setFields();

			}
		});
		add(comboBox);

		ComboBoxModel model = new DefaultComboBoxModel(getModel());
		comboModel = new JComboBox();
		comboModel.setBounds(43, 46, 167, 27);
		comboModel.setModel(model);
		add(comboModel);

		JLabel lblNewLabel = new JLabel(PluginServices.getText(this,"Model_project"));
		lblNewLabel.setBounds(43, 27, 167, 16);
		add(lblNewLabel);

		JLabel lblRiverLayer = new JLabel(PluginServices.getText(this,"Ghb_layer"));
		lblRiverLayer.setBounds(43, 85, 156, 16);
		add(lblRiverLayer);

		this.add(getSp());

		JLabel lblNewLabel_1 = new JLabel(PluginServices.getText(this,"First_stress_period_field"));
		lblNewLabel_1.setBounds(43, 143, 167, 16);
		add(lblNewLabel_1);
		
		final JCheckBox chckBoxCBC = new JCheckBox("Write cell by cell budget");
		chckBoxCBC.setBounds(43, 222, 206, 23);
		add(chckBoxCBC);

		JButton btnRun = new JButton(PluginServices.getText(this,"Run"));
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(comboBox.getSelectedItem() == null)
					return;
				PluginServices.getMDIManager().closeWindow(GhbFileWrapper.this);
				final WaitingPanel wait = new WaitingPanel();

				new Thread() {
					@Override
					public void run() {

						String progetto = (String) comboModel.getSelectedItem();
						ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
						HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
							

						String selectedLayerGhb = (String)comboBox.getSelectedItem();
						FLayers layer = mapCtrl.getMapContext().getLayers();										
						FLyrVect ghb = (FLyrVect)layer.getLayer(selectedLayerGhb);
						gvVectorLayer layerGhb = new gvVectorLayer();
						layerGhb.create(ghb);

						String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();

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
						
						
						GhbFileAlgorithm alg = new GhbFileAlgorithm();
						ParametersSet params = alg.getParameters();

						try {
							params.getParameter(GhbFileAlgorithm.LAYER).setParameterValue(layerGhb);
							params.getParameter(GhbFileAlgorithm.SP).setParameterValue(Sp.getSelectedIndex());
							params.getParameter(GhbFileAlgorithm.COUNT).setParameterValue(doc.getStress());
							params.getParameter(GhbFileAlgorithm.IGHBCB).setParameterValue(cbc);
							params.getParameter(GhbFileAlgorithm.GHB).setParameterValue(filepath+".ghb");

							alg.execute(null, m_OutputFactory);

							wait.dispose(); 
							
							JOptionPane.showMessageDialog(null,
									PluginServices.getText(this, "Run_successfull"),
									"Message",
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
							if(wait.isDisplayable())
								wait.dispose();
						}

					}					
				}
				.start();

			}
		});
//		btnRun.setAction(action);
		btnRun.setBounds(150, 303, 80, 22);
		add(btnRun);

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(41, 278, 169, 12);
		add(separator);
		
		add(getBtnCloseTool());

	}

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Ghb_package"));

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
/* DUPLICATED METHOD
	private String[] getType1Layers() {
		String[] solution = new String[]{};
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

	protected void setFields() {

		// final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(getFieldLayerNumericFields());
		final ComboBoxModel jComboBoxFieldModel = new DefaultComboBoxModel(Utils.getFieldLayerNumericFields(mapCtrl, (String)comboBox.getSelectedItem()));
		Sp.setModel(jComboBoxFieldModel);

	}


	private JComboBox getSp() {
		if(Sp == null) {
			Sp = new JComboBox();
			Sp.setBounds(43, 168, 167, 27);
			setFields();
		}
		return Sp;
	}


//	public String[] getFieldLayerNumericFields() {
//		String[] fields;
//		String selectedLayerVar = (String)comboBox.getSelectedItem();
//		FLayers layers = mapCtrl.getMapContext().getLayers();
//		FLyrVect layer = (FLyrVect)layers.getLayer(selectedLayerVar);
//		ArrayList<String> list = new ArrayList<String>();
//		if(layer!=null)
//			try {
//				SelectableDataSource recordset = layer.getRecordset();
//				int numFields = recordset.getFieldCount();
//				for (int i = 0; i < numFields; i++) {												
//
//
//					list.add(recordset.getFieldName(i));
//
//				}
//			} catch (ReadDriverException e) {
//				return null;
//			}
//			fields = new String[list.size()];
//			list.toArray(fields);
//			return fields;
//	}

//	private class SwingAction extends AbstractAction {
//		public SwingAction() {
//			putValue(NAME, PluginServices.getText(this,"Run"));
//
//		}
//		public void actionPerformed(ActionEvent e) {
//		}
//	}

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(GhbFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(43, 303, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
}
