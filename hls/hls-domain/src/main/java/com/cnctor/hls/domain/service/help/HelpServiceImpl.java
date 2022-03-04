package com.cnctor.hls.domain.service.help;

import java.io.IOException;
import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.Help;
import com.cnctor.hls.domain.repository.help.HelpRepository;
import com.cnctor.hls.domain.service.aws.s3.S3Service;
import com.cnctor.hls.domain.service.aws.s3.S3Service.BucketName;

@Service
public class HelpServiceImpl implements HelpService {
  
  @Inject
  HelpRepository repository;
  
  @Inject
  S3Service s3Service;
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public Help uploadManual(String manualFileName, byte[] fileDatas) throws IOException {
    
    // exist --> update else insert
    Help existHelp = repository.fetchOne();
    
    String s3FileName = genereateS3FileName(manualFileName);
    
    // upload file to s3 bucket
    String url = s3Service.upload(BucketName.HLS_BUCKET, manualFileName, s3FileName, fileDatas);
    
    if(existHelp == null) {
      
      // insert help
      Help help = new Help();
      
      help.setManualName(manualFileName);
      help.setCreatedDate(new Date());
      help.setFileSize(fileDatas.length);
      help.setManualUrl(url);
      
      repository.insert(help);
      
      return help;
    
    } else {
      
      // delete old manual file in s3
      s3Service.deleteFile(BucketName.HLS_BUCKET, existHelp.getManualUrl());
      
      // update help
      existHelp.setManualName(manualFileName);
      existHelp.setCreatedDate(new Date());
      existHelp.setFileSize(fileDatas.length);
      existHelp.setManualUrl(url);
      
      repository.update(existHelp);
      
      return existHelp;
    }
  }
  
  private String genereateS3FileName(String attachFileName) {
    String fileExt = FilenameUtils.getExtension(attachFileName);
    if(fileExt == null)
      fileExt = StringUtils.EMPTY;
    
    String s3FileName = PREFIX + MINUS + new Date().getTime() + DOT + fileExt;
    return s3FileName;
  }
  
  private static final String PREFIX = "manuals/manualfile";
  private static final String DOT = "."; 
  private static final String MINUS = "-";

  @Override
  public Help fetchOne() {
    return repository.fetchOne();
  }
}
