package com.cnctor.hls.domain.service.help;

import java.io.IOException;
import com.cnctor.hls.domain.model.Help;

public interface HelpService {
  Help uploadManual(String manualFileName, byte[] fileDatas) throws IOException ;
  Help fetchOne();
}
