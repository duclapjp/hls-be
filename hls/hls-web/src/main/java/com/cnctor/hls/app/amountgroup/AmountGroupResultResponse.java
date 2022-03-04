package com.cnctor.hls.app.amountgroup;

import java.util.List;
import com.cnctor.hls.domain.model.AmountGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AmountGroupResultResponse {
  private List<AmountGroup> AmountGroups;
  private long total;
}
