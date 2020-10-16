package com.zedplanet.markdown2confluence.core.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result {
  private List<Ancestor> ancestors;
  private Body body;
  private String id;
  private String status;
  private String title;
  private String type;
  private Version version;
}
