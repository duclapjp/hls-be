package com.cnctor.hls.app.comment;

import java.util.List;
import com.cnctor.hls.domain.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResultResponse {
  private List<Comment> comments;
  private long total;
}
