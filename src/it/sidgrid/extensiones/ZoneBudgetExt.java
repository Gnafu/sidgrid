package it.sidgrid.extensiones;

import it.sidgrid.tools.CreateZoneBudget;
import it.sidgrid.tools.ExecuteZoneBudget;
import it.sidgrid.wrapper.ZoneBudWrapper;

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

public class ZoneBudgetExt extends Extension{

	@Override
	public void execute(String actionCommand) {
			try {		
				if (actionCommand.equalsIgnoreCase("runzonebud")){
					PluginServices.getMDIManager().addWindow(new ExecuteZoneBudget());
				}
				else
				{
					// Use mapCtrl only when necessary
					View vista = (View) PluginServices.getMDIManager().getActiveWindow();
					MapControl mapCtrl = vista.getMapControl();
					if (actionCommand.equalsIgnoreCase("zonebudget")){
						PluginServices.getMDIManager().addWindow(new CreateZoneBudget(mapCtrl));
					}
					else if (actionCommand.equalsIgnoreCase("zone")) {
						PluginServices.getMDIManager().addWindow(new ZoneBudWrapper(mapCtrl));			       
					}
				}
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO This exception is ignored, ever thrown?
				e.printStackTrace();
			}
	}

	@Override
	public void initialize() {
		
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
