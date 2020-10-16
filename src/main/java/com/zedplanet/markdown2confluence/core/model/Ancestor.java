package com.zedplanet.markdown2confluence.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ancestor {
  private String id;
  private String status;
  private String title;
  private String type;
}
