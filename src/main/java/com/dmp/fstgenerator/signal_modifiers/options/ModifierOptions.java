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
   private Map<String,Object> options = new HashMap<String, Object>();
   
   public void setOption(String name, Object value){
      this.options.put(name.toLowerCase(), value);
   }
   
   public Object getOption(String name){
      return this.options.get(name.toLowerCase());
   }
   
}
