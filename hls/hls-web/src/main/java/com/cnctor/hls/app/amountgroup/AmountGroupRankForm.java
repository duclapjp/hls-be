package com.cnctor.hls.app.amountgroup;

import java.util.List;
import com.cnctor.hls.app.amountrank.AmountForm;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AmountGroupRankForm {
  private Long amountGroupId;
  private Long storeId;
  private Integer amountGroupNo;
  private Integer totalPeople;
  
  private String amountGroupName;
  private boolean enable;
  
  private Long accountId;
  private String displayName;
  
  private List<AmountForm> amountGroupRanks;
}
