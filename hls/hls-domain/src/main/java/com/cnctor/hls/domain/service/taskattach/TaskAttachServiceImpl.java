package com.cnctor.hls.domain.service.taskattach;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.TaskAttach;
import com.cnctor.hls.domain.repository.taskattach.TaskAttachRepository;
import com.cnctor.hls.domain.service.aws.s3.S3Service;
import com.cnctor.hls.domain.service.aws.s3.S3Service.BucketName;

@Service
public class TaskAttachServiceImpl implements TaskAttachService {

  @Inject
  TaskAttachRepository repository;
  
  @Inject
  S3Service s3Service;
  
  @Override
  @Transactional(rollbackFor = Exception.class)
  public TaskAttach attachFile(Long taskId, String attachFileName, byte[] fileDatas) throws IOException {
    
    // insert attach
    TaskAttach taskAttach = new TaskAttach();
    
    taskAttach.setTaskId(taskId);
    taskAttach.setCreatedDate(new Date());
    taskAttach.setAttachName(attachFileName);
    taskAttach.setSize(fileDatas.length);
    
    repository.insert(taskAttach);
    
    String s3FileName = genereateS3FileName(attachFileName, taskAttach.getTaskAttachId());
    
    // upload file to s3 bucket
    String url = s3Service.upload(BucketName.HLS_BUCKET, attachFileName, s3FileName, fileDatas);
    
    // update attach url
    taskAttach.setAttachUrl(url);
    repository.update(taskAttach);
    
    return taskAttach;
  }

  private static final String PREFIX = "attached-";
  private static final String DOT = "."; 
  private static final String MINUS = "-";
  
  @Override
  public TaskAttach findTaskAttach(long taskAttachId) {
    return repository.findOne(taskAttachId);
  }

  @Override
  public void updateTaskId(TaskAttach ta) {
    repository.updateTaskId(ta);
  }
  /**
   * Duplicate srcAttach to a child task
   * 
   */
  @Override
  public TaskAttach duplicateFile(TaskAttach srcAttach) throws Exception {
    
    TaskAttach dupAttach = new TaskAttach();
    dupAttach.setAttachName(srcAttach.getAttachName());
    dupAttach.setCreatedDate(srcAttach.getCreatedDate());
    dupAttach.setParentAttachId(srcAttach.getTaskAttachId());
    dupAttach.setSize(srcAttach.getSize());
    
    repository.insert(dupAttach);
    
    // duplicate file in s3 bucket
    String s3FileName = genereateS3FileName(srcAttach.getAttachName(), dupAttach.getTaskAttachId());
    String url = s3Service.duplicate(BucketName.HLS_BUCKET, srcAttach.getAttachUrl(), BucketName.HLS_BUCKET, s3FileName);
    
    // update attach url
    dupAttach.setAttachUrl(url);
    repository.update(dupAttach);
    
    return dupAttach;
  }
  
  /**
   * delete attach in db and s3 storage
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public boolean delete(TaskAttach taskAttach) {
    
    // delete file in s3
    s3Service.deleteFile(BucketName.HLS_BUCKET, taskAttach.getAttachUrl());
    
    // delete file in db
    repository.delete(taskAttach);
    return true;
  }
  
  private String genereateS3FileName(String attachFileName, long attachId) {
    String fileExt = FilenameUtils.getExtension(attachFileName);
    if(fileExt == null)
      fileExt = StringUtils.EMPTY;
    
    String s3FileName = PREFIX + attachId + MINUS + new Date().getTime() + DOT + fileExt;
    return s3FileName;
  }

  @Override
  public List<TaskAttach> getByParentId(long parentId) {
    return repository.getByParentId(parentId);
  }
  
  @Override
  public void createAttachFile(Long taskId, Long[] taskAttachIds) throws Exception {
    for (Long taskAttachId : taskAttachIds) {
      
      TaskAttach attach = repository.findOne(taskAttachId);

      // if attachment : taskId is null --> this is parent attach --> update taskId 
      if(attach.getTaskId() == null) {
        attach.setTaskId(taskId);
        repository.updateTaskId(attach);
      } else {               // else duplicate attachment to child attach
        TaskAttach dupAttach = duplicateFile(attach);
        dupAttach.setTaskId(taskId);
        repository.updateTaskId(dupAttach);
      }
    }
  }

  @Override
  public List<TaskAttach> createParentAttachFile(Long taskId, Long[] taskAttachIds) throws Exception {
    List<TaskAttach> attachs = new ArrayList<TaskAttach>();
    for (Long taskAttachId : taskAttachIds) {
      TaskAttach attach = repository.findOne(taskAttachId);

      
      TaskAttach dupAttach = new TaskAttach();
      dupAttach.setAttachName(attach.getAttachName());
      dupAttach.setCreatedDate(attach.getCreatedDate());
      dupAttach.setParentAttachId(null);
      dupAttach.setSize(attach.getSize());
      dupAttach.setTaskId(taskId);
      
      repository.insert(dupAttach);
      
      // duplicate file in s3 bucket
      String s3FileName = genereateS3FileName(dupAttach.getAttachName(), dupAttach.getTaskAttachId());
      String url = s3Service.duplicate(BucketName.HLS_BUCKET, attach.getAttachUrl(), BucketName.HLS_BUCKET, s3FileName);
      
      // update attach url
      dupAttach.setAttachUrl(url);
      repository.update(dupAttach);
      
      attachs.add(dupAttach);
      
      // update child 
      attach.setParentAttachId(dupAttach.getTaskAttachId());
      repository.update(attach);
      
      
    }
    return attachs;
  }
}