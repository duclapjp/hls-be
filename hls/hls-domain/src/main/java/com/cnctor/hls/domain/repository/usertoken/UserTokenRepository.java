package com.cnctor.hls.domain.repository.usertoken;

import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.UserToken;

public interface UserTokenRepository {
  void insert(UserToken userToken);
  UserToken findByToken(@Param("token") String token);
  void updateInactive(@Param("token") String token);
}
