/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.fstgenerator.signal_modifiers;

import com.dmp.fstgenerator.signal.Signal;
import com.dmp.fstgenerator.signal_modifiers.options.ModifierOptions;
import com.dmp.fstgenerator.signal_modifiers.options.WrongOptionsException;
import java.util.Random;

public class NeutralSignalModifier extends SignalModifier {

   private Random oRandom = new Random();
   private double backgroundFst = 0.1;
   private double variance = 0.1;
   private int snpsMeanDistance = 1000;
   private float snpsDistanceDeviationRate = 0.9f;
   private int snpsDistanceDeviation;
   private int numberOfSnps;

   @Override
   public Signal apply(Signal signal) {
      signal = new Signal();
      int snpPos = 0;

      for (int i = 1; i <= numberOfSnps; i++) {
         // Define a new SNP Position
         //int sign = oRandom.nextDouble() > 0.5 ? 1 : -1;
         snpPos += distance();

         int sign = oRandom.nextDouble() > 0.5 ? 1 : -1;
         double snpVal = backgroundFst
                 + sign * (oRandom.nextDouble() * variance);



         signal.addComponent(new Signal(new Double(snpPos), snpVal));
      }

      return signal;
   }

   private int distance() {
      int rand = oRandom.nextInt(100);

      if (rand < 30) {
         return oRandom.nextInt(250);
      } else if (rand < 50) {
         return 250 + oRandom.nextInt(250);
      } else if (rand < 90){
         return 500 + oRandom.nextInt(2000);
      } else {
         return 3000 + oRandom.nextInt(150000);
      }
   }

   @Override
   public void setOptions(ModifierOptions options) throws WrongOptionsException {
      super.setOptions(options);


      if (options.getOption("numberOfSnps") != null) {
         numberOfSnps = ((Integer) options.getOption("numberOfSnps")).intValue();
      } else {
         throw new WrongOptionsException("Missing option: numberOfSnps:Integer");
      }

      if (options.getOption("backgroundFst") != null) {
         backgroundFst = ((Double) options.getOption("backgroundFst")).doubleValue();
      }

      if (options.getOption("variance") != null) {
         variance = ((Double) options.getOption("variance")).doubleValue();
      }

      if (options.getOption("snpsMeanDistance") != null) {
         snpsMeanDistance = ((Integer) options.getOption("snpsMeanDistance")).intValue();
      }

      if (options.getOption("snpsDistanceDeviationRate") != null) {
         snpsDistanceDeviationRate = ((Float) options.getOption("snpsDistanceDeviationRate")).floatValue();
      }

      snpsDistanceDeviation = Math.round(snpsMeanDistance * snpsDistanceDeviationRate);


   }
}
