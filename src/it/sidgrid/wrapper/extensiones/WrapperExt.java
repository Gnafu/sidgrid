package it.sidgrid.wrapper.extensiones;


import it.sidgrid.wrapper.CflFileWrapper;
import it.sidgrid.wrapper.ChdFileWrapper;
import it.sidgrid.wrapper.DrainFileWrapper;
import it.sidgrid.wrapper.EvtFileWrapper;
import it.sidgrid.wrapper.GhbFileWrapper;
import it.sidgrid.wrapper.LgrControlFileWrapper;
import it.sidgrid.wrapper.OcontrolFileWrapper;
import it.sidgrid.wrapper.PndFileWrapper;
import it.sidgrid.wrapper.RchFileWrapper;
import it.sidgrid.wrapper.RefVsfFileWrapper;
import it.sidgrid.wrapper.RivFileWrapper;
import it.sidgrid.wrapper.RzeFileWrapper;
import it.sidgrid.wrapper.SevFileWrapper;
import it.sidgrid.wrapper.SpfFileWrapper;
import it.sidgrid.wrapper.StreamFlowFileWrapper;
import it.sidgrid.wrapper.UzfFileWrapper;
import it.sidgrid.wrapper.WellFileWrapper;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
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
public class WrapperExt extends Extension{

	@Override
	public void execute(String actionCommand) {

		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = vista.getMapControl();

		if (actionCommand.equalsIgnoreCase("bas")) {			
			OcontrolFileWrapper gui = new OcontrolFileWrapper(mapCtrl);
			gui = (OcontrolFileWrapper) PluginServices.getMDIManager().addWindow(gui);
		}

		else if (actionCommand.equalsIgnoreCase("well")) {

			PluginServices.getMDIManager().addWindow(new WellFileWrapper(mapCtrl));			       

		}

		else if (actionCommand.equalsIgnoreCase("chd")) {

			PluginServices.getMDIManager().addWindow(new ChdFileWrapper(mapCtrl));			       

		}

		else if (actionCommand.equalsIgnoreCase("rch")) {

			PluginServices.getMDIManager().addWindow(new RchFileWrapper(mapCtrl));			       

		}

		else if (actionCommand.equalsIgnoreCase("cfl")) {

			PluginServices.getMDIManager().addWindow(new CflFileWrapper(mapCtrl));			       

		}

		else if (actionCommand.equalsIgnoreCase("uzf")) {

			PluginServices.getMDIManager().addWindow(new UzfFileWrapper(mapCtrl));			       

		}

		else if (actionCommand.equalsIgnoreCase("evt")) {

			PluginServices.getMDIManager().addWindow(new EvtFileWrapper(mapCtrl));			       

		}		
		else if (actionCommand.equalsIgnoreCase("riv")) {

			PluginServices.getMDIManager().addWindow(new RivFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("drn")) {

			PluginServices.getMDIManager().addWindow(new DrainFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("ghb")) {

			PluginServices.getMDIManager().addWindow(new GhbFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("sfr2")) {

			PluginServices.getMDIManager().addWindow(new StreamFlowFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("ref")) {

			PluginServices.getMDIManager().addWindow(new RefVsfFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("spf")) {

			PluginServices.getMDIManager().addWindow(new SpfFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("pnd")) {

			PluginServices.getMDIManager().addWindow(new PndFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("sev")) {

			PluginServices.getMDIManager().addWindow(new SevFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("rze")) {

			PluginServices.getMDIManager().addWindow(new RzeFileWrapper(mapCtrl));			       

		}
		else if (actionCommand.equalsIgnoreCase("lgr")) {

			try {
				PluginServices.getMDIManager().addWindow(new LgrControlFileWrapper(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}			       

		}
		/*
		else if(actionCommand.equalsIgnoreCase("testgui")){
			// DEBUG 
			PluginServices.getMDIManager().addWindow(new OcontrolFileWrapper(mapCtrl));
			PluginServices.getMDIManager().addWindow(new WellFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new ChdFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new RchFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new CflFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new UzfFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new EvtFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new RivFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new DrainFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new GhbFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new StreamFlowFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new RefVsfFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new SpfFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new PndFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new SevFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new RzeFileWrapper(mapCtrl));			       
			PluginServices.getMDIManager().addWindow(new LgrControlFileWrapper(mapCtrl));
		}
		*///////DEBUG

	}



	@Override
	public void initialize() {
		/* do nothing */
	}

	@Override
	public boolean isEnabled() {
		return (PluginServices.getMDIManager().getActiveWindow() instanceof View);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

}

