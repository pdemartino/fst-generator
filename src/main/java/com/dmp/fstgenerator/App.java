package com.dmp.fstgenerator;

import com.dmp.fstgenerator.signal.Signal;
import com.dmp.fstgenerator.signal_modifiers.NeutralSignalModifier;
import com.dmp.fstgenerator.signal_modifiers.NoiseModifier;
import com.dmp.fstgenerator.signal_modifiers.SelectionModifier;
import com.dmp.fstgenerator.signal_modifiers.SignalModifier;
import com.dmp.fstgenerator.signal_modifiers.options.ModifierOptions;
import com.dmp.fstgenerator.signal_modifiers.options.WrongOptionsException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App {

   static ModifierOptions options = new ModifierOptions() {
      {
         setOption("numberOfSnps",
                 Integer.parseInt(
                 System.getProperty("numberOfSnps") != null
                 ? System.getProperty("numberOfSnps") : "1000"));

         setOption("selectionPosition",
                 Double.parseDouble(
                 System.getProperty("selectionPosition") != null
                 ? System.getProperty("selectionPosition") : "0.5"));

         setOption("selectionFstValue",
                 Double.parseDouble(
                 System.getProperty("selectionFstValue") != null
                 ? System.getProperty("selectionFstValue") : "0.5"));

         setOption("backgroundFst",
                 Double.parseDouble(
                 System.getProperty("backgroundFst") != null
                 ? System.getProperty("backgroundFst") : "0.1"));

         setOption("variance",
                 Double.parseDouble(
                 System.getProperty("variance") != null
                 ? System.getProperty("variance") : "0.1"));

         setOption("noiseRate",
                 Float.parseFloat(
                 System.getProperty("noiseRate") != null
                 ? System.getProperty("noiseRate") : "0.07"));


      }
   };
   static boolean applySelection =
           System.getProperty("applySelection") != null
           ? Boolean.getBoolean(System.getProperty("applySelection")) : true;
   static boolean applyNoise =
           System.getProperty("applyNoise") != null
           ? Boolean.getBoolean(System.getProperty("applyNoise")) : true;
   static String outputCsvFile =
           System.getProperty("outputCsvFile") != null
           ? System.getProperty("outputCsvFile") : "./signal.csv";

   public static void main(String[] args) throws WrongOptionsException, IOException {
      if (args.length > 0 && args[0].equalsIgnoreCase("--help")){
         showHelp();
         System.exit(0);
      }
      
      Signal signal = applyNeutralModifier();
      if (applySelection) {
         signal = applySelectionModifier(signal);
      }
      if (applyNoise) {
         signal = applyNoise(signal);
      }

      printToCsv(signal, outputCsvFile);
   }

   private static Signal applyNoise(Signal signal) throws WrongOptionsException {
      SignalModifier modifier = new NoiseModifier();
      modifier.setOptions(options);
      return modifier.apply(signal);
   }

   private static Signal applySelectionModifier(Signal signal) throws WrongOptionsException {
      SignalModifier selection = new SelectionModifier();
      selection.setOptions(options);

      return selection.apply(signal);
   }

   private static Signal applyNeutralModifier() throws WrongOptionsException {

      SignalModifier tested = new NeutralSignalModifier();
      tested.setOptions(options);

      Signal signal = tested.apply(new Signal());

      return signal;
   }

   private static void printToCsv(Signal signal, String filename) throws IOException {
      String delimiter = ",";
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

      writer.append(String.format("Position%sFst", delimiter));
      writer.newLine();
      for (Signal snp : signal) {
         writer.append(snp.getTime() + delimiter + snp.getValue());
         writer.newLine();
      }

      writer.close();
   }
   
   
   private static void showHelp(){
      System.out.println("These properties are settable: ");
      
      System.out.println("\tBase properties:");
      System.out.println("\t\tnumberOfSnps:Integer - def=" + options.getOption("numberOfSnps"));
      System.out.println("\t\tbackgroundFst:Double - def="+ options.getOption("backgroundFst"));
      System.out.println("\t\tvariance:Double - def="+ options.getOption("variance"));
      
      System.out.println("\tSelection properties (use applySelection): - def="+ applySelection);
      System.out.println("\t\tselectionPosition:Double (Relative position) - def="+ options.getOption("selectionPosition"));
      System.out.println("\t\tselectionFstValue:Double (Selected SNP Fst value) - def="+ options.getOption("selectionFstValue"));
      
      System.out.println("\tNoise properties (use applyNoise) - def="+ applyNoise);
      System.out.println("\t\tnoiseRate:Double (SNPs affected by noise: 0..1) - def="+ options.getOption("noiseRate"));
      
      System.out.println("\tOutput properties:");
      System.out.println("\t\toutputCsvFile:String - def="+ outputCsvFile);
   }
    
}
