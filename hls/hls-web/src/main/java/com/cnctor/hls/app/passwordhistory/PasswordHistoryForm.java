package com.cnctor.hls.app.passwordhistory;

import java.util.Date;
import lombok.Data;

@Data
public class PasswordHistoryForm {
  private long otaPasswordHistoryId;
  private long otaId;
  private long accountId;
  private long storeId;
  private String password;
  private Date updatedTime;
  
  private int page;
  private int size;
}
