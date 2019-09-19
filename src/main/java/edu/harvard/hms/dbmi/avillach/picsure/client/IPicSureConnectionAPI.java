package edu.harvard.hms.dbmi.avillach.picsure.client;

import edu.harvard.dbmi.avillach.domain.QueryRequest;
import edu.harvard.dbmi.avillach.domain.QueryStatus;
import edu.harvard.dbmi.avillach.domain.ResourceInfo;
import edu.harvard.dbmi.avillach.domain.SearchResults;
import edu.harvard.dbmi.avillach.data.entity.Resource;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface IPicSureConnectionAPI {
    // @Path("/info/{resourceId}")
    public ResourceInfo resourceInfo(UUID resourceId, QueryRequest credentialsQueryRequest);

    // @Path("/info/resources")
    public List<UUID> resources();

    // @Path("/search/{resourceId}")
    public SearchResults search(UUID resourceId, QueryRequest searchQueryRequest);

    // @Path("/query")
    public QueryStatus query(QueryRequest dataQueryRequest);

    // @Path("/query/{queryId}/status")
    public QueryStatus queryStatus(UUID queryId, QueryRequest credentialsQueryRequest);

    // @Path("/query/{queryId}/result")
    public InputStream queryResult(UUID queryId, QueryRequest credentialsQueryRequest);

    // @Path("/query/sync")
    public InputStream querySync(QueryRequest credentialsQueryRequest);

    // @Path("/query/{queryId}/metadata")
    public QueryStatus queryMetdata(UUID queryId);
}
