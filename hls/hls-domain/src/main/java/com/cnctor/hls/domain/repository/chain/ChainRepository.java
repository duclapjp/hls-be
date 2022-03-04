package com.cnctor.hls.domain.repository.chain;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Chain;

public interface ChainRepository {
  long countById(@Param("chainId")Long chainId);
  List<Chain> getAllChain();
  void insert(Chain chain);
  Chain findOne(long chainId);
  void update(Chain chain);
  long countBySearchCriteria(@Param("criteria") ChainSearchCriteria searchCriteria);
  List<Chain> searchCriteria(@Param("criteria") ChainSearchCriteria searchCriteria);
}
