package edu.harvard.hms.dbmi.avillach.picsure.client;

import java.util.UUID;

public interface IPicSureAdapter {
    public void help();
    public void version();
    public void list();
    public IResourceConnection useResource(UUID resource_uuid);
}
