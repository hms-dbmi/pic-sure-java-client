package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.URL;
import java.util.List;

public interface IConnection {
    public final URL ENDPOINT = null;
    public final String TOKEN = null;

     void help();     // jShell
   String about(String resourceId);
     void list();     // jShell
   String getInfo(String uuid);
   String getResources();
}