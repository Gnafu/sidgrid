package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.WellFileAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
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
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
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
 * WELL package
 * WellFileAlgorithm SEXTANTE algorithm
 * 
 */

public class WellFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox Sp;
	private JComboBox comboBox;
	private JComboBox comboModel;
	private JButton btnCloseTool;
//	private final Action action = new SwingAction();

	public WellFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();

	private void initGUI() {
		this.setLayout(null);
		this.setSize(255, 360);
		// La vista DEVE avere dei layer puntuali (shapetype == 1)
		ComboBoxModel wellcbox = new DefaultComboBoxModel(getType1Layers());
		comboBox = new JComboBox();
		comboBox.setBounds(43, 104, 167, 27);
		comboBox.setModel(wellcbox);
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

		JLabel lblWellLayer = new JLabel(PluginServices.getText(this,"Well_layer"));
		lblWellLayer.setBounds(43, 85, 167, 16);
		add(lblWellLayer);

		this.add(getSp());

		JLabel checkLabelCBC = new JLabel(PluginServices.getText(this,"First_stress_period_field"));
		checkLabelCBC.setBounds(43, 143, 167, 16);
		add(checkLabelCBC);

		JButton btnRun = new JButton(PluginServices.getText(this,"Run"));
		btnRun.setBounds(150, 303, 80, 22);
		add(btnRun);

		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(41, 278, 169, 12);
		add(separator);
		
		add(getBtnCloseTool());
		
		final JCheckBox chckBoxCBC = new JCheckBox("Write cell by cell budget");
		chckBoxCBC.setBounds(43, 222, 206, 23);
		add(chckBoxCBC);
		
		
		btnRun.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(comboBox.getSelectedItem() == null)
					return;
				PluginServices.getMDIManager().closeWindow(WellFileWrapper.this);
				final WaitingPanel wait = new WaitingPanel();

				new Thread() {
					@Override
					public void run() {

						String progetto = (String) comboModel.getSelectedItem();
						ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
						HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
							

						String selectedLayerWell = (String)comboBox.getSelectedItem();
						FLayers layer = mapCtrl.getMapContext().getLayers();										
						FLyrVect well = (FLyrVect)layer.getLayer(selectedLayerWell);
						gvVectorLayer layerWell = new gvVectorLayer();
						layerWell.create(well);

						String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();						
						WellFileAlgorithm alg = new WellFileAlgorithm();
						ParametersSet params = alg.getParameters();

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
							System.out.println(cbc);
							params.getParameter(WellFileAlgorithm.LAYER).setParameterValue(layerWell);
							params.getParameter(WellFileAlgorithm.SP1).setParameterValue(Sp.getSelectedIndex());
							params.getParameter(WellFileAlgorithm.WELCB).setParameterValue(cbc);
							params.getParameter(WellFileAlgorithm.COUNT).setParameterValue(doc.getStress());
							params.getParameter(WellFileAlgorithm.WELL).setParameterValue(filepath+".wel");

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
		

	}

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(WellFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(43, 303, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Well_package"));

		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	/**
	 * This is an actual usefull method Returns the FLyrVect ONLY if they are of Type = 1
	 * @return
	 */
	private String[] getType1Layers() {
		String[] solution = new String[]{};
		final FLayers layers = mapCtrl.getMapContext().getLayers();
		int numLayers = layers.getLayersCount();
		if(numLayers > 0)
		{
			ArrayList<String> list = new ArrayList<String>();
			for(int i = 0; i < numLayers; i++)
			{
				FLayer layer = layers.getLayer(i);
				if(layer instanceof FLyrVect)
				{
					try {
						if (((FLyrVect) layer).getShapeType() == 1){
							list.add(layer.getName());
						}
					} catch (ReadDriverException e) {
						e.printStackTrace();
					}

				}
			}
			solution = new String[list.size()];
			list.toArray(solution);
		}
		return solution;		
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

}
