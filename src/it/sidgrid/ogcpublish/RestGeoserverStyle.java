package it.sidgrid.ogcpublish;

import java.io.File;
import java.util.Properties;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.sidgrid.utils.Utils;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
//import com.iver.cit.gvsig.fmap.MapControl;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Class to publish a new style SLD into local Geoserver
 *
 */
public class RestGeoserverStyle extends javax.swing.JPanel implements IWindow{
	private JTextField pathStyle;
	private WindowInfo viewInfo = null;
	final Logger LOGGER = LoggerFactory.getLogger(RestGeoserverStyle.class);
	private GeoServerRESTPublisher publisher;
//	private MapControl mapCtrl;
	private final Action action = new SwingAction();
	private String resturl;
	private String restuser;
	private String restpassw;
	private JButton btnCloseTool;
	private final Action action_1 = new StyleSelector();
	private JTextField styleName;
	private JLabel lblCheckingGeoserver;
	private JLabel lblGeoserverStatus;
	
	
	
	public RestGeoserverStyle() {
		super();
//		this.mapCtrl = mc;	    
		initGUI();
		
	}
	
	private void initGUI(){
		this.setName(PluginServices.getText(this,"OGC_Publish_Style"));
		setLayout(null);
		this.setVisible(true);
		this.setSize(395, 247);
		ImageIcon geoserv = new ImageIcon(getClass().getResource("/images/GeoServer_75.png"));
		
		JLabel labelLayer = new JLabel("Style to publish");
		labelLayer.setBounds(43, 33, 118, 16);
		add(labelLayer);
		
		JLabel lblNewLabel = new JLabel(geoserv);
		lblNewLabel.setBounds(267, 22, 100, 27);
		add(lblNewLabel);

		
		JButton btnNewButton = new JButton("Publish");
		btnNewButton.setAction(action);
		btnNewButton.setBounds(280, 182, 92, 29);
		add(btnNewButton);
		
		this.add(getBtnCloseTool());
		
		pathStyle = new JTextField();
		pathStyle.setBounds(43, 62, 225, 28);
		add(pathStyle);
		pathStyle.setColumns(10);
		pathStyle.setEditable(false);
		
		JButton styleButton = new JButton("Select");
		styleButton.setAction(action_1);
		styleButton.setBounds(280, 63, 90, 29);
		add(styleButton);
		
		styleName = new JTextField();
		styleName.setBounds(43, 130, 225, 28);
		add(styleName);
		
		
		JLabel labelStyleName = new JLabel("Style name");
		labelStyleName.setBounds(43, 102, 140, 16);
		add(labelStyleName);
		
		add(getLblCheckingGeoserver());
		add(getLblGeoserverStatus());
		Utils.getGeoserveStatusByThread(getLblGeoserverStatus());
	}

	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Publish_style"));

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
		
			
			Properties props = Utils.getDBProperties(); 					
			resturl = props.getProperty("geo.resturl");
			restuser = props.getProperty("geo.restuser");
			restpassw = props.getProperty("geo.restpassw");
						
			publisher = new GeoServerRESTPublisher(resturl, restuser, restpassw);
												
			String style = styleName.getText();
						
			File styleFile = new File(pathStyle.getText());
			
			try {
				publisher.publishStyle(styleFile, style);
			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			PluginServices.getMDIManager().closeWindow(RestGeoserverStyle.this);
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
							PluginServices.getMDIManager().closeWindow(RestGeoserverStyle.this);
						}
					});
			btnCloseTool.setBounds(176, 182, 92, 29);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private class StyleSelector extends AbstractAction {
		public StyleSelector() {
			putValue(NAME, "Select");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(new JPanel());
			if(returnVal == JFileChooser.APPROVE_OPTION) {				
				pathStyle.setText(chooser.getSelectedFile().getPath());
			}
		}
	}
	
	private JLabel getLblCheckingGeoserver() {
		if (lblCheckingGeoserver == null) {
			lblCheckingGeoserver = new JLabel("Geoserver");
			lblCheckingGeoserver.setHorizontalAlignment(SwingConstants.CENTER);
			lblCheckingGeoserver.setBounds(267, 103, 118, 27);
		}
		return lblCheckingGeoserver;
	}
	
	private JLabel getLblGeoserverStatus() {
		if (lblGeoserverStatus == null) {
			lblGeoserverStatus = new JLabel("Checking...");
			lblGeoserverStatus.setHorizontalAlignment(SwingConstants.CENTER);
			lblGeoserverStatus.setBounds(267, 137, 118, 27);
		}
		return lblGeoserverStatus;
	}

}
