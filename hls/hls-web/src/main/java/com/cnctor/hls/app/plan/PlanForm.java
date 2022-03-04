package com.cnctor.hls.app.plan;

import java.util.List;
import lombok.Data;

@Data
public class PlanForm {
  private String name;
  private String status;
  private List<ItemForm> items;
}
