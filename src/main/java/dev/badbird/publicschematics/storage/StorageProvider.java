package dev.badbird.publicschematics.storage;

import dev.badbird.publicschematics.PublicSchematics;

import java.io.OutputStream;

public interface StorageProvider {
    void init(PublicSchematics plugin);
    OutputStream getOutputStream(String name);
}
