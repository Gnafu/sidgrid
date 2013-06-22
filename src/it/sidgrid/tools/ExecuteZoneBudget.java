package it.sidgrid.tools;

import it.sidgrid.ModelDocument.HydrologicalModel;
import it.sidgrid.ModelDocument.HydrologicalModelFactory;
import it.sidgrid.utils.Utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileSystemView;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import javax.swing.JLabel;
import javax.swing.Action;
import javax.swing.JComboBox;

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
 * 
 * @author SidGrid
 * Menu: SG_Analysis/Run_Zone_Budget
 *
 */
public class ExecuteZoneBudget extends javax.swing.JPanel implements IWindow{
	private WindowInfo viewInfo = null;
	private JTextPane textPane;
	private JScrollPane consolePane;
	private SpringLayout springLayout;
	private JTextField textField;
	private final Action action = new SwingAction();
	private JTextField title;
	private JComboBox modelBox;
	
	public ExecuteZoneBudget() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		springLayout = new SpringLayout();
		setLayout(springLayout);
		this.setSize(440, 419);		
		this.add(getConsolePane());
		
		textField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, textField, 27, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, textField, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, textField, -123, SpringLayout.EAST, this);
		textField.setText("Insert ZoneBudget program path");
		add(textField);
		
		JButton btnNewButton = new JButton("path");
		springLayout.putConstraint(SpringLayout.NORTH, btnNewButton, 1, SpringLayout.NORTH, textField);
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton, 6, SpringLayout.EAST, textField);
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton, -10, SpringLayout.EAST, this);
		btnNewButton.setAction(new AbstractAction("Process", null) {
			@Override
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
		
		JButton btnNewButton_1 = new JButton(PluginServices.getText(this,"Run"));
		springLayout.putConstraint(SpringLayout.WEST, btnNewButton_1, 140, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, btnNewButton_1, -6, SpringLayout.NORTH, getConsolePane());
		springLayout.putConstraint(SpringLayout.EAST, btnNewButton_1, -123, SpringLayout.EAST, this);
		btnNewButton_1.setAction(action);
		add(btnNewButton_1);
		
		title = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, title, 0, SpringLayout.WEST, btnNewButton_1);
		springLayout.putConstraint(SpringLayout.SOUTH, title, -6, SpringLayout.NORTH, btnNewButton_1);
		springLayout.putConstraint(SpringLayout.EAST, title, -123, SpringLayout.EAST, this);
		add(title);
		title.setColumns(10);
		
		JLabel lblTitleToBe = new JLabel(PluginServices.getText(this,"Title_to_be_printed"));
		springLayout.putConstraint(SpringLayout.NORTH, lblTitleToBe, 6, SpringLayout.NORTH, title);
		springLayout.putConstraint(SpringLayout.WEST, lblTitleToBe, 0, SpringLayout.WEST, getConsolePane());
		add(lblTitleToBe);
		
		modelBox = new JComboBox(Utils.getModelsNames());
		springLayout.putConstraint(SpringLayout.WEST, modelBox, 0, SpringLayout.WEST, btnNewButton_1);
		springLayout.putConstraint(SpringLayout.SOUTH, modelBox, -6, SpringLayout.NORTH, title);
		springLayout.putConstraint(SpringLayout.EAST, modelBox, -5, SpringLayout.EAST, textField);
		add(modelBox);
		
		JLabel lblModelProject = new JLabel(PluginServices.getText(this,"Model_project"));
		springLayout.putConstraint(SpringLayout.NORTH, lblModelProject, 4, SpringLayout.NORTH, modelBox);
		springLayout.putConstraint(SpringLayout.WEST, lblModelProject, 10, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, lblModelProject, -345, SpringLayout.EAST, this);
		add(lblModelProject);
	}
	
	
	private JScrollPane getConsolePane() {
		if (consolePane == null) {
			consolePane = new JScrollPane();
			springLayout.putConstraint(SpringLayout.NORTH, consolePane, 175, SpringLayout.NORTH, this);
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
			springLayout.putConstraint(SpringLayout.WEST, textPane, 10, SpringLayout.WEST, this);
			springLayout.putConstraint(SpringLayout.SOUTH, textPane, -140, SpringLayout.SOUTH, this);
			textPane.setBounds(0, 150, 96, -150);
			Font font = new Font("Monospaced", Font.PLAIN, 12);
			textPane.setFont(font);			
			textPane.setEditable(false);
			textPane.setDoubleBuffered(true);
			textPane.setForeground(Color.BLACK);
		}
		return textPane;
	}
	
	
	@Override
	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.ICONIFIABLE | WindowInfo.RESIZABLE);
			viewInfo.setTitle(PluginServices.getText(this, "Run_Zone_Budget"));
		}
		return viewInfo;
	}

	@Override
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, PluginServices.getText(this,"Run_Zone_Budget"));
			//putValue(SHORT_DESCRIPTION, "");
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			BufferedReader result;
			BufferedWriter send;
			String line;			
			try {				
				String progetto = (String) modelBox.getSelectedItem();
				ProjectExtension ext = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
				HydrologicalModel doc = (HydrologicalModel) ext.getProject().getProjectDocumentByName(progetto, HydrologicalModelFactory.registerName);
				String filepath = doc.getWorkingDirectory() +"/"+ doc.getName();
				
				
				Process process = Runtime.getRuntime().exec(textField.getText());
				
				result = new BufferedReader (new InputStreamReader(process.getInputStream()));
				send = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
				System.out.println("avviato");
				send.append(filepath+".zblst");
				send.flush();
				 send.newLine();
				 send.append(filepath+".cbc");
				 send.flush();
				 send.newLine();
				 send.append(title.getText());
				 send.flush();
				 send.newLine();
				 send.append(filepath+".zon");
				 send.flush();
				 send.newLine();
				 send.append("A");
				 send.flush();
				 send.newLine();
				 send.close();
				 while ((line = result.readLine()) != null)
				 {
					 
					 textPane.setText(textPane.getText().concat('\n'+line));
				 }
				 result.close();
				consolePane.validate();
				
				/*Open zonebudget listing file*/
				String os = System.getProperty("os.name").toLowerCase();

				File zonlist_file = new File(doc.getWorkingDirectory(), doc.getName()+".zblst");
				if (os.contains("mac"))
				{					
					Process p = Runtime.getRuntime().exec("open -a "+doc.getTxtEditor()+" "+zonlist_file);
				}
				else if(os.contains("win"))
				{					
					Process p = Runtime.getRuntime().exec(doc.getTxtEditor()+" "+zonlist_file);
				}				
				else if(os.contains("nix") || os.contains("nux"))
				{					
					Process p = Runtime.getRuntime().exec("open -a "+doc.getTxtEditor()+" "+zonlist_file);
				}
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
		}
	}
}
