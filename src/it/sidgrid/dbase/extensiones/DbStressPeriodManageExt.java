package it.sidgrid.dbase.extensiones;

import javax.swing.JOptionPane;

import it.sidgrid.dbase.DbRchStressManage;
import it.sidgrid.dbase.DbStressPeriodManage;
import it.sidgrid.dbase.DbUnsaturatedStressManage;
import it.sidgrid.dbase.DbWellStressManage;
import it.sidgrid.dbase.LayerToDb;
import it.sidgrid.dbase.StressTablePostgres;

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
/**
 * 
 * gvSIG extension to manage stress period parameter into data base table
 */
public class DbStressPeriodManageExt extends Extension{

	@Override
	public void execute(String actionCommand) {
		if (actionCommand.equalsIgnoreCase("mstressp")) {
			try {
				PluginServices.getMDIManager().addWindow(new DbStressPeriodManage());
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
		
		else if (actionCommand.equalsIgnoreCase("wellstress")) {
			try {
				PluginServices.getMDIManager().addWindow(new DbWellStressManage());
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
		
		else if (actionCommand.equalsIgnoreCase("stressdefine")) {
			try {
				PluginServices.getMDIManager().addWindow(new StressTablePostgres());
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
		
		else if (actionCommand.equalsIgnoreCase("rchstress")) {
			try {
				PluginServices.getMDIManager().addWindow(new DbRchStressManage());
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
		else if (actionCommand.equalsIgnoreCase("unsaturatedstress")) {
			try {
				PluginServices.getMDIManager().addWindow(new DbUnsaturatedStressManage());
			} catch (SingletonDialogAlreadyShownException e) {
				e.printStackTrace();
			}
		}
		
		else if (actionCommand.equalsIgnoreCase("import")) {
			if(PluginServices.getMDIManager().getActiveWindow() instanceof View)
			{
				try {
					View vista = (View) PluginServices.getMDIManager().getActiveWindow();
					MapControl mapCtrl = vista.getMapControl();
					PluginServices.getMDIManager().addWindow(new LayerToDb(mapCtrl));
				} catch (SingletonDialogAlreadyShownException e) {
					e.printStackTrace();
				}
			}else
			{
				JOptionPane.showMessageDialog(null, PluginServices.getText(this, "select_a_view"));
			}
		}
		
		
	}

	@Override
	public void initialize() {
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

