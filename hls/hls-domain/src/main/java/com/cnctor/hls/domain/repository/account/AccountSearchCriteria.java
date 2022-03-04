package com.cnctor.hls.domain.repository.account;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class AccountSearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private List<String> params;
  private String role;
  private String mail;
  private String searchKeyword;
  private String userRole;
  
  private Long chainId;
  private Long storeId;
  
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
