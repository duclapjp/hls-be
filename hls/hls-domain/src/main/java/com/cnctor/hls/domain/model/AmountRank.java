package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class AmountRank implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private Long amountRankId;
  private Long storeId;
  private Integer amountRankNo;
  
  private String amountRankName;
  private boolean enable;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date createdDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date updatedTime;
  
  private Long accountId;
  private String displayName;
}
