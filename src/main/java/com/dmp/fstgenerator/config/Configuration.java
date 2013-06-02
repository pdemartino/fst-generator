
package com.dmp.fstgenerator.config;

/**
 *
 * @author pdemartino
 */
public class Configuration {
   // The SNP's real distance from the previous one may lay between
   // distance +|- (distance * DISTANCE_PERC_VAR)
   public static final Double DISTANCE_PERC_VAR = 0.5;
   public static final Double FST_BACKGROUND = 0.15;
}
