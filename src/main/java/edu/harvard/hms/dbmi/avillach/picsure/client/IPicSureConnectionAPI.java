package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.net.URL;
import java.util.UUID;

public interface IPicSureConnectionAPI {
    String info(UUID resource_uuid);
    String search(UUID resource_uuid, String query);
    String asynchQuery(UUID resource_uuid, String query);
    String syncQuery(UUID resource_uuid, String query);
    String queryStatus(UUID resource_uuid, UUID query_uuid);
    String queryResults(UUID resource_uuid, UUID query_uuid);
}
