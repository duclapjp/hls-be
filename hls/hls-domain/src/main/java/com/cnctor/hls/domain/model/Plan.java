package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"availableFor"})
public class Plan implements Serializable {

  private static final long serialVersionUID = 1L;
  
  private long planId;
  private String name;
  private Long cOrder;
  private String status;
  private Date createdDate;
  private Date updatedDate;
  
  private Long createdAccountId;
  private String createdAccount;
  
  private Long storeId;
  
  private boolean defaultPlan;
  private String availableFor;
  private boolean canSelectPlan;
  
  private List<PlanItem> items;
}
