package com.cnctor.hls.domain.repository.ota;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.OTA;

public interface OTARepository {
  
  void insert(OTA ota);
  OTA findOne(long otaId);
  void update(OTA ota);
  long countBySearchCriteria(@Param("criteria") OTASearchCriteria searchCriteria);
  List<OTA> filter(@Param("criteria") OTASearchCriteria criteria);
  List<OTA> getStoreOTA(long storeId);
}
