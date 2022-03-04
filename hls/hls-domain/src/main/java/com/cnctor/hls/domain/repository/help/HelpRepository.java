package com.cnctor.hls.domain.repository.help;

import com.cnctor.hls.domain.model.Help;

public interface HelpRepository {
  void insert(Help help);
  void update(Help help);
  Help fetchOne();
}