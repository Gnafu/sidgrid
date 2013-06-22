package it.sidgrid.tools;

import it.sidgrid.sextante.tools.CreateChildAlgorithm;
import it.sidgrid.utils.Utils;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.drivers.DBException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.project.ProjectFactory;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import es.unex.sextante.core.OutputFactory;

import es.unex.sextante.gui.core.DefaultTaskMonitor;
import es.unex.sextante.gvsig.core.gvOutputFactory;
import es.unex.sextante.gvsig.core.gvVectorLayer;
import es.unex.sextante.outputs.FileOutputChannel;
import es.unex.sextante.outputs.Output;
import es.unex.sextante.core.OutputObjectsSet;
import es.unex.sextante.core.ParametersSet;
import es.unex.sextante.exceptions.GeoAlgorithmExecutionException;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import org.cresques.cts.IProjection;

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
 * @author  sid&grid
 * Main GUI to create Child model layers and Child HydrologicalModel
 * call CreateChildAlgorithm from SEXTANTE
 *
 */
public class CreateChildLayer extends javax.swing.JPanel implements IWindow {
	private AbstractAction createChild;
	private JButton Export;
	private JLabel lblROrizzontale;
	private JComboBox jComboBoxDB;
	private JFormattedTextField txtbxROrizzontale;
	private MapControl mapCtrl;
	private WindowInfo viewInfo = null;
	FLyrVect shape;
	private Properties props;   						
	private String username;  
	private String password;
	private String port;
	private String host;
	private String dbase;
	Connection conn;
	
	private int p_ul_c = 0;
	private int p_ul_r = 0;
	private int p_br_c = 0;
	private int p_br_r = 0;
	int dROrizzontale;
	int dRVerticale;

	public CreateChildLayer(MapControl mc){
		super();
		this.mapCtrl = mc;
		initGUI();
	}

	private void initGUI() {

		ComboBoxModel jComboBoxDBaseModel = new DefaultComboBoxModel(Utils.getDbase());
		jComboBoxDB = new JComboBox();
		this.add(jComboBoxDB);
		this.add(getExport());
		this.add(getLblROrizzontale());
		this.add(getTxtbxROrizzontale());
		this.add(getBtnCloseTool());

		jComboBoxDB.setModel(jComboBoxDBaseModel);
		jComboBoxDB.setSelectedIndex(0);
		jComboBoxDB.setBounds(238, 289, 140, 22);

		this.setName(PluginServices.getText(this,"Create_child_model_layers"));
		this.setSize(430, 400);
		this.setVisible(true);
		this.setLayout(null);
		this.setPreferredSize(new Dimension(410, 378));
		add(getLblGrigliaPadre());
		add(getLblRVerticale());
		add(getTxtbxRVerticale());
		add(getScrollPane());
		add(getLblModello());
		add(getModelsCBox());
		add(getLblDatabaseToUse());
		add(getLblPrefixToChild());
		add(getTxtbxChildName());
		popola_lista();
	}

	private JButton getExport() {
		if(Export == null) {
			Export = new JButton();

			Export.setBounds(298, 330, 80, 22);
			Export.setAction(getchildCreate());
		}
		return Export;
	}

	public static OutputFactory m_OutputFactory = new gvOutputFactory();
//	private JComboBox dominioCBox;
//	private JLabel lblDominio;
	private JLabel lblGrigliaPadre;
	private JLabel lblRVerticale;
	private JFormattedTextField txtbxRVerticale;
	private JScrollPane scrollPane;
	private JList list;
	private JLabel lblModello;
	private JComboBox modelsCBox;
	private JLabel lblDatabaseToUse;
	private JLabel lblPrefixToChild;
	private JFormattedTextField txtbxChildName;
	private JButton btnCloseTool;

	
	/**
	 * 
	 * MAIN ACTION TO GENERATE AND CALCULATE CHILD LAYER MODEL
	 */
		
