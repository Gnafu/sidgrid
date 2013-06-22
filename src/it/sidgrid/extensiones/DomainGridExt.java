package it.sidgrid.extensiones;

import it.sidgrid.tools.CreateChildLayer;
import it.sidgrid.tools.CreateTopModelLayer;
import it.sidgrid.tools.CreateModelLayers;
import it.sidgrid.tools.LocalRefinementConfigure;
import it.sidgrid.tools.SurfaceModelLayer;

import javax.swing.JOptionPane;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.SingletonDialogAlreadyShownException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
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
 * gvSIG extension to create model top and bottom grid
 */ 
public class DomainGridExt extends Extension{

	@Override
	public void execute(String actionCommand) {
		View vista = (View) PluginServices.getMDIManager().getActiveWindow();
		MapControl mapCtrl = vista.getMapControl();

		if (actionCommand.equalsIgnoreCase("groundlayer")){
			try {
				FLayer[] Layers = mapCtrl.getMapContext().getLayers().getActives();

				boolean trovato = false;
				for(int i=0; i<Layers.length && !trovato; i++)
				{
					if(Layers[i] instanceof FLyrVect && ((FLyrVect) Layers[i]).getRecordset().getFieldIndexByName("TOP") >= 0)
						trovato = true;
				}
				if (trovato)
				{
					CreateModelLayers dlg = new CreateModelLayers(mapCtrl);
					dlg = (CreateModelLayers) PluginServices.getMDIManager().addWindow(dlg);
				}
				else
				{
					JOptionPane.showMessageDialog(null,
							"Select a valid Model Layer",
							"Create layer warning",
							JOptionPane.WARNING_MESSAGE);
					this.terminate();
				}

			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("grid")) {
			try {		
				CreateTopModelLayer prova = new CreateTopModelLayer(mapCtrl);
				prova = (CreateTopModelLayer) PluginServices.getMDIManager().addWindow(prova);
			}
			catch (java.lang.NullPointerException e) {
				JOptionPane.showMessageDialog(null,
						"The View is empty!",
						"Create layer warning",
						JOptionPane.WARNING_MESSAGE);
			}
		}

		else if (actionCommand.equalsIgnoreCase("surface_grid")) {
			try {		
				SurfaceModelLayer prova = new SurfaceModelLayer(mapCtrl);
				prova = (SurfaceModelLayer) PluginServices.getMDIManager().addWindow(prova);
			}
			catch (java.lang.NullPointerException e) {
				JOptionPane.showMessageDialog(null,
						"The View is empty!",
						"Create layer warning",
						JOptionPane.WARNING_MESSAGE);
			}
		}

		else if (actionCommand.equalsIgnoreCase("refined_grid")) {
			try {		
				CreateChildLayer prova = new CreateChildLayer(mapCtrl);
				prova = (CreateChildLayer) PluginServices.getMDIManager().addWindow(prova);
			}
			catch (java.lang.NullPointerException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"The View is empty!",
						"Create layer warning",
						JOptionPane.WARNING_MESSAGE);
			}
		}

		else if (actionCommand.equalsIgnoreCase("managegridrefine")) {
			try {		
				PluginServices.getMDIManager().addWindow(new LocalRefinementConfigure());
			}
			catch (java.lang.NullPointerException e) {
				e.printStackTrace();
			} catch (SingletonDialogAlreadyShownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	@Override
	public void initialize() {
		/* nothing to do here */
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
