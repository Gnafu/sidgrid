package it.sidgrid.ModelDocument;

import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.exceptions.OpenException;
import com.iver.cit.gvsig.project.documents.exceptions.SaveException;
import com.iver.cit.gvsig.project.documents.view.ProjectViewBase;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;
import it.sidgrid.ModelDocument.DocumentGUI;
import it.sidgrid.ModelDocument.DocumentProperties;
import com.iver.utiles.XMLEntity;

import es.unex.sextante.gvsig.core.gvVectorLayer;

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
 * Hydro Model Document object
 *
 */
public class HydrologicalModel extends ProjectDocument {
	
	
	private static final long serialVersionUID = 1L;
	private String pathWD = "";
	private String pathEseguibile = "";
	private String pathTxtEditor = "";
	private ArrayList<gvVectorLayer> modellayer = new ArrayList<gvVectorLayer>();
	private String time = "";
	private String space = "";
	private int stress = 0;
	private double angle = 0;
	private int numModel = 0;
//	private HydrologicalModel model;

//	public void setModel(HydrologicalModel doc) {
//		model=doc;
//
//	}

	@Override
	public XMLEntity getXMLEntity() throws SaveException {
		XMLEntity xml = super.getXMLEntity();

		xml.putProperty("path", pathWD);
		xml.putProperty("eseguibile", pathEseguibile);
		xml.putProperty("editor", pathTxtEditor);
		xml.putProperty("time", time);
		xml.putProperty("space", space);
		xml.putProperty("stress", stress);
		xml.putProperty("angle", angle);
		xml.putProperty("numModel", numModel);

		for (int i = 0; i<modellayer.size(); i++)
		{
			XMLEntity layer = new XMLEntity();
			xml.addChild(layer);
			layer.putProperty("layer"+i, modellayer.get(i));
		}

		return xml;
	}

	/**
	 * Carica i dati dal'XML del progetto
	 * @throws OpenException
	 * @see com.iver.cit.gvsig.project.documents.ProjectDocument#setXMLEntity(com.iver.utiles.XMLEntity)
	 */
	@Override
	public void setXMLEntity(XMLEntity xml)
	throws XMLException, OpenException, ReadDriverException{

		this.setComment(xml.getStringProperty("comment"));
		this.setCreationDate(xml.getStringProperty("creationDate"));
		this.setName(xml.getStringProperty("name"));
		this.setOwner(xml.getStringProperty("owner"));
		this.setWorkingDirectory(xml.getStringProperty("path"));
		this.setTime(xml.getStringProperty("time"));
		this.setSpace(xml.getStringProperty("space"));
		
try
{
	
	this.setEseguibile(xml.getStringProperty("eseguibile"));
	this.setTxtEditor(xml.getStringProperty("editor"));
	this.setStress(xml.getIntProperty("stress"));
	this.setAngle(xml.getDoubleProperty("angle"));
	this.setNumModel(xml.getIntProperty("numModel"));
	

}catch(Exception e){
	System.out.println("Sono nel catch");
	this.setEseguibile("");
	this.setTxtEditor("");
	this.setAngle(0.0);
	this.setNumModel(0);
}
		

		ArrayList<ProjectDocument> pvb_list = this.getProject().getDocumentsByType(ProjectViewFactory.registerName);
		// Controllo che esista almeno una vista!!
		if(pvb_list.size()>0){
			ArrayList<FLayer> myFLayers = new ArrayList<FLayer>();
			/* 
			 * Questo presuppone che la vista e i suoi 
			 * layer siano gia' stati caricati.
			 */
			for(int i=0; i<pvb_list.size(); i++){
				ProjectViewBase pvb = (ProjectViewBase) pvb_list.get(i);
				MapContext mc = pvb.getMapContext();
				FLayers mcFLayers = mc.getLayers();
				for(int k=0; k<mcFLayers.getLayersCount(); k++)
					myFLayers.add(mcFLayers.getLayer(k));
			}

			for (int i =0; i< xml.getChildrenCount(); i++)
			{
				XMLEntity layer = xml.getChild(i);
				System.out.println(" ******  Inserisco layer"+i);  // debug
				FLyrVect layermulti = null;
				for(int j=0; j<myFLayers.size(); j++)
					if(myFLayers.get(j).getName().equals(layer.getStringProperty("layer"+i)))
						layermulti = (FLyrVect)myFLayers.get(j);
				//FLyrVect layermulti = (FLyrVect)myFLayers.getLayer(layer.getStringProperty("layer"+i));

				if(layermulti != null){
					gvVectorLayer layerInput = new gvVectorLayer();
					layerInput.create(layermulti);
					System.out.println("Nome Layer: "+layerInput.getName());  // debug
					modellayer.add(layerInput);
					//modellayer.add(i, (gvVectorLayer) layer.getObjectProperty("layer"+i));
				}else{
					System.out.println("theDocument problem! layermulti is null! No such layer is found: "+ layer.getStringProperty("layer"+i));
				}

			}

		}
	}
	/**
	 * @param p 
	 * @throws ReadDriverException 
	 * @throws XMLException 
	 * @see com.iver.cit.gvsig.project.documents.ProjectDocument#setXMLEntity(com.iver.utiles.XMLEntity)
	 */
	public void setXMLEntity03(XMLEntity xml, Project p) throws XMLException, ReadDriverException {
		super.setXMLEntity03(xml);


	}


