package net.hova_it.barared.brio.apis.sync.entities;

/**
 * Created by Alejandro Gomez on 16/03/2016.
 */
public class FTPData {
    private int port;
    private String filename;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
