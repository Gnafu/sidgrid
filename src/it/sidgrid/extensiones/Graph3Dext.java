package it.sidgrid.extensiones;

import it.sidgrid.tools.ModelGraph3D;
import it.sidgrid.tools.ModelGraph3DRaster;

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

/**
 * This extension generates a 3D model of the model_layer
 * 
 */
public class Graph3Dext extends Extension {

	/* (non-Javadoc)
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
	@Override
	public void execute(String arg0) {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
        MapControl mapCtrl = vista.getMapControl();
        System.out.println(arg0);
        if(arg0.equalsIgnoreCase("raster"))
    		PluginServices.getMDIManager().addWindow(new ModelGraph3DRaster(mapCtrl));
        else
        	PluginServices.getMDIManager().addWindow(new ModelGraph3D(mapCtrl));
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
