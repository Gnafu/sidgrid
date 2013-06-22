package it.sidgrid.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
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
 * tool
 * close GUI button
 * 
 */
public class Close  implements ActionListener {
	IWindow window;
	
	
	public Close(IWindow parametro)
	{
		window=parametro;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		PluginServices.getMDIManager().closeWindow(window);
	}
}
