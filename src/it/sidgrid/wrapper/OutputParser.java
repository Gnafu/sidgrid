package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.utils.TreeOutputManager;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;

import org.cresques.cts.IProjection;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.raster.dataset.NotSupportedExtensionException;
import org.gvsig.raster.dataset.io.RasterDriverException;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import java.awt.Dimension;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import javax.swing.SpringLayout;
import javax.swing.JSeparator;

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
 * GUI
 * SID&GRID mandatory wrapper input file
 * Output reader for 
 * hydraulic head distribution
 * water table drawdown distribution
 */

public class OutputParser extends javax.swing.JPanel implements IWindow{
	// TODO: Si puo' rimuovere il primo elemento dei parametri, lo tengo solo per leggibilita'
	// Implementarlo come Attributes?
	private String[] parametri_head = new String[]{"SID&GRID - View hydraulic head distribution",".fhd","HEAD", "_head_"};
	private String[] parametri_drawdown = new String[]{"SID&GRID - View water table drawdown distribution", ".fdn", "DRAWDOWN", "_drawdown_"};
	private WindowInfo viewInfo = null;
	private MapControl mapCtrl;
	private JComboBox ModelLayer_cbox;
	private AbstractAction showAction;
	private AbstractAction carica_datiAction1;
//	FLayer layer;
	FLayer geoTif;
	private JButton jButtonShow;
	private JTree tree1;
	private DefaultMutableTreeNode root1 ;
	private TreeOutputManager checkTreeManager1;
	private JTree tree2;
	private DefaultMutableTreeNode root2 ;
	private TreeOutputManager checkTreeManager2;
	private JScrollPane scrollPane1;
	private JScrollPane scrollPane2;
	private JCheckBox chckbx1;
	private JCheckBox chckbx2;
	private SpringLayout springLayout;
	private JSeparator separator;
	private JButton btnCarica;

	/**
	 * Called by it.sidgrid.wrapper.extensiones.OutputParserExt, action-command"output"
	 * Menu text SG_Configure/Tools/View_Model_Output
	 * @param mc MapControl
	 */
	public OutputParser(MapControl mc){
		super();
		this.mapCtrl = mc;
		initGUI();		
	}

