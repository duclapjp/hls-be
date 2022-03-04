package com.cnctor.hls.domain.service.category;

import java.util.List;
import com.cnctor.hls.domain.model.Category;

public interface CategoryService {
  List<Category> getCategories(String sortBy);
  Category findCategory(long categoryId);
}
