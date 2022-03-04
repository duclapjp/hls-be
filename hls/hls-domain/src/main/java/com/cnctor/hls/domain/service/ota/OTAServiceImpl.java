package com.cnctor.hls.domain.service.ota;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.repository.ota.OTARepository;
import com.cnctor.hls.domain.repository.ota.OTASearchCriteria;

@Service
public class OTAServiceImpl implements OTAService {
  
  @Inject
  OTARepository repository;
  
  @Override
  public OTA createOTA(OTA ota) {
    repository.insert(ota);
    return ota;
  }

  @Override
  public OTA findOTA(long otaId) {
    return repository.findOne(otaId);
  }

  @Override
  public OTA updateOTA(OTA ota) {
    repository.update(ota);
    return ota;
  }

  @Override
  public long countBySearchCriteria(OTASearchCriteria searchCriteria) {
    return repository.countBySearchCriteria(searchCriteria);
  }

  @Override
  public List<OTA> searchCriteria(OTASearchCriteria searchCriteria) {
    return repository.filter(searchCriteria);
  }
}
