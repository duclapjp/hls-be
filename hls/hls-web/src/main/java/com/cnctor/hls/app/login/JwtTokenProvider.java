package com.cnctor.hls.app.login;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtTokenProvider {
  private final String JWT_SECRET = "lodaaaaaa";

  private final long JWT_EXPIRATION = 604800000L;

  public String generateToken(AccountUserDetails userDetails) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

    System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(expiryDate));

    return Jwts.builder().setSubject(userDetails.getAccount().getMail()).setIssuedAt(now)
        .setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, JWT_SECRET).compact();
  }
  
  public String generateToken(String userEmail) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

    System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(expiryDate));

    return Jwts.builder().setSubject(userEmail).setIssuedAt(now)
        .setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, JWT_SECRET).compact();
  }

  public String getUserIdFromJWT(String token) {
    Claims claims = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();

    return claims.getSubject();
  }

  public boolean validateToken(String authToken) throws JwtTokenException {
    if (StringUtils.isBlank(authToken)) {
      throw new JwtTokenException("Not has token");
    }
    try {
      Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
      return true;
    } catch (MalformedJwtException ex) {
      throw new JwtTokenException("ERROR-JWT-TOKENINVALID");
    } catch (ExpiredJwtException ex) {
      throw new JwtTokenException("ERROR-JWT-TOKENEXPIRED");
    } catch (UnsupportedJwtException ex) {
      throw new JwtTokenException("ERROR-JWT-TOKENUNSUPPORTED");
    } catch (IllegalArgumentException ex) {
      throw new JwtTokenException("ERROR-JWT-TOKENERROR");
    }
  }
}
