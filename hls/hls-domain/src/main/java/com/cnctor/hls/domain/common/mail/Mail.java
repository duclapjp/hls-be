package com.cnctor.hls.domain.common.mail;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class Mail {

  private String from;
  
  private String to;
  
  private String subject;
  
  private String content;
  
  private String template;
  
  private Map<String, Object> model = new HashMap<>();
}
