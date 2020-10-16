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
public class Page {
  private String id;
  private String type;
  private String title;
  private Space space;
  private Body body;
  private List<Ancestor> ancestors;
  private Version version;
}
