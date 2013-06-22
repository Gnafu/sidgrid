package it.sidgrid.task;

import javax.swing.*;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

/*Copyright (C) 2013  SID&GRID Project

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
 * Progress bar for sid&grid gvSIG tools
 * Indefinite method
 *
 */

public class WaitingPanel extends JDialog implements IWindow{
	
	private static final long serialVersionUID = -7822614408706605650L;
	private JProgressBar jProgressBar1;
	private JLabel jLabel;
	private WindowInfo viewInfo = null;
	public WaitingPanel() {
	
	this.setSize(285, 131);
	this.setVisible(true);
	this.setLocation(100, 100);
	getContentPane().setLayout(null);
	{
		jProgressBar1 = new JProgressBar();
		getContentPane().add(jProgressBar1);
		jProgressBar1.setIndeterminate(true);
		jProgressBar1.setBounds(22, 38, 226, 14);
		
	}
	{
		jLabel = new JLabel();
		getContentPane().add(jLabel);
		jLabel.setText("Processing data, waiting...");
		jLabel.setBounds(22, 12, 221, 15);
	}

	}
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE);
			viewInfo.setTitle(PluginServices.getText(this,"Processing"));
			
		}
		return viewInfo;
	}
	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	}
