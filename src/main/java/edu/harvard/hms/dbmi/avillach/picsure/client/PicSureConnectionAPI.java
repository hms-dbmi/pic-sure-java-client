package edu.harvard.hms.dbmi.avillach.picsure.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.harvard.dbmi.avillach.data.entity.Resource;
import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.QueryStatus;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;
import edu.harvard.dbmi.avillach.domain.SearchResults;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.*;
import java.net.http.HttpRequest.*;
import java.net.http.HttpResponse.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.UUID;


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
        this.ENDPOINT = url;
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
    public ResourceInfo resourceInfo(UUID resourceId, QueryRequest credentialsQueryRequest) {
        // POST "/info/{resourceId}"

        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        try {
            targetUri = new URL(this.ENDPOINT, "/info/" + resourceId.toString()).toURI();
        } catch (MalformedURLException e) {
            throw new Error(ERROR_MSG_URL_ERROR + this.ENDPOINT + "/info/" + resourceId.toString() + " [MalformedURLException]");
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + this.ENDPOINT + "/info/" + resourceId.toString() + " [URISyntaxException]");
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
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = this.httpClient.send(requestBuilder, HttpResponse.BodyHandlers.ofString());
            response = objectMapper.readValue(httpResponse.toString(), ResourceInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // process the returned result to be a ResourceInfo object?
        return response;
    }

    @Override
    public List<UUID> resources() {
        // GET "/info/resources"

        ObjectMapper objectMapper = new ObjectMapper();
        URI targetUri = null;
        try {
            targetUri = new URL(this.ENDPOINT, "/resources").toURI();
        } catch (MalformedURLException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + "/resources [MalformedURLException]");
        } catch (URISyntaxException e) {
            throw new Error(ERROR_MSG_URL_ERROR + ENDPOINT + "/resources [URISyntaxException]");
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
            HttpResponse<String> httpResponse = this.httpClient.send(requestBuilder, BodyHandlers.ofString());
            ret = objectMapper.readValue(httpResponse.toString(), new TypeReference<List<UUID>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // process the returned result to be a ResourceInfo object?
        return ret;
    }

    @Override
    public SearchResults search(UUID resourceId, QueryRequest searchQueryRequest) {
        SearchResults ret = new SearchResults();
        return ret;
    }

    @Override
    public QueryStatus query(QueryRequest dataQueryRequest) {

        return null;
    }

    @Override
    public QueryStatus queryStatus(UUID queryId, QueryRequest credentialsQueryRequest) {
        return null;
    }

    @Override
    public InputStream queryResult(UUID queryId, QueryRequest credentialsQueryRequest) {
        return null;
    }

    @Override
    public InputStream querySync(QueryRequest credentialsQueryRequest) {
        return null;
    }

    @Override
    public QueryStatus queryMetdata(UUID queryId) {
        return null;
    }

}
