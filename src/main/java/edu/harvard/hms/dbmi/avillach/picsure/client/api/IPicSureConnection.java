package edu.harvard.hms.dbmi.avillach.picsure.client.api;

import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;

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
    void help();                        // for jShell
    String about(String resourceId);    // for jShell
    String list();                      // for jShell


    ResourceInfo getResourceInfo(String uuid, QueryRequest queryRequest);
    List<UUID>  getResources();
    IPicSureConnectionAPI getApiObject();

}