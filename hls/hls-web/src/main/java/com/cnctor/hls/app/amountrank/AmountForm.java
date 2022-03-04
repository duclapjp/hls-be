package com.cnctor.hls.app.amountrank;

import java.io.Serializable;
import lombok.Data;

@Data
public class AmountForm implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private Long amountGroupId;
  private Long amountRankId;
  private String amountRankName;
  private long[] amounts;
 
}
