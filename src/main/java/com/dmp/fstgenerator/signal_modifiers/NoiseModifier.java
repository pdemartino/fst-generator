/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.fstgenerator.signal_modifiers;

import com.dmp.fstgenerator.signal.Signal;
import com.dmp.fstgenerator.signal_modifiers.options.ModifierOptions;
import com.dmp.fstgenerator.signal_modifiers.options.WrongOptionsException;
import java.util.Random;

/**
 *
 * @author pdemartino
 */
public class NoiseModifier extends SignalModifier {

   private Random oRandom = new Random();
   private float noiseRate = 0.07f;
   private double variance = 0.08;

   @Override
   public Signal apply(Signal signal) {
      Signal outSignal = new Signal();

      double minVal = signal.getMinVal();
      double maxVal = signal.getMaxVal();
      int count = 0;
      for (Signal component : signal) {
         double value = component.getValue();
         if (oRandom.nextFloat() < noiseRate) {
            count++;
            value = minVal + (maxVal * oRandom.nextDouble());
         }
         outSignal.addComponent(new Signal(component.getTime(), value));
      }
      System.out.println("Noised: " + count);

      return outSignal;
   }

   @Override
   public void setOptions(ModifierOptions options) throws WrongOptionsException {
      super.setOptions(options);
      if (options.getOption("noiseRate") != null) {
         noiseRate = ((Float) options.getOption("noiseRate")).floatValue();
      }

      if (options.getOption("variance") != null) {
         variance = ((Double) options.getOption("variance")).doubleValue();
      }


   }
}
