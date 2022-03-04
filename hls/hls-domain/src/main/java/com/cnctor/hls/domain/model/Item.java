package com.cnctor.hls.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(value = {"showInDefaultPlan"})
public class Item {
  private long itemId;
  private String itemCode;
  private String name;
  private String type;
  private boolean showInDefaultPlan;
}
