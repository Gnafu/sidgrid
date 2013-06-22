package it.sidgrid.extensiones;

import it.sidgrid.tools.ModelGridToModelGrid;
import it.sidgrid.tools.RasterToModLayer;
import it.sidgrid.tools.RasterToModLayerInterpolator;
import it.sidgrid.tools.VectorToModelGrid;

import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
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
public class IncrociaVarExt extends Extension {
	/**
	 * @author sid&grid
	 * gvSIG extension to intersect raster values to model layer cells
	 *
	 */
	@Override
	public void initialize() {
       
	}
	
	@Override
	public void execute(String actionCommand) {
		
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
        MapControl mapCtrl = vista.getMapControl();
		
		if (actionCommand.equalsIgnoreCase("vec")) {			
			VectorToModelGrid dlg = new VectorToModelGrid(mapCtrl);
	        dlg = (VectorToModelGrid) PluginServices.getMDIManager().addWindow(dlg);
		}
		
		else if (actionCommand.equalsIgnoreCase("rast")) {
			
			RasterToModLayer gui = new RasterToModLayer(mapCtrl);		       
	        gui = (RasterToModLayer) PluginServices.getMDIManager().addWindow(gui);
		}
		else if (actionCommand.equalsIgnoreCase("rastinterpolator")) {
			
			RasterToModLayerInterpolator gui = new RasterToModLayerInterpolator(mapCtrl);		       
	        gui = (RasterToModLayerInterpolator) PluginServices.getMDIManager().addWindow(gui);
		}					
		else if (actionCommand.equalsIgnoreCase("gridtogrid")) {
			
			ModelGridToModelGrid gui = new ModelGridToModelGrid(mapCtrl);		       
	        gui = (ModelGridToModelGrid) PluginServices.getMDIManager().addWindow(gui);
		}	
	}

	
	/**
	 * @see com.iver.andami.plugins.Extension#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return (PluginServices.getMDIManager().getActiveWindow() instanceof View);
	}

	/**
	 * @see com.iver.andami.plugins.Extension#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return true;
	}
}

