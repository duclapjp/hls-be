package com.cnctor.hls.domain.model;

import java.util.Date;
import lombok.Data;

@Data
public class PasswordHistory {
  private long otaPasswordHistoryId;
  private long otaId;
  private long accountId;
  private long storeId;
  private String password;
  private Date updatedTime;
  private String otaName;
  private String accountName;
}