	private AbstractAction getchildCreate(){
		if(createChild == null) {
			createChild = new AbstractAction(PluginServices.getText(this,"Run"), null) {
				@Override
				public void actionPerformed(ActionEvent evt) {
					// Valido i parametri inseriti
					// Denominatore -> Intero dispari >0
					int checkd = Integer.parseInt(getTxtbxROrizzontale().getText());
					int checkv = Integer.parseInt(getTxtbxRVerticale().getText());
					if(checkd<=0 || checkd%2==0)
					{			
						JOptionPane.showMessageDialog(null,
								PluginServices.getText(this, "Refinement_must_be_odd"),
								"Avviso", JOptionPane.WARNING_MESSAGE);
						return;
					}
					if(checkv<=0 || checkv%2==0)
					{			
						JOptionPane.showMessageDialog(null,
								PluginServices.getText(this, "VRefinement_must_be_odd"),
								"Avviso", JOptionPane.WARNING_MESSAGE);
						return;
					}

					// Controllo che il primo layer tra quelli selezionati abbia un FBitSet di selezione
					// Eventualmente lo copio su i layer successivi
					FLayers layers = mapCtrl.getMapContext().getLayers();
					Object[] olist = getList().getSelectedValues();
					FBitSet masterBS = new FBitSet();
					int ul_r = -1, ul_c = -1, br_r = -1, br_c = -1;
					
					int continua = JOptionPane.YES_OPTION;
					
					/*CREATE LPF CHILD TABLE TO PROJECT*/
					/*Data base setup*/
					props = Utils.getDBProperties();   						
					try {
						//db = props.getProperty("db.url");  
					    username = props.getProperty("db.user");  
					    password = props.getProperty("db.passwd");
					    port = props.getProperty("db.port");
					    //template = props.getProperty("db.template");
					    host = props.getProperty("db.host");
					    dbase = (String) jComboBoxDB.getSelectedItem();
					    //conn = DriverManager.getConnection(db+":"+port+"/"+dbase,username,password);				    
					    conn = Utils.getConnectionToDatabase(dbase);				    
						createLpfChild(conn, txtbxChildName.getText());
					} /*END */ catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					for(int i=0; i<olist.length; i++)
					{
						//System.out.println("La classe è "+olist[i].getClass());  // DEBUG
						
						String lname = ((gvVectorLayer)olist[i]).getName();
						ReadableVectorial rv = ((FLyrVect)layers.getLayer(lname)).getSource();
						try {
							rv.start();
							SelectableDataSource  sds = rv.getRecordset();
							FBitSet selection = rv.getRecordset().getSelection();

							if( sds.getFieldIndexByName("ROW") < 0  // il layer non ha l'attributo ROW
								|| sds.getFieldIndexByName("COL") < 0 // il layer non ha l'attributo COL
								|| sds.getFieldIndexByName("TOP") < 0 // il layer non ha l'attributo TOP
								|| sds.getFieldIndexByName("BOTTOM") < 0 ){ // il layer non ha l'attributo BOTTOM
								System.out.println("Il layer "+lname+" non è valido");
								rv.stop();
								return;
							}
							
							if(i==0)
							{
								if(selection.isEmpty()){
									continua = JOptionPane.showConfirmDialog(null, 
											"!! ATTENZIONE !!\n" +
											"Sul layer "+lname+" non è attiva nessuna selezione\n" +
											"Il raffinamento sarà su tutta la griglia\n" +
											"Vuoi continuare?", "Create Child Layer", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
									
									// stop all
									if(continua==JOptionPane.NO_OPTION){
										rv.stop();
										return;
									}
									
									// continue refinenment
									//System.out.println("******** PRINTING TOTAL CELLS ********");
									int rowFieldIndex = sds.getFieldIndexByName("ROW");
									int colFieldIndex = sds.getFieldIndexByName("COL");
									// Set all variables to minimum (1), only "br" must grow
									ul_r = 1;
									ul_c = 1;
									br_r = 1;
									br_c = 1;
									int r,c;
									long ibit_max = sds.getRowCount();
									for (int ibit = 0; ibit <= ibit_max; ibit++ ) {
										// or r = (Integer) sds.getFieldValue( ibit,rowFieldIndex);
										r = Integer.valueOf(sds.getFieldValue( ibit,rowFieldIndex).toString());
										c = Integer.valueOf(sds.getFieldValue( ibit,colFieldIndex).toString());
										//System.out.println("index: "+ibit+" ROW: "+r+" COL: "+c);
										// TODO: Metodo non ottimizzato per l'input che avremo (vedi CreateChildAlgorithm)
										//			(Il tempo di esecuzione è comunque ininfluente per l'utente)
										//ul_r= (ul_r<0)?r:(r<ul_r)?r:ul_r;
										//ul_c= (ul_c<0)?c:(c<ul_c)?c:ul_c;
										br_r= (r>br_r)?r:br_r;
										br_c= (c>br_c)?c:br_c;
																				
										p_ul_c = ul_c;
										p_ul_r = ul_r;
										p_br_c = br_c;
										p_br_r = br_r;
										
									 }
									//System.out.println("ul_r: "+ul_r+" ul_c: "+ul_c+" br_r: "+br_r+" br_c: "+br_c);
									//System.out.println("***** DONE PRINTING TOTAL CELLS *****");
								}
								else
								{
									//System.out.println("******** PRINTING SELECTED CELLS ********");
									int rowFieldIndex = sds.getFieldIndexByName("ROW");
									int colFieldIndex = sds.getFieldIndexByName("COL");
									//int ul_r = -1, ul_c = -1, br_r = -1, br_c = -1;
									int r,c;
									for (int ibit = selection.nextSetBit(0); ibit >= 0; ibit = selection.nextSetBit(ibit+1)) {
										// or r = (Integer) sds.getFieldValue( ibit,rowFieldIndex);
										r = Integer.valueOf(sds.getFieldValue( ibit,rowFieldIndex).toString());
										c = Integer.valueOf(sds.getFieldValue( ibit,colFieldIndex).toString());
										//System.out.println("index: "+ibit+" ROW: "+r+" COL: "+c);
										// TODO: Metodo non ottimizzato per l'input che avremo (vedi CreateChildAlgorithm)
										ul_r= (ul_r<0)?r:(r<ul_r)?r:ul_r;
										ul_c= (ul_c<0)?c:(c<ul_c)?c:ul_c;
										br_r= (br_r<0)?r:(r>br_r)?r:br_r;
										br_c= (br_c<0)?c:(c>br_c)?c:br_c;
																				
										//System.out.println("ul_r: "+ul_r+" ul_c: "+ul_c+" br_r: "+br_r+" br_c: "+br_c);
										p_ul_c = ul_c;
										p_ul_r = ul_r;
										p_br_c = br_c;
										p_br_r = br_r;
										
									 }
									
								}
								
								masterBS=selection;
//								System.out.println("Layer "+lname+": "+selection.cardinality()); // DEBUG
//								System.out.println("SET masterBS "+masterBS.cardinality()); // DEBUG

							}else{
								
								sds.setSelection(masterBS);
//								System.out.println("Layer "+lname+": "+sds.getSelection().cardinality()); // DEBUG
//								System.out.println("masterBS "+masterBS.cardinality()); // DEBUG
							}			
							//sds.stop();
							rv.stop();
						} catch (InitializeDriverException e1) {
							e1.printStackTrace();
							System.out.println(" *** Qualcosa e' andato storto INITIALIZE *** ");
							return;
						} catch (ReadDriverException e1) {
							e1.printStackTrace();
							System.out.println(" *** Qualcosa e' andato storto READ *** ");
							return;
						}
					}
					
					
					
// START DEBUG
					
					continua = JOptionPane.showConfirmDialog(null, 
						(getList().getMaxSelectionIndex()+1)+" model layer selected", "Confirm selection", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if(continua==JOptionPane.NO_OPTION){
						return;
					}
// END DEBUG
					
					PluginServices.getMDIManager().closeWindow(CreateChildLayer.this);
					final DefaultTaskMonitor progress = new DefaultTaskMonitor(
							PluginServices.getText(this, "creating_refinement_grid"),
							true, null );

					new Thread() {
						/* (non-Javadoc)
						 * @see java.lang.Thread#run()
						 */
						/* (non-Javadoc)
						 * @see java.lang.Thread#run()
						 */
						@Override
						public void run() {
							CreateChildAlgorithm alg_instance =  new CreateChildAlgorithm();
							//CaricaPostGis test = new CaricaPostGis();
							try {																
							    VectorialFileDriver driver = (VectorialFileDriver) LayerFactory.getDM().getDriver("gvSIG shp driver");
								dROrizzontale = Integer.parseInt(getTxtbxROrizzontale().getText());
								dRVerticale = Integer.parseInt(getTxtbxRVerticale().getText());
								/*Data base insert into gridrefine table*/
								PreparedStatement ps;
								ps = conn.prepareStatement("INSERT INTO gridrefine (row_start,col_start,row_end,col_end,lay_end,ncpp,ncppl)"+
											"VALUES (?, ?, ?, ?, ?, ?, ?)");																
								ps.setInt(1, p_ul_r);
								ps.setInt(2, p_ul_c);
								ps.setInt(3, p_br_r);
								ps.setInt(4, p_br_c);
								ps.setInt(5, (list.getMaxSelectionIndex()+1));
								ps.setInt(6, dROrizzontale);
								ps.setInt(7, dRVerticale);
								ps.execute();
								/*END */
																
								String layerName = getTxtbxChildName().getText();
								if(layerName.isEmpty()) layerName="child_model_layer_";
								
								int n=1;

								ParametersSet params = alg_instance.getParameters();
								params.getParameter(CreateChildAlgorithm.RORIZZONTALE).setParameterValue(dROrizzontale);
								params.getParameter(CreateChildAlgorithm.RVERTICALE).setParameterValue(dRVerticale);
								params.getParameter(CreateChildAlgorithm.UL_COL).setParameterValue(p_ul_c);
								params.getParameter(CreateChildAlgorithm.UL_ROW).setParameterValue(p_ul_r);
								params.getParameter(CreateChildAlgorithm.BR_COL).setParameterValue(p_br_c);
								params.getParameter(CreateChildAlgorithm.BR_ROW).setParameterValue(p_br_r);
								
								OutputObjectsSet outputs = alg_instance.getOutputObjects();
								Output out = outputs.getOutput(CreateChildAlgorithm.GRATICULE);	

								for(Object o: getList().getSelectedValues())
								{
									// DEFINE PARENT MODEL LAYER
									params.getParameter(CreateChildAlgorithm.LAYER).setParameterValue(o);									
									for(int i=1; i<=dRVerticale; i++ ){
										// A che punto siamo della creazione (1/3, 2/3, 3/3..)
										params.getParameter(CreateChildAlgorithm.PVERTICALE).setParameterValue(i);
										out.setOutputChannel(new FileOutputChannel(".shp"));
										alg_instance.execute(progress, m_OutputFactory);
										IProjection viewProj = mapCtrl.getMapContext().getViewPort().getProjection();
										String lgr_name = layerName+"_"+n++;
										shape = (FLyrVect) LayerFactory.createLayer(lgr_name, driver, new File (".shp"), viewProj);							
										Utils.saveToPostGIS(shape, dbase);
										
										/*ALTER TABLE CHILD LAYER TO SORT FEATURES*/									
										Statement lgr_layer = conn.createStatement();
										String sql = "select setval('"+lgr_name+"_gid_seq', 1, false);";										
										String sql1 = "Create table "+lgr_name+"_tmp as SELECT nextval('"+lgr_name+"_gid_seq') AS gid, * from (select \"ID\", \"ROW\", \"COL\", \"BORDER\", \"ACTIVE\", \"TOP\", \"BOTTOM\", \"STRT\", \"KX\", \"KY\", \"KZ\", \"SS\", \"SY\", \"NT\", \"NE\", \"DRYWET\", the_geom FROM "+lgr_name+" order by 1) as s";
										String sql2= "DROP TABLE "+lgr_name;																
										String sql3 = "ALTER TABLE "+lgr_name+"_tmp RENAME TO "+lgr_name;
										lgr_layer.execute(sql);
										lgr_layer.execute(sql1);
										lgr_layer.execute(sql2);
										lgr_layer.execute(sql3);							
										mapCtrl.getMapContext().getLayers().addLayer(CaricaPostGis.getLayerPostGIS(host, Integer.valueOf(port), dbase,  username, password, lgr_name, lgr_name));																		
									}						
								}
								
								/*Data base insert into gridrefine table*/
								int lpf_row=(getList().getMaxSelectionIndex()+1)*dRVerticale;
								for(int i = 1; i<=lpf_row; i++){
									PreparedStatement psInsert = conn.prepareStatement("INSERT INTO lpf_"+layerName+" (id, model_layer, layer_type, layer_average, anisotropia, value_anisotropia, layer_vka, layer_wet) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
									psInsert.setInt(1, i);
									psInsert.setString(2, layerName+"_"+i);
									psInsert.setString(3, "confined");
									psInsert.setString(4, "harmonic");
									psInsert.setString(5, "si");
									psInsert.setDouble(6, 1);
									psInsert.setInt(7, 0);
									psInsert.setString(8, "inactive");
									psInsert.execute();										
									/*END */
								}
														
								/*ADD LPF CHILD TABLE TO PROJECT*/
								loadLpfChild(username, password, port, host,
										dbase, layerName);
								/*END*/
								
								Utils.createChild(layerName);
								conn.close();
								
//								ProjectExtension ext3 = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
//								ProjectTable child = (ProjectTable) ext3.getProject().getProjectDocumentByName("gridrefine", ProjectTableFactory.registerName);
//								child.getModelo().getRecordset().reload();
								
								ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		                        ProjectTable pt = (ProjectTable) ext.getProject().getProjectDocumentByName("gridrefine", ProjectTableFactory.registerName);
		                        pt.setModel(pt.getModelo());
								
								progress.close();

							} catch (GeoAlgorithmExecutionException e) {
								e.printStackTrace();
							} catch (DriverLoadException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} 
								catch (ReadDriverException e) {
								e.printStackTrace();
							} catch (InitializeWriterException e) {
								e.printStackTrace();
							} catch (VisitorException e) {
								e.printStackTrace();
							} catch (DriverIOException e) {
								e.printStackTrace();
							} catch (DriverException e) {
								e.printStackTrace();
							} catch (SQLException e) {
								e.printStackTrace();
							} catch (DBException e) {
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchTableException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finally{
								progress.close();
							}


							// TODO : Manca l'inserimento dei dati nella tabella lpf per ogni leyer creato.
							

							
							JOptionPane.showMessageDialog(null,
									PluginServices.getText(this, "Run_successfull"),
									"Avviso", JOptionPane.INFORMATION_MESSAGE);//NON ED
						}

					}.start();


				}
			};
		}

		return createChild;
	}


	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Create_refined_model_layer"));
			
		}
		return viewInfo;
	}


	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	private JButton getBtnCloseTool() {
		if (btnCloseTool == null) {
			btnCloseTool = new JButton(
					new AbstractAction(PluginServices.getText(this, "Close")) {
						@Override
						public void actionPerformed(ActionEvent evt) {
							PluginServices.getMDIManager().closeWindow(CreateChildLayer.this);
						}
					}
				);
			btnCloseTool.setBounds(190, 330, 80, 22);
			btnCloseTool.setToolTipText(PluginServices.getText(this, "Close_window"));
		}
		return btnCloseTool;
	}
	
	private JLabel getLblROrizzontale() {
		if(lblROrizzontale == null) {
			lblROrizzontale = new JLabel();
			lblROrizzontale.setText(PluginServices.getText(this,"Horizontal_refinement"));
			lblROrizzontale.setBounds(40, 232, 182, 16);
		}
		return lblROrizzontale;
	}

	private JFormattedTextField getTxtbxROrizzontale() {
		if(txtbxROrizzontale == null) {
			txtbxROrizzontale = new JFormattedTextField(double.class);
			txtbxROrizzontale.setToolTipText("int");
			txtbxROrizzontale.setText("3");
			txtbxROrizzontale.setValue(3);
			txtbxROrizzontale.setBounds(239, 229, 137, 21);
		}
		return txtbxROrizzontale;
	}
	
	private JLabel getLblGrigliaPadre() {
		if (lblGrigliaPadre == null) {
			lblGrigliaPadre = new JLabel(PluginServices.getText(this,"Select_Model_Layers_to_refine"));
			lblGrigliaPadre.setBounds(40, 99, 319, 22);
		}
		return lblGrigliaPadre;
	}
	private JLabel getLblRVerticale() {
		if (lblRVerticale == null) {
			lblRVerticale = new JLabel();
			lblRVerticale.setText(PluginServices.getText(this,"Vertical_refinement"));
			lblRVerticale.setBounds(40, 260, 182, 16);
		}
		return lblRVerticale;
	}
	private JFormattedTextField getTxtbxRVerticale() {
		if (txtbxRVerticale == null) {
			txtbxRVerticale = new JFormattedTextField((Object) null);
			txtbxRVerticale.setToolTipText("int");
			txtbxRVerticale.setText("1");
			txtbxRVerticale.setBounds(239, 256, 137, 21);
		}
		return txtbxRVerticale;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			scrollPane.setBounds(40, 132, 340, 85);
			scrollPane.setViewportView(getList());
		}
		return scrollPane;
	}
	private JList getList() {
		if (list == null) {
			list = new JList();
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		}
		return list;
	}
	private JLabel getLblModello() {
		if (lblModello == null) {
			lblModello = new JLabel(PluginServices.getText(this,"Model"));
			lblModello.setBounds(40, 41, 89, 22);
		}
		return lblModello;
	}
	private JComboBox getModelsCBox() {
		if (modelsCBox == null) {
			modelsCBox = new JComboBox();
			modelsCBox.setBounds(243, 42, 137, 21);
			modelsCBox.setModel(new DefaultComboBoxModel(Utils.getModelsNames()));
			modelsCBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent arg0) {
					// Questo metodo su windows viene eseguito 2 volte perchè
					// arrivano 2 eventi.
					popola_lista();
				}
			});

		}
		return modelsCBox;
	}

	protected void popola_lista() {
		DefaultComboBoxModel cbm = new DefaultComboBoxModel();
		for(gvVectorLayer g:Utils.getModelLayers(getModelsCBox().getModel().getSelectedItem().toString()))
			cbm.addElement(g);
		getList().setModel(cbm);
	}
	private JLabel getLblDatabaseToUse() {
		if (lblDatabaseToUse == null) {
			lblDatabaseToUse = new JLabel(PluginServices.getText(this,"GeoDataBase"));
			lblDatabaseToUse.setBounds(40, 288, 182, 22);
		}
		return lblDatabaseToUse;
	}
	private JLabel getLblPrefixToChild() {
		if (lblPrefixToChild == null) {
			lblPrefixToChild = new JLabel(PluginServices.getText(this,"Prefix_to_child_layer_name"));
			lblPrefixToChild.setBounds(40, 74, 192, 22);
		}
		return lblPrefixToChild;
	}
	private JFormattedTextField getTxtbxChildName() {
		if (txtbxChildName == null) {
			txtbxChildName = new JFormattedTextField((Object) null);
			txtbxChildName.setToolTipText("string");
			txtbxChildName.setText("child");
			txtbxChildName.setBounds(243, 75, 137, 21);
		}
		return txtbxChildName;
	}
	

	private void createLpfChild(Connection conn, String layerName) throws SQLException {
		Statement st2 = conn.createStatement();
		String sql_create = "CREATE table "+ "LPF_"+layerName+ " (id integer NOT NULL, Model_layer character(50) NOT NULL, Layer_type character(50) NOT NULL, Layer_average character(50) NOT NULL, Anisotropia character(10) NOT NULL, Value_anisotropia numeric(50,20) NOT NULL, Layer_VKA integer NOT NULL, Layer_Wet character(10) NOT NULL, CONSTRAINT LPF_"+layerName+"pkey PRIMARY KEY (id))";
		st2.execute(sql_create);
	}

	private void loadLpfChild(String username, String password, String port,
			String host, String dbase, String layerName)
			throws DriverLoadException, NoSuchTableException,
			ReadDriverException {
		String driverInfo="";
		String [] driverList = LayerFactory.getDM().getDriverNames();
		for (int d=0; d<driverList.length; d++)
		{
			if (driverList[d].toLowerCase().contains("postgresql"))
				driverInfo = driverList[d].toString();			
		}
		
		LayerFactory.getDataSourceFactory().addDBDataSourceByTable("lpf_"+layerName, host, Integer.parseInt(port), username, password, dbase, "lpf_"+layerName, driverInfo); 
		DataSource dataSource = LayerFactory.getDataSourceFactory ().createRandomDataSource("lpf_"+layerName,DataSourceFactory.AUTOMATIC_OPENING);
		SelectableDataSource sds = new SelectableDataSource(dataSource);
		EditableAdapter auxea = new EditableAdapter();
		auxea.setOriginalDataSource(sds);
		ProjectTable projectTable = ProjectFactory.createTable("lpf_"+layerName,auxea);
		projectTable.setProjectDocumentFactory(new ProjectTableFactory());  // ARGH!!
		ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class ); 
		ext.getProject().addDocument(projectTable);
	}
}

