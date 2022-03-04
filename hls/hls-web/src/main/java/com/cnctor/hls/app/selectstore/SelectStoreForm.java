package com.cnctor.hls.app.selectstore;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SelectStoreForm {
  
  private long directorId;
  private String searchKeyword;
  private List<Long> chainIds;
  
  private long page;
  private long size;
}
