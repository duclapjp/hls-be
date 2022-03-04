package com.cnctor.hls.app.login;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetailsService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private AccountUserDetailsService accountUserDetailService;
  
  @Autowired
  private StoreService storeService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    try {
      String jwt = getJwtFromRequest(request);
      JwtTokenProvider tokenProvider = new JwtTokenProvider();

      if (tokenProvider.validateToken(jwt)) {
        String userId = tokenProvider.getUserIdFromJWT(jwt);

        log.info("[Debug Login] userId : {}", userId);
        UserDetails userDetails = accountUserDetailService.loadUserByUsername(userId);
        
        if(userDetails == null)
          returnJson(response, HttpStatus.OK, new HlsResponse(HlsResponse.AUTHEN_FAILED, "Token user is not exist"));
        
        if(!userDetails.isAccountNonLocked()) {
          throw new LockedException("user is locked");
        }
        
        // user authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        
        Long storeId = fetchStoreIdFromRequest(request);
        log.info("[Debug Login] storeId : {}", storeId);
        
        // chain login to store --> set STORE_ROLE, set storeId
        // check this token is chain and has role with this store 
        
        if(storeId != null) {
          
          AccountUserDetails accountUserDetail = (AccountUserDetails) userDetails;
          Account userAccount = accountUserDetail.getAccount();
          
          if (userAccount.isChain() && userAccount.getChainId() != null) {
            List<Store> stores = storeService.findByChainId(userAccount.getChainId());
            for (Store store : stores) {
              if (store.getStoreId() == storeId) {
                authorities = AuthorityUtils.createAuthorityList("ROLE_STORE");
                userAccount.setStoreId(storeId);
                break;
              }
            }
          }
        }
        
        log.info("[Debug Login] userDetails : {} - {}", userDetails.getUsername(), userDetails.getPassword());
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);

      } else {
        returnJson(response, HttpStatus.OK, new HlsResponse(HlsResponse.AUTHEN_FAILED, "ERROR-JWT-TOKENINVALID"));
      }
    } catch (JwtTokenException jtx) {
      log.error("JwtTokenException Url : ", request.getRequestURL().toString());
      log.error("JwtTokenException : ", jtx);
      returnJson(response, HttpStatus.OK, new HlsResponse(HlsResponse.AUTHEN_FAILED, jtx.getMessage()));
    } catch (UsernameNotFoundException une) {
      log.info("User not found  :", une.getMessage());
      returnJson(response, HttpStatus.OK, new HlsResponse(HlsResponse.AUTHEN_FAILED, "ERROR-LOGIN-POST-USERNOTFOUND"));
    } catch (LockedException le) {
      log.info("User is inactive  :", le.getMessage());
      returnJson(response, HttpStatus.OK, new HlsResponse(HlsResponse.AUTHEN_FAILED, "ERROR-LOGIN-POST-USERINACTIVE"));
    }  catch (Exception ex) {
      
      log.error("failed on set user authentication", ex);
      returnJson(response, HttpStatus.OK, new HlsResponse(HlsResponse.AUTHEN_FAILED, "ERROR-JWT-TOKENINVALID"));
    }

  }

  private String getJwtFromRequest(HttpServletRequest request) {
    
    // get token from url
    String urlToken = request.getParameter("jwt");
    log.info("urlToken : {}", urlToken);
    
    if(StringUtils.isNotBlank(urlToken)) {
      return urlToken;
    }
    
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private void returnJson(HttpServletResponse response, HttpStatus status, Object data) {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    try {
      new ObjectMapper().writeValue(response.getWriter(), data);
    } catch (JsonGenerationException e) {
      e.printStackTrace();
    } catch (JsonMappingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Long fetchStoreIdFromRequest(HttpServletRequest request) {
    
    String xStoreId = request.getHeader(X_REQUESTED_STORE_ID);
    if (StringUtils.isNotBlank(xStoreId) && NumberUtils.isCreatable(xStoreId)){
      return NumberUtils.createLong(xStoreId);
    }
    return null;
  }
  
  private static final String X_REQUESTED_STORE_ID = "X-Requested-StoreId";
}
