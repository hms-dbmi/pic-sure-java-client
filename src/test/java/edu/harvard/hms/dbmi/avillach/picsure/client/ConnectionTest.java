package edu.harvard.hms.dbmi.avillach.picsure.client;

import edu.harvard.hms.dbmi.avillach.picsure.client.impl.Client;
import edu.harvard.hms.dbmi.avillach.picsure.client.impl.Connection;
import edu.harvard.hms.dbmi.avillach.picsure.client.impl.PicSureConnectionAPI;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

class MockAdapter implements IPicSureAdapter {
    public final Connection connection;

    public MockAdapter(IConnection connection) {
        this.connection = (Connection) connection;
    }
    public void help() {}
    public void version() {}
    public void list() {}
    public IResourceConnection useResource(UUID resource_uuid) {
        return null;
    }
    public IPicSureConnectionAPI getApiObj() {
        URL endpoint;
        try {
            endpoint = new URL("http://localhost/anything");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return this.connection._api_obj();
    }
}

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
        IConnection connection = testClient.connect(anyUrl, "any.token.text", false);
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
        IConnection connection = testClient.connect(anyUrl, "any.token.text", false);
        MockAdapter adapter = new MockAdapter(connection);

        IPicSureConnectionAPI apiObj = adapter.getApiObj();
        Class[] interfaceList = apiObj.getClass().getInterfaces();
        for (Class iface : interfaceList) {
            if (iface.getName().equals("edu.harvard.hms.dbmi.avillach.picsure.client.IPicSureConnectionAPI")) return;
        }
        fail("returned connection does not implement IConnection class!");

    }


}
