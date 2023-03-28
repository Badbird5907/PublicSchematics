package dev.badbird.publicschematics.storage.impl;

import dev.badbird.publicschematics.PublicSchematics;
import dev.badbird.publicschematics.storage.StorageProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileStorageProvider implements StorageProvider {
    @Override
    public OutputStream getOutputStream(String name) {
        File folder = new File(PublicSchematics.getInstance().getDataFolder(), "schems");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(folder, name);
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init(PublicSchematics plugin) {

    }
}
