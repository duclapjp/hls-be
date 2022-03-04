package com.cnctor.hls.domain.repository.storesitecontroller;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.SiteController;
import com.cnctor.hls.domain.model.StoreSiteController;

public interface StoreSiteControllerRepository {
  void upsert(StoreSiteController storeSiteController);
  void delete(@Param("controllers") List<StoreSiteController> controllers, @Param("storeId") long storeId);
  List<SiteController> getAllSiteController();
}
