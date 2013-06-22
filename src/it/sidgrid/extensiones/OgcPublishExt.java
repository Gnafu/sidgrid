package it.sidgrid.extensiones;

import it.sidgrid.ogcpublish.RestGeoserverPublish;
import it.sidgrid.ogcpublish.RestGeoserverStyle;
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

public class OgcPublishExt extends Extension{

	@Override
	public void execute(String actionCommand) {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = vista.getMapControl();
		
		if (actionCommand.equalsIgnoreCase("publish")){
			try {
				PluginServices.getMDIManager().addWindow(new RestGeoserverPublish(mapCtrl));
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("style")){
			try {
				PluginServices.getMDIManager().addWindow(new RestGeoserverStyle());
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
	}

	
	
	@Override
	public void initialize() {
		/* do nothing */
		
	}

	@Override
	public boolean isEnabled() {
		return true;		
	}

	@Override
	public boolean isVisible() {
		return true;
	}

}
