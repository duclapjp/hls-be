package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Chain implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private long chainId;
  private String name;
  private String contractStatus;
  private String note;
  private Long directorId1;
  private Long directorId2;
  private Long directorId3;
  private String directorName1;
  private String directorName2;
  private String directorName3;
  private String managerMail;
  private String managerName;
  private String managerPhone;
  
  private List<Store> stores;
}
