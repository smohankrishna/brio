package net.hova_it.barared.brio.apis.models;

import android.support.annotation.NonNull;

/**
 * Created by guillermo.ortiz on 21/03/18.
 */

public class DLSortMenu implements Comparable <DLSortMenu> {
    
    String key;//ENUM
    int value;//
    
    public DLSortMenu (String key, int value) {
        this.key = key;
        this.value = value;
    }
    
    public String getKey () {
        return key;
    }
    
    public void setKey (String key) {
        this.key = key;
    }
    
    public int getValue () {
        return value;
    }
    
    public void setValue (int value) {
        this.value = value;
    }
    
    @Override
    public int compareTo (@NonNull DLSortMenu another) {
        int compareage= another.getValue ();
        /* For Ascending order*/
//        return this.value-compareage;
         /* For Descending order do like this */
         return compareage-this.value;
    }
}
