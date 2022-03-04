package com.cnctor.hls.domain.repository.amountrank;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.AmountRank;

public interface AmountRankRepository {

  List<AmountRank> findAmountRanks(@Param("storeId") Long storeId,@Param("enable") boolean enable);

  void insert(AmountRank amountRank);

  void update(AmountRank amountRank);

  void delete(@Param("storeId") Long storeId, @Param("amountRankIds") List<Long> amountRankIds);
  
}
