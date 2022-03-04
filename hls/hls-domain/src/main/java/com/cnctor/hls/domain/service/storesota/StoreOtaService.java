package com.cnctor.hls.domain.service.storesota;

import java.util.List;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.StoreOta;

public interface StoreOtaService {
  StoreOta insert(StoreOta storeOta);
  StoreOta findOne(long storeId, long otaId);
  void upsert(StoreOta storeOta);
  List<OTA> getStoreOTA(long storeId);
}
