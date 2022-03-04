package com.cnctor.hls.domain.common.mail;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Service
@EnableRetry
public class MailService {
  
  @Inject
  JavaMailSender mailSender;
  
  @Inject
  Configuration freemarkerConfiguration;
  
  @Async
  @Retryable(value = {Exception.class}, maxAttemptsExpression = "#{${retry.maxAttempts}}",
      backoff = @Backoff(delayExpression = "#{${retry.backoff.delay}}"))
  public void sendMail(Mail mail) throws MessagingException, TemplateNotFoundException,
      MalformedTemplateNameException, ParseException, IOException, TemplateException {
    MimeMessage message = mailSender.createMimeMessage();

    mail.setContent(parse(mail.getTemplate(), mail.getModel()));
    buildMimeMessageHelper(message, mail);

    mailSender.send(message);
  }

  private String parse(String template, Map<String, Object> model) throws TemplateNotFoundException,
      MalformedTemplateNameException, ParseException, IOException, TemplateException {
    Template t = freemarkerConfiguration.getTemplate(template);

    return FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
  }

  private void buildMimeMessageHelper(MimeMessage mimeMessage, Mail mail)
      throws MessagingException {
    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

    helper.setTo(mail.getTo());
    helper.setText(mail.getContent(), true);
    helper.setSubject(mail.getSubject());
    helper.setFrom(mail.getFrom());
  }
}
