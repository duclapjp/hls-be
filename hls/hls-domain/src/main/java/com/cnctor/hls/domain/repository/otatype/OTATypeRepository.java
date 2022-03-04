package com.cnctor.hls.domain.repository.otatype;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.OTAType;
import com.cnctor.hls.domain.repository.ota.OTASearchCriteria;

public interface OTATypeRepository {
  List<OTAType> filter(@Param("criteria") OTASearchCriteria criteria);
  long count();
}
