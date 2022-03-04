package com.cnctor.hls.domain.repository.plan;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class PlanSearchCriteria implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private String sortBy;
  private long sortByType;

  private int size;
  private int page;

  // extra fields
  private List<Long> storeIds;
  private String name;
  private String status;
  private String availableFor;
  private boolean defaultPlan;
}
