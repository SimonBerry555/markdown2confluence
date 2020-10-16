package com.zedplanet.markdown2confluence.core.service;

import com.zedplanet.markdown2confluence.core.ConfluenceConfig;
import com.zedplanet.markdown2confluence.core.ConfluenceException;
import com.zedplanet.markdown2confluence.core.model.Ancestor;
import com.zedplanet.markdown2confluence.core.model.Body;
import com.zedplanet.markdown2confluence.core.model.Result;
import com.zedplanet.markdown2confluence.core.model.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Optional;

@Service
public class PageService {

  private static final Logger LOG = LoggerFactory.getLogger(PageService.class);

  private final ConfluenceService confluenceService;

  public PageService(final ConfluenceService confluenceService) {
    this.confluenceService = confluenceService;
  }

  public String postWikiPageToConfluence(final ConfluenceConfig.Page page, final String wiki) {
    LOG.info("Posting page {} to Confluence...", page.getTitle());

    try {
      Optional<Result> oldPage = confluenceService.findPageByTitle(page.getTitle());

      if (oldPage.isPresent()) { // page exists
        LOG.info("Update existing page");
        oldPage.get().getBody().getStorage().setValue(wiki);
        confluenceService.updatePage(oldPage.get());

        return oldPage.get().getId();

      } else {
        LOG.info("Create new page");
        String ancestorId = confluenceService.findAncestorId(page.getParentTitle());

        Result newPage =
            Result.builder()
                .title(page.getTitle())
                .ancestors(List.of(Ancestor.builder().id(ancestorId).build()))
                .body(
                    Body.builder()
                        .storage(Storage.builder().value(wiki).representation("storage").build())
                        .build())
                .build();

        String pageId = confluenceService.createPage(newPage);

        return pageId;
      }
    } catch (HttpStatusCodeException e) {
      throw new ConfluenceException(e.getResponseBodyAsString(), e);
    }
  }
}
