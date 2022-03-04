package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AmountGroup implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private Long amountGroupId;
  private Long storeId;
  private Integer amountGroupNo;
  private Integer totalPeople;
  
  private String amountGroupName;
  private boolean enable;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date updatedTime;
  
  private Long accountId;
  private String displayName;
  
  private List<AmountGroupRank> amountGroupRanks;
}
