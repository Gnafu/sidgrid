package it.sidgrid.ModelDocument;

import javax.swing.JPanel;

import javax.swing.JTextField;

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
 * Model info panel for gvSIG
 *
 */
public class InfoPanel extends JPanel {

	private JTextField jTextField = null;


	public InfoPanel() {
		super();
		initialize();
	}


	private void initialize() {
		this.setSize(489, 245);
		this.add(getJTextField(), null);
	}


	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(400,200));
			jTextField.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 72));
		}
		return jTextField;
	}
	public void setText(String s) {
		getJTextField().setText(s);
	}

} 
