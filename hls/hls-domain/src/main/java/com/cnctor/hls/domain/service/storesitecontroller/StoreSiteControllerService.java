package com.cnctor.hls.domain.service.storesitecontroller;

import java.util.List;
import com.cnctor.hls.domain.model.SiteController;
import com.cnctor.hls.domain.model.StoreSiteController;

public interface StoreSiteControllerService {
  StoreSiteController insert(StoreSiteController storeSiteController);
  List<SiteController> getAllSiteController();
}
