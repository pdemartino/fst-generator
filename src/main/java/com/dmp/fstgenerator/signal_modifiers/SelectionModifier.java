package com.dmp.fstgenerator.signal_modifiers;

import com.dmp.fstgenerator.signal.Signal;
import com.dmp.fstgenerator.utils.LoggingManager;
import com.dmp.fstgenerator.signal_modifiers.options.ModifierOptions;
import com.dmp.fstgenerator.signal_modifiers.options.WrongOptionsException;
import com.dmp.fstgenerator.utils.SAMath;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author pdemartino
 */
public class SelectionModifier extends SignalModifier {

   private Random random = new Random();
   private long selectionPosition;
   private float haplotypeNormalizedSize = 0.01f;
   private double selectionFstValue = 0.3;
   private double variance = 0.086;
   private long numberOfBases;

   @Override
   public Signal apply(Signal signal) {
      LoggingManager.logField("SelFst",Double.toString(selectionFstValue));

      // Insert selected SNP
      System.out.println("Selection in " + selectionPosition);
      Signal snp = signal.get(selectionPosition);

      if (snp == null) {
         snp = new Signal((double) selectionPosition, selectionFstValue);
         signal.addComponent(snp);
      } else {
         snp.setValue(selectionFstValue);
      }

      // adjust all the  rest of the snps
      return applyDistanceScalingFactor(signal, selectionPosition);
   }

   private static int snpIntegerPosition(double relativePosition, Signal signal) {
      double min = signal.getTStart();
      double max = signal.getTStop();

      double doublePosition = min + (max - min) * relativePosition;
      return (int) Math.round(doublePosition);
   }

   private Signal applyDistanceScalingFactor(Signal inputSignal, long selectionPosition) {
      Signal scaledSignal = new Signal();

      //Map<String, Double> distanceFactors = getDistanceFactors(inputSignal, selectionPosition);
      //double minDistance = distanceFactors.get("minDistance");
      //double maxDistance = distanceFactors.get("maxDistance");
      double minDistance = 0;
      double maxDistance = inputSignal.getTStop() - inputSignal.getTStart();
      System.out.println(String.format("minD: %s; maxD: %s", minDistance, maxDistance));

      int count = 0;
      int haplotypeHalfSize = Math.round((haplotypeNormalizedSize * numberOfBases) / 2);
      System.out.println("Haplo Half Size: " + haplotypeHalfSize);
      LoggingManager.logField("SelectionStart" , Long.toString((selectionPosition - haplotypeHalfSize)));
      LoggingManager.logField("SelectionStop" , Long.toString((selectionPosition + haplotypeHalfSize)));
      LoggingManager.logField("SelectedBases",  Integer.toString((haplotypeHalfSize * 2)));

      
      for (Signal component : inputSignal) {
         double fstVal = component.getValue();
         double distance = Math.abs(component.getTime() - selectionPosition);

         if (distance <= haplotypeHalfSize) {
            double normalizedDistance = SAMath.minMaxNormalization(distance, 0, haplotypeHalfSize);
            System.out.println(normalizedDistance);
//            if ((oRandom.nextDouble() < 0.2)){
               double shift = 
                       random.nextDouble() * 0.005
                       * (random.nextBoolean() ? -1 : 1);
               fstVal = Math.max(fstVal,
                       selectionFstValue * scalingFactor(normalizedDistance) + shift);
  //          }
            
            count ++;
         }

         scaledSignal.addComponent(new Signal(component.getTime(), fstVal));

      }


      LoggingManager.logField("SelectedSNPs", Integer.toString(count));
      return scaledSignal;
   }

   private double scalingFactor(double normalizedDistance){
      double scalingFactor;

      scalingFactor = 1 - Math.pow(normalizedDistance, 2);
      //scalingFactor = Math.pow(1 - normalizedDistance,10);
      //scalingFactor *= oRandom.nextDouble();
      return scalingFactor;
   }

   private static Map<String, Double> getDistanceFactors(Signal signal, int selection) {
      Map<String, Double> distanceFactors = new HashMap<String, Double>();

      double maxDistance = 0f;
      double minDistance = Double.MAX_VALUE;
      // retrieve max and min distance
      for (Signal component : signal) {
         double distance = Math.abs(component.getTime() - selection);
         maxDistance = Math.max(maxDistance, distance);
         minDistance = Math.min(minDistance, distance);
      }
      distanceFactors.put("minDistance", minDistance);
      distanceFactors.put("maxDistance", maxDistance);
      distanceFactors.put("distanceRange", maxDistance - minDistance);

      return distanceFactors;
   }

   @Override
   public void setOptions(ModifierOptions options) throws WrongOptionsException {
      super.setOptions(options);

      if (options.getOption("selectionFstValue") != null) {
         this.selectionFstValue = Double.valueOf(options.getOption("selectionFstValue"));
      }

      if (options.getOption("variance") != null) {
         variance = Double.valueOf(options.getOption("variance"));
      }

      if (options.getOption("haplotypeSize") != null) {
         haplotypeNormalizedSize = Float.valueOf(options.getOption("haplotypeSize"));
      }

      if (options.getOption("numberOfBases") != null) {
         numberOfBases = Double.valueOf(options.getOption("numberOfBases")).longValue();
      } else {
         throw new WrongOptionsException("Missing option: numberOfSnps:Integer");
      }
      
      if (options.getOption("selectionPosition") != null) {
         selectionPosition = Math.round(Double.valueOf(options.getOption("selectionPosition")) * numberOfBases);
      } else {
         throw new WrongOptionsException("Missing Option: selectionPosition:Integer");
      }
   }
}
