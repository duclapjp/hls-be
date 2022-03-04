package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import org.apache.commons.io.FilenameUtils;
import com.cnctor.hls.domain.common.utils.MimeTypeConstants;
import com.cnctor.hls.domain.common.utils.Timezone;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class Help implements Serializable {

private static final long serialVersionUID = 1L;

  private long helpId;
  private String manualUrl;
  private String manualName;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date createdDate;
  
  private long fileSize;
  private String contentType;
  
  public String getContentType() {
    String fileExt = FilenameUtils.getExtension(manualName);
    this.contentType = MimeTypeConstants.getMimeType(fileExt);
    return contentType;
  }
}
