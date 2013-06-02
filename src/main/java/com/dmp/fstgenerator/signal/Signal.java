package com.dmp.fstgenerator.signal;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Pasquale De Martino <paco.dmp@gmail.com>
 */
public class Signal implements Iterable<Signal> {

   //private Logger logger = Logger.getLogger(Signal.class.getName());
   private Double tStart = null, tStop = null;
   private Double value = null;
   // Using a TreeMap to automatically keep items ordered by time (the key value)
   private TreeMap<Double, Signal> components;
   private double lowerBound = Double.NEGATIVE_INFINITY;
   private double upperBound = Double.POSITIVE_INFINITY;
   
   private double minVal = 1., maxVal = 0.;

   public Signal(Double tStart, Double tStop, Double value) {
      this();
      this.tStart = tStart;
      this.tStop = tStop;
      this.value = Math.max(0, Math.min(1, value));
   }

   public Signal(Double time, Double value) {
      this(time, time, value);
   }
   
   public Signal() {
      reset();
   }


   public boolean addComponent(Signal component) {
      if ((component.getTime() >= lowerBound) && (component.getTime() <= upperBound)) {
         if (this.components.get(component.getTime()) != null) {
            //logger.warn(String.format("Component at time %s already existing into Signal!", component.getTime()));
         } else {
            this.components.put(Double.valueOf(component.getTime()), component);
            if ((this.tStart == null) || (this.tStart > component.getTStart())) {
               this.tStart = component.getTStart();
            }

            if ((this.tStop == null) || (this.tStop < component.getTStop())) {
               this.tStop = component.getTStop();
            }
            
            // min max val
            minVal = Math.min(minVal, component.getValue());
            maxVal = Math.max(maxVal, component.getValue());
            return true;
         }
      }
      return false;
   }

   @SuppressWarnings("empty-statement")
   public double[] distances(){
      if (this.count() == 0) {
         double[] distances = {0};
         return distances;
      }
      
      // the first item has not to be considered
      double[] distances = new double[this.count()-1];
      Signal previous = null;
      int i = 0;
      for (Signal component : this){
         if (previous != null){
            distances[i] = component.getTStart() - previous.getTStop();
         }
         previous = component;
      }
      return distances;
   }
   
   public void reset() {
      this.tStart = null;
      this.tStop = null;
      this.components = new TreeMap<Double, Signal>();
   }

   @Override
   public String toString() {
      String outString = "";
      
      if (this.count()>0){
         outString+= "{";
         for (Signal pulse : this) {
            outString += String.format("\n\t%s,", pulse);
         }
         outString += "}";
      }else{
         outString += String.format("\nt:%s,v:%s", this.getTime(), this.getValue());
      }

      return outString;
   }

   // Accessors
   public double getTStart() {
      return tStart;
   }

   public double getTStop() {
      return tStop;
   }

   public double getTime() {
      return (this.getTStart() + this.getTStop()) / 2;
   }

   public double getValue() {
      if (this.value != null) {
         return this.value.floatValue();
      } else {
         return 0f;
      }
   }

   public void setLowerBound(double lowerBound) {
      this.lowerBound = lowerBound;
   }

   public void setUpperBound(double upperBound) {
      this.upperBound = upperBound;
   }

   public Signal get(double time) {
      return this.components.get(time);
   }

   public Signal firstEntry() {
      return this.components.firstEntry().getValue();
   }

   public Signal lastEntry() {
      return this.components.lastEntry().getValue();
   }

   public int numberOfComponents() {
      return this.components.size();
   }

   public List<Signal> toList() {
      List<Signal> outList = new LinkedList<Signal>();

      for (Signal component : this) {
         outList.add(component);
      }

      return outList;
   }

   public void setValue(double wholeSignalValue) {
      this.value = wholeSignalValue;
   }

   public double getMinVal() {
      return minVal;
   }

   public double getMaxVal() {
      return maxVal;
   }
   
   public double[] toDoubleValuesArray() {
      double[] valuesArray = new double[this.components.size()];
      int i = -1;
      for (Signal pulse : this) {
         valuesArray[++i] = pulse.getValue();
      }
      return valuesArray;
   }

   public Signal cloneAtLevel(int level) {
      Signal signalClone = new Signal(this.getTStart(), this.getTStop(), this.getValue());

      for (Signal component : this) {
         if (level > 0) { // Clone objects
            signalClone.addComponent(component.cloneAtLevel(level - 1));
         } else { // copy object references
            signalClone.addComponent(component);
         }
      }
      return signalClone;
   }

   public int count() {
      return components.size();
   }
   
   public Signal flat(){
      Signal flatted = new Signal();
      for (Signal window : this){
         double flattedValue = window.getValue();
         for (Signal component : window){
            flatted.addComponent(new Signal(component.getTStart(),component.getTStop(),flattedValue));
         }
      }
      return flatted;
   }


   @Override
   public Signal clone() {
      return this.cloneAtLevel(Integer.MAX_VALUE);
   }

   public Iterator<Signal> iterator() {
      return new SignalIterator();
   }

   // Auxiliary classes
   private static class SignalComparatorByValue implements Comparator<Signal> {

      public int compare(Signal o1, Signal o2) {
         return (o1.getValue() < o2.getValue() ? -1
                 : (o1.getValue() == o2.getValue() ? 0 : 1));
      }
   }

   public static Comparator<Signal> comparatorByValue() {
      return new SignalComparatorByValue();
   }

   private class SignalIterator implements Iterator<Signal> {

      Iterator innerIterator;

      public SignalIterator() {
         super();
         innerIterator = components.entrySet().iterator();
      }

      public boolean hasNext() {
         return innerIterator.hasNext();
      }

      public Signal next() {
         Map.Entry<Float, Signal> nextOne = (Map.Entry<Float, Signal>) innerIterator.next();
         return nextOne.getValue();
      }

      public void remove() {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
