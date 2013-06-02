/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.fstgenerator.signal_modifiers;

import com.dmp.fstgenerator.signal.Signal;
import com.dmp.fstgenerator.signal_modifiers.options.ModifierOptions;
import com.dmp.fstgenerator.signal_modifiers.options.WrongOptionsException;

/**
 *
 * @author pdemartino
 */
public abstract class SignalModifier {

   protected ModifierOptions options;

   public abstract Signal apply(Signal signal);

   public ModifierOptions getOptions() {
      return options;
   }

   public void setOptions(ModifierOptions options) throws WrongOptionsException {
      this.options = options;
   }
}
