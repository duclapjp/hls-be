package com.cnctor.hls.app.task;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.amazonaws.services.s3.model.S3Object;
import com.cnctor.hls.domain.common.utils.MimeTypeConstants;
import com.cnctor.hls.domain.service.aws.s3.S3Service;
import com.cnctor.hls.domain.service.aws.s3.S3Service.BucketName;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileResponseHelper {

  @Inject
  S3Service s3Service;
  
  public void retrieveFile(String fileName, String filePath, HttpServletResponse response, HttpServletRequest request) throws IOException {
    InputStream in = null;
    OutputStream outstream = null;
    
    try {
      
      String fileExt = FilenameUtils.getExtension(filePath);
      String contentType = MimeTypeConstants.getMimeType(fileExt);
      
      S3Object s3Object = s3Service.getS3Object(BucketName.HLS_BUCKET, filePath);
      
      if (s3Object != null) {
        
        response.reset();
        
        InputStream s3InputStream = s3Object.getObjectContent();
        response.setContentType(contentType);
        response.setCharacterEncoding("UTF-8");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Authorization");
        
        if (request.getHeader("Access-Control-Request-Method") != null
            && "OPTIONS".equals(request.getMethod())) {
          log.info("Sending Header....");
          response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
          response.addHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With");
          response.addHeader("Access-Control-Max-Age", "1");
        }
        
        response.addHeader("content-disposition", "attachment; filename=\"" + fileName + "\"" + "; filename*=utf-8''" + URLEncoder.encode(fileName, "UTF-8"));
        //response.setHeader("X-Frame-Options", "SAMEORIGIN");
        IOUtils.copy(s3InputStream, response.getOutputStream());
        Thread.sleep(100);
        
        response.flushBuffer();
        log.info("Download finished");
        
        close(s3InputStream);
        close(s3Object);
      } else {
        response.setStatus(HttpStatus.SC_NOT_FOUND);
      }
    } catch (Exception e) {
      log.error("Unable to download file " + e.getMessage());
    } finally {
      IOUtils.closeQuietly(outstream);
      IOUtils.closeQuietly(in);
    }
  }
  
  private static void close(Closeable resource) {
    if (resource != null) {
      try {
        resource.close();
      } catch (IOException ignore) {
      }
    }
  }
}
