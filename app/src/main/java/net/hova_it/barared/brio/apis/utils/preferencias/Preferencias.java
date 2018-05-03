package net.hova_it.barared.brio.apis.utils.preferencias;

import android.content.Context;
import android.content.SharedPreferences;

import net.hova_it.barared.brio.apis.interfaces.Initializer;
import net.hova_it.barared.brio.apis.models.CachedValue;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by guillermo.ortiz on 09/02/18.
 */

public enum Preferencias implements PreferenciasCol, Initializer {
    i;
    
    
    private static SharedPreferences sp;
    
    
    private CachedValue<Boolean> show_dialog_beta;
    private CachedValue<Boolean> brio_ver_isbeta;
    private CachedValue<String> brio_ver_prev;
    private CachedValue<String> brio_ver_prevName;
    private CachedValue<Boolean> brio_ver_exist_update;
    private CachedValue<String> brio_ver_installed;
    private CachedValue<Integer> brio_contador_rollback;
    private CachedValue<Integer> id_comercio;
    
    private Set<CachedValue> cachedValues;
    private static final String EMPTY_STRING = "";
    private static final int DEFAULT_INT = 0;
    
    /**
     * Metodo que inicializa las preferencias de la aplicación.
     * Estas preferencias deberán ser inicializadas cuando la aplicación se inicie. (AppController)
     * @param context
     */
    
    @Override
    public void init(Context context) {
        sp = context.getSharedPreferences(PREFERENCIA_USUARIO, Context.MODE_PRIVATE);
        CachedValue.initialize(sp);
        
        cachedValues = new HashSet<>();
        
        cachedValues.add(show_dialog_beta = new CachedValue<>(MUESTRA_DIALOGO_BETA, true, Boolean.class));
        cachedValues.add(brio_ver_isbeta = new CachedValue<>(BRIO_VER_ISBETA, false, Boolean.class));
        cachedValues.add(brio_ver_prev = new CachedValue<>(BRIO_VER_PREV, EMPTY_STRING, String.class));
        cachedValues.add(brio_ver_prevName = new CachedValue<>(BRIO_VER_PREV_NAME, EMPTY_STRING, String.class));
        cachedValues.add(brio_ver_exist_update = new CachedValue<>(BRIO_VER_EXIST_UPDATE, false, Boolean.class));
        cachedValues.add(brio_ver_installed = new CachedValue<>(BRIO_VER_INSTALLED, EMPTY_STRING, String.class));
        cachedValues.add(brio_contador_rollback = new CachedValue<>(BRIO_CONTADOR_ROLLBACK, DEFAULT_INT, Integer.class));
        cachedValues.add(id_comercio = new CachedValue<>(ID_COMERCIO, DEFAULT_INT, Integer.class));
        
        
    }
    
    
    /**//////////////////////////////////////////////////////////////////////////
    // limpia todas las preferencias de la aplicación
    //////////////////////////////////////////////////////////////////////////*/
    @Override
    public void clear() {
        
        for (CachedValue value : cachedValues) {
            value.clear();
        } sp.edit().clear().apply();
    }
    
    
    public boolean getShowDialogBeta() {
        return show_dialog_beta.getValue();
    }
    
    public void setShowDialogBeta(boolean sesion) {
        this.show_dialog_beta.setValue(sesion);
    }
    
    public boolean getBrioVerIsBeta() {
        return brio_ver_isbeta.getValue();
    }
    
    public void setBrioVerIsBeta(boolean value) {
        this.brio_ver_isbeta.setValue(value);
    }
    
    public String getBrioVerPrev() {
        return brio_ver_prev.getValue();
    }
    
    public void setBrioVerPrev(String value) {
        this.brio_ver_prev.setValue(value);
    }
    
    public String getBrioVerPrevName() {
        return brio_ver_prevName.getValue();
    }
    
    public void setBrioVerPrevName(String value) {
        this.brio_ver_prevName.setValue(value);
    }
    
    public boolean getBrioVerExistUpdate() {
        return brio_ver_exist_update.getValue();
    }
    
    public void setBrioVerExistUpdate(boolean value) {
        this.brio_ver_exist_update.setValue(value);
    }
    
    public String getBrioVerInstalled() {
        return brio_ver_installed.getValue();
    }
    
    public void setBrioVerInstalled(String value) {
        this.brio_ver_installed.setValue(value);
    }
    
    public int getContadorRollback() {
        return brio_contador_rollback.getValue();
    }
    
    public void setContadorRollbackPlus() {
        int cont = brio_contador_rollback.getValue(); cont++;
        this.brio_contador_rollback.setValue(cont);
    }
    
    public void setContadorRollbackReset() {
        this.brio_contador_rollback.setValue(DEFAULT_INT);
    }
    
    public int getIdComercio() {
        return id_comercio.getValue();
    }
    
    
    public void setIdComercio(int value) {
        this.id_comercio.setValue(value);
    }
    
    
}
