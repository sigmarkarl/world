package org.simmi.javafasta.shared;

import java.util.Map;

/**
 * Created by sigmar on 10/07/2017.
 */
public interface GenomeSet {
    Map<String,String> getCazyAAMap();
    Map<String,String> getCazyCEMap();
    Map<String,String> getCazyGHMap();
    Map<String,String> getCazyGTMap();
    Map<String,String> getCazyPLMap();
    Map<String,String> getDesignationMap();
}
