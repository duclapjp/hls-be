package com.cnctor.hls.app.store;

import java.util.List;
import com.cnctor.hls.domain.model.Store;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreResultResponse {
  private List<Store> stores;
  private long total;
}
