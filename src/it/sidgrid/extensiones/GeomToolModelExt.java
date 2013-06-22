package it.sidgrid.extensiones;

import it.sidgrid.tools.CreateEvapotranspLayer;
import it.sidgrid.tools.CreateUnsaturatedLayer;
import it.sidgrid.tools.CreateVsfModelLayer;
import it.sidgrid.tools.CreateWeatherLayer;
import it.sidgrid.tools.DrainPolylineToModelInterpolator;
import it.sidgrid.tools.GhbPolylineToModelInterpolator;
import it.sidgrid.tools.LineToChd;
import it.sidgrid.tools.PointToWell;
import it.sidgrid.tools.RiverPolylineToModelInterpolator;
import it.sidgrid.tools.RechargeZone;
import it.sidgrid.tools.PointToModelCell;
import it.sidgrid.tools.SoilTableProperties;
import it.sidgrid.tools.StreamFlowToModelCell;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.SingletonDialogAlreadyShownException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.project.documents.view.gui.View;

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
 * gvSIG extension to intersect vector values to model layer cells
 */
public class GeomToolModelExt extends Extension{

	@Override
	public void execute(String actionCommand) {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = vista.getMapControl();
		
		if (actionCommand.equalsIgnoreCase("pointwell")){
			try {
				PluginServices.getMDIManager().addWindow(new PointToWell(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("linechd")){
			try {
				PluginServices.getMDIManager().addWindow(new LineToChd(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("polyrch")){
			try {
				PluginServices.getMDIManager().addWindow(new RechargeZone(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("unsaturated")){
			try {
				PluginServices.getMDIManager().addWindow(new CreateUnsaturatedLayer(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("evapotra")){
			try {
				PluginServices.getMDIManager().addWindow(new CreateEvapotranspLayer(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("lineinterpol")){
			PluginServices.getMDIManager().addWindow(new RiverPolylineToModelInterpolator(mapCtrl));
		}
		else if (actionCommand.equalsIgnoreCase("pointriver")){
			PluginServices.getMDIManager().addWindow(new PointToModelCell(mapCtrl));
		}
		else if (actionCommand.equalsIgnoreCase("draininterpol")){
			PluginServices.getMDIManager().addWindow(new DrainPolylineToModelInterpolator(mapCtrl));
		}
		else if (actionCommand.equalsIgnoreCase("ghbinterpol")){
			PluginServices.getMDIManager().addWindow(new GhbPolylineToModelInterpolator(mapCtrl));
		}
		else if (actionCommand.equalsIgnoreCase("strflow")){
			try {
				PluginServices.getMDIManager().addWindow(new StreamFlowToModelCell(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("soiltable")){
			try {
				PluginServices.getMDIManager().addWindow(new SoilTableProperties(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("vsflayer")){
			try {
				PluginServices.getMDIManager().addWindow(new CreateVsfModelLayer(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("weather")){
			try {
				PluginServices.getMDIManager().addWindow(new CreateWeatherLayer(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/* // DEBUG
		else if(actionCommand.equalsIgnoreCase("testgui")){
		 
			PluginServices.getMDIManager().addWindow(new PointToWell(mapCtrl));
			PluginServices.getMDIManager().addWindow(new LineToChd(mapCtrl));
			PluginServices.getMDIManager().addWindow(new RechargeZone(mapCtrl));
			PluginServices.getMDIManager().addWindow(new CreateUnsaturatedLayer(mapCtrl));
			PluginServices.getMDIManager().addWindow(new CreateEvapotranspLayer(mapCtrl));
			PluginServices.getMDIManager().addWindow(new RiverPolylineToModelInterpolator(mapCtrl));
			PluginServices.getMDIManager().addWindow(new PointToModelCell(mapCtrl));
			PluginServices.getMDIManager().addWindow(new DrainPolylineToModelInterpolator(mapCtrl));
			PluginServices.getMDIManager().addWindow(new GhbPolylineToModelInterpolator(mapCtrl));
			PluginServices.getMDIManager().addWindow(new StreamFlowToModelCell(mapCtrl));
			PluginServices.getMDIManager().addWindow(new SoilTableProperties(mapCtrl));
			PluginServices.getMDIManager().addWindow(new CreateVsfModelLayer(mapCtrl));
			PluginServices.getMDIManager().addWindow(new CreateWeatherLayer(mapCtrl));

		}
		*/
	}

	
	@Override	
	public void initialize() {
		/* nothing to do here */
	}

	@Override
	public boolean isEnabled() {
		IWindow attuale = PluginServices.getMDIManager().getActiveWindow();		
		return (attuale instanceof View);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

}
