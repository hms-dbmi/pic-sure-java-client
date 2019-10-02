package edu.harvard.hms.dbmi.avillach.picsure.client.api;

import java.util.UUID;

public abstract class BasePicSureAdapter {

    public BasePicSureAdapter() {}

    // this must have override done by an implementing PIC-SURE Resource library
    public static BasePicSureResourceConnection useResource(IPicSureConnection connection, UUID resource_uuid) {
        return null;
    }
}
