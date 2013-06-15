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
   private long numberOfBases;

   @Override
   public Signal apply(Signal signal) {
      signal = new Signal();
      int startingPosition = 0;

      double snpVal = 1;
      double snpPos = startingPosition;
      int snps = 0;
      while (snpPos - startingPosition < numberOfBases){
         snps++;
         snpPos += distance();
         snpVal = value(snpVal);
         signal.addComponent(new Signal(new Double(snpPos), snpVal));
      }
      System.out.println("SNPs: " + snps);
      return signal;
   }

   /**
    * Generate random distance based on the ones observed in chr1
    *
    * @return
    */
   private int distance() {
      int rand = oRandom.nextInt(100);

      if (rand <= 30) {
         return oRandom.nextInt(210);
      } else if (rand <= 50) {
         return 200 + oRandom.nextInt(250);
      } else if (rand <= 90) {
         return 500 + oRandom.nextInt(1700);
      } else if (rand <= 95){
         return 2250 + oRandom.nextInt(1200);
      }else if (rand <= 99){
         return 3000 + oRandom.nextInt(3500);
      }else{   
         return 6000 + oRandom.nextInt(150000);
      }
   }

   /**
    * Generate random values based on the ones observed in chr1
    *
    * @param prev
    * @return
    */
   private double value(double prev) {

      // Condirer the previous SNP value
      return oRandom.nextDouble()
              * valueMult()
              * (prev + oRandom.nextDouble());

   }
   
    public double valueMult() {
      double mult = 1.;

      int rand = oRandom.nextInt(100);
      if (rand <= 30) {
         mult = 0.035;
      } else if (rand <= 50) {
         mult = 0.063;
      } else if (rand <= 90) {
         mult = 0.26;
      } else if (rand <= 99) {
         mult = 0.55;
      } else {
         mult = 1.;
      }

      return mult;
   }

   @Override
   public void setOptions(ModifierOptions options) throws WrongOptionsException {
      super.setOptions(options);

      if (options.getOption("numberOfBases") != null) {
         numberOfBases = Double.valueOf(options.getOption("numberOfBases")).longValue();
      } else {
         throw new WrongOptionsException("Missing option: numberOfSnps:Integer");
      }

   }
}
