package it.sidgrid.dbase;

import it.sidgrid.tools.CaricaPostGis;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import es.unex.sextante.core.AnalysisExtent;
import es.unex.sextante.core.OutputFactory;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import javax.swing.JLabel;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


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
 * GUI to export model layer from gvSIG to SID&GRID data base
 * Menu: SG_Data_Base/Import_Model_layer
 * Extension: it.sidgrid.dbase.extensiones.DbStressPeriodManageExt
 * Command: import
 */

public class LayerToDb extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private JComboBox jComboPoint;
	private JComboBox jComboDB;
	private JButton jButtonRun;
	private AbstractAction runAction1;
	public static OutputFactory m_OutputFactory = new gvOutputFactory();
	public static AnalysisExtent m_AnalysisExtent = new AnalysisExtent();
	private JButton btnCloseTool;
	FLayer shape;
	private JTextField nameField;
	private JLabel lblNewname;
	
	public LayerToDb(MapControl mc) {
		super();
		this.mapCtrl = mc;	    
		initGUI();
		
	}
	
	private void initGUI() {
		
		this.setVisible(true);
		this.setLayout(null);
		this.setSize(new Dimension(508, 118));
		this.add(getJComboVectPoint());
		this.add(getJComboDB());
		this.add(getJButtonRun());
		this.add(getBtnCloseTool());
		
		JLabel lblNewLabel = new JLabel(PluginServices.getText(this,"SG_layer"));
		lblNewLabel.setBounds(10, 13, 71, 16);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel(PluginServices.getText(this,"SG_Data_Base"));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.TRAILING);
		lblNewLabel_1.setBounds(238, 14, 103, 16);
		add(lblNewLabel_1);
		add(getNameField());
		add(getLblNewname());

}
	
	
	private JComboBox getJComboVectPoint() {
		if(jComboPoint == null) {
			ComboBoxModel jComboVectParamsModel = 
				new DefaultComboBoxModel(Utils.getVectLayers(mapCtrl));
			jComboPoint = new JComboBox();
			jComboPoint.setModel(jComboVectParamsModel);
			jComboPoint.setBounds(91, 11, 137, 22);
		}
		return jComboPoint;
	}
	
	private JComboBox getJComboDB(){
		if(jComboDB == null) {
			ComboBoxModel jComboDBModel =  new DefaultComboBoxModel(Utils.getDbase());
			jComboDB = new JComboBox();
			jComboDB.setModel(jComboDBModel);
			jComboDB.setBounds(351, 11, 131, 22);
		}
		return jComboDB;
	}

	
	
	private JButton getJButtonRun() {
		if(jButtonRun == null) {
			jButtonRun = new JButton();
			jButtonRun.setBounds(402, 57, 80, 22);
			jButtonRun.setAction(getRunAction());
		}
		return jButtonRun;
	}
	
	private AbstractAction getRunAction() {
		if(runAction1 == null) {
			runAction1 = new AbstractAction(PluginServices.getText(this, "Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					
					if(getNameField().getText().isEmpty())
						return;
					
					PluginServices.getMDIManager().closeWindow(LayerToDb.this);
					String cell = (String)jComboPoint.getSelectedItem();
//					FLayers layers = mapCtrl.getMapContext().getLayers();										
//					FLyrVect cellGeomtry = (FLyrVect)layers.getLayer(cell);										
										
					FLayer export = mapCtrl.getMapContext().getLayers().getLayer(cell);

					try {
						String dbase = (String) jComboDB.getSelectedItem();
						Properties props = Utils.getDBProperties();					  
					    String username = props.getProperty("db.user");  
					    String password = props.getProperty("db.passwd");
					    String port = props.getProperty("db.port");
					    String host = props.getProperty("db.host");

						Utils.saveToPostGIS( (FLyrVect) export, dbase, getNameField().getText());
					
						
						// TODO: removeLayer not working if layer is inside a Group
						mapCtrl.getMapContext().getLayers().removeLayer(export);
						mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS( host, Integer.parseInt(port), dbase, username, password, getNameField().getText(), getNameField().getText()));
						
					} catch (ReadDriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InitializeWriterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (VisitorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DriverLoadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DriverIOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DriverException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
		}
		return runAction1;
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(LayerToDb.this);
						}
					});
			btnCloseTool.setBounds(312, 57, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Import_Model_layer"));
			
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	private JTextField getNameField() {
		if (nameField == null) {
			nameField = new JTextField();
			nameField.setBounds(91, 58, 137, 20);
			nameField.setColumns(10);
		}
		return nameField;
	}
	private JLabel getLblNewname() {
		if (lblNewname == null) {
			lblNewname = new JLabel(PluginServices.getText(this, "Name"));
			lblNewname.setBounds(10, 61, 71, 14);
		}
		return lblNewname;
	}
}
