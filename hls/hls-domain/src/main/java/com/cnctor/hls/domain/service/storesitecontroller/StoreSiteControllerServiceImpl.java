package com.cnctor.hls.domain.service.storesitecontroller;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.SiteController;
import com.cnctor.hls.domain.model.StoreSiteController;
import com.cnctor.hls.domain.repository.storesitecontroller.StoreSiteControllerRepository;

@Service
public class StoreSiteControllerServiceImpl implements StoreSiteControllerService{
  
  @Inject
  StoreSiteControllerRepository repository;
  
  @Override
  public StoreSiteController insert(StoreSiteController storeSiteController) {
    repository.upsert(storeSiteController);
    return storeSiteController;
  }

  @Override
  public List<SiteController> getAllSiteController() {
    return repository.getAllSiteController();
  }

}
