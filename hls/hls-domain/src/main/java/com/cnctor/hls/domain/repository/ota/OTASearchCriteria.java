package com.cnctor.hls.domain.repository.ota;

import java.io.Serializable;
import lombok.Data;

@Data
public class OTASearchCriteria implements Serializable {

  private static final long serialVersionUID = 1L;
  
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
  
  
  // extra fields
  private Long otaTypeId;
  private String name;
  private Long passwordUpdateDeadline;
  private String status;
}
