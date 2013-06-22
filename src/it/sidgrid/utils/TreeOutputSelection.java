package it.sidgrid.utils;

import java.util.ArrayList;
import java.util.Stack;

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
 

public class TreeOutputSelection extends DefaultTreeSelectionModel{ 
    private TreeModel model; 
 
    public TreeOutputSelection(TreeModel model){ 
        this.model = model; 
        setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION); 
    } 

    public boolean isPartiallySelected(TreePath path){ 
        if(isPathSelected(path, true)) 
            return false; 
        TreePath[] selectionPaths = getSelectionPaths(); 
        if(selectionPaths==null) 
            return false; 
        for(int j = 0; j<selectionPaths.length; j++){ 
            if(isDescendant(selectionPaths[j], path)) 
                return true; 
        } 
        return false; 
    } 

    public boolean isPathSelected(TreePath path, boolean dig){ 
        if(!dig) 
            return super.isPathSelected(path); 
        while(path!=null && !super.isPathSelected(path)) 
            path = path.getParentPath(); 
        return path!=null; 
    } 
 

    private boolean isDescendant(TreePath path1, TreePath path2){ 
        Object obj1[] = path1.getPath(); 
        Object obj2[] = path2.getPath(); 
        for(int i = 0; i<obj2.length; i++){ 
            if(obj1[i]!=obj2[i]) 
                return false; 
        } 
        return true; 
    } 
 
    @Override
	public void setSelectionPaths(TreePath[] pPaths){ 
        throw new UnsupportedOperationException("not implemented yet!!!"); 
    } 
 
    @Override
	public void addSelectionPaths(TreePath[] paths){ 
        for(int i = 0; i<paths.length; i++){ 
            TreePath path = paths[i]; 
            TreePath[] selectionPaths = getSelectionPaths(); 
            if(selectionPaths==null) 
                break; 
            ArrayList<TreePath> toBeRemoved = new ArrayList<TreePath>(); 
            for(int j = 0; j<selectionPaths.length; j++){ 
                if(isDescendant(selectionPaths[j], path)) 
                    toBeRemoved.add(selectionPaths[j]); 
            } 
            super.removeSelectionPaths(toBeRemoved.toArray(new TreePath[0])); 
        } 
 
        for(int i = 0; i<paths.length; i++){ 
            TreePath path = paths[i]; 
            TreePath temp = null; 
            while(areSiblingsSelected(path)){ 
                temp = path; 
                if(path.getParentPath()==null) 
                    break; 
                path = path.getParentPath(); 
            } 
            if(temp!=null){ 
                if(temp.getParentPath()!=null) 
                    addSelectionPath(temp.getParentPath()); 
                else{ 
                    if(!isSelectionEmpty()) 
                        removeSelectionPaths(getSelectionPaths()); 
                    super.addSelectionPaths(new TreePath[]{temp}); 
                } 
            }else 
                super.addSelectionPaths(new TreePath[]{ path}); 
        } 
    } 
 
    private boolean areSiblingsSelected(TreePath path){ 
        TreePath parent = path.getParentPath(); 
        if(parent==null) 
            return true; 
        Object node = path.getLastPathComponent(); 
        Object parentNode = parent.getLastPathComponent(); 
 
        int childCount = model.getChildCount(parentNode); 
        for(int i = 0; i<childCount; i++){ 
            Object childNode = model.getChild(parentNode, i); 
            if(childNode==node) 
                continue; 
            if(!isPathSelected(parent.pathByAddingChild(childNode))) 
                return false; 
        } 
        return true; 
    } 
 
    @Override
	public void removeSelectionPaths(TreePath[] paths){ 
        for(int i = 0; i<paths.length; i++){ 
            TreePath path = paths[i]; 
            if(path.getPathCount()==1) 
                super.removeSelectionPaths(new TreePath[]{ path}); 
            else 
                toggleRemoveSelection(path); 
        } 
    } 
 
    private void toggleRemoveSelection(TreePath path){ 
        Stack<TreePath> stack = new Stack<TreePath>(); 
        TreePath parent = path.getParentPath(); 
        while(parent!=null && !isPathSelected(parent)){ 
            stack.push(parent); 
            parent = parent.getParentPath(); 
        } 
        if(parent!=null) 
            stack.push(parent); 
        else{ 
            super.removeSelectionPaths(new TreePath[]{path}); 
            return; 
        } 
 
        while(!stack.isEmpty()){ 
            TreePath temp = stack.pop(); 
            TreePath peekPath = stack.isEmpty() ? path : (TreePath)stack.peek(); 
            Object node = temp.getLastPathComponent(); 
            Object peekNode = peekPath.getLastPathComponent(); 
            int childCount = model.getChildCount(node); 
            for(int i = 0; i<childCount; i++){ 
                Object childNode = model.getChild(node, i); 
                if(childNode!=peekNode) 
                    super.addSelectionPaths(new TreePath[]{temp.pathByAddingChild(childNode)}); 
            } 
        } 
        super.removeSelectionPaths(new TreePath[]{parent}); 
    } 
}
