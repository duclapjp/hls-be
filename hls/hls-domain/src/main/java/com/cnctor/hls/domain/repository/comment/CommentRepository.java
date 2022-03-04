package com.cnctor.hls.domain.repository.comment;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Comment;

public interface CommentRepository {
  void insert(Comment comment);
  long countComment(@Param("taskId") long taskId,
      @Param("criteria") CommentSearchCriteria searchCriteria);
  List<Comment> searchComment(@Param("taskId") long taskId,
      @Param("criteria") CommentSearchCriteria searchCriteria);
  
  void deleteByTaskId(long taskId);
}
