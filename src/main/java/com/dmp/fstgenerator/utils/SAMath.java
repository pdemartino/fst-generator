package com.dmp.fstgenerator.utils;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class SAMath {
   
   /**
    * Returns a normalized value using min max method
    * @param value
    * @param min
    * @param max
    * @return
    */
   public static double minMaxNormalization(double value, double min, double max){
      double range = max - min;
      double normalized = (value - min) / range;
      return normalized;
   }
   
   public static double average(double... values){
      int count = 0;
      double sum = 0;
      
      for (double value : values){
         sum += value;
         count++;
      }
      
      return count==0 ? 0 : sum / count;
   }
   
   public static double min(double... values){
      double min = Double.MAX_VALUE;
      for (double value : values){
         min = Math.min(min, value);
      }
      return min;
   }
   
   public static double max(double... values){
      double max = -1 * Double.MAX_VALUE;
      for (double value : values){
         max = Math.max(max, value);
      }
      return max;
   }
}
