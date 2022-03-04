package com.cnctor.hls.domain.repository.storeota;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.StoreOta;

public interface StoreOtaRepository {
  void upsert(StoreOta storeOta);
  void delete(@Param("otas") List<StoreOta> otas, @Param("storeId") long storeId);
  void upsertProcedure(StoreOta storeOta);
  StoreOta findOne(@Param("storeId") long storeId, @Param("otaId") long otaId);
}
