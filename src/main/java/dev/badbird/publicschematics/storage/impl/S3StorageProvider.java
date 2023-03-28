package dev.badbird.publicschematics.storage.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import dev.badbird.publicschematics.PublicSchematics;
import dev.badbird.publicschematics.storage.StorageProvider;
import dev.badbird.publicschematics.util.S3OutputStream;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.OutputStream;

public class S3StorageProvider implements StorageProvider {
    private AWSCredentials credentials;
    private AmazonS3 s3client;
    private String bucket;

    @Override
    public void init(PublicSchematics plugin) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("s3.auth.enabled")) {
            credentials = new BasicAWSCredentials(
                    config.getString("s3.auth.accessKey", "dummy"),
                    config.getString("s3.auth.secretKey", "dummy")
            );
        }
        bucket = config.getString("s3.bucket");
        bucket = config.getString("s3.path");
        String region = config.getString("s3.region");
        AmazonS3ClientBuilder cb = AmazonS3ClientBuilder.standard();
        if (credentials != null) {
            cb.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        if (region != null) {
            cb.withRegion(Regions.fromName(region));
        }
        s3client = cb.build();
    }

    @Override
    public OutputStream getOutputStream(String name) {
        return new S3OutputStream(s3client, bucket, name);
    }
}
