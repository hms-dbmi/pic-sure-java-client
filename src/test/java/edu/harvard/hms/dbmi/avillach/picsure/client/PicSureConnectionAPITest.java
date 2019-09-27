package edu.harvard.hms.dbmi.avillach.picsure.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.QueryStatus;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;
import edu.harvard.dbmi.avillach.domain.SearchResults;
import edu.harvard.dbmi.avillach.util.PicSureStatus;
import edu.harvard.hms.dbmi.avillach.picsure.client.api.IPicSureConnectionAPI;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;


public class PicSureConnectionAPITest {
    private final int WireMockPort = 8089;
    private URL urlEndpoint;
    private String tokenValue;
    private UUID resourceUUID;
    private IPicSureConnectionAPI testApiObj;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockPort);

    @Before
    public void setup() {
        try {
            urlEndpoint = new URL("http://localhost:" + Integer.toString(WireMockPort) + "/PIC-SURE/");
        } catch (MalformedURLException e) {
            fail("The testing url is malformed!");
            return;
        }

        tokenValue = "any.token.value";
        resourceUUID = UUID.randomUUID();
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

        ObjectMapper objectMapper = new ObjectMapper();
        QueryRequest queryRequest = new QueryRequest();
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setId(resourceUUID);
        resourceInfo.setName("TESTING_NAME");
        String body = "BAD RESULT";
        try {
            body = objectMapper.writeValueAsString(resourceInfo);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/info/" + resourceUUID.toString()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody(body)
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        ResourceInfo results = testApiObj.resourceInfo(resourceUUID, queryRequest);

        assertEquals(resourceInfo.getId(), results.getId());
        assertEquals(resourceInfo.getName(), results.getName());

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/info/" + resourceUUID.toString()))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_SearchId() {
        // @POST   @Path("/search/{resourceId}")
        //  OutputStream search(UUID resource_uuid, Object query);

        String path = "search/" + resourceUUID.toString();
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
        SearchResults results = testApiObj.search(resourceUUID, queryRequest);

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

        UUID queryID = UUID.randomUUID();
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery("{\"test\":\"search query\"}");
        queryRequest.setResourceUUID(resourceUUID);

        ObjectMapper objectMapper = new ObjectMapper();
        QueryStatus queryStatus = new QueryStatus();
        queryStatus.setDuration(1000);
        queryStatus.setStatus(PicSureStatus.AVAILABLE);
        queryStatus.setResourceID(resourceUUID );
        queryStatus.setPicsureResultId(queryID);
        queryStatus.setSizeInBytes(2000);
        queryStatus.setResourceStatus("OK");
        queryStatus.setResultMetadata("SOME_METADATA".getBytes());
        // queryStatus.setResourceResultId(); // generated by IRCT
        String body = "BAD RESULT";
        try {
            body = objectMapper.writeValueAsString(queryStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/query/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody(body)
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
        //  QueryStatus queryStatus(UUID resource_uuid, UUID query_uuid);

        UUID queryID = UUID.randomUUID();
        UUID resourceID = UUID.randomUUID();
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setQuery("{\"test\":\"search query\"}");
        queryRequest.setResourceUUID(resourceID);

        ObjectMapper objectMapper = new ObjectMapper();
        QueryStatus queryStatus = new QueryStatus();
        queryStatus.setDuration(1000);
        queryStatus.setStatus(PicSureStatus.AVAILABLE);
        queryStatus.setResourceID(resourceID);
        queryStatus.setPicsureResultId(queryID);
        queryStatus.setSizeInBytes(2000);
        queryStatus.setResourceStatus("OK");
        queryStatus.setResultMetadata("SOME_METADATA".getBytes());
        // queryStatus.setResourceResultId(); // generated by IRCT
        String body = "BAD RESULT";
        try {
            body = objectMapper.writeValueAsString(queryStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/query/" + queryID.toString() + "/status"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody(body)
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        Object results = testApiObj.queryStatus(queryID, queryRequest);

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/query/" + queryID.toString() + "/status"))
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
        stubFor(post(urlEqualTo("/PIC-SURE/query/" + queryID.toString() + "/result"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("THIS\tIS\tSOMETHING\tIN\tCSV\tFORMAT")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        InputStream results = testApiObj.queryResult(queryID, queryRequest);

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/query/" + queryID.toString() + "/result"))
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

        ObjectMapper objectMapper = new ObjectMapper();
        QueryStatus queryStatus = new QueryStatus();
        queryStatus.setDuration(1000);
        queryStatus.setStatus(PicSureStatus.AVAILABLE);
        queryStatus.setResourceID(resourceUUID);
        queryStatus.setPicsureResultId(queryID);
        queryStatus.setSizeInBytes(2000);
        queryStatus.setResourceStatus("OK");
        queryStatus.setResultMetadata("SOME_METADATA".getBytes());
        // queryStatus.setResourceResultId(); // generated by IRCT

        String body = "BAD BODY";
        try {
            body = objectMapper.writeValueAsString(queryStatus);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // setup wiremock for the request
        stubFor(get(urlEqualTo("/PIC-SURE/query/" + queryID.toString() + "/metadata"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody(body)
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        Object results = testApiObj.queryMetdata(queryID);

        // verify that wiremock request was correct
        verify(getRequestedFor(urlPathMatching("/PIC-SURE/query/" + queryID.toString() + "/metadata"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }

    @Test
    public void testApiCall_QuerySync() {
        // @POST   @Path("/query/sync")
        //  InputStream syncQuery(UUID resource_uuid, Object query);

        QueryRequest queryRequest = new QueryRequest();

        // setup wiremock for the request
        stubFor(post(urlEqualTo("/PIC-SURE/query/sync"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withHeader("AUTHORIZATION", "Bearer " + tokenValue)
                        .withBody("THIS\tIS\tSOMETHING\tIN\tCSV\tFORMAT")
                )
        );

        // send the request
        testApiObj = new PicSureConnectionAPI(urlEndpoint, tokenValue, false);
        InputStream results = testApiObj.querySync(queryRequest);

        // verify that wiremock request was correct
        verify(postRequestedFor(urlPathMatching("/PIC-SURE/query/sync"))
                .withHeader("Content-Type", matching("application/json"))
                .withHeader("AUTHORIZATION", matching("Bearer " + tokenValue))
        );
    }
}