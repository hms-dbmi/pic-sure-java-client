package edu.harvard.hms.dbmi.avillach.picsure.client.api;

import java.net.URL;
import java.util.List;
import java.util.UUID;

public interface IPicSureConnection {


    URL getENDPOINT();


    String getTOKEN();


    /**
     * Class function for use in jShell to print help instructions on the screen for this object's use.
     * @since   1.0
     */
    void help();


    String about(String resourceId);


    List<UUID> list();


    String getInfo(String uuid);


    String getResources();         // for jShell


    IPicSureConnectionAPI getApiObject();


}