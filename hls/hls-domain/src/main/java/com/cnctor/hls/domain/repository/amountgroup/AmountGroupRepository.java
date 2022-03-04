package com.cnctor.hls.domain.repository.amountgroup;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.AmountGroup;
import com.cnctor.hls.domain.model.AmountGroupRank;

public interface AmountGroupRepository {

  List<AmountGroup> findAmountGroups(@Param("storeId") Long storeId);

  void insert(AmountGroup amountGroup);

  void update(AmountGroup amountGroup);

  void delete(@Param("storeId") Long storeId, @Param("amountGroupIds") List<Long> amountGroupIds);

  AmountGroup getById(@Param("amountGroupId") Long amountGroupId);

  void upsertAmountGroupRank(@Param("amountGroupRank") AmountGroupRank agr);
  
}
