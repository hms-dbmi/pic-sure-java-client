package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.util.UUID;

public abstract class BasePicSureAdapter {

    public BasePicSureAdapter() {}

    public static BasePicSureResourceConnection useResource(IPicSureConnection connection, UUID resource_uuid) {
        return null;
    }
}
