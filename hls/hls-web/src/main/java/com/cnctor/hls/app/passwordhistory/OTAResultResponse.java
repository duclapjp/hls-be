package com.cnctor.hls.app.passwordhistory;

import java.util.List;
import com.cnctor.hls.domain.model.OTA;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OTAResultResponse {
  private List<OTA> otas;
  private long total;
}
