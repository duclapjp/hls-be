package com.cnctor.hls.domain.service.comment;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.Comment;
import com.cnctor.hls.domain.repository.comment.CommentRepository;
import com.cnctor.hls.domain.repository.comment.CommentSearchCriteria;

@Service
public class CommentServiceImpl implements CommentService{

  @Inject
  CommentRepository repository;
  
  @Override
  public Comment createComment(Comment comment) {
    repository.insert(comment);
    return comment;
  }

  @Override
  public long countComment(long taskId, CommentSearchCriteria searchCriteria) {
    return repository.countComment(taskId, searchCriteria);
  }

  @Override
  public List<Comment> searchComment(long taskId, CommentSearchCriteria searchCriteria) {
    return repository.searchComment(taskId, searchCriteria);
  }

}
