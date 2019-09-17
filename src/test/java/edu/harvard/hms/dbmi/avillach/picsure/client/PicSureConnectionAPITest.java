package edu.harvard.hms.dbmi.avillach.picsure.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;
import edu.harvard.dbmi.avillach.domain.SearchResults;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javax.management.Query;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;


public class PicSureConnectionAPITest {
    public final int WireMockPort = 8089;
    public URL urlEndpoint;
    public String tokenValue;
    public IPicSureConnectionAPI testApiObj;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockPort);

    @Before
    public void setup() {
        tokenValue = "any.token.value";
        try {
            urlEndpoint = new URL("http://localhost:" + Integer.toString(WireMockPort) + "/PIC-SURE/");
        } catch (MalformedURLException e) {
            fail("The testing url is malformed!");
            return;
        }

    }

    @Test
    public void testInstantiation() {
        String tokenValue = "any.token.value";
        URL anyUrl;

        try {
            anyUrl = new URL("http://localhost:8080/nothing");
        } catch (MalformedURLException e) {
            fail("The testing url is malformed!");
            return;
        }

        testApiObj = new PicSureConnectionAPI(anyUrl, tokenValue, false);
        assertNotNull(testApiObj);
    }


    @Test
    public void testApiCall_InfoResources() {
        // @GET    @Path("/info/resources")
        //  List<UUID> resources();

        // setup wiremock for the request
        stubFor(get(urlEqualTo("/PIC-SURE/info/resources"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("[\"995e4d38-6c46-475a-bdbf-ed706818982b\"]")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        List<UUID> results = testApiObj.resources();

        assertEquals("The returned UUID is not correct", UUID.fromString("995e4d38-6c46-475a-bdbf-ed706818982b"), results.get(0));

        // verify that wiremock request was correct
        verify(getRequestedFor(urlPathMatching("/PIC-SURE/info/resources"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_InfoId() {
        // @POST   @Path("/info/{resourceId}")
        //  OutputStream info(UUID resource_uuid);


        UUID resourceID = UUID.randomUUID();
        ObjectMapper objectMapper = new ObjectMapper();
        QueryRequest queryRequest = new QueryRequest();
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setId(resourceID);
        resourceInfo.setName("TESTING_NAME");
        String body = "BAD RESULT";
        try {
            body = objectMapper.writeValueAsString(resourceInfo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/info/" + resourceID.toString().replace("-", "")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody(body)
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        ResourceInfo results = testApiObj.resourceInfo(resourceID, queryRequest);

        assertEquals(resourceInfo.getId(), results.getId());
        assertEquals(resourceInfo.getName(), results.getName());

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/info/" + resourceID.toString().replace("-", "")))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_SearchId() {
        // @POST   @Path("/search/{resourceId}")
        //  OutputStream search(UUID resource_uuid, Object query);

        UUID resourceID = UUID.randomUUID();
        String path = "search/" + resourceID.toString().replace("-", "");
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery("{\"test\":\"search query\"}");
        String body = "BAD RESULT";
        ObjectMapper objectMapper = new ObjectMapper();

        SearchResults httpReturn = new SearchResults();
        httpReturn.setResults("GOOD RESULT");
        try {
            body = objectMapper.writeValueAsString(httpReturn);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/" + path))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody(body)
                )
        );


        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        SearchResults results = testApiObj.search(resourceID, queryRequest);

        assertEquals("The query results do not match", httpReturn.getResults(), results.getResults());

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/" + path))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_Query() {
        // @POST   @Path("/query")
        //  UUID query(UUID resource_uuid, Object query);

        QueryRequest queryRequest = new QueryRequest();

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/query"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        Object results = testApiObj.query(queryRequest);

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/query/"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_QueryIdStatus() {
        // @POST   @Path("/query/{queryId}/status")
        //  String queryStatus(UUID resource_uuid, UUID query_uuid);

        UUID queryID = UUID.randomUUID();
        QueryRequest queryRequest = new QueryRequest();

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/query/" + queryID.toString().replace("-", "") + "/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        Object results = testApiObj.queryStatus(queryID, queryRequest);

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/query/" + queryID.toString().replace("-", "") + "/status"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_QueryIdResult() {
        // @POST   @Path("/query/{queryId}/result")
        //  OutputStream queryResults(UUID resource_uuid, UUID query_uuid);

        UUID queryID = UUID.randomUUID();
        QueryRequest queryRequest = new QueryRequest();

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/query/" + queryID.toString().replace("-", "") + "/result"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        Object results = testApiObj.queryResult(queryID, queryRequest);

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/query/" + queryID.toString().replace("-", "") + "/result"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_QueryIdMetadata() {
        // @GET    @Path("/query/{queryId}/metadata")
        //  QueryStatus queryMetadata(UUID queryId);

        UUID queryID = UUID.randomUUID();
        QueryRequest queryRequest = new QueryRequest();

        // setup wiremock for the request
        stubFor(get(urlEqualTo("/PIC-SURE/query/" + queryID.toString().replace("-", "") + "/metadata"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        Object results = testApiObj.queryMetdata(queryID);

        // verify that wiremock request was correct
        verify(getRequestedFor(urlPathMatching("/PIC-SURE/query/" + queryID.toString().replace("-", "") + "/metadata"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_QuerySync() {
        // @POST   @Path("/query/sync")
        //  OutputStream syncQuery(UUID resource_uuid, Object query);

        QueryRequest queryRequest = new QueryRequest();

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/query/sync"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        Object results = testApiObj.querySync(queryRequest);

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/query/sync"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }
}