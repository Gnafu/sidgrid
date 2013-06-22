package it.sidgrid.ogcpublish;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.sidgrid.utils.Utils;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayers;

import es.unex.sextante.gvsig.core.gvRasterLayer;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.SwingConstants;
import javax.swing.Icon;

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
 * Class to publish raster layer into local Geoserver
 *
 */
public class RestGeoserverPublish extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	final Logger LOGGER = LoggerFactory.getLogger(RestGeoserverPublish.class);
	private GeoServerRESTPublisher publisher;
	private MapControl mapCtrl;
	private final Action action = new SwingAction();
	private String resturl;
	private String restuser;
	private String restpassw;
	private String workspace;
	private JComboBox raster;
	private JComboBox comboStyle;
	private JTextField layerNamePublish;
	private JButton btnCloseTool;
	private JLabel lblCheckingGeoserver;
	private JLabel lblGeoserverStatus;
	
	
	public RestGeoserverPublish(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
		
	}
	
	private void initGUI() {
		this.setName(PluginServices.getText(this,"OGC_Publish_Layer"));
		setLayout(null);
		this.setVisible(true);
		this.setSize(405, 290);
		ImageIcon geoserv = new ImageIcon(getClass().getResource("/images/GeoServer_75.png"));
		
		raster = new JComboBox(Utils.getRasterLayers(mapCtrl));
		raster.setBounds(36, 39, 232, 27);
		add(raster);
		
		JLabel labelLayer = new JLabel("Layer to publish");
		labelLayer.setBounds(43, 22, 118, 16);
		add(labelLayer);
		
		JLabel lblNewLabel = new JLabel(geoserv);
		lblNewLabel.setBounds(278, 22, 100, 27);
		add(lblNewLabel);

		
		JButton btnNewButton = new JButton("Publish");
		btnNewButton.setAction(action);
		btnNewButton.setBounds(290, 220, 92, 29);
		add(btnNewButton);
		
		this.add(getJCombocomboStyle());
		
		JLabel labelStyle = new JLabel("Style");
		labelStyle.setBounds(46, 78, 61, 16);
		add(labelStyle);
		
		layerNamePublish = new JTextField();
		layerNamePublish.setBounds(36, 157, 232, 28);
		add(layerNamePublish);
		layerNamePublish.setColumns(10);
		
		JLabel nameLabel = new JLabel("Geoserver layer name");
		nameLabel.setBounds(46, 140, 232, 16);
		add(nameLabel);
		
		this.add(getBtnCloseTool());
		add(getLblCheckingGeoserver());
		add(getLblGeoserverStatus());
		Utils.getGeoStyleByThread(getJCombocomboStyle(), getLblGeoserverStatus());

	}

	private JComboBox getJCombocomboStyle() {
		if(comboStyle == null) {
			comboStyle = new JComboBox();
			comboStyle.setBounds(36, 94, 232, 27);
		}
		return comboStyle;
	}

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Publish_raster_output"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Publish");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			String selectedRasterLayer = (String)raster.getSelectedItem();
			FLayers layersras = mapCtrl.getMapContext().getLayers();										
			FLyrRasterSE Ras = (FLyrRasterSE)layersras.getLayer(selectedRasterLayer);
			gvRasterLayer layerRast = new gvRasterLayer();
			layerRast.create(Ras);
			
			Properties props = Utils.getDBProperties(); 					
			resturl = props.getProperty("geo.resturl");
			restuser = props.getProperty("geo.restuser");
			restpassw = props.getProperty("geo.restpassw");
			workspace = props.getProperty("geo.workspace");
						
			publisher = new GeoServerRESTPublisher(resturl, restuser, restpassw);
									
			publisher.createWorkspace(workspace);
			String layerName = layerNamePublish.getText();
			String storeName = layerNamePublish.getText();
			IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
			File worldImageFile = Ras.getFile();
			
			try {
				publisher.publishGeoTIFF(workspace, storeName, layerName, worldImageFile, viewProj.getAbrev(), ProjectionPolicy.FORCE_DECLARED, comboStyle.getSelectedItem().toString(), null);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PluginServices.getMDIManager().closeWindow(RestGeoserverPublish.this);
			JOptionPane.showMessageDialog(null,
					PluginServices.getText(this, "Run_successfull"),
					"Avviso",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(RestGeoserverPublish.this);
						}
					});
			btnCloseTool.setBounds(176, 220, 92, 29);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	private JLabel getLblCheckingGeoserver() {
		if (lblCheckingGeoserver == null) {
			lblCheckingGeoserver = new JLabel("Geoserver");
			lblCheckingGeoserver.setHorizontalAlignment(SwingConstants.CENTER);
			lblCheckingGeoserver.setBounds(278, 60, 104, 27);
		}
		return lblCheckingGeoserver;
	}
	private JLabel getLblGeoserverStatus() {
		if (lblGeoserverStatus == null) {
			lblGeoserverStatus = new JLabel((Icon) null);
			lblGeoserverStatus.setText("Checking ...");
			lblGeoserverStatus.setBounds(278, 94, 104, 27);
		}
		return lblGeoserverStatus;
	}
}
