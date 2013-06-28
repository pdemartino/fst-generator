package com.dmp.fstgenerator.utils;

import java.util.Map;
import java.util.HashMap;
import java.io.*;


public class LoggingManager {
   private static String filename;
   private static Map<String,String> logData;
   private static boolean started = false;
   private static boolean stopped = false;

   public static void start(String directory){
      if (!started){
         filename = directory + File.separator + "info.csv";
         logData = new HashMap<String,String>();
         started = true;
      }
   }
   public static void logField(String field, String value){
      if (!stopped && started){
         logData.put(field,value);   
      }
   }
   public static void stop(){
      if (!stopped && started){
         flushToFile();
         stopped = true;
      }
   }

   private static void flushToFile(){
      String head = "";
      String values = "";

      for (Map.Entry<String,String> entry : logData.entrySet()){
         head += String.format("%s,",entry.getKey());
         values += String.format("%s,",entry.getValue());
      }
      try{
         BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
         writer.write(head.substring(0, head.length()-1));
         writer.newLine();
         writer.write(values.substring(0, values.length()-1));
         writer.newLine();
         writer.close();
      }catch(IOException ex){
         System.err.println("Error while writing info file: " + ex.getMessage());
      }
   }

}
