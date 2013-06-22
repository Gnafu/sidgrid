package it.sidgrid.ModelDocument;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.SingletonWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import it.sidgrid.ModelDocument.HydrologicalModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import javax.swing.Action;
import javax.swing.SpringLayout;

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
 * Model document object GUI for gvSIG
 *
 */

public class DocumentGUI extends JPanel implements SingletonWindow {
//  private InfoPanel infoPanel=null;
	private HydrologicalModel model;
	private JTextField textField;
	private JTextField pathTextEditor;
	private JCheckBox chckbxBasFile;
	private JCheckBox chckbxLpfFile;
	private JCheckBox chckbxDisFile;
	private JCheckBox chckbxOcFile;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JButton btnAvvia;
//	private JButton btnRead;
	private Icon tick;
	private Icon cross;
	private Icon play;
	private HashMap<String, JCheckBox> base;
	private HashMap<String, JCheckBox> optional;
	private HashMap<String, String[]> pacchetti;
	private JLabel Wdlabel;
	private JButton btnAggiorna;
	private JScrollPane consolePane;
	private JTextPane textPane;
	private JButton btnViewNAM;
	private JButton btnCreaNam;
	private final Action action = new SwingAction();
	private SpringLayout springLayout;
	private JLabel lblNam;

	/**
	 * initializes
	 * @param doc 
	 *
	 */
	public DocumentGUI(HydrologicalModel doc) {
		super();

		this.setModel(doc);
		initialize();
		springLayout = new SpringLayout();
		setLayout(springLayout);
	

		textField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textField, 24, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, textField, 85, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, textField, 53, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, textField, 585, SpringLayout.WEST, this);
		textField.setText((model.getEseguibile().isEmpty() ? "vuoto" : model.getEseguibile()));
		add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("path");
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 26, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 597, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton, 53, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton, 690, SpringLayout.WEST, this);
		btnNewButton.setAction(new AbstractAction("Process", null) {
			public void actionPerformed(ActionEvent evt) {
				File f= new File(textField.getText());
				// TODO : forse si puo' omettere il controllo null, jfilechooser è robusto
				if(!f.exists() && f.getParentFile()==null)
					f = FileSystemView.getFileSystemView().getHomeDirectory();
				else
					if(!f.isDirectory())f=f.getParentFile();

				JFileChooser chooser = new JFileChooser(f);
//				FileNameExtensionFilter filter = new FileNameExtensionFilter("Eseguibili", "exe");
//				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(new JPanel());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
					textField.setText(chooser.getSelectedFile().getPath());
				}
			}
		});
		add(btnNewButton);

		add(getWdlabel());
		add(getChckbxBasFile());
		add(getChckbxLpfFile());
		add(getChckbxDisFile());
		add(getChckbxOcFile());
		add(getScrollPane());
		add(getBtnAvvia());
		// Possiamo inserire elementi nello scrollpane nel seguente modo:
		JPanel jp = getPanel();
		optional = new HashMap<String, JCheckBox>(21,1);
		//Queste sono tutte le etichette dei pacchetti implementati
//		String[] etichette_pacchetti = new String[]{"CHD", "WEL", "PCG", "UZF", "REF", "PND", "SPF", "SEV", "RZE", "ATS", "RCH", "SFR", "LAK", "CFL", "SIP", "DE4", "RIV", "DRN", "EVT", "GHB", "HFB"};
		String[] etichette_pacchetti = new String[]{"CHD", "WEL", "PCG", "UZF", "REF", "PND", "SPF", "SEV", "RZE", "RCH", "SFR", "CFL", "RIV", "DRN", "EVT", "GHB"};
		for(int i=0; i<etichette_pacchetti.length; i++) {
			JCheckBox check = new JCheckBox(etichette_pacchetti[i]);
			check.setIconTextGap(50-check.getFontMetrics(check.getFont()).stringWidth(check.getText()));
			check.setHorizontalTextPosition(SwingConstants.LEADING);
			jp.add(check,i);
			optional.put(etichette_pacchetti[i], check);
		}
		// TODO: valutare se è il caso di inserire il nome completo del file nel HashMap
		base = new HashMap<String, JCheckBox>(4,1);
		base.put(".bas", chckbxBasFile);
		base.put(".dis", chckbxDisFile);
		base.put(".lpf", chckbxLpfFile);
		base.put(".oc", chckbxOcFile);
		add(getBtnAggiorna());
		add(getConsolePane());
		add(getBtnViewNAM());
		add(getBtnCreaNam());

		JButton openLstButton = new JButton();
		springLayout.putConstraint(SpringLayout.NORTH, openLstButton, 262, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, openLstButton, 580, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, openLstButton, 298, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, openLstButton, 690, SpringLayout.WEST, this);
		openLstButton.setAction(action);
		add(openLstButton);

		pathTextEditor = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, pathTextEditor, 63, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, pathTextEditor, 85, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, pathTextEditor, 91, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, pathTextEditor, 585, SpringLayout.WEST, this);
		pathTextEditor.setText((model.getTxtEditor().isEmpty() ? "vuoto" : model.getTxtEditor()));
		add(pathTextEditor);
		pathTextEditor.setColumns(10);

