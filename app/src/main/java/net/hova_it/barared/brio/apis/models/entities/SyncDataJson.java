package net.hova_it.barared.brio.apis.models.entities;

import com.google.gson.Gson;

/**
 * Created by Herman Peralta on 01/08/2016.
 */
public class SyncDataJson {
    private Long id;
    private String json;

    /*
    public SyncDataJson(Gson gson, String json) {
        Type type = new TypeToken<SyncDataJson<T>>(){}.getType();
        SyncDataJson<T> copy = gson.fromJson(json, type);

        this.id = copy.id;
        this.json = copy.json;
    }
    */

    public SyncDataJson(Gson gson, String json) {
        SyncDataJson copy = gson.fromJson(json, SyncDataJson.class);

        this.id = copy.id;
        this.json = copy.json;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Object getObject(Gson gson, Class clazz) {
        return gson.fromJson(json, clazz);
    }
}

