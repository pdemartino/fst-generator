package com.dmp.fstgenerator.signal_modifiers;

import com.dmp.fstgenerator.signal.Signal;
import com.dmp.fstgenerator.signal_modifiers.options.ModifierOptions;
import com.dmp.fstgenerator.signal_modifiers.options.WrongOptionsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
   private static LinkedList<ArrayList<Integer>> distancePercentile = 
           new LinkedList<ArrayList<Integer>> () {{
              // percentileIndex, minDistance, maxDistanceOffset
              add(new ArrayList<Integer>(Arrays.asList(30,0,210)));
              add(new ArrayList<Integer>(Arrays.asList(50,200,250)));
              add(new ArrayList<Integer>(Arrays.asList(90,500,1200)));
              add(new ArrayList<Integer>(Arrays.asList(95,2250,1500)));
              add(new ArrayList<Integer>(Arrays.asList(99,3000,3500)));
              add(new ArrayList<Integer>(Arrays.asList(100,6000,140000)));
           }};

   private int distance() {
      int distance = 0;
      int rand = oRandom.nextInt(100);
      
      
      
      for (ArrayList<Integer> percentileConf : distancePercentile){
         if (rand <= percentileConf.get(0)){
            distance = percentileConf.get(1) 
                    + oRandom.nextInt(percentileConf.get(2));
            break;
         }
      }
      
      return distance;
   }

   /**
    * Generate random values based on the ones observed in chr1
    *
    * @param prev
    * @return
    */
   private double value(double prev) {

      // Consirer the previous SNP value
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
