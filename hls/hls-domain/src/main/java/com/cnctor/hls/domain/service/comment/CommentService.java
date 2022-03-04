package com.cnctor.hls.domain.service.comment;

import java.util.List;
import com.cnctor.hls.domain.model.Comment;
import com.cnctor.hls.domain.repository.comment.CommentSearchCriteria;

public interface CommentService {
  Comment createComment(Comment comment);
  long countComment(long taskId, CommentSearchCriteria searchCriteria);
  List<Comment> searchComment(long taskId, CommentSearchCriteria searchCriteria);
}
