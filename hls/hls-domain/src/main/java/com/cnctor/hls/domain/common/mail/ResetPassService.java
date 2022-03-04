package com.cnctor.hls.domain.common.mail;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.common.utils.DateUtils;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.UserToken;
import com.cnctor.hls.domain.service.usertoken.UserTokenService;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResetPassService {

  @Value("${mail.template.resetpass.name}")
  private String resetPassMailTemplate;

  @Value("${mail.template.resetpass.subject}")
  private String resetPassMailSubject;

  @Value("${mail.template.resetpass.from}")
  private String from;
  
  @Value("${mail.template.resetpass.baseurl}")
  private String baseUrl;
  
  @Inject
  UserTokenService userTokenService;

  @Inject
  private MailService mailService;

  public void doSendMail(Account account)
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
      MessagingException, IOException, TemplateException {
    
    // send mail
    log.info("[DEBUG API resetPassword]  send mail ");


    Mail mail = new Mail();

    mail.setFrom(from);
    mail.setTo(account.getMail());

    mail.setSubject(resetPassMailSubject);
    mail.setTemplate(resetPassMailTemplate);

    Map<String, Object> m = new HashMap<>();
    m.put("resetPassUrl", generateResetUrl(account.getAccountId()));
    m.put("expiredTime", DateUtils.formatDate(getExpiredTime()));

    mail.setModel(m);

    mailService.sendMail(mail);
  }
  
  private String generateResetUrl(long accountId) {
    // create token
    String token = UUID.randomUUID().toString();

    // create reset link
    UserToken userToken = userTokenService.insert(accountId, token, UserToken.RESET_PASS, "");
    log.info("[USER TOKEN : {}", userToken);
    
    return baseUrl+token;
  }
  
  private Date getExpiredTime() {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.HOUR, 24);
    return c.getTime();
  }
}
