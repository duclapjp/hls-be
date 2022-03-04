package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;
import com.cnctor.hls.domain.common.utils.MimeTypeConstants;
import com.cnctor.hls.domain.common.utils.Timezone;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TaskAttach implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private long taskAttachId;
  private Long taskId;
  private String attachName;
  private String attachUrl;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date createdDate;
  
  private Long parentAttachId;
  private long size;
  private String contentType;
  
  public String getContentType() {
    String fileExt = FilenameUtils.getExtension(attachName);
    this.contentType = MimeTypeConstants.getMimeType(fileExt);
    return contentType;
  }
}
