package com.naijaplanet.magosla.android.moviesplanet.models;

public class Video {
    // id, key , name , site , type, size
    private String id;
    private String key;
    private String name;
    private String site;
    private String type;
    private int size;

    @SuppressWarnings("unused")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @SuppressWarnings("unused")
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

