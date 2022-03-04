package com.cnctor.hls.app.account;

import java.util.List;

import com.cnctor.hls.domain.model.Account;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountResultResponse {
  private List<Account> accounts;
  private long total;
}