//		JButton button = new JButton("...");
//		button.setBounds(597, 65, 93, 29);
//		add(button);

		JButton editorNewButton = new JButton("path");
		springLayout.putConstraint(SpringLayout.NORTH, editorNewButton, 65, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, editorNewButton, 597, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, editorNewButton, 94, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.EAST, editorNewButton, 690, SpringLayout.WEST, this);
		editorNewButton.setAction(new AbstractAction("Editor", null) {
			public void actionPerformed(ActionEvent evt) {
				File f= new File(pathTextEditor.getText());
				// TODO : forse si puo' omettere il controllo null, jfilechooser è robusto
				if(!f.exists() && f.getParentFile()==null)
					f = FileSystemView.getFileSystemView().getHomeDirectory();
				else
					if(!f.isDirectory())f=f.getParentFile();

				JFileChooser chooser = new JFileChooser(f);
//				FileNameExtensionFilter filter = new FileNameExtensionFilter("Eseguibili", "exe");
//				chooser.setFileFilter(filter);
				int returnVal = chooser.showOpenDialog(new JPanel());
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
					pathTextEditor.setText(chooser.getSelectedFile().getPath());
				}
			}
		});
		add(editorNewButton);
		add(getLblNam());

		// TODO: ORRIBILE costruttore, organizzare meglio il codice!!!
		checkFiles();

	}
	/**
	 * This method initializes this
	 *
	 */
	private void initialize() {
		this.setSize(new Dimension(704, 595));
		// Ho messo qui il caricamento delle risorse per gestire una volta sola le eccezioni
		play = new ImageIcon(getClass().getResource("/images/play.png"));
		tick = new ImageIcon(getClass().getResource("/images/tick.png"));
		cross = new ImageIcon(getClass().getResource("/images/cross.png"));
 
		// Lista di dati che verranno scritti nel file NAM, la chiave deve coincidere con quella in etichette_pacchetti
		pacchetti = new HashMap<String, String[]>(21,1);
		// necessari
		pacchetti.put("BAS",new String[]{"BAS6","21",".bas OLD\n"}); // Era 13
		pacchetti.put("DIS",new String[]{"DIS","20",".dis OLD\n"}); // Era 12
		pacchetti.put("LPF",new String[]{"LPF","23",".lpf OLD\n"}); // Era 14
		pacchetti.put("OC",new String[]{"OC","22",".oc OLD\n"});  // Era 39
		// output
		pacchetti.put("LST",new String[]{"LIST","100",".lst REPLACE\n"}); // Era 11
		pacchetti.put("CBC",new String[]{"DATA(BINARY)","90",".cbc REPLACE\n"}); // Era 9  // TODO aggiungere alla lista
		pacchetti.put("FHD",new String[]{"DATA","101",".fhd REPLACE\n"});  // Era 37 TODO : generare tramite .oc
		pacchetti.put("FDN",new String[]{"DATA","102",".fdn REPLACE\n"});  // Era 38 TODO : generare tramite .oc
		pacchetti.put("BFH_HEAD",new String[]{"DATA","131",".bfh_head REPLACE\n"});
		pacchetti.put("BFH_FLUX",new String[]{"DATA","132",".bfh_flux REPLACE\n"});
//		pacchetti.put("IWELCB",new String[]{"DATA","70",".welcb REPLACE\n"});
//		pacchetti.put("IGHBCB",new String[]{"DATA","72",".ghbcb REPLACE\n"});
//		pacchetti.put("IDRNCB",new String[]{"DATA","71",".drncb REPLACE\n"});
//		pacchetti.put("IRCHCB",new String[]{"DATA","73",".rchcb REPLACE\n"});
//		pacchetti.put("IRIVCB",new String[]{"DATA","74",".rivcb REPLACE\n"});
//		pacchetti.put("ISTCB1",new String[]{"DATA","75",".stcb1 REPLACE\n"});
//		pacchetti.put("ISTCB2",new String[]{"DATA","76",".stcb2 REPLACE\n"});
//		pacchetti.put("ILPFCB",new String[]{"DATA","77",".lpfcb REPLACE\n"});
		// opzionali
//		pacchetti.put("ATS",new String[]{""});
		pacchetti.put("CFL",new String[]{"CFL","35",".cfl OLD\n"});
		pacchetti.put("CHD",new String[]{"CHD","25",".chd OLD\n"}); // Era 17
//		pacchetti.put("DE4",new String[]{""});
		pacchetti.put("DRN",new String[]{"DRN","36",".drn OLD\n"});
		pacchetti.put("EVT",new String[]{"EVT","37",".evt OLD\n"});
		pacchetti.put("GHB",new String[]{"GHB","38",".ghb OLD\n"});
//		pacchetti.put("HFB",new String[]{""});
//		pacchetti.put("LAK",new String[]{""});
		pacchetti.put("PCG",new String[]{"PCG","24",".pcg OLD\n"}); // Era 18
		pacchetti.put("PND",new String[]{"PND","28",".pnd OLD\n"});
		pacchetti.put("RCH",new String[]{"RCH","34",".rch OLD\n"});
		pacchetti.put("REF",new String[]{"REF","27",".ref OLD\n"});
		pacchetti.put("RIV",new String[]{"RIV","42",".riv OLD\n"});
		pacchetti.put("RZE",new String[]{"RZE","31",".rze OLD\n"});
		pacchetti.put("SEV",new String[]{"SEV","30",".sev OLD\n"});
		pacchetti.put("SFR",new String[]{"SFR","32",".sfr OLD\n"});
		pacchetti.put("SIP",new String[]{""});
		pacchetti.put("SPF",new String[]{"SPF","29",".spf OLD\n"});
		pacchetti.put("UZF",new String[]{"UZF","26",".uzf OLD\n"});
		pacchetti.put("WEL",new String[]{"WEL","33",".wel OLD\n"}); // Era 20



	}

	public void setModel(HydrologicalModel doc) {
		model=doc;
	}
	public WindowInfo getWindowInfo() {
		WindowInfo wi= new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE |WindowInfo.MAXIMIZABLE);

		wi.setWidth(710);
		wi.setHeight(580);
		wi.setTitle(PluginServices.getText(this, "HydrologicalModel") + " : " +model.getName());
		return wi;
	}
	public Object getWindowModel() {
		return model;
	}
	@Override
	public Object getWindowProfile() {
		return model;
	}

	private void setCheckbox(JCheckBox cbox){
		cbox.setIcon(cross);
		cbox.setDisabledSelectedIcon(tick);
		cbox.setIconTextGap(50-cbox.getFontMetrics(cbox.getFont()).stringWidth(cbox.getText()));
		cbox.setHorizontalTextPosition(SwingConstants.LEADING);
		cbox.setEnabled(false);
	}

	private JCheckBox getChckbxBasFile() {
		if (chckbxBasFile == null) {
			chckbxBasFile = new JCheckBox("Bas File");
			springLayout.putConstraint(SpringLayout.NORTH, chckbxBasFile, 137, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, chckbxBasFile, 95, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, chckbxBasFile, 182, SpringLayout.WEST, this);
			setCheckbox(chckbxBasFile);
		}
		return chckbxBasFile;
	}
	private JCheckBox getChckbxLpfFile() {
		if (chckbxLpfFile == null) {
			chckbxLpfFile = new JCheckBox("Lpf File");
			springLayout.putConstraint(SpringLayout.NORTH, chckbxLpfFile, 163, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, chckbxLpfFile, 95, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, chckbxLpfFile, 182, SpringLayout.WEST, this);
			setCheckbox(chckbxLpfFile);
		}
		return chckbxLpfFile;
	}
	private JCheckBox getChckbxDisFile() {
		if (chckbxDisFile == null) {
			chckbxDisFile = new JCheckBox("Dis File");
			springLayout.putConstraint(SpringLayout.NORTH, chckbxDisFile, 189, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, chckbxDisFile, 95, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, chckbxDisFile, 182, SpringLayout.WEST, this);
			setCheckbox(chckbxDisFile);
		}
		return chckbxDisFile;
	}
	private JCheckBox getChckbxOcFile() {
		if (chckbxOcFile == null) {
			chckbxOcFile = new JCheckBox("Oc File");
			springLayout.putConstraint(SpringLayout.NORTH, chckbxOcFile, 217, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, chckbxOcFile, 95, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, chckbxOcFile, 182, SpringLayout.WEST, this);
			setCheckbox(chckbxOcFile);
		}
		return chckbxOcFile;
	}
	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();
			springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 133, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, scrollPane, 236, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, scrollPane, 298, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, scrollPane, 396, SpringLayout.WEST, this);
			scrollPane.setViewportView(getPanel());
		}
		return scrollPane;
	}
	private JPanel getPanel() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new GridLayout(0, 1, 0, 0));
			panel.setBorder(new EmptyBorder(1, 10, 0, 0));
		}
		return panel;
	}
	private JButton getBtnAvvia() {
		if (btnAvvia == null) {
			btnAvvia = new JButton(play);
			springLayout.putConstraint(SpringLayout.NORTH, btnAvvia, 237, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, btnAvvia, 38, SpringLayout.EAST, getScrollPane());
			springLayout.putConstraint(SpringLayout.SOUTH, btnAvvia, 0, SpringLayout.SOUTH, getScrollPane());
			springLayout.putConstraint(SpringLayout.EAST, btnAvvia, 523, SpringLayout.WEST, this);
			btnAvvia.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					// modflow sara' in textField.getText()
					if(checkFiles() && checkEseguibile())
					{
						textPane.setText("Processing.../");
						String res = "";
						for(String s:optional.keySet())
							if(optional.get(s).isSelected())
								res = res.concat(s+"\n");
						JOptionPane.showMessageDialog(null, "Running: "+textField.getText()+"\n"+res);
						// Salvo l'impostazione
						model.setEseguibile(textField.getText());
						model.setTxtEditor(pathTextEditor.getText());
						// Cerco il nam o lo creo
						File nam = new File(model.getWorkingDirectory(), model.getName()+".nam");
						if(!nam.exists())
							switch(JOptionPane.showConfirmDialog(null, "File NAM doesn't exist.\nCreate it?", "Nam not found", JOptionPane.YES_NO_OPTION) ){
								case JOptionPane.YES_OPTION : crea_nam();
								case JOptionPane.NO_OPTION :
									JOptionPane.showMessageDialog(null, "Esecuzione annullata");
									return;
							}
						new Thread() {
							public void run() {
								// Eseguo il comando
								String nam_file = new File(model.getWorkingDirectory(), model.getName()).getPath();
								try {
									String line;
									File lgr = new File(model.getWorkingDirectory(), model.getName()+".lgr");
									
									if(lgr.exists()){
										Process p = Runtime.getRuntime().exec(model.getEseguibile()+" "+lgr);
										BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));
										while ((line = input.readLine()) != null) {
											System.out.println(line);

											textPane.setText(textPane.getText().concat('\n'+line));
										}
										input.close();
										consolePane.validate();
									}
									else{
										Process p = Runtime.getRuntime().exec(model.getEseguibile()+" "+nam_file);
										BufferedReader input = new BufferedReader (new InputStreamReader(p.getInputStream()));														
										while ((line = input.readLine()) != null) {
											System.out.println(line);

											textPane.setText(textPane.getText().concat('\n'+line));
										}
										
										input.close();
										consolePane.validate();
									}
									
									
								}
								catch (Exception err) {
									err.printStackTrace();
								}
							}
						}.start();
						
						/*Clear raster output files in working directory*/
						File f = new File (model.getWorkingDirectory());
						File[] files = f.listFiles();
						for (int i=0; i<files.length; i++){
							if(files[i].getName().contains(".asc") || files[i].getName().contains(".tif")
									 || files[i].getName().contains(".rmf~") || files[i].getName().contains(".rmf")){					
								files[i].delete();
							}
						}
						
					}

					else
						JOptionPane.showMessageDialog(null, "Same mandatory file is missing!!");

				}
			});
		}
		return btnAvvia;
	}



	protected void crea_nam() {
		File nam = new File(model.getWorkingDirectory(), model.getName()+".nam");
		Object[] scelte = {"Sostituisci", "Rinomina", "Annulla"};
		if(nam.exists())
			switch(JOptionPane.showOptionDialog(null, "File NAM exist, Replace it?", "NAM exist", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, scelte, scelte[1]) ){
				case JOptionPane.YES_OPTION : break;
				case JOptionPane.NO_OPTION : rinomina_file(nam); break;
				case JOptionPane.CANCEL_OPTION : return;
			}
		// Inizio a popolare

		try {
			FileWriter fstream = new FileWriter(nam);
			BufferedWriter out = new BufferedWriter(fstream);
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			String filePackage = new File(model.getWorkingDirectory(), model.getName()).getCanonicalPath();
			out.write("# Name File for MODFLOW created on "+sdf.format(cal.getTime())+" by Sid&Grid\n");
			scrivi_riga_nam(out, pacchetti.get("LST"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("DIS"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("BAS"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("LPF"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("OC"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("CBC"), filePackage);  // <- Inserimento del file Cell By Cell
			scrivi_riga_nam(out, pacchetti.get("FHD"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("FDN"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("BFH_HEAD"), filePackage);
			scrivi_riga_nam(out, pacchetti.get("BFH_FLUX"), filePackage);
//			scrivi_riga_nam(out, pacchetti.get("IWELCB"), filePackage);
//			scrivi_riga_nam(out, pacchetti.get("IGHBCB"), filePackage);
//			scrivi_riga_nam(out, pacchetti.get("IDRNCB"), filePackage);
//			scrivi_riga_nam(out, pacchetti.get("IRCHCB"), filePackage);
//			scrivi_riga_nam(out, pacchetti.get("IRIVCB"), filePackage);
//			scrivi_riga_nam(out, pacchetti.get("ISTCB1"), filePackage);
//			scrivi_riga_nam(out, pacchetti.get("ISTCB2"), filePackage);
			
			
			
			
			for(String s:optional.keySet())
				if(optional.get(s).isSelected())
				{
					scrivi_riga_nam(out, pacchetti.get(s), filePackage);
				}
			//"LIST","100",".lst REPLACE\n"
//			out.write("LIST 11 "+pippo+".lst REPLACE\n");
//			out.write("DIS 12 "+pippo+".dis OLD\n");
//			out.write("BAS6 13 "+pippo+".bas OLD\n");
//			out.write("LPF 14 "+pippo+".lpf OLD\n");
//			out.write("OC 39 "+pippo+".oc OLD\n");
//			out.write("DATA(BINARY) 9 "+pippo+".cbc REPLACE\n");
//			out.write("DATA 37 "+pippo+".fhd REPLACE\n");
//			out.write("DATA 38 "+pippo+".fdn REPLACE\n");
//			out.write("PCG 18 "+pippo+".pcg OLD\n");
//			out.write("CHD 17 "+pippo+".chd OLD\n");
//			out.write("WEL 20 "+pippo+".wel OLD\n");


			//Close the output stream
			out.close();
			getLblNam().setText("File NAM Ok!");
		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	private void scrivi_riga_nam(BufferedWriter out, String[] valori, String pippo) throws IOException {
		
		int flag = model.getNumModel();
		
		if(flag ==  0){
			out.write(valori[0]+' '+valori[1]+' '+pippo+valori[2]);
		}
		else
			out.write(valori[0]+' '+flag+valori[1]+' '+pippo+valori[2]);
		
		
	}

	private void rinomina_file(File nam) {
		String nome = nam.getName();
		System.out.println(nome);
		String nuovo_nome = nome.concat("_old");
//		System.out.println(nuovo_nome);
		String[] spezzato = nome.split("\\.");
//		for(String s: spezzato) System.out.println("->"+s);
//		System.out.println(spezzato.length);
		if(spezzato.length>1)
			nuovo_nome = nome.replace("."+spezzato[spezzato.length-1], "._"+spezzato[spezzato.length-1]);			
//		System.out.println(nuovo_nome);
//		File backup = new File(nam.getParent(), nam.getName());
		File new_file = new File(nam.getParent(), nuovo_nome);
//		System.out.println("-->"+new_file.getName());
		if(new_file.exists())
			rinomina_file(new_file);
		System.out.println(nam.getName()+ "---->"+new_file.getName());
		scrivi_log(textPane, "Rinomino "+nam.getName()+ " in "+new_file.getName());
		System.out.println(nam.renameTo(new_file));
	}

	private boolean checkEseguibile(){
		File f = new File (textField.getText());
		if(!f.exists())
		{
			getWdlabel().setText(PluginServices.getText(this,"Path_not_exist"));
			return false;
		}
		return true;
	}

	private boolean checkFiles() {
		// TODO : Catturare eccezioni
		File f = new File (model.getWorkingDirectory());
		if(!f.exists())
		{
			//JOptionPane.showMessageDialog(null, "ATTENZIONE!! Path inesistente!\nControlla nelle proprieta'");
			//TODO: LOCALIZZARE!
			getWdlabel().setText("WARNING!! Path not exist!");
			return false;
		}
		getWdlabel().setText(model.getWorkingDirectory());

//		System.out.println(f.getPath());
//		System.out.println(f.getPath()+"  "+model.getName()+base.keySet().iterator().next());
//		System.out.println(new File(f.getPath(),model.getName()+base.keySet().iterator().next()).exists());

		if(!f.isDirectory())
			return false;
		// TODO: forse è meglio un iteratore
//		String[] listachiavi = base.keySet().toArray(new String[]{});
//		for (int i=0; i<files.length; i++)
//			for(int k=0; k<listachiavi.length;k++){
//				if(files[i].contains( listachiavi[k]))
//					base.get(listachiavi[k]).setSelected(true);
//			}
		Iterator<String> keyiter = base.keySet().iterator();
		Iterator<String> keyopt = optional.keySet().iterator();
		String next;
		boolean risultato = true;
		File fck;
		while(keyiter.hasNext()){
			next = keyiter.next();
			//System.out.println("n->"+next );  // DEBUG
			fck = new File(f.getPath(), model.getName()+next);
			risultato = risultato & fck.exists();
			if(fck.exists())
				base.get(next).setSelected(true);
			else
				base.get(next).setSelected(false);
		}
		while(keyopt.hasNext()){
			next = keyopt.next();
			//System.out.println("opt->"+next );  // DEBUG
			if(!new File(f.getPath(), model.getName()+"."+next.toLowerCase()).exists()){
				optional.get(next).setEnabled(false);	
				optional.get(next).setSelected(false);	
			}else
				optional.get(next).setEnabled(true);	
		}

		if(!new File(f.getPath(), model.getName()+".nam").exists())
			getLblNam().setText("Il file NAM non esiste");
		else
			getLblNam().setText("Il file NAM esiste");

		return risultato;
	}

	private JLabel getWdlabel() {
		if (Wdlabel == null) {
			Wdlabel = new JLabel("WDLabel");
			springLayout.putConstraint(SpringLayout.NORTH, Wdlabel, 99, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, Wdlabel, 85, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, Wdlabel, 122, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.EAST, Wdlabel, 0, SpringLayout.EAST, textField);
		}
		return Wdlabel;
	}
	private JButton getBtnAggiorna() {
		if (btnAggiorna == null) {
			btnAggiorna = new JButton("Refresh");
			springLayout.putConstraint(SpringLayout.NORTH, btnAggiorna, 275, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, btnAggiorna, 93, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, btnAggiorna, 182, SpringLayout.WEST, this);
			btnAggiorna.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					checkFiles();
					model.setEseguibile(textField.getText());
					model.setTxtEditor(pathTextEditor.getText());
				}
			});
		}
		return btnAggiorna;
	}
	private JScrollPane getConsolePane() {
		if (consolePane == null) {
			consolePane = new JScrollPane();
			springLayout.putConstraint(SpringLayout.NORTH, consolePane, 310, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, consolePane, 10, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, consolePane, -10, SpringLayout.SOUTH, this);
			springLayout.putConstraint(SpringLayout.EAST, consolePane, -10, SpringLayout.EAST, this);
			consolePane.setDoubleBuffered(true);
			consolePane.setAutoscrolls(true);
			consolePane.setViewportView(getTextPane());
		}
		return consolePane;
	}
	private JTextPane getTextPane() {
		if (textPane == null) {
			textPane = new JTextPane();
			Font font = new Font("Monospaced", Font.PLAIN, 14);
			textPane.setFont(font);			
			textPane.setEditable(false);
			textPane.setDoubleBuffered(true);
			textPane.setForeground(Color.BLACK);
		}
		return textPane;
	}
	private JButton getBtnViewNAM() {
		if (btnViewNAM == null) {
			btnViewNAM = new JButton("Open NAM");
			springLayout.putConstraint(SpringLayout.NORTH, btnViewNAM, 191, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, btnViewNAM, 418, SpringLayout.WEST, this);
			btnViewNAM.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					File nam_file = new File(model.getWorkingDirectory(), model.getName()+".nam");
					System.out.println("Apro "+nam_file.getName());
					if(nam_file.exists())
						try {
							FileInputStream fstream = new FileInputStream(nam_file.getPath());
							// Get the object of DataInputStream
							DataInputStream in = new DataInputStream(fstream);
							BufferedReader br = new BufferedReader(new InputStreamReader(in));
							String strLine;
							//Process p = Runtime.getRuntime().exec(model.getEseguibile()+" "+nam_file);
							textPane.setText(textPane.getText().concat("\n\nApertura file "+nam_file.getName()));
							while ((strLine = br.readLine()) != null) {
								System.out.println(strLine);
								textPane.setText(textPane.getText().concat('\n'+strLine));
							}
							br.close();
							consolePane.validate();
						}
						catch (Exception err) {
							err.printStackTrace();
						}					

				}
			});
		}
		return btnViewNAM;
	}
	private JButton getBtnCreaNam() {
		if (btnCreaNam == null) {
			btnCreaNam = new JButton("Create NAM");
			springLayout.putConstraint(SpringLayout.EAST, getBtnViewNAM(), 0, SpringLayout.EAST, btnCreaNam);
			springLayout.putConstraint(SpringLayout.NORTH, btnCreaNam, 133, SpringLayout.NORTH, this);
			springLayout.putConstraint(SpringLayout.WEST, btnCreaNam, 418, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.EAST, btnCreaNam, 536, SpringLayout.WEST, this);
			btnCreaNam.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					crea_nam();
				}
			});
		}
		return btnCreaNam;
	}

	private void scrivi_log(JTextPane jtp, String s){
		jtp.setText(jtp.getText().concat('\n'+s));
	}



	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "Open Report");
			putValue(SHORT_DESCRIPTION, "Open model report file");
		}

		public void actionPerformed(ActionEvent e) {
			try {
				String os = System.getProperty("os.name").toLowerCase();

				File lst_file = new File(model.getWorkingDirectory(), model.getName()+".lst");
				if (os.contains("mac"))
				{					
					Process p = Runtime.getRuntime().exec("open -a "+model.getTxtEditor()+" "+lst_file);
				}
				else if(os.contains("win"))
				{					
					Process p = Runtime.getRuntime().exec(model.getTxtEditor()+" "+lst_file);
				}				
				else if(os.contains("nix") || os.contains("nux"))
				{					
					Process p = Runtime.getRuntime().exec("open -a "+model.getTxtEditor()+" "+lst_file);
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}
	private JLabel getLblNam() {
		if (lblNam == null) {
			// Non imposto nessun valore di default, non so se il nam esiste o meno
			lblNam = new JLabel("");
			springLayout.putConstraint(SpringLayout.NORTH, lblNam, 4, SpringLayout.NORTH, getChckbxLpfFile());
			springLayout.putConstraint(SpringLayout.WEST, lblNam, 22, SpringLayout.EAST, getScrollPane());
			springLayout.putConstraint(SpringLayout.EAST, lblNam, -126, SpringLayout.EAST, this);
		}
		return lblNam;
	}
}

