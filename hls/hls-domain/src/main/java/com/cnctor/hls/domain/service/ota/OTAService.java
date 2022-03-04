package com.cnctor.hls.domain.service.ota;

import java.util.List;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.repository.ota.OTASearchCriteria;

public interface OTAService {
  OTA createOTA(OTA ota);
  OTA findOTA(long otaId);
  OTA updateOTA(OTA ota);
  long countBySearchCriteria(OTASearchCriteria searchCriteria);
  List<OTA> searchCriteria(OTASearchCriteria searchCriteria);
}
