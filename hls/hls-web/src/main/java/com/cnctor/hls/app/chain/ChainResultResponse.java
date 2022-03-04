package com.cnctor.hls.app.chain;

import java.util.List;
import com.cnctor.hls.domain.model.Chain;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChainResultResponse {
  private List<Chain> chains;
  private long total;
}
