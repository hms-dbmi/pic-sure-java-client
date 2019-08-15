package edu.harvard.hms.dbmi.avillach.picsure.client;

import edu.harvard.hms.dbmi.avillach.picsure.client.impl.Client;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ClientTest {

    @Test
    public void testInstantiation() {
        IClient testClient = new Client();
        assertNotNull(testClient);
    }

    @Test
    public void testConnectionBuilding() {
        IClient testClient = new Client();
        URL anyUrl;

        // the connection object returned by testObj should implement the IConnection interface
        try {
            anyUrl = new URL("http://localhost:8080/nothing");
        } catch (MalformedURLException e) {
            fail("The testing url is malformed!");
            return;
        }
        IConnection conn = testClient.connect(anyUrl, "any.token.value", false);
        Class[] interfaceList = conn.getClass().getInterfaces();
        for (Class iface : interfaceList) {
            if (iface.getName().equals("edu.harvard.hms.dbmi.avillach.picsure.client.IConnection")) return;
        }
        fail("returned connection does not implement IConnection class!");
    }



}
