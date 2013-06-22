package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.UzfFileAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

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

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvTable;
import es.unex.sextante.gvsig.core.gvVectorLayer;
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
 * UZF package
 * UzfFileAlgorithm SEXTANTE algorithm
 * 
 */

public class UzfFileWrapper extends javax.swing.JPanel implements IWindow{

	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox modelCombobox;
	private JComboBox surface;
	private JComboBox cboxUnsaturated;
	private JComboBox nuztop;
	private JLabel lblunsaturatedlLayer;
	private JSeparator separator;
	private JButton btnCloseTool;
	private JCheckBox checkEvapotra;
	private JCheckBox chckBoxCBC;
	private JCheckBox checkIURNBND;
	private final Action action = new SwingAction();
	
	public UzfFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(null);
		this.setSize(400, 381);

		modelCombobox = new JComboBox(getModel());
		modelCombobox.setToolTipText("Model Document");
		modelCombobox.setBounds(196, 46, 168, 27);
		add(modelCombobox);
		
		ComboBoxModel Surface = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		surface = new JComboBox();
		surface.setBounds(196, 85, 168, 27);
		surface.setModel(Surface);
		surface.setToolTipText("surface layer");
		add(surface);
		
		
		JLabel lblNewLabel = new JLabel(PluginServices.getText(this,"Model_project"));
		lblNewLabel.setBounds(32, 50, 134, 16);
		add(lblNewLabel);
		
		lblunsaturatedlLayer = new JLabel(PluginServices.getText(this,"Surface_model_layer"));
		lblunsaturatedlLayer.setBounds(32, 89, 158, 16);
		add(lblunsaturatedlLayer);
		
		separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(26, 276, 345, 16);
		add(separator);
		
		JButton btnRunChd = new JButton();
		btnRunChd.setAction(action);
		btnRunChd.setBounds(284, 319, 80, 22);
		add(btnRunChd);
		
		add(getBtnCloseTool());
		
		JLabel lblUnsaturated = new JLabel(PluginServices.getText(this,"Unsaturated_Model_layer"));
		lblUnsaturated.setBounds(32, 128, 158, 16);
		add(lblUnsaturated);
		
		ComboBoxModel cboxUnsaturatedModel = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		cboxUnsaturated = new JComboBox();
		cboxUnsaturated.setBounds(196, 124, 168, 27);
		cboxUnsaturated.setModel(cboxUnsaturatedModel);
		cboxUnsaturated.setToolTipText("unsaturated layer");
		add(cboxUnsaturated);
		
		checkEvapotra = new JCheckBox("Simulate evapotranspiration");
		checkEvapotra.setBounds(26, 163, 237, 23);
		add(checkEvapotra);
		
		checkIURNBND = new JCheckBox("Use SFR2 package");
		checkIURNBND.setBounds(26, 187, 237, 23);
		add(checkIURNBND);
		
		chckBoxCBC = new JCheckBox("Write cell by cell budget");
		chckBoxCBC.setBounds(86, 284, 206, 23);
		add(chckBoxCBC);
		
		nuztop = new JComboBox();
		nuztop.setBounds(50, 245, 292, 27);
		nuztop.setModel(new DefaultComboBoxModel(new String[] {"only the top Model layer", "the specified layer in variable IUZFBND", "the highest active cell in each vertical column"}));
		add(nuztop);
		
		JLabel lblRecharge = new JLabel(PluginServices.getText(this,"Recharge_to_and_discharge_from"));
		lblRecharge.setBounds(86, 222, 256, 16);
		add(lblRecharge);
		
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(UzfFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(160, 319, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Uzf_package"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
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
			String surfacelayer = (String)surface.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect surfacegeometry = (FLyrVect)layers.getLayer(surfacelayer);
			final gvVectorLayer layerSurface = new gvVectorLayer();
			layerSurface.create(surfacegeometry);
			
			String unsaturated = (String)cboxUnsaturated.getSelectedItem();
			FLayers layersUn = mapCtrl.getMapContext().getLayers();										
			FLyrVect unsaturatedgeometry = (FLyrVect)layersUn.getLayer(unsaturated);
			final gvVectorLayer layerUnsaturated = new gvVectorLayer();
			layerUnsaturated.create(unsaturatedgeometry);
			
			
			PluginServices.getMDIManager().closeWindow(UzfFileWrapper.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelCombobox.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
					
					String stressp = "stressperiod";
					ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					ProjectTable stresstable = (ProjectTable) ext2.getProject().getProjectDocumentByName(stressp, ProjectTableFactory.registerName);
					gvTable tableStress = new gvTable();
					tableStress.create(stresstable);
					
					UzfFileAlgorithm alg =  new UzfFileAlgorithm();

					int irunflag = 0;
					if (checkIURNBND.isSelected()){
						irunflag = 1;
					}
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
						
						
						ParametersSet params = alg.getParameters();
						params.getParameter(UzfFileAlgorithm.SURFACE).setParameterValue(layerSurface);	
						params.getParameter(UzfFileAlgorithm.UNSATURATED).setParameterValue(layerUnsaturated);
						params.getParameter(UzfFileAlgorithm.TABLE).setParameterValue(tableStress);
						params.getParameter(UzfFileAlgorithm.IUZFCB1).setParameterValue(cbc);
						params.getParameter(UzfFileAlgorithm.IUZFCB2).setParameterValue(cbc1);
						params.getParameter(UzfFileAlgorithm.IRUNBND).setParameterValue(checkIURNBND.isSelected());
						params.getParameter(UzfFileAlgorithm.EVAPOTRA).setParameterValue(checkEvapotra.isSelected());
						params.getParameter(UzfFileAlgorithm.COUNT).setParameterValue(doc.getStress());			
						params.getParameter(UzfFileAlgorithm.IRUNFLG).setParameterValue(irunflag);
						params.getParameter(UzfFileAlgorithm.NUZTOP).setParameterValue(nuztop.getSelectedIndex()+1);
						
						params.getParameter(UzfFileAlgorithm.UZF).setParameterValue(filepath+".uzf");						
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
