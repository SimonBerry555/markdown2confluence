package com.zedplanet.markdown2confluence.core.service;

import com.zedplanet.markdown2confluence.core.ConfluenceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.file.Path;

@Service
public class AttachmentService {

  private static final Logger LOG = LoggerFactory.getLogger(AttachmentService.class);

  private final ConfluenceService confluenceService;

  public AttachmentService(final ConfluenceService confluenceService) {
    this.confluenceService = confluenceService;
  }

  public void postAttachmentToPage(String pageId, Path filePath) {

    LOG.info("Posting attachment {} to page {} in Confluence...", filePath.toString(), pageId);

    try {
      String attachmentId =
          confluenceService.getAttachmentId(pageId, filePath.getFileName().toString());
      if (attachmentId == null) {
        LOG.info("Create new attachment");
        confluenceService.createAttachment(pageId, filePath.toString());
      } else {
        LOG.info("Update existing attachment");
        confluenceService.updateAttachment(pageId, attachmentId, filePath.toString());
      }
    } catch (HttpStatusCodeException e) {
      LOG.error("Error creating/updating attachment.", e);
      throw new ConfluenceException(e.getResponseBodyAsString(), e);
    }
  }
}
