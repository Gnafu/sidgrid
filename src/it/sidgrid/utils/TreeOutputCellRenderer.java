package it.sidgrid.utils;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.*;
import javax.swing.tree.*;

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
 
public class TreeOutputCellRenderer extends JPanel implements TreeCellRenderer{ 
    private TreeOutputSelection selectionModel; 
    private TreeCellRenderer delegate; 
    private TristateCheckBox checkBox = new TristateCheckBox(); 
 
    public TreeOutputCellRenderer(TreeCellRenderer delegate, TreeOutputSelection selectionModel){ 
        this.delegate = delegate; 
        this.selectionModel = selectionModel; 
        setLayout(new BorderLayout()); 
        setOpaque(false); 
        checkBox.setOpaque(false); 
    } 
 
 
    @Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus){ 
        Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus); 
 
        TreePath path = tree.getPathForRow(row); 
        if(path!=null){ 
            if(selectionModel.isPathSelected(path, true)) 
                checkBox.setState(TristateState.SELECTED); 
            else 
                checkBox.setState(selectionModel.isPartiallySelected(path) ? TristateState.INDETERMINATE : TristateState.DESELECTED); 
            checkBox.setEnabled(tree.isEnabled());
        } 
        removeAll(); 
        add(checkBox, BorderLayout.WEST); 
        add(renderer, BorderLayout.CENTER); 
        return this; 
    } 
} 
