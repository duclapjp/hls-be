package com.cnctor.hls.app.comment;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommentSearchForm {
  private String type;
  private String sortBy;
  private long sortByType;
  private long page;
  private long size;
}
