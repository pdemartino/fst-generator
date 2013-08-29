package com.dmp.fstgenerator.signal_modifiers;

import com.dmp.fstgenerator.signal.Signal;
import com.dmp.fstgenerator.utils.LoggingManager;
import com.dmp.fstgenerator.signal_modifiers.options.ModifierOptions;
import com.dmp.fstgenerator.signal_modifiers.options.WrongOptionsException;
import com.dmp.fstgenerator.utils.SAMath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class NeutralSignalModifier extends SignalModifier {

   private Random random = new Random();
   private long numberOfBases;

   @Override
   public Signal apply(Signal signal) {
      signal = new Signal();
      int startingPosition = 0;

      double snpVal = 1;
      double snpPos = startingPosition;
      double distaceFromPrev = 0;
      int snps = 0;
      while (snpPos - startingPosition < numberOfBases) {
         snps++;
         double prevPos = snpPos;
         snpPos += distance();
         snpVal = random.nextDouble() * 0.45
                 + snpVal * SAMath.minMaxNormalization(snpPos-prevPos,0,140000);
         signal.addComponent(new Signal(new Double(snpPos), snpVal));
      }
      LoggingManager.logField("SNPs", Integer.toString(snps));
      return signal;
   }
   /**
    * Generate random distance based on the ones observed in chr1
    *
    * @return
    */
   private static LinkedList<ArrayList<Integer>> distances =
           new LinkedList<ArrayList<Integer>>() {
              {
                 // perc, min distance, max distance
                 add(new ArrayList<Integer>(Arrays.asList(45, 0, 2)));
                 add(new ArrayList<Integer>(Arrays.asList(72, 2, 4)));
                 add(new ArrayList<Integer>(Arrays.asList(87, 5, 6)));
                 add(new ArrayList<Integer>(Arrays.asList(92, 6, 8)));
                 add(new ArrayList<Integer>(Arrays.asList(94, 8, 10)));
                 add(new ArrayList<Integer>(Arrays.asList(99, 10, 20)));
                 add(new ArrayList<Integer>(Arrays.asList(100, 20, 140)));
              }
           };

   private int distance() {
      int distance = 0;
      int prob = random.nextInt(100);
      for (ArrayList<Integer> dist : distances) {
         if (prob <= dist.get(0)) {
            int min = dist.get(1);
            int delta = dist.get(2) - min;
            distance = (min + random.nextInt(delta)) * 1000;
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
      return random.nextDouble()
              * valueMult()
              * (prev + random.nextDouble());

   }

   public double valueMult() {
      double mult = 1.;

      int rand = random.nextInt(100);
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
