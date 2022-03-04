package com.cnctor.hls.domain.repository.notification;

import java.io.Serializable;
import lombok.Data;

@Data
public class NotificationSearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private long recipientId;
  private String sortBy;
  private long sortByType;
  
  private int size;
  private int page;

  public int getSize() {
    if (size < 0 || size >= 20)
      return 10;
    return size;
  }
  public int getPage() {
    if (page <= 0 )
      return 1;
    return page;
  }
  
}
