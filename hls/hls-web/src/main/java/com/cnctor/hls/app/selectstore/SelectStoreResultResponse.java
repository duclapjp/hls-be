package com.cnctor.hls.app.selectstore;

import java.util.List;
import com.cnctor.hls.domain.model.SelectStore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SelectStoreResultResponse {
  private List<SelectStore> stores;
  private long total;
}
