package com.cnctor.hls.app.sitecontroller;

import java.util.List;
import com.cnctor.hls.domain.model.SiteController;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SiteControllerResultResponse {
  private List<SiteController> siteControllers;
  private long total;
}
