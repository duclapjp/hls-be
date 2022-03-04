package com.cnctor.hls.domain.model;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AmountGroupRank implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private Long amountGroupId;
  private Long amountRankId;
  private String amountRankName;
  private String amounts;
  
}
