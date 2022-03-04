package com.cnctor.hls.domain.service.category;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.Category;
import com.cnctor.hls.domain.repository.category.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Inject
  CategoryRepository categoryRepository;

  @Override
  public List<Category> getCategories(String sortBy) {
    return categoryRepository.getCategories(sortBy);
  }

  @Override
  public Category findCategory(long categoryId) {
    return categoryRepository.findOne(categoryId);
  }

 
}
