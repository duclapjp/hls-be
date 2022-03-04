package com.cnctor.hls.app.login;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoginForm {
  private String mail;
  private String password;
  
  // use for reset pass
  private String resetmail;
  private String newPassword;
  private String repeatNewPassword;
}
