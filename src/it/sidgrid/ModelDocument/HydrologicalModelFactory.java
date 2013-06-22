package it.sidgrid.ModelDocument;

import com.iver.andami.PluginServices;

import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.ProjectDocumentFactory;
import com.iver.utiles.XMLEntity;

import java.text.DateFormat;

import java.util.Date;
import java.util.Hashtable;

import javax.swing.ImageIcon;

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
 * Model document factory class for gvSIG
 */
public class HydrologicalModelFactory extends ProjectDocumentFactory {
    public static String registerName = "HydrologicalModel";  // TODO: cambiare in "idrologicalModel"
    private int numDocument = 0;

    @Override
	public ImageIcon getButtonIcon() {
        return new ImageIcon(HydrologicalModel.class.getClassLoader().getResource("images/document-hydro-icon.png"));
    }

    @Override
	public ImageIcon getSelectedButtonIcon() {
        return new ImageIcon(HydrologicalModel.class.getClassLoader().getResource("images/document-hydro-icon-sel.png"));
    }

    @Override
	public ProjectDocument create(Project project) {
        HydrologicalModel m = new HydrologicalModel();
        m.setName(PluginServices.getText(this, "untitled") + " - " +
            numDocument);
       
        m.setCreationDate(DateFormat.getInstance().format(new Date()));
        numDocument++;
        
        m.setProject(project, 0);

        return m;
    }

    /**
     * @return Name of register.
     */
    @Override
	public String getRegisterName() {
        return registerName;
    }


    @Override
	public String getNameType() {
        return PluginServices.getText(null, registerName);
    }

    public static void register() {
        register(registerName, new HydrologicalModelFactory());
    }

    /**
     * @return Priority.
     */
    @Override
	public int getPriority() {
        return 4;
    }

	@Override
	@SuppressWarnings("rawtypes")
	public boolean resolveImportXMLConflicts(XMLEntity root, Project project, Hashtable conflicts) {
		return false;
	}
}
