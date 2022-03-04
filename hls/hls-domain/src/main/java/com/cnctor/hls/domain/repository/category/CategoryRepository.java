package com.cnctor.hls.domain.repository.category;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Category;

public interface CategoryRepository {
  List<Category> getCategories(@Param("sortBy") String sortBy);
  Category findOne(long categoryId);
}
