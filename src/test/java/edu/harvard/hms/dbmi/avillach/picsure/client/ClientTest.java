package edu.harvard.hms.dbmi.avillach.picsure.client;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class ClientTest {

    @Test
    public void testInstantiation() {
        Client testClient = new Client();
        assertNotNull(testClient);
    }

    @Test
    public void testConnectionBuilding() {
        Client testClient = new Client();
        URL anyUrl;

        // the connection object returned by testObj should implement the IConnection interface
        try {
            anyUrl = new URL("http://localhost:8080/nothing");
        } catch (MalformedURLException e) {
            fail("The testing url is malformed!");
            return;
        }
        Object conn = testClient.connect(anyUrl, "any.token.value", false);
        Class[] interfaceList = conn.getClass().getInterfaces();

        for (Class iface : interfaceList) {
            if (iface.getName().equals("edu.harvard.hms.dbmi.avillach.picsure.client.IPicSureConnection")) return;
        }
        fail("returned connection does not implement IPicSureConnection class!");
    }


}
