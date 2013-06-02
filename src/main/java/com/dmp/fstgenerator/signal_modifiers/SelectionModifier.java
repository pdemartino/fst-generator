package com.dmp.fstgenerator.signal_modifiers;

import com.dmp.fstgenerator.signal.Signal;
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

   private Random oRandom = new Random();
   private double selectionPosition;
   private double haplotypeHalfSize = 0.1;
   private double selectionFstValue = 0.3;
   private double variance = 0.086;

   @Override
   public Signal apply(Signal signal) {


      // Insert selected SNP
      int position = snpIntegerPosition(selectionPosition, signal);
      Signal snp = signal.get(position);

      if (snp == null) {
         snp = new Signal((double) position, selectionFstValue);
         signal.addComponent(snp);
      } else {
         snp.setValue(selectionFstValue);
      }

      // adjust all the  rest of the snps
      return applyDistanceScalingFactor(signal, position);
   }

   private static int snpIntegerPosition(double relativePosition, Signal signal) {
      double min = signal.getTStart();
      double max = signal.getTStop();

      double doublePosition = min + (max - min) * relativePosition;
      return (int) Math.round(doublePosition);
   }

   private Signal applyDistanceScalingFactor(Signal inputSignal, int selectionPosition) {
      Signal scaledSignal = new Signal();

      Map<String, Double> distanceFactors = getDistanceFactors(inputSignal, selectionPosition);
      double minDistance = distanceFactors.get("minDistance");
      double maxDistance = distanceFactors.get("maxDistance");

      int count = 0;
      double normalizedHaplotypeHalfSize = (maxDistance - minDistance) * haplotypeHalfSize;
      for (Signal component : inputSignal) {
         double fstVal = component.getValue();
         double distance = Math.abs(component.getTime() - selectionPosition);
         

         
        // if (distance <= normalizedHaplotypeHalfSize) {
            double scaledValue = selectionFstValue;
            double normalizedDistance = SAMath.minMaxNormalization(distance, minDistance, maxDistance);

            // apply scaling factor based on distance from selected SNP
            //double scalingFactor = Math.pow(1 - SAMath.minMaxNormalization(distance, minDistance, maxDistance), 4);
            double scalingFactor = Math.pow(1 - normalizedDistance,64);
            scaledValue *= scalingFactor;

            // apply deviation
            int sign = oRandom.nextDouble() > 0.5 ? 1 : -1;
            scaledValue += sign * (oRandom.nextDouble() * variance);
            
            if (fstVal <= scaledValue){
               fstVal = scaledValue;
               count ++;
            }
         //}

         scaledSignal.addComponent(new Signal(component.getTime(), fstVal));

      }


      System.out.println("Selected: " + count);
      return scaledSignal;
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

      if (options.getOption("selectionPosition") != null) {
         this.selectionPosition = ((Double) options.getOption("selectionPosition"));
      } else {
         throw new WrongOptionsException("Missing Option: selectionPosition:Integer");
      }

      if (options.getOption("selectionFstValue") != null) {
         this.selectionFstValue = ((Double) options.getOption("selectionFstValue"));
      }

      if (options.getOption("variance") != null) {
         variance = ((Double) options.getOption("variance")).doubleValue();
      }
      
      if (options.getOption("haplotypeHalfSize") != null) {
         haplotypeHalfSize = ((Double) options.getOption("haplotypeHalfSize")).doubleValue();
      }

   }
}