	@Override
	public WindowInfo getWindowInfo() {
		// Questa funzione viene chiamata dal sistema grafico in continuazione
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this,"View_Model_Output"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	private void initGUI(){

		//getWindowInfo().setTitle(parametri[0]);
		this.setName(PluginServices.getText(this,"View_Model_Output"));	
		this.setVisible(true);
		this.setSize(new Dimension(610, 375));
		springLayout = new SpringLayout();
		setLayout(springLayout);
		this.add(getJComboModel());
		this.add(getJButtonShow());

		//		// Create the nodes.
		//		root = new DefaultMutableTreeNode("Carica il documento");
		//
		//		tree = new JTree(root);
		//		tree.setShowsRootHandles(true);
		//		tree.setAutoscrolls(true);
		//		tree.setBounds(12, 57, 261, 172);
		//		// makes your tree as CheckTree
		//		checkTreeManager = new CheckTreeManager(tree); 
		//		//checkTreeManager.hashCode();
		//		add(tree);

		btnCarica = new JButton();
		springLayout.putConstraint(SpringLayout.NORTH, btnCarica, 24, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnCarica, 280, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, btnCarica, 395, SpringLayout.WEST, this);
		btnCarica.setAction(getCarica_dati());
		add(btnCarica);

		scrollPane1 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane1, 102, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane1, -12, SpringLayout.SOUTH, this);
		add(scrollPane1);
		scrollPane2 = new JScrollPane();
		springLayout.putConstraint(SpringLayout.EAST, scrollPane1, -12, SpringLayout.WEST, scrollPane2);
		springLayout.putConstraint(SpringLayout.WEST, scrollPane2, 322, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, scrollPane2, -12, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, scrollPane2, -12, SpringLayout.EAST, this);
		springLayout.putConstraint(SpringLayout.NORTH, scrollPane2, 102, SpringLayout.NORTH, this);
		add(scrollPane2);

		popola_alberi();
		add(getChckbx1());
		add(getChckbx2());
		add(getSeparator());


	}
	private JCheckBox getChckbx1(){
		if(chckbx1 == null){
			chckbx1 = new JCheckBox("Hydraulic head distribution");
			springLayout.putConstraint(SpringLayout.WEST, scrollPane1, -278, SpringLayout.EAST, chckbx1);
			springLayout.putConstraint(SpringLayout.NORTH, chckbx1, 69, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, chckbx1, 12, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, chckbx1, 290, SpringLayout.WEST, this);
			chckbx1.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
				       int state = arg0.getStateChange();
				        if (state == ItemEvent.SELECTED) {
				        	scrollPane1.setEnabled(true);
				        	tree1.setEnabled(true);
				        }else{
				        	scrollPane1.setEnabled(false);
				        	tree1.setEnabled(false);
				        }
				        	
				}
			});
			//chckbx1.setSelected(true);
			chckbx1.setEnabled(false);
		}
		return chckbx1;
	}
	private JCheckBox getChckbx2(){
		if(chckbx2 == null){
			chckbx2 = new JCheckBox("Drawdown distribution");
			springLayout.putConstraint(SpringLayout.WEST, chckbx2, 0, SpringLayout.WEST, scrollPane2);
			springLayout.putConstraint(SpringLayout.SOUTH, chckbx2, -6, SpringLayout.NORTH, scrollPane2);
			springLayout.putConstraint(SpringLayout.EAST, chckbx2, 600, SpringLayout.WEST, this);
			chckbx2.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
				       int state = arg0.getStateChange();
				        if (state == ItemEvent.SELECTED) {
				        	scrollPane2.setEnabled(true);
				        	tree2.setEnabled(true);
				        }else{
				        	scrollPane2.setEnabled(false);
				        	tree2.setEnabled(false);
				        }
				        	
				}
			});
			//chckbx2.setSelected(true);
			chckbx2.setEnabled(false);
		}
		return chckbx2;
	}
	
	private JComboBox getJComboModel() {
		if(ModelLayer_cbox == null) {
			ComboBoxModel ModelLayerModel = 
				new DefaultComboBoxModel(Utils.getModelsNames());
			ModelLayer_cbox = new JComboBox();
			springLayout.putConstraint(SpringLayout.NORTH, ModelLayer_cbox, 24, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, ModelLayer_cbox, 12, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, ModelLayer_cbox, 46, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, ModelLayer_cbox, 270, SpringLayout.WEST, this);
			ModelLayer_cbox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					// System.out.println("cambiato in "+ModelLayer_cbox.getSelectedItem() );
					popola_alberi();
					chckbx1.setSelected(false);
					chckbx1.setEnabled(false);
					chckbx2.setSelected(false);
					chckbx2.setEnabled(false);
				}
			});
			ModelLayer_cbox.setModel(ModelLayerModel);
		}
		return ModelLayer_cbox;
	}

	private JButton getJButtonShow() {
		if(jButtonShow == null) {
			jButtonShow = new JButton();
			springLayout.putConstraint(SpringLayout.NORTH, jButtonShow, 24, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, jButtonShow, 435, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, jButtonShow, 46, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, jButtonShow, 550, SpringLayout.WEST, this);
			jButtonShow.setAction(getShowAction());

		}
		return jButtonShow;
	}

	private AbstractAction getShowAction() {
		if(showAction == null) {
			showAction = new AbstractAction(PluginServices.getText(this, "View_data"), null) {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO non mi piace
					if(ModelLayer_cbox.getItemCount()<=0) return;
					if(chckbx1.isSelected())
						elabora_dati(tree1, checkTreeManager1, parametri_head);
					if(chckbx2.isSelected())
						elabora_dati(tree2, checkTreeManager2, parametri_drawdown);
				}

			};
		}
		return showAction;
	}

	private AbstractAction getCarica_dati() {
		if(carica_datiAction1 == null) {
			carica_datiAction1 = new AbstractAction(PluginServices.getText(this, "Load_Data"), null) {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(ModelLayer_cbox.getItemCount()<=0) return;
					carica_dati(tree1, root1, parametri_head, chckbx1);
					carica_dati(tree2, root2, parametri_drawdown, chckbx2);
				}

			};
		}
		return carica_datiAction1;
	}

	private void popola_alberi(){
		// TODO Metodo ORRIBILE per sostituire i dati visualizzati.
		root1 = new DefaultMutableTreeNode(PluginServices.getText(this, "Data_not_loaded"));
		if(tree1 != null)
			scrollPane1.getViewport().remove(tree1);
		tree1 = new JTree(root1);
		scrollPane1.setViewportView(tree1);
		tree1.setShowsRootHandles(true);
		checkTreeManager1 = new TreeOutputManager(tree1); 
		scrollPane1.repaint();

		root2 = new DefaultMutableTreeNode("Carica il documento");
		if(tree2 != null)
			scrollPane2.getViewport().remove(tree2);
		tree2 = new JTree(root2);
		scrollPane2.setViewportView(tree2);
		tree2.setShowsRootHandles(true);
		checkTreeManager2 = new TreeOutputManager(tree2); 
		scrollPane2.repaint();
		

	}


	/**
	 * Parse output files and list their content in JTree
	 * @param tree
	 * @param root
	 * @param parametri
	 * @param checkbox
	 */
	public void carica_dati(JTree tree, DefaultMutableTreeNode root, String[] parametri, JCheckBox checkbox) {

		String progetto = (String) ModelLayer_cbox.getSelectedItem();
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);

		try {
			System.out.println("Path: "+doc.getWorkingDirectory());  // DEBUG
			System.out.println("Name: "+doc.getName());  // DEBUG
			File my_file = new File(doc.getWorkingDirectory() +"/"+ doc.getName()+parametri[1]);
			checkbox.setEnabled(my_file.exists());
			checkbox.setSelected(my_file.exists());		
			if(!my_file.exists()) return;
			// FileInputStream fis = new FileInputStream(doc.getPath() +"/"+ doc.getName()+".fdn");
			FileInputStream fis = new FileInputStream(my_file);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);

			String linea = br.readLine();
			int n_linea=0;
			int n_intestazione=0;   	
			root.setUserObject(doc.getName());
			int current_row = 0;
			int current_column = 0;				
			String[] attributi = null;
			while (linea != null) {
				//System.out.println("n: "+n_linea+" c_row: "+current_row+" c_column: " + current_column);   //DEBUG

				// Controllo che la precedente analisi abbia dato risultato positivo
				// TODO: Non posso controllare perchè il nuovo formato del file va a capo ogni 10 elementi
				/*
				if(attributi != null)
				{
					if(Integer.parseInt(attributi[5])!=current_column  && current_row!=0)
						System.out.println(" *** Le colonne non coincidono!");
				}
				*/
				
				if (linea.contains(parametri[2]) == true)    			   
				{   	

					// Controllo che la precedente analisi abbia dato risultato positivo
					// TODO: Non posso controllare perchè il nuovo formato del file va a capo ogni 10 elementi
					/*
					if(attributi != null)
					{
						if(Integer.parseInt(attributi[6])!=current_row)
							System.out.println(" *** Le righe non coincidono!");
					}
					 */
					
					// Parse della linea
					Scanner scanner = new Scanner(linea);
					int n_attribute = 0;
					// [  0 ,   1   ,  2  ,  3  ,     4     ,    5   ,  6  ,   7  ,      8      ]
					// [step, period, ukn1, ukn2, "DRAWDOWN", Colonna, Riga, Layer, pyton_format]
					attributi = new String[9];
					if ( scanner.hasNext() ){
						while(scanner.hasNext()){
							String attribute = scanner.next();
							//System.out.println("Leggo : '" + attribute + "', e trimmato : '" + attribute.trim() +"'");  // DEBUG
							attributi[n_attribute++] = attribute;
						}
						if(n_attribute==9){
							// System.out.println("Intestazione letta correttamente");
							current_row = 0;
							current_column = 0;
						}else{
							System.out.println("*** Intestazione CORROTTA!! ***");
							throw new IOException("Intestazione Corrotta");
						}
					}
					else {
						System.out.println("Empty or invalid line. Unable to process.");
					}
					// aggiunta del nodo
					root.add(new DefaultMutableTreeNode("Step "+attributi[0]+" Period "+attributi[1]+" Layer "+attributi[7]));
					n_intestazione++;   			    
				}
				else {
					if(n_intestazione==0)
						System.out.println(" *** Manca intestazione!");
					// Parse della linea della tabella
					current_column = 0;
					Scanner scanner = new Scanner(linea);
					while(scanner.hasNext()){
						scanner.next();
						current_column++;
					}
					current_row++;

				}
				//	System.out.println("linea PRE: "+linea);   //DEBUG
				linea=br.readLine();
				//	System.out.println("linea POST: "+linea);  //DEBUG
				n_linea++;
			}
			tree.expandPath(tree.getPathForRow(0));
			//serve invalidate?
			tree.invalidate();

			br.close();
			isr.close();
			fis.close();

		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Algoritmo effettivo
	/**
	 * Read selected values and load respective layers into current View
	 */
	public void elabora_dati(JTree tree, TreeOutputManager checkTreeManager, String[] parametri) {

		// System.out.println("this is hard part");
		ArrayList<TreePath> lista_selezionati = getAllCheckedPaths(checkTreeManager,tree);
		//		for(Object tp:lista_selezionati)
		//			System.out.println(tp + " - " +((TreePath)tp).getLastPathComponent());
		
		if(lista_selezionati.isEmpty()) return;

		String[] selezionati = new String[lista_selezionati.size()];
		int k=0;
		for(TreePath tp:lista_selezionati)
			selezionati[k++] = tp.getLastPathComponent().toString();

		// DEBUG
		System.out.println("Risultato liste");
		for(String s:selezionati)
			System.out.println(s);

		ArrayList<File> files = new ArrayList<File>(selezionati.length);
		
		String progetto = (String) ModelLayer_cbox.getSelectedItem();
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);

		PluginServices.getMDIManager().closeWindow(OutputParser.this);

		try {
			FileInputStream fis = new FileInputStream(doc.getWorkingDirectory() +"/"+ doc.getName()+ parametri[1]);
			InputStreamReader isr=new InputStreamReader(fis);
			BufferedReader br=new BufferedReader(isr);

			String linea = br.readLine();
			int n=0;
			int sp=0;   	
			FileWriter fw = null;
			File file = null;
			boolean can_write = false;
			//System.out.println("can_write = false");

			while (linea != null) {
				if (linea.contains(parametri[2]) == true)    			   
				{   							    			    
					can_write = false;
					//System.out.println("can_write = false");
					// Parse della linea
					Scanner scanner = new Scanner(linea);
					int n_attribute = 0;
					// [  0 ,   1   ,  2  ,  3  ,     4     ,    5   ,  6  ,   7  ,      8      ]
					// [step, period, ukn1, ukn2, "DRAWDOWN", Colonna, Riga, Layer, pyton_format]
					String[] attributi = new String[9];
					if ( scanner.hasNext() ){
						while(scanner.hasNext()){
							String name = scanner.next();
							attributi[n_attribute++] = name;
						}
					}
					else {
						// Can the "scanner.hasNext()" return false if "linea.contains(parametri[2]) == true" ?
						System.out.println("View - Empty or invalid line. Unable to process.");
					}								

					String attuale = "Step "+attributi[0]+" Period "+attributi[1]+" Layer "+attributi[7];
					for(String s:selezionati)
						if(s.equalsIgnoreCase(attuale)) 
							can_write = true;

					if(can_write){
						File file_geo = new File(doc.getWorkingDirectory() +"/"+ doc.getName()+".geo");
						// TODO: Controllare PRIMA che il file GEO esista!!
						FileInputStream fisGeo = new FileInputStream(file_geo);
						InputStreamReader isrGeo =new InputStreamReader(fisGeo);
						BufferedReader brGeo =new BufferedReader(isrGeo);
						String lineaGeo = brGeo.readLine();

						file = new File(doc.getWorkingDirectory() +"/"+ doc.getName()+parametri[3]+"_"+attributi[0]+"_"+attributi[1]+"_"+attributi[7]+ ".asc");
						files.add(file);
						fw = new FileWriter(file);    			      

						while (lineaGeo != null)
						{
							// System.out.println("ScrivoG:"+lineaGeo);
							fw.write(lineaGeo + "\n");
							lineaGeo=brGeo.readLine();
						}		
					}
					sp++;   			    
				}
				else {
					if(n==0) System.out.println("La prima riga non è un'intestazione!!\n"+linea);
					if(fw!=null && can_write){
						// System.out.println("Scrivo:"+linea);
						fw.write(linea);
						fw.flush();
					}
				}
				linea=br.readLine();
				n++;
			}
			if(fw!= null)
				fw.close();					
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println("*************************************************************************");

		// Cerco i file .asc e li converto in tif, poi li carico nella vista
//**		//File f = new File (doc.getWorkingDirectory());
		// TODO: questa è una finezza, ri-controllare che i file esistano prima di usarli,
		//		 possono passare alcuni secondi da qui all'effettivo accesso
//**		//File[] files = f.listFiles();

		for (int i=0; i<files.size(); i++){
			System.out.println("elaborando il file:"+files.get(i).getName());
			if(files.get(i).getName().contains(parametri[3]) && files.get(i).getName().endsWith(".asc"))
			{
				//System.out.println("files[i] Non sostituito "+files[i].getAbsolutePath());
				//System.out.println("files[i] Sostituito "+files[i].getAbsolutePath().substring(0, files[i].getAbsolutePath().lastIndexOf(".asc"))+".tif");
				File check = new File(files.get(i).getAbsolutePath().substring(0, files.get(i).getAbsolutePath().lastIndexOf(".asc"))+".tif");
				if(check.exists())
					check.delete();
				try {
					//long inizio = System.nanoTime();
					IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
					//long fine = System.nanoTime();
					//System.out.println("Projection got in "+(fine-inizio)+" NANOseconds");
					
					// devo crearlo per forza per avere il file rmf?
					// Non c'è un metodo più leggero?
					FLyrRasterSE.createLayer(files.get(i).getName(), new File(files.get(i).getAbsolutePath()), viewProj);
					
					// asc and tif are FULL PATH
					String asc = files.get(i).getAbsolutePath();
					String tif = asc.substring(0, asc.lastIndexOf(".asc"))+".tif";
					// name is just the filename
					String name = files.get(i).getName().substring(0, files.get(i).getName().lastIndexOf(".asc"))+".tif";				
					// ***********  CONVERT  ************
					//Utils.convertASCtoGeoTIFF(raster.getFile().getAbsolutePath(), doc.getName());
					try {
						Utils.convertASCtoGeoTIFF(files.get(i).getAbsolutePath(), doc.getName(), viewProj);
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					geoTif = FLyrRasterSE.createLayer(name, new File(tif), viewProj);
					mapCtrl.getMapContext().getLayers().addLayer(geoTif);					
					
				} catch (LoadLayerException e) {
					e.printStackTrace();
				} catch (NotSupportedExtensionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RasterDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}

		}
	}


	// ***********  TOOLS  ************

//	private String[] getListaLayer() {
//		String[] solution = null;
//
//		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
//		ArrayList<ProjectDocument> lista = ext.getProject().getDocumentsByType(HydrologicalModelFactory.registerName);
//		ArrayList<String> list = new ArrayList<String>();
//		for (int i=0; i<lista.size(); i++)
//		{
//			ProjectDocument doc = lista.get(i);
//			list.add(doc.getName());
//		}
//		solution = new String[list.size()];
//		list.toArray(solution);	    
//		return solution;
//
//	}


	// TODO IMPLEMENTARE NELLE CLASSI DEL CHECKTREE!!!

	public void addChildPaths(TreePath path, TreeModel model, List<TreePath> result_list){
		Object item = path.getLastPathComponent();
		int childCount = model.getChildCount(item);
		for(int i = 0; i<childCount; i++)
			result_list.add(path.pathByAddingChild(model.getChild(item, i)));
	}


	public ArrayList<TreePath> getDescendants(TreePath paths[] , TreeModel model){
		ArrayList<TreePath> result = new ArrayList<TreePath>();
		if(paths != null){
			Stack<TreePath> pending = new Stack<TreePath>();
			pending.addAll(Arrays.asList(paths));
			TreePath path;
			while(!pending.isEmpty()){
				path = pending.pop();
				addChildPaths(path, model, pending);
				result.add(0,path);
			}
		}
		return result;
	}

	/**
	 * Return all the checked path in the given JTree
	 * @param TreeOutputManager manager
	 * @param JTree tree
	 * @return ArrayList<TreePath> 
	 */
	public ArrayList<TreePath> getAllCheckedPaths(TreeOutputManager manager, JTree tree){
		return getDescendants(manager.getSelectionModel().getSelectionPaths(), tree.getModel());
	}
	
	private JSeparator getSeparator() {
		if (separator == null) {
			separator = new JSeparator();
			springLayout.putConstraint(SpringLayout.NORTH, separator, 71, SpringLayout.SOUTH, btnCarica);
			springLayout.putConstraint(SpringLayout.WEST, separator, 294, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, separator, 185, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, separator, -28, SpringLayout.WEST, scrollPane2);
		}
		return separator;
	}
	
}
