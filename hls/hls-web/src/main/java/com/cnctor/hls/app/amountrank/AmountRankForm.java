package com.cnctor.hls.app.amountrank;

import com.cnctor.hls.domain.model.AmountRank;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AmountRankForm {
  private AmountRank[] amountRanks;
}
