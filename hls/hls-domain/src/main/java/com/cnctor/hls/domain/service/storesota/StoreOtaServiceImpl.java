package com.cnctor.hls.domain.service.storesota;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.StoreOta;
import com.cnctor.hls.domain.repository.ota.OTARepository;
import com.cnctor.hls.domain.repository.storeota.StoreOtaRepository;

@Service
public class StoreOtaServiceImpl implements StoreOtaService {

  @Inject
  StoreOtaRepository repository;
  
  @Inject
  OTARepository otaRepository;
  
  @Override
  public StoreOta insert(StoreOta storeOta) {
    repository.upsertProcedure(storeOta);
    return storeOta;
  }

  @Override
  public StoreOta findOne(long storeId, long otaId) {
    return repository.findOne(storeId, otaId);
  }

  @Override
  public void upsert(StoreOta storeOta) {
    repository.upsert(storeOta);
  }

  @Override
  public List<OTA> getStoreOTA(long storeId) {
    return otaRepository.getStoreOTA(storeId);
  }
}
