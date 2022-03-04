package com.cnctor.hls.app.store;

import java.util.List;
import com.cnctor.hls.domain.model.Director;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectorResultResponse {
  private List<Director> accounts;
  private long total;
}
