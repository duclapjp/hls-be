package com.cnctor.hls.domain.repository.item;

import java.io.Serializable;
import lombok.Data;

@Data
public class ItemSearchCriteria implements Serializable {

  private static final long serialVersionUID = 1L;

  private String sortBy;
  private long sortByType;

  private int size;
  private int page;

  // extra fields
  private String itemCode;
  private String name;
  private String type;

}
