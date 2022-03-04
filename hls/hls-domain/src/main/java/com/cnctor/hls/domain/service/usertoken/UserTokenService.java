package com.cnctor.hls.domain.service.usertoken;

import com.cnctor.hls.domain.model.UserToken;

public interface UserTokenService {
  UserToken insert(long accountId, String token, String action, String actionValue);
  boolean tokenIsValid(String token);
  UserToken findByToken(String token);
  void updateInactive(String token);
}
