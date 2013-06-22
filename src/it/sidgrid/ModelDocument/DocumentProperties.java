package it.sidgrid.ModelDocument;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.filechooser.FileSystemView;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewBase;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;


import es.unex.sextante.gvsig.core.gvVectorLayer;

import it.sidgrid.ModelDocument.HydrologicalModel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.AbstractAction;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.Action;
import java.awt.Font;
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
 * properties for model document object
 * space
 * time
 * path
 * name
 * ...
 *
 */

public class DocumentProperties extends JPanel implements IWindow {
	private HydrologicalModel model;	
	private JButton jButton1;
	private AbstractAction abstractAction1;
	private JLabel workingLabel;
	private JLabel gwSelect;
	private JLabel gwLayer;
	private JList jList2;
	private JTextField jTextField1;
	private JFormattedTextField jStresPeriod;
	private JFormattedTextField jModelNumber;
	private JList jList1;
	private AbstractAction abstractActionClose;
	private JButton jButtonClose;
	private JSeparator jSeparator1;
	private JComboBox jComboBoxMapUnit;
	private JLabel jLabelSpace;
	private JLabel jLabelTime;
	private JComboBox jComboBoxTime;
	private JButton jButton2;
	private AbstractAction savepath;
	ProjectDocument temp_doc;
	private JScrollPane scrollPane;
	private ListModel jList1Model;
	private ListModel jList2Model;
	private final Action action = new SwingAction();
	//private JTextField textField;


