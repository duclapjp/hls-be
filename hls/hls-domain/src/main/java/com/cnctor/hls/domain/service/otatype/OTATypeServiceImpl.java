package com.cnctor.hls.domain.service.otatype;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.OTAType;
import com.cnctor.hls.domain.repository.ota.OTASearchCriteria;
import com.cnctor.hls.domain.repository.otatype.OTATypeRepository;

@Service
public class OTATypeServiceImpl implements OTATypeService{
  
  @Inject
  OTATypeRepository repository;
  
  @Override
  public List<OTAType> filter(OTASearchCriteria criteria) {
    return repository.filter(criteria);
  }

  @Override
  public long count() {
    return repository.count();
  }
}
