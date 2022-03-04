package com.cnctor.hls.app.passwordhistory;

import java.util.List;
import com.cnctor.hls.domain.model.PasswordHistory;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordHistoryResultResponse {
  private List<PasswordHistory> passwordHistories;
  private long total;
}
