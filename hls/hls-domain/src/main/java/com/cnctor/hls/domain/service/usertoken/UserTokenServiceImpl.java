package com.cnctor.hls.domain.service.usertoken;

import java.util.Calendar;
import java.util.Date;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.UserToken;
import com.cnctor.hls.domain.repository.usertoken.UserTokenRepository;

@Service
public class UserTokenServiceImpl implements UserTokenService{
  
  @Inject
  UserTokenRepository userTokenRepository;
  
  @Override
  public UserToken insert(long accountId, String token, String action, String actionValue) {
    
    UserToken userToken = new UserToken();
    Calendar now = Calendar.getInstance();
    
    userToken.setToken(token);
    userToken.setUserId(accountId);
    userToken.setAction(action);
    userToken.setCreatedDate(now.getTime());
    userToken.setActionValue(actionValue);
    
    now.add(Calendar.HOUR, 24);
    
    userToken.setExpiredDate(now.getTime());
    
    userTokenRepository.insert(userToken);
    
    return userToken;
  }

  @Override
  public boolean tokenIsValid(String token) {
    UserToken userToken  = userTokenRepository.findByToken(token);
    
    // check accountId
    if(userToken == null || userToken.isActive() == false)
      return false;
    
    // check expired time
    Calendar now = Calendar.getInstance();
    Date expiredDate = userToken.getExpiredDate();
    
    if(now.getTime().after(expiredDate)) 
      return false;

    return true;
  }

  @Override
  public UserToken findByToken(String token) {
    return userTokenRepository.findByToken(token);
  }

  @Override
  public void updateInactive(String token) {
    userTokenRepository.updateInactive(token);
  }
}
