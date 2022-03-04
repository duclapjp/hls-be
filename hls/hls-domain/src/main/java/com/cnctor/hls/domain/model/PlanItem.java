package com.cnctor.hls.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"itemOrder"})
public class PlanItem {
  private long planId;
  private long itemId;
  private String itemJsonValue;
  private String tab;
  
  // extra fields
  private String itemCode;
  private Integer itemOrder;
}
