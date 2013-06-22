package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.sextante.wrapperTools.CflFileAlgorithm;
import it.sidgrid.task.WaitingPanel;
import it.sidgrid.utils.Utils;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
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

import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;
import es.unex.sextante.exceptions.WrongParameterIDException;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;

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
 * CFL cascading flow
 * CflFileAlgorithm SEXTANTE algorithm
 */

public class CflFileWrapper extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox modelCombobox;
	private JComboBox cflLayer;
	private JLabel lblSurfaceModelLayer;
	private JSeparator separator;
	private JButton btnCloseTool;
	private final Action action = new SwingAction();
	
	
	public CflFileWrapper(MapControl mc) {
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setLayout(null);
		this.setSize(222, 232);

		modelCombobox = new JComboBox(getModel());
		modelCombobox.setToolTipText("Model Document");
		modelCombobox.setBounds(19, 45, 168, 27);
		add(modelCombobox);
		
		ComboBoxModel cfllayer = new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
		cflLayer = new JComboBox();
		cflLayer.setBounds(19, 106, 168, 27);
		cflLayer.setModel(cfllayer);
		cflLayer.setToolTipText("Chd Model layer");
		add(cflLayer);
		
		
		JLabel lblNewLabel = new JLabel(PluginServices.getText(this,"Model_project"));
		lblNewLabel.setBounds(26, 17, 134, 16);
		add(lblNewLabel);
		
		lblSurfaceModelLayer = new JLabel(PluginServices.getText(this,"Surface_model_layer"));
		lblSurfaceModelLayer.setBounds(29, 84, 158, 16);
		add(lblSurfaceModelLayer);
		
		separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(19, 145, 168, 16);
		add(separator);
		
		JButton btnRunChd = new JButton();
		btnRunChd.setAction(action);
		btnRunChd.setBounds(116, 173, 80, 22);
		add(btnRunChd);
		
		add(getBtnCloseTool());

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
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(CflFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(19, 173, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Cfl_package"));

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

		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String Chd = (String)cflLayer.getSelectedItem();
			FLayers layers = mapCtrl.getMapContext().getLayers();										
			FLyrVect chdgeometry = (FLyrVect)layers.getLayer(Chd);
			final gvVectorLayer layerChd = new gvVectorLayer();
			layerChd.create(chdgeometry);
			
			PluginServices.getMDIManager().closeWindow(CflFileWrapper.this);
			final WaitingPanel test = new WaitingPanel();
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelCombobox.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
					
					CflFileAlgorithm alg =  new CflFileAlgorithm();

					try {
						
						ParametersSet params = alg.getParameters();
						params.getParameter(CflFileAlgorithm.LAYER).setParameterValue(layerChd);					
						params.getParameter(CflFileAlgorithm.CFL).setParameterValue(filepath+".cfl");						
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
