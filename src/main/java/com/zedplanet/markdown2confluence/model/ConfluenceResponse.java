package com.zedplanet.markdown2confluence.model;

import lombok.Data;

import java.util.List;

@Data
public class ConfluenceResponse {
  private List<Result> results;
}
