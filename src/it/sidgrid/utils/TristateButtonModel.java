package it.sidgrid.utils;

import javax.swing.JToggleButton.ToggleButtonModel;
import java.awt.event.ItemEvent;

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

public class TristateButtonModel extends ToggleButtonModel {
  private TristateState state = TristateState.DESELECTED;

  public TristateButtonModel(TristateState state) {
    setState(state);
  }

  public TristateButtonModel() {
    this(TristateState.DESELECTED);
  }

  public void setIndeterminate() {
    setState(TristateState.INDETERMINATE);
  }

  public boolean isIndeterminate() {
    return state == TristateState.INDETERMINATE;
  }

  // Overrides of superclass methods
  @Override
public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    // Restore state display
    displayState();
  }

  @Override
public void setSelected(boolean selected) {
    setState(selected ?
        TristateState.SELECTED : TristateState.DESELECTED);
  }

  // Empty overrides of superclass methods
  @Override
public void setArmed(boolean b) {
  }

  @Override
public void setPressed(boolean b) {
  }

  void iterateState() {
    setState(state.next());
  }

  private void setState(TristateState state) {
    //Set internal state
    this.state = state;
    displayState();
    if (state == TristateState.INDETERMINATE && isEnabled()) {
      // force the events to fire

      // Send ChangeEvent
      fireStateChanged();

      // Send ItemEvent
      int indeterminate = 3;
      fireItemStateChanged(new ItemEvent(
          this, ItemEvent.ITEM_STATE_CHANGED, this,
          indeterminate));
    }
  }

  private void displayState() {
    super.setSelected(state != TristateState.DESELECTED);
    super.setArmed(state == TristateState.INDETERMINATE);
    super.setPressed(state == TristateState.INDETERMINATE);

  }

  public TristateState getState() {
    return state;
  }
}
  