	public DocumentProperties(HydrologicalModel doc) {

		model=doc;
		initialize();

	}
	/**
	 * initializes
	 *
	 */
	private void initialize() {
		{
			

			if (getLayers_names()==null)
			{
				jList1Model = new DefaultComboBoxModel();
			}
			else
			{
				jList1Model = new DefaultComboBoxModel(getLayers_names());
			}
			

			this.setLayout(null);

			this.setSize(new Dimension(430, 350));
			this.add(getJTextField1());
			this.add(getJButton1());
			this.add(getJList2());
			this.add(getGwLayer());
			this.add(getGwSelect());
			this.add(getWorkingLabel());
			this.add(getJButton2());
			this.add(getJComboBoxTime());
			this.add(getJLabelTime());
			this.add(getJLabelSpace());
			this.add(getJComboBoxMapUnit());
			this.add(getJSeparator1());
			this.add(getJButtonClose());
			this.add(getScrollPane());
			this.add(getJStressSimulate());
			this.add(getJModelNumber());
		}
		
		JButton btnNewButton = new JButton(">");
		btnNewButton.setAction(action);
		btnNewButton.setBounds(180, 123, 51, 29);
		add(btnNewButton);
		
		JLabel lblStressPeriodTo = new JLabel(PluginServices.getText(this,"Stress_Period_to_simulate"));
		lblStressPeriodTo.setFont(new Font("Dialog", Font.PLAIN, 10));
		lblStressPeriodTo.setBounds(21, 253, 136, 22);
		add(lblStressPeriodTo);
		
		final JCheckBox checkChild = new JCheckBox("is child model");
		checkChild.setFont(new Font("Dialog", Font.PLAIN, 10));
		checkChild.setBounds(241, 253, 154, 23);
		add(checkChild);
		checkChild.addItemListener(new ItemListener(){ 
			@Override
			public void itemStateChanged(ItemEvent e){
				if(checkChild.isSelected())
				jModelNumber.setEnabled(true);
				else
					jModelNumber.setEnabled(false);
				}}
		);
		

	}
	@Override
	public WindowInfo getWindowInfo() {
		WindowInfo wi= new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE |
				WindowInfo.MAXIMIZABLE);
		wi.setWidth(410);
		wi.setHeight(350);
		wi.setTitle(PluginServices.getText(this, "Model_properties") + " : " +	model.getName());
		return wi;
	}

	@Override
	public Object getWindowProfile() {
		return model;
	}

	private JTextField getJTextField1() {
		if(jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setText(model.getWorkingDirectory());
			jTextField1.setBounds(21, 30, 319, 22);
		}
		return jTextField1;
	}
	
	private JFormattedTextField getJStressSimulate() {
		if(jStresPeriod == null) {
			jStresPeriod = new JFormattedTextField();
			jStresPeriod.setValue(model.getStress());
			jStresPeriod.setBounds(101, 277, 74, 28);
		}
		return jStresPeriod;
	}
	
	private JFormattedTextField getJModelNumber() {
		if(jModelNumber == null) {
			jModelNumber = new JFormattedTextField();
			jModelNumber.setValue(model.getNumModel());
			jModelNumber.setBounds(246, 277, 90, 28);
			jModelNumber.setEnabled(false);
			
		}
		return jModelNumber;
	}

	private JButton getJButton1() {
		if(jButton1 == null) {
			jButton1 = new JButton();
			//jButton1.setText("jButton1");
			jButton1.setBounds(316, 322, 74, 22);
			jButton1.setAction(getAbstractAction1());
		}
		return jButton1;
	}

	private AbstractAction getAbstractAction1() {
		if(abstractAction1 == null) {
			abstractAction1 = new AbstractAction(PluginServices.getText(this,"Apply"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					if(jTextField1.getText().contains(" ")){
						JOptionPane.showMessageDialog(null,
								"La Working Directory non puo' contenere spazi",
								"Working Directory non valida",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					model.setWorkingDirectory(jTextField1.getText());
					model.setTime(jComboBoxTime.getSelectedItem().toString());
					model.setSpace(jComboBoxMapUnit.getSelectedItem().toString());
					model.setStress(Integer.parseInt(jStresPeriod.getText()));
					model.setNumModel(Integer.parseInt(jModelNumber.getText()));
					ArrayList<gvVectorLayer> layers = new ArrayList<gvVectorLayer>();


					Object[] selezionati = jList1.getSelectedValues();
					for(Object obj: selezionati)
						System.out.println(obj.toString());

					ListModel lm = jList2.getModel();
					int numero = lm.getSize();
					for(int i = 0; i < numero; i++)
						System.out.println(i + " "+lm.getElementAt(i));

					/*da fare se la lista 2 non  vuota*******************************/

					ArrayList<FLayer> allVectLayers = getAllVectLayers();
					//if(allVectLayers == null) System.out.println("Il allVectLayers è null!");

					for(int i = 0; i < jList2.getModel().getSize(); i++)
					{
						System.out.println(i);

							System.out.println(i + " E' selezionato");
							String checklayer = (String) jList2.getModel().getElementAt(i);
							System.out.println(checklayer);
							if(checklayer == null) System.out.println("Il checklayer è null!");

							// Ho bisogno di recuperare tutti i layer di ogni vista, non solo i nomi.
//							FLayers layer = ((ProjectViewBase) doc).getMapContext().getLayers();

							FLyrVect layermulti = null;
							for(int j=0; j<allVectLayers.size(); j++)
								if(allVectLayers.get(j).getName()== checklayer)
									layermulti = (FLyrVect)allVectLayers.get(j);
							if(layermulti == null) System.out.println("Il layermulti è null!");

							gvVectorLayer layerInput = new gvVectorLayer();
							layerInput.create(layermulti);

//							System.out.println("Prima: "+layers.toString());
							layers.add(layerInput);
//							System.out.println("Dopo: "+layers.toString());

						

					}

					model.setLayers(layers);
					PluginServices.getMDIManager().closeWindow(DocumentProperties.this);
				}
			};
		}
		return abstractAction1;
	}

	private AbstractAction getAbstractActionClose() {
		if(abstractActionClose == null) {
			abstractActionClose = new AbstractAction(PluginServices.getText(this,"Close"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PluginServices.getMDIManager().closeWindow(DocumentProperties.this);
				}
			};
		}
		return abstractActionClose;
	}

	private String[] getLayers_names() {
		String[] solution = null;

		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		ArrayList<ProjectDocument> lista = ext.getProject().getDocumentsByType("ProjectView");
		ArrayList<String> list = new ArrayList<String>();
		for (int i=0; i<lista.size(); i++)
		{
			temp_doc = lista.get(i);
			if(temp_doc instanceof ProjectView)
			{
				final FLayers layers = ((ProjectViewBase) temp_doc).getMapContext().getLayers();
				int numLayers = layers.getLayersCount();
				if(layers != null && numLayers > 0)
				{
					for(int k = 0; k < numLayers; k++)
					{
						FLayer layer = layers.getLayer(k);
						if(layer instanceof FLyrVect)
						{
							list.add(layer.getName());
						}
					}
				}
			}
		}
		
		Collections.sort(list);
		
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;

	}

	private ArrayList<FLayer> getAllVectLayers() {
		// Non posso usare FLayers come collection di FLayer perchè esige un mapcontext associato
		ArrayList<FLayer> solution = new ArrayList<FLayer>();

		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		ArrayList<ProjectDocument> lista = ext.getProject().getDocumentsByType(ProjectViewFactory.registerName);
		for (int i=0; i<lista.size(); i++)
		{
			temp_doc = lista.get(i);
			final FLayers layers = ((ProjectViewBase) temp_doc).getMapContext().getLayers();
			int numLayers = layers.getLayersCount();
			if(layers != null && numLayers > 0)
			{
				for(int k = 0; k < numLayers; k++)
				{
					FLayer layer = layers.getLayer(k);
					if(layer instanceof FLyrVect)
					{
						solution.add(layer);
					}
				}
			}

		}

		return solution;

	}

	private JList getJList2() {
		if(jList2 == null) {
			jList2Model = 
				new DefaultComboBoxModel(
						getLayers_namesList2());
			jList2 = new JList();
			jList2.setModel(jList2Model);
			jList2.setBounds(241, 76, 149, 132);
			jList2.setBorder(BorderFactory.createTitledBorder(""));
		}
		return jList2;
	}

	private JLabel getGwLayer() {
		if(gwLayer == null) {
			gwLayer = new JLabel();
			gwLayer.setText(PluginServices.getText(this, "Ground_Water_Model_Layer"));
			gwLayer.setBounds(21, 56, 154, 15);
			gwLayer.setFont(new java.awt.Font("Dialog",0,10));
		}
		return gwLayer;
	}

	private JLabel getGwSelect() {
		if(gwSelect == null) {
			gwSelect = new JLabel();
			gwSelect.setText(PluginServices.getText(this,"Selected"));
			gwSelect.setBounds(241, 56, 101, 15);
			gwSelect.setFont(new java.awt.Font("Dialog",0,10));
		}
		return gwSelect;
	}

	private JLabel getWorkingLabel() {
		if(workingLabel == null) {
			workingLabel = new JLabel();
			workingLabel.setText(PluginServices.getText(this,"Working_directory"));
			workingLabel.setBounds(21, 15, 120, 15);
			workingLabel.setFont(new java.awt.Font("Dialog",0,10));
		}
		return workingLabel;
	}

	private AbstractAction getSavepath() {
		if(savepath == null) {
			savepath = new AbstractAction("...", null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					File f = new File(jTextField1.getText());
					if(!f.exists() || !f.isDirectory())
						f = FileSystemView.getFileSystemView().getHomeDirectory();
					
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = chooser.showOpenDialog(new JPanel());
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						   System.out.println("You chose to open this directory: " +
						        chooser.getSelectedFile().getAbsolutePath());
						   		jTextField1.setText(chooser.getSelectedFile().getAbsolutePath());
						}
					

				}
			};
		}
		return savepath;
	}

	private JButton getJButton2() {
		if(jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setBounds(350, 30, 40, 22);
			jButton2.setAction(getSavepath());
		}
		return jButton2;
	}

	private JComboBox getJComboBoxTime() {
		if(jComboBoxTime == null) {
			ComboBoxModel jComboBoxTimeModel = 
				new DefaultComboBoxModel(
						new String[] { "undefined", "seconds", "minutes", "hours", "days", "years" });
			jComboBoxTime = new JComboBox();
			jComboBoxTime.setModel(jComboBoxTimeModel);
			jComboBoxTime.setBounds(21, 230, 154, 22);
			jComboBoxTime.setSelectedItem(model.getTime());
		}
		return jComboBoxTime;
	}

	private JLabel getJLabelTime() {
		if(jLabelTime == null) {
			jLabelTime = new JLabel();
			jLabelTime.setText(PluginServices.getText(this,"Time_Unit"));
			jLabelTime.setBounds(21, 214, 55, 15);
			jLabelTime.setFont(new java.awt.Font("Dialog",0,10));
		}
		return jLabelTime;
	}

	private JLabel getJLabelSpace() {
		if(jLabelSpace == null) {
			jLabelSpace = new JLabel();
			jLabelSpace.setText(PluginServices.getText(this,"Map_Unit"));
			jLabelSpace.setBounds(241, 214, 51, 15);
			jLabelSpace.setFont(new java.awt.Font("Dialog",0,10));
		}
		return jLabelSpace;
	}

	private JComboBox getJComboBoxMapUnit() {
		if(jComboBoxMapUnit == null) {
			ComboBoxModel jComboBoxMapUnitModel = 
				new DefaultComboBoxModel(
						new String[] { "undefined", "feet", "meters", "centimeters" });
			jComboBoxMapUnit = new JComboBox();
			jComboBoxMapUnit.setModel(jComboBoxMapUnitModel);
			jComboBoxMapUnit.setBounds(241, 230, 154, 22);
			jComboBoxMapUnit.setSelectedItem(model.getSpace());
		}
		return jComboBoxMapUnit;
	}

	private JSeparator getJSeparator1() {
		if(jSeparator1 == null) {
			jSeparator1 = new JSeparator();
			jSeparator1.setBounds(26, 317, 364, 6);
		}
		return jSeparator1;
	}

	private JButton getJButtonClose() {
		if(jButtonClose == null) {
			jButtonClose = new JButton();
			jButtonClose.setBounds(236, 322, 64, 22);
			jButtonClose.setAction(getAbstractActionClose());
		}
		return jButtonClose;
	}



	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			jList1 = new JList();
			jList1.setModel(jList1Model);
			scrollPane.setBounds(21, 77, 154, 134);
			scrollPane.setViewportView(jList1);
		}
		return scrollPane;
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, ">");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			
			Object[] selezionati = jList1.getSelectedValues();
			DefaultListModel listModel;
			listModel = new DefaultListModel();
			
			for (int i = 0; i < selezionati.length; i++)
			{
				listModel.addElement(selezionati[i]);
				
			}
			jList2.setModel(listModel);
		}
	}
	
	private String[] getLayers_namesList2() {
		String[] solution = null;
		ArrayList<String> list = new ArrayList<String>();
		Object[] layers = model.getLayers().toArray();
		for (int i=0; i<model.getLayers().toArray().length; i++)
		{
			list.add(layers[i].toString());
			
		}
		
		Collections.sort(list);
		
		solution = new String[list.size()];
		list.toArray(solution);
		return solution;

	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
