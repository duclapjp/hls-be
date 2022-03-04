package com.cnctor.hls.app.account;

import java.util.List;
import lombok.Data;

@Data
public class AccountForm {
  private Long accountId;
  private List<String> params;
  private String displayName;
  private String password;
  private String role;
  private String mail;
  private String searchKeyword;
  private int page;
  private int size;
  private String status;
  private String phone;
  private String note;
  private Long chainId;
  private Long storeId;

}
