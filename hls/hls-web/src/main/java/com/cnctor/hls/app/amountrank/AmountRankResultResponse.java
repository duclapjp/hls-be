package com.cnctor.hls.app.amountrank;

import java.util.List;
import com.cnctor.hls.domain.model.AmountRank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AmountRankResultResponse {
  private List<AmountRank> amountRanks;
  private long total;
}
