package com.cnctor.hls.app.amountgroup;

import com.cnctor.hls.domain.model.AmountGroup;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AmountGroupForm {
  private AmountGroup[] amountGroups;
}
