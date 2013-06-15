/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dmp.fstgenerator.signal_modifiers.options;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author pdemartino
 */
public class ModifierOptions {
   private Map<String,String> options = new HashMap<String, String>();
   
   public void setOption(String name, String value){
      this.options.put(name.toLowerCase(), value);
   }
   
   public String getOption(String name){
      return this.options.get(name.toLowerCase());
   }
   
}
