package com.cnctor.hls.app.store;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreForm {
  
  private String name;
  private Long directorId;
  private Long chainId;
  private String managerMail;
  private String managerName;
  private String managerPhone;
  private String note;
  private String contractStatus;
  
  private List<SiteControllerForm> siteControllers;
  private List<OtaForm> otas;
}
