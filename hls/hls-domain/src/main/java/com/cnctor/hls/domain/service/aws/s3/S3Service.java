package com.cnctor.hls.domain.service.aws.s3;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class S3Service {
  
  @Value("${aws.s3.bucket.files.storage}")
  private String storageBucket;
  /*
  @Value("${aws.s3.credentials.awsAccessKeyId}")
  private String awsAccessKeyId;
  
  @Value("${aws.s3.credentials.awsSecretAccessKey}")
  private String awsSecretAccessKey;
  */
  private AmazonS3 s3Client;
  
  
  @PostConstruct
  private void initializeAmazon() {
    
    /*
    AWSCredentials credentials = new BasicAWSCredentials(
        awsAccessKeyId, 
        awsSecretAccessKey
    );
    */
    
    this.s3Client = AmazonS3ClientBuilder
        .standard()
        //.withCredentials(new AWSStaticCredentialsProvider(credentials))
        .withCredentials(new InstanceProfileCredentialsProvider(false))
        .withRegion(Regions.AP_NORTHEAST_1)
        .build();
  }
  
  public String upload(BucketName bucket, String originFileName, String s3FileName, byte[] fileDatas) throws IOException {
    String bucketName = getBucket(bucket);

    try (InputStream input = new ByteArrayInputStream(fileDatas)) {
      
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(fileDatas.length);
      //metadata.addUserMetadata("x-amz-meta-originname", originFileName);
      
      s3Client.putObject(bucketName, s3FileName, input, metadata);
      
      return s3FileName;
    } catch (IOException e) {
      log.error("Cannot upload file to s3", e);
      throw e;
    }
  }
  
  public S3Object getS3Object(BucketName bucketName, String name) {
    if (doesFileExistInS3Bucket(bucketName, name)) {
      S3Object s3Object = s3Client.getObject(getBucket(bucketName), name);
      return s3Object;
    }
    return null;
  }
  
  
  public boolean doesFileExistInS3Bucket(BucketName bucketName, String key) {
    boolean exist = false;

    try {
      exist = s3Client.doesObjectExist(getBucket(bucketName), key);
    } catch (SdkClientException ex) {
      log.error("Cannot check file exist in s3", ex);
    }

    return exist;
  }
  
  /**
   *  Duplicate Object 
   * 
   */
  public String duplicate(BucketName srcBucket, String sourceKey, BucketName destBucket, String destKey) throws Exception{
    try {
      
      String fileExt = FilenameUtils.getExtension(destKey);
      if(fileExt == null)
        fileExt = StringUtils.EMPTY;
      
      s3Client.copyObject(getBucket(srcBucket), sourceKey, getBucket(destBucket), destKey);
      return destKey;
    } catch (Exception e) {
      log.error("cannot move file ", e);
      throw e;
    }
  }
  
  
  /**
   * Delete file in s3.
   * 
   * @param category
   * @param name
   * @return delete complete or not
   */
  public boolean deleteFile(BucketName bucketName, String name) {
    boolean deleted = false;

    try {
      if (doesFileExistInS3Bucket(bucketName, name)) {
        s3Client.deleteObject(getBucket(bucketName), name);

        deleted = true;
      }
    } catch (SdkClientException ex) {
      log.error("Cannot delete file in s3", ex);
    }

    return deleted;
  }
  
  public static enum BucketName {
    HLS_BUCKET
  }
  
  /**
   * Get the name of bucket on Aws S3.
   * 
   * @param bucketName
   * @return bucket name
   */
  private String getBucket(BucketName bucketName) {
    switch (bucketName) {
      case HLS_BUCKET:
        return storageBucket;
      default:
        return null;
    }
  }
}
