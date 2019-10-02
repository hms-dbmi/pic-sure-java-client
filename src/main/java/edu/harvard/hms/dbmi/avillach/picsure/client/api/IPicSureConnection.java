package edu.harvard.hms.dbmi.avillach.picsure.client.api;

import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;

import java.net.URL;
import java.util.List;
import java.util.UUID;

/**
 * IPicSureConnection is the core functionality which is used
 * by PIC-SURE Resources in their code to access information
 * about the connection to the PIC-SURE network and resources
 * that are hosted on it.
 * @author  Nick Benik
 * @version %I%, %G%
 * @since   1.0
 */

public interface IPicSureConnection {


    URL getENDPOINT();


    String getTOKEN();


    ResourceInfo getResourceInfo(String uuid, QueryRequest queryRequest);


    List<UUID>  getResources();


    IPicSureConnectionAPI getApiObject();

}