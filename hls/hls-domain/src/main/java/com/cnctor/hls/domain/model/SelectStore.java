package com.cnctor.hls.domain.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class SelectStore  implements Serializable{
  private static final long serialVersionUID = 1L;
  
  private long storeId;
  private String name;
  
  private long directorId;
  private String directorName;
  
  private long chainId;
  private String chainName;
  
}
