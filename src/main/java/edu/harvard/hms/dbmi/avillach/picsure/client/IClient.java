package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.URL;

public interface IClient {
           void version();      // for jShell
           void help();         // for jShell
    IConnection connect(URL url, String token, Boolean allowSelfSignedSSL);
}
