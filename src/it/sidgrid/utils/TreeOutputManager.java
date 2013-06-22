package it.sidgrid.utils;

import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

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

public class TreeOutputManager extends MouseAdapter implements TreeSelectionListener{ 
    private TreeOutputSelection selectionModel; 
    private JTree tree = new JTree(); 
    int hotspot = new JCheckBox().getPreferredSize().width; 
 
    public TreeOutputManager(JTree tree){ 
        this.tree = tree; 
        selectionModel = new TreeOutputSelection(tree.getModel()); 
        tree.setCellRenderer(new TreeOutputCellRenderer(tree.getCellRenderer(), selectionModel)); 
        tree.addMouseListener(this); 
        selectionModel.addTreeSelectionListener(this); 
    } 
 
    @Override
	public void mouseClicked(MouseEvent me){ 
    	if(!tree.isEnabled())
    		return;
        TreePath path = tree.getPathForLocation(me.getX(), me.getY()); 
        if(path==null) 
            return; 
        if(me.getX()>tree.getPathBounds(path).x+hotspot) 
            return; 
 
        boolean selected = selectionModel.isPathSelected(path, true); 
        selectionModel.removeTreeSelectionListener(this); 
 
        try{ 
            if(selected) 
                selectionModel.removeSelectionPath(path); 
            else 
                selectionModel.addSelectionPath(path); 
        } finally{ 
            selectionModel.addTreeSelectionListener(this); 
            tree.treeDidChange(); 
        } 
    } 
 
    public TreeOutputSelection getSelectionModel(){ 
        return selectionModel; 
    } 
 
    @Override
	public void valueChanged(TreeSelectionEvent e){ 
        tree.treeDidChange(); 
    } 
}
