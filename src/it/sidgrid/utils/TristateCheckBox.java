package it.sidgrid.utils;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.*;
import java.awt.event.*;

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
 

public final class TristateCheckBox extends JCheckBox {
	private final ChangeListener enableListener =
		new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent e) {
			TristateCheckBox.this.setFocusable(
					getModel().isEnabled());
		}
	};

	public TristateCheckBox() {
		this(null, null, TristateState.DESELECTED);
	}
	public TristateCheckBox(String text) {
		this(text, null, TristateState.DESELECTED);
	}

	public TristateCheckBox(String text, Icon icon,
			TristateState initial) {
		super(text, icon);

		//Set default single model
		setModel(new TristateButtonModel(initial));

		// override action behaviour
		super.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				TristateCheckBox.this.iterateState();
			}
		});
		ActionMap actions = new ActionMapUIResource();
		actions.put("pressed", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TristateCheckBox.this.iterateState();
			}
		});
		actions.put("released", null);
		SwingUtilities.replaceUIActionMap(this, actions);
	}

	// Next two methods implement new API by delegation to model
	public void setIndeterminate() {
		getTristateModel().setIndeterminate();
	}

	public boolean isIndeterminate() {
		return getTristateModel().isIndeterminate();
	}

	public TristateState getState() {
		return getTristateModel().getState();
	}

	//Overrides superclass method
	@Override
	public void setModel(ButtonModel newModel) {
		super.setModel(newModel);

		//Listen for enable changes
		if (model instanceof TristateButtonModel)
			model.addChangeListener(enableListener);
	}

	//Empty override of superclass method
	@Override
	public void addMouseListener(MouseListener l) {
	}

	// Mostly delegates to model
	private void iterateState() {
		//Maybe do nothing at all?
		if (!getModel().isEnabled()) return;

		grabFocus();

		// Iterate state
		getTristateModel().iterateState();

		// Fire ActionEvent
		int modifiers = 0;
		AWTEvent currentEvent = EventQueue.getCurrentEvent();
		if (currentEvent instanceof InputEvent) {
			modifiers = ((InputEvent) currentEvent).getModifiers();
		} else if (currentEvent instanceof ActionEvent) {
			modifiers = ((ActionEvent) currentEvent).getModifiers();
		}
		fireActionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, getText(),
				System.currentTimeMillis(), modifiers));
	}

	//Convenience cast
	public TristateButtonModel getTristateModel() {
		return (TristateButtonModel) super.getModel();
	}
	public void setState(TristateState state) {
		switch(state){
			case SELECTED: this.setSelected(true); break;
			case INDETERMINATE: this.setIndeterminate(); break;
			case DESELECTED: this.setSelected(false); break;
		}
		
	}
}
