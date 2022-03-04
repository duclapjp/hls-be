package com.cnctor.hls.domain.service.otatype;

import java.util.List;
import com.cnctor.hls.domain.model.OTAType;
import com.cnctor.hls.domain.repository.ota.OTASearchCriteria;

public interface OTATypeService {
  List<OTAType> filter(OTASearchCriteria criteria);
  long count();
}
