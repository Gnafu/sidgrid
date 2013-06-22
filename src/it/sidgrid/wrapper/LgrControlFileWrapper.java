package it.sidgrid.wrapper;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.task.ProgressTask;
import it.sidgrid.utils.Utils;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.unex.sextante.dataObjects.IRecord;
import es.unex.sextante.dataObjects.IRecordsetIterator;
import es.unex.sextante.exceptions.IteratorException;
import es.unex.sextante.gvsig.core.gvTable;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

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
 * @category Write Local Grid Refinement Control File for SID&GRID numerical code
 */

public class LgrControlFileWrapper extends javax.swing.JPanel implements IWindow{
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	private JComboBox modelsCBox;
	private JLabel lblModello;
	private JLabel lblTable;
	private JComboBox jComboBoxTable;
	private JButton apply;
	private Action action;
	private JButton btnCloseTool;
	
	public LgrControlFileWrapper(MapControl mc){
		super();
		this.mapCtrl = mc;
		initGUI();
	}
	
	private void initGUI() {
		this.setName(PluginServices.getText(this,"Write_LGR_Control_File"));
		this.setSize(410, 192);
		this.setVisible(true);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(410, 206));
		add(getModelsCBox());
		add(getLblModello());
		add(getJComboBoxTable());
		add(getLblTable());
		add(getApply());
		add(getBtnCloseTool());
	}
	
	private JLabel getLblModello() {
		if (lblModello == null) {
			lblModello = new JLabel(PluginServices.getText(this,"Parent_Model"));
			lblModello.setBounds(40, 40, 161, 22);
		}
		return lblModello;
	}
	
	private JLabel getLblTable() {
		if (lblTable == null) {
			lblTable = new JLabel(PluginServices.getText(this,"LGR_table_(gridrefine)"));
			lblTable.setBounds(40, 76, 161, 22);
		}
		return lblTable;
	}
	
	private JComboBox getJComboBoxTable() {
		if(jComboBoxTable == null) {
			ComboBoxModel jComboBoxTableModel = 
				new DefaultComboBoxModel(
						Utils.getProjectTableNames());
			jComboBoxTable = new JComboBox();
			jComboBoxTable.setBounds(213, 75, 167, 27);
			jComboBoxTable.setModel(jComboBoxTableModel);
		}
		return jComboBoxTable;
	}
	
	private JComboBox getModelsCBox() {
		if (modelsCBox == null) {
			modelsCBox = new JComboBox();
			modelsCBox.setBounds(213, 42, 167, 21);
			modelsCBox.setModel(new DefaultComboBoxModel(Utils.getModelsNames()));
		}
		return modelsCBox;
	}
	
	private JButton getApply() {
		if(apply == null) {
			apply = new JButton();
			apply.setAction(getLGRwriteControl());
			apply.setBounds(300, 138, 80, 22);
			
		}
		return apply;
	}
	
	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(LgrControlFileWrapper.this);
						}
					});
			btnCloseTool.setBounds(40, 138, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Lgr_Control_File"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}


	
	/**
	 * WRITE LGR CONTROL FILE BY APPLY ACTION
	 *
	 */
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Apply"));
			putValue(SHORT_DESCRIPTION, "Write LGR control file");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			PluginServices.getMDIManager().closeWindow(LgrControlFileWrapper.this);
			final ProgressTask test = new ProgressTask();
			test.setMax(2);
			
			new Thread() {
				@Override
				public void run() {
					String progetto = (String) modelsCBox.getSelectedItem();
					ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
					
					
					String gridRefine = (String) jComboBoxTable.getSelectedItem();
					ProjectExtension ext2 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
					ProjectTable gridrefine = (ProjectTable) ext2.getProject().getProjectDocumentByName(gridRefine, ProjectTableFactory.registerName);
					gvTable childTable = new gvTable();
					childTable.create(gridrefine);
					childTable.getRecordCount();
						
					test.setValue(1);
					
					File file = new File(doc.getWorkingDirectory() +File.separator+ doc.getName()+".lgr");
					BufferedWriter out = null;
					try {
						FileWriter fw = new FileWriter(file);
						out = new BufferedWriter(fw);						
						out.write("# LGR Control File for SID&GRID"+ "\n");
						out.write("LGR"+ "\n");
						out.write((int) (childTable.getRecordCount() +1)+"  # NGRIDS"+ "\n");
						out.write(doc.getWorkingDirectory() +File.separator+ doc.getName()+".nam  # NAME FILE"+ "\n");
						out.write("PARENTONLY"+ "\n");
						out.write("131 132 # Data set 5 IUPBHSV, IUPBFSV"+ "\n");
						IRecordsetIterator iterTable = childTable.iterator();
						for(int i=0; i<childTable.getRecordCount(); i++){
							IRecord record = iterTable.next();
							String name = (String) record.getValue(1);
							out.write(name+"   # NAME FILE"+ "\n");
							out.write("CHILDONLY # Data set 7 GRIDSTATUS"+ "\n");
							
							String ishflg = record.getValue(2).toString();
							String ibflg = record.getValue(3).toString();						
							out.write(ishflg+" "+ibflg+" "+getUnit(name, "101", ".bfh_head")+" "+getUnit(name, "102", ".bfh_flux")+" # Data set 8 ISHFLG IBFLG IUCBHSV IUCBFSV"+ "\n");
							
							String iter = record.getValue(4).toString();
							out.write(iter+" "+"1"+" # Data set 9 MXLGRITER IOUTLGR"+ "\n");
														
							String relaxh = record.getValue(5).toString();
							String relaxf = record.getValue(6).toString();
							out.write(relaxh+" "+relaxf+" # Data set 10 RELAXH RELAXF"+ "\n");
																					
							String hcloser = record.getValue(7).toString();
							String fcloser = record.getValue(8).toString();
							out.write(hcloser+" "+fcloser+" # Data set 11 HCLOSELGR FCLOSELGR"+ "\n");
							 							
							String row_start = record.getValue(9).toString();
							String col_start = record.getValue(10).toString();
							out.write("1"+" "+row_start+" "+col_start+" # Data set 12 NPLBEG NPRBEG NPCBEG"+ "\n");
												
							String row_end = record.getValue(11).toString();
							String col_end = record.getValue(12).toString();
							String lay_end = record.getValue(13).toString();
							out.write(lay_end+" "+row_end+" "+col_end+" # Data set 13 NPLEND NPREND NPCEND"+ "\n");
														
							String ncpp = record.getValue(14).toString();
							out.write(ncpp+" # Data set 14 NCPP"+ "\n");
							
							String ncppl = record.getValue(15).toString();
							out.write(ncppl+" "+ncppl+" # Data set 15 NCPPL"+ "\n");							 
						}
						
						out.close();
						
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null,
								e.getLocalizedMessage(),
								PluginServices.getText(this, "Error"),
								JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					} catch (IteratorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}finally{
						try {
							if(out!=null)out.close();
						} catch (IOException e) { /* IGNORED */ }
						test.dispose();
					}
				}
			}
			.start();
		}
	}
	
	private Action getLGRwriteControl() {
		if (action == null) {
			action = new SwingAction();
		}
		return action;
	}
	
	 public String getUnit(String name, String Unit, String breakWord) throws IOException{
		 	File nam = new File(name);
	        BufferedReader streamNam = new BufferedReader(new FileReader(nam));
	        String lineaLetta;	       
	        String unit;
	        String[] attributi = new String[4];	        
	        while( (lineaLetta=streamNam.readLine()) != null ) 
	        {        	        	
	        	int words=0;
	            if(lineaLetta.indexOf(breakWord)!=-1){
	            	Scanner scanner = new Scanner(lineaLetta);
	            	while(scanner.hasNext()){
	            		String word = scanner.next();	            		
	                	attributi[words]=word;	 
	                	words++;                	
	            	}	            	           	
	            }	           
	        }
	       unit = attributi[1];	       
	       streamNam.close();
	       return unit;
	 }
	
}
