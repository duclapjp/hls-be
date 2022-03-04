package com.cnctor.hls.domain.repository.selectstore;

import java.io.Serializable;
import lombok.Data;

@Data
public class SelectStoreSearchCriteria implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long chainId;
  private boolean isChainRole;
  private String sortBy;
  private long sortByType;
}
