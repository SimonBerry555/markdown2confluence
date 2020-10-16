package com.zedplanet.markdown2confluence.core.model;

import lombok.Data;

import java.util.Collection;

@Data
public class ConfluencePage {
  private Long ancestorId;
  private String title;
  private Long id;
  private Integer version;
  private String content;
  private Collection<String> labels;
}
