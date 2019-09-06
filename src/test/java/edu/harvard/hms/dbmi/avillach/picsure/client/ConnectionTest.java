package edu.harvard.hms.dbmi.avillach.picsure.client;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ConnectionTest {
    private URL anyUrl;

    @Test
    public void testInstantiation() {
        try {
            anyUrl = new URL("http://localhost:8080/nothing");
        } catch (MalformedURLException e) {
            fail("The testing url is malformed!");
            return;
        }
        Client testClient = new Client();
        Object connection = testClient.connect(anyUrl, "any.token.text", false);
        assertNotNull(connection);
    }

    @Test
    public void testConnectionAPI() {
        try {
            anyUrl = new URL("http://localhost:8080/nothing");
        } catch (MalformedURLException e) {
            fail("The testing url is malformed!");
            return;
        }
        Client testClient = new Client();
        Connection connection = testClient.connect(anyUrl, "any.token.text", false);

        Object apiObj = connection.getApiObject();
        Class[] interfaceList = apiObj.getClass().getInterfaces();
        for (Class iface : interfaceList) {
            if (iface.getName().equals("edu.harvard.hms.dbmi.avillach.picsure.client.IPicSureConnectionAPI")) return;
        }
        fail("returned connection does not implement IPicSureConnection class!");
    }

}
