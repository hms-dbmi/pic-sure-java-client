package edu.harvard.hms.dbmi.avillach.picsure.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.QueryStatus;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;
import edu.harvard.dbmi.avillach.domain.SearchResults;
import edu.harvard.dbmi.avillach.util.PicSureStatus;
import edu.harvard.hms.dbmi.avillach.picsure.client.api.IPicSureConnectionAPI;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * PicSureConnectionAPI is the core functionality of this
 * library and is used by all PIC-SURE Resource Adapter
 * libraries' code to perform actions via the PIC-SURE
 * Network's standard API calls.
 * @author  Nick Benik
 * @version %I%, %G%
 * @since   1.0
 * @see     edu.harvard.dbmi.avillach.PicsureRS
 */
@SuppressWarnings("JavadocReference")
public class PicSureConnectionAPI implements IPicSureConnectionAPI {

    public static final String ERROR_MSG_URL_ERROR = "The specified URL has caused an error: ";
    private static final int TIMEOUT_SECS = 60;

    public final URL ENDPOINT;
    public final String TOKEN;
    private boolean allowSelfSigned = false;
    private static TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };
    private HttpClient httpClient;

    public PicSureConnectionAPI(URL url, String token, boolean allowSelfSignedSSL) {
        URL tempEndpoint = null;
        // make sure the endpoint ends with a "/"
        if (url.getFile().endsWith("/")) {
            tempEndpoint = url;
        } else {
            try {
                tempEndpoint = new URL(url, url.getFile() + "/");
            } catch (MalformedURLException e) {
                // TODO: throw error
                e.printStackTrace();
            }
        }
        this.ENDPOINT = tempEndpoint;
        this.TOKEN = token;
        this.allowSelfSigned = allowSelfSignedSSL;

        if (allowSelfSignedSSL == true) {
            SSLContext sslContext;
            try {
                sslContext = SSLContext.getInstance("TLS");
                try {
                    sslContext.init(null, trustAllCerts, new SecureRandom());
                    this.httpClient = HttpClient.newBuilder()
                            .followRedirects(HttpClient.Redirect.NORMAL)
                            .connectTimeout(Duration.ofSeconds(TIMEOUT_SECS))
                            .sslContext(sslContext) // SSL context initialised earlier
                            .build();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            this.httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(TIMEOUT_SECS))
                    .build();
        }
    }


    @Override
    public ResourceInfo resourceInfo(UUID resourceId, QueryRequest credentialsQueryRequest) throws SecurityException {
        // @POST   @Path("/info/{resourceId}")
        //  OutputStream info(UUID resource_uuid);

        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        String path = "info/" + resourceId.toString();
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + this.ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        String qrCredentials = null;
        try {
            qrCredentials = objectMapper.writeValueAsString(credentialsQueryRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .POST(BodyPublishers.ofString(qrCredentials))
                .build();

        // send request and process response
        ResourceInfo response = null;
        HttpResponse httpResponse = null;
        try {
            httpResponse = this.httpClient.send(requestBuilder, HttpResponse.BodyHandlers.ofString());
            switch (httpResponse.statusCode()) {
                case 200:
                    response = objectMapper.readValue(httpResponse.body().toString(), ResourceInfo.class);
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // process the returned result to be a ResourceInfo object?
        return response;
    }

    @Override
    public List<UUID> resources() throws SecurityException {
        // @GET    @Path("/info/resources")
        //  List<UUID> resources();

        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        String path = "info/resources";
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + this.ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        String qrCredentials = null;
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .GET()
                .build();

        // send request and process response
        List<UUID> ret = null;
        try {
            HttpResponse httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofString());
            switch (httpResponse.statusCode()) {
                case 200:
                    ret = objectMapper.readValue(httpResponse.body().toString(), new TypeReference<List<UUID>>(){});
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // process the returned result to be a ResourceInfo object?
        return ret;
    }

    @Override
    public SearchResults search(UUID resourceId, QueryRequest searchQueryRequest) throws SecurityException {
        // @POST   @Path("/search/{resourceId}")
        //  OutputStream search(UUID resource_uuid, Object query);

        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        String path = "search/" + resourceId.toString();
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        String qrCredentials = null;
        String body = "";
        try {
            body = objectMapper.writeValueAsString(searchQueryRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .POST(BodyPublishers.ofString(body))
                .build();


        // send request and process response
        SearchResults ret = new SearchResults();
        try {
            HttpResponse httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofString());
            switch (httpResponse.statusCode()) {
                case 200:
                    ret = objectMapper.readValue(httpResponse.body().toString(), new TypeReference<SearchResults>(){});
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public QueryStatus query(QueryRequest dataQueryRequest) throws SecurityException {
        // @POST   @Path("/query")
        //  QueryStatus query(UUID resource_uuid, Object query);

        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        String path = "query/";
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        String body = "";
        QueryStatus queryStatus = new QueryStatus();
        queryStatus.setStatus(PicSureStatus.PENDING);
        try {
            body = objectMapper.writeValueAsString(dataQueryRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .POST(BodyPublishers.ofString(body))
                .build();


        // send request and process response
        QueryStatus ret = new QueryStatus();
        try {
            HttpResponse httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofString());
            switch (httpResponse.statusCode()) {
                case 200:
                    ret = objectMapper.readValue(httpResponse.body().toString(), new TypeReference<QueryStatus>(){});
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public QueryStatus queryStatus(UUID queryId, QueryRequest credentialsQueryRequest) throws SecurityException {
        // @POST   @Path("/query/{queryId}/status")
        //  QueryStatus queryStatus(UUID resource_uuid, UUID query_uuid);

        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        String path = "query/" + queryId.toString() + "/status";
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        String body = "";
        try {
            body = objectMapper.writeValueAsString(credentialsQueryRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .POST(BodyPublishers.ofString(body))
                .build();

        // send request and process response
        QueryStatus ret = new QueryStatus();
        try {
            HttpResponse httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofString());
            switch (httpResponse.statusCode()) {
                case 200:
                    ret = objectMapper.readValue(httpResponse.body().toString(), new TypeReference<QueryStatus>(){});
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public InputStream queryResult(UUID queryId, QueryRequest credentialsQueryRequest) throws SecurityException {
        // @POST    @Path("/query/{queryId}/result")
        //  InputStream queryResult(UUID queryId, QueryRequest credentialsQueryRequest);

        URI targetUri = null;
        String path = "query/" + queryId.toString()+"/result";
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        ObjectMapper objectMapper = new ObjectMapper();
        String body = "";
        try {
            body = objectMapper.writeValueAsString(credentialsQueryRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .POST(BodyPublishers.ofString(body))
                .build();

        // send request and process response
        HttpResponse httpResponse = null;
        InputStream ret = null;
        try {
            httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofInputStream());
            switch (httpResponse.statusCode()) {
                case 200:
                    ret = (InputStream) httpResponse.body();
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public InputStream querySync(QueryRequest credentialsQueryRequest) throws SecurityException {
        // @POST    @Path("/query/sync")
        //  InputStream querySync(QueryRequest credentialsQueryRequest);
        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        String path = "query/sync";
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        String body = "";
        QueryStatus queryStatus = new QueryStatus();
        queryStatus.setStatus(PicSureStatus.PENDING);
        try {
            body = objectMapper.writeValueAsString(credentialsQueryRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .POST(BodyPublishers.ofString(body))
                .build();

        // send request and process response
        HttpResponse httpResponse = null;
        InputStream ret = null;
        try {
            httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofInputStream());
            switch (httpResponse.statusCode()) {
                case 200:
                    ret = (InputStream) httpResponse.body();
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public QueryStatus queryMetdata(UUID queryId) throws SecurityException {
        // @POST   @Path("/query/{queryId}/status")
        //  QueryStatus queryStatus(UUID resource_uuid, UUID query_uuid);

        URI targetUri = null;
        String path = "query/" + queryId.toString() + "/metadata";
        try {
            targetUri = this.ENDPOINT.toURI().resolve(path);
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + path + " [URISyntaxException]");
        }

        // build the request
        HttpRequest requestBuilder = HttpRequest.newBuilder()
                .header("Content-Type", "application/json")
                .header("AUTHORIZATION", "Bearer "+this.TOKEN)
                .uri(targetUri)
                .GET()
                .build();

        // send request and process response
        ObjectMapper objectMapper = new ObjectMapper();
        QueryStatus ret = new QueryStatus();
        try {
            HttpResponse httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofString());
            switch (httpResponse.statusCode()) {
                case 200:
                    ret = objectMapper.readValue(httpResponse.body().toString(), new TypeReference<QueryStatus>(){});
                    break;
                case 401:
                    throw new SecurityException(httpResponse.body().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

}