	@Override
	public IWindow createWindow() {
		DocumentGUI gui = new DocumentGUI(this);
		//gui.setModel(this);

		return gui;
	}


	@Override
	public IWindow getProperties(){


		DocumentProperties dp = new DocumentProperties(this);

		return dp;


	}

	@Override
	public void afterRemove() {
	}

	@Override
	public void afterAdd() {
		
//		View vista = new View();     
//		int numViews=ProjectDocument.NUMS.get("ProjectView").intValue();
//		ProjectView pv = ProjectFactory.createView("sidgrid MapView_"+numViews);
//		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);        
//		vista.initialize();
//		vista.setModel(pv);		
//		ext.getProject().addDocument(pv);                
		
		

	}

	@Override
	public void exportToXML(XMLEntity root, Project project) throws SaveException {
		// TODO: nothing?

	}

	@Override
	public void importFromXML(XMLEntity root, XMLEntity typeRoot, int elementIndex, Project project, boolean removeDocumentsFromRoot) throws XMLException, OpenException {
		// TODO: nothing?

	}

//	public boolean isModified() {
//		System.out.println("Sono modificato!");
//		return true;
//	}
//
//	public void setModified(boolean modified) {
//		System.out.println("Vengo modificato!");
//	}

	public String getWorkingDirectory() {
		return pathWD;
	}

	public void setWorkingDirectory(String string) {
		pathWD = string;
		//modified = true;
		change.firePropertyChange("", null, null);
	}

	public String getTxtEditor() {
		return pathTxtEditor;
	}
	
	public void setTxtEditor(String string) {
		pathTxtEditor = string;
		//modified = true;
		change.firePropertyChange("", null, null);
	}
	
	public String getEseguibile() {
		return pathEseguibile;
	}

	public void setEseguibile(String string) {
		pathEseguibile = string;
		//modified = true;
		change.firePropertyChange("", null, null);
	}

	public ArrayList<gvVectorLayer> getLayers() {
		return modellayer;
	}

	public void setLayers(ArrayList<gvVectorLayer> array) {
		modellayer = array;
		//modified = true;
		change.firePropertyChange("", null, modellayer);
	}

	public String getTime() {
		return time;
	}

	public void setTime(String string) {
		time = string;
		//modified = true;
		change.firePropertyChange("", null, null);
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String string) {
		space = string;
		//modified = true;
		change.firePropertyChange("", null, null);
	}
	
	public int getTimeCode() {
		String tOption[] = new String[] { "undefined", "seconds", "minutes", "hours", "days", "years" };
		int tCode = 0;
		for (int i = 0; i<tOption.length; i++)
		{
			if (tOption[i].equalsIgnoreCase(time))
			{
				tCode = i;
			}
		}
		
		return tCode;
				
		}

	public int getSpaceCode() {
		String tOption[] = new String[] { "undefined", "feet", "meters", "centimeters" };
		int sCode = 0;
		for (int i = 0; i<tOption.length; i++)
		{
			if (tOption[i].equalsIgnoreCase(space))
			{
				sCode = i;
			}
		}
		
		return sCode;
				
		}
	
	public int getStress() {
		return stress;
	}

	public void setStress(int num) {
		stress = num;
		//modified = true;
		change.firePropertyChange("", null, null);
	}
	
	public double getAngle() {
		return angle;
	}

	public void setAngle(double rotation) {
		angle = rotation;
		//modified = true;
		change.firePropertyChange("", null, null);
	}
	
	public int getNumModel() {
		return numModel;
	}
	public void setNumModel(int num) {
		numModel = num;
		//modified = true;
		change.firePropertyChange("", null, null);
	}
}

