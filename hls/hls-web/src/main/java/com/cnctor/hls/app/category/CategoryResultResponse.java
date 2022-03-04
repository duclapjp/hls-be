package com.cnctor.hls.app.category;

import java.util.List;
import com.cnctor.hls.domain.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryResultResponse {
  private List<Category> category;
  private long total;
}
