package dev.badbird.publicschematics.storage.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import dev.badbird.publicschematics.PublicSchematics;
import dev.badbird.publicschematics.storage.StorageProvider;
import dev.badbird.publicschematics.util.S3OutputStream;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.OutputStream;

public class S3StorageProvider implements StorageProvider {
    private AWSCredentials credentials;
    private AmazonS3 s3client;
    private String bucket;
    private String endpoint;

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
        endpoint = config.getString("s3.endpoint");
        String region = config.getString("s3.region");
        AmazonS3ClientBuilder cb = AmazonS3ClientBuilder.standard();
        if (credentials != null) {
            cb.withCredentials(new AWSStaticCredentialsProvider(credentials));
        }
        if (endpoint != null) {
            cb.setEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region));
        } else if (region != null) {
            cb.withRegion(Regions.fromName(region));
        }
        System.out.println("S3StorageProvider: " + cb + " bucket: " + bucket + " endpoint: " + endpoint + " region: " + region + " credentials: " + credentials);
        s3client = cb.build();
        BucketLifecycleConfiguration lifecycleConfiguration = new BucketLifecycleConfiguration();
        lifecycleConfiguration.withRules(
                new BucketLifecycleConfiguration.Rule()
                        .withId("Delete after 1 day")
                        .withExpirationInDays(1)
        );
        // s3client.setBucketLifecycleConfiguration(bucket, lifecycleConfiguration);
    }

    @Override
    public OutputStream getOutputStream(String name) {
        return new S3OutputStream(s3client, bucket, name);
    }
}
