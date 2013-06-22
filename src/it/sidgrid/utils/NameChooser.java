package it.sidgrid.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
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
public class NameChooser extends AbstractCellEditor implements TableCellEditor,
           ActionListener {
	
	JButton button;
	JFileChooser Chooser;
	JDialog dialog;
	String name;
	protected static final String EDIT = "edit";

	public NameChooser() {
		button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

		//Set up the dialog that the button brings up.
		Chooser = new JFileChooser();

}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (EDIT.equals(e.getActionCommand())) {
//The user has clicked the cell, so
//bring up the dialog.
			Chooser = new JFileChooser();
		    FileFilter filter = new FileNameExtensionFilter("name files", "nam");
		    Chooser.addChoosableFileFilter(filter);

		    int ret = Chooser.showDialog(null, "Open file");

		    if (ret == JFileChooser.APPROVE_OPTION) {
		      File file = Chooser.getSelectedFile();
		      String filename = file.getAbsolutePath().toString();
	            button.setText(filename);
	            name=filename;
		    }
		  }
			
	}	

//Implement the one CellEditor method that AbstractCellEditor doesn't.
@Override
public Object getCellEditorValue() {
return name;
}

//Implement the one method defined by TableCellEditor.
@Override
public Component getTableCellEditorComponent(JTable table,
                        Object value,
                        boolean isSelected,
                        int row,
                        int column) {

return button;
}


}
