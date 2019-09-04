package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public interface IPicSureConnection {

    URL getENDPOINT();
    String getTOKEN();

    void help();                   // for jShell
    String about(String resourceId);
    List<UUID> list();
    String getInfo(String uuid);
    String getResources();         // for jShell
    IPicSureConnectionAPI getApiObject();
}