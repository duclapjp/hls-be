package com.cnctor.hls.domain.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class OTAPasswordHistoryAccount implements Serializable {
  private static final long serialVersionUID = 1L;
  private long accountId;
  private String displayName;
}
