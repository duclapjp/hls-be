package com.cnctor.hls.app.category;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Category;
import com.cnctor.hls.domain.service.category.CategoryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class CategoryRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  CategoryService categoryService;
  

  @GetMapping("/categories1")
  public @ResponseBody HlsResponse getCategories(HttpServletRequest request,
      @RequestParam(defaultValue = "name") String sortBy) {
    log.info("[DEBUG API GetListCategory] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)
        || request.isUserInRole(Constants.ROLE_USER)) {

      List<Category> categorires = categoryService.getCategories(sortBy);
      int total = categorires == null ? 0 : categorires.size();
      CategoryResultResponse response = new CategoryResultResponse(categorires, total);
      return HlsResponse.SUCCESS(response);

    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
