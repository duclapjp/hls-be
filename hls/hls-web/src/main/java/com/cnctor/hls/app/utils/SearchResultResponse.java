package com.cnctor.hls.app.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResultResponse {
  private Object rows;
  private long total;
}
