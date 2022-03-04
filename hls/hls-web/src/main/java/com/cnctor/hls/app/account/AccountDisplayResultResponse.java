package com.cnctor.hls.app.account;

import java.util.List;
import com.cnctor.hls.domain.model.AccountDisplay;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDisplayResultResponse {
  private List<AccountDisplay> accounts;
  private long total;
}
