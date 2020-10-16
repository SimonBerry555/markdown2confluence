package com.zedplanet.markdown2confluence.service;

import com.zedplanet.markdown2confluence.ConfluenceConfig;
import com.zedplanet.markdown2confluence.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class ConfluenceService {

  private static final String EXPAND = "expand";
  private static final String SPACE_KEY = "spaceKey";
  private static final String TITLE = "title";

  private final RestTemplate restTemplate;

  private ConfluenceConfig confluenceConfig;
  private HttpHeaders httpHeaders;
  private HttpHeaders httpHeadersForAttachment;

  @Autowired
  public ConfluenceService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private static HttpHeaders buildHttpHeaders(final String confluenceAuthentication) {
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("Basic %s", confluenceAuthentication));
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);

    return headers;
  }

  private static HttpHeaders buildHttpHeadersForAttachment(final String confluenceAuthentication) {
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", String.format("Basic %s", confluenceAuthentication));
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    headers.set("X-Atlassian-Token", "no-check");

    return headers;
  }

  public void setConfluenceConfig(ConfluenceConfig confluenceConfig) {
    this.confluenceConfig = confluenceConfig;
    httpHeaders = buildHttpHeaders(confluenceConfig.getAuthentication());
    httpHeadersForAttachment = buildHttpHeadersForAttachment(confluenceConfig.getAuthentication());
  }

  public Optional<Result> findPageByTitle(final String title) {

    final HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

    final URI targetUrl =
        UriComponentsBuilder.fromUriString(confluenceConfig.getRestApiUrl())
            .path("/content")
            .queryParam(SPACE_KEY, confluenceConfig.getSpaceKey())
            .queryParam(TITLE, title)
            .queryParam(EXPAND, "body.storage,version,ancestors")
            .build(false)
            .encode()
            .toUri();

    final ResponseEntity<ConfluenceResponse> responseEntity =
        restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, ConfluenceResponse.class);

    return responseEntity.getBody().getResults().stream()
        .filter(result -> result.getTitle().equals(title))
        .findAny();
  }

  public String findSpaceHomePageId() {

    final HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

    final URI targetUrl =
        UriComponentsBuilder.fromUriString(confluenceConfig.getRestApiUrl())
            .path("/content")
            .queryParam(SPACE_KEY, confluenceConfig.getSpaceKey())
            .queryParam(EXPAND, "body.storage,version,ancestors")
            .build(false)
            .encode()
            .toUri();

    final ResponseEntity<ConfluenceResponse> responseEntity =
        restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, ConfluenceResponse.class);

    return responseEntity.getBody().getResults().stream().filter(result -> result.getAncestors().isEmpty()).map(Result::getId).findFirst().orElse(null);
  }

  public void updatePage(final Result page) {

    final URI targetUrl =
        UriComponentsBuilder.fromUriString(confluenceConfig.getRestApiUrl())
            .path(String.format("/content/%s", page.getId()))
            .build()
            .toUri();

    Page page1 =
        Page.builder()
            .type("page")
            .id(page.getId())
            .title(page.getTitle())
            .space(Space.builder().key(confluenceConfig.getSpaceKey()).build())
            .body(page.getBody())
            .ancestors(page.getAncestors())
            .version(Version.builder().number(page.getVersion().getNumber() + 1).build())
            .build();

    final HttpEntity<Page> requestEntity = new HttpEntity<Page>(page1, httpHeaders);

    final HttpEntity<Page> responseEntity =
        restTemplate.exchange(targetUrl, HttpMethod.PUT, requestEntity, Page.class);

    log.debug("Response of updating page: {}", responseEntity.getBody());
  }

  public String createPage(final Result page) {
    final URI targetUrl =
        UriComponentsBuilder.fromUriString(confluenceConfig.getRestApiUrl())
            .path("/content")
            .build()
            .toUri();

    Page page1 =
        Page.builder()
            .type("page")
            .title(page.getTitle())
            .space(Space.builder().key(confluenceConfig.getSpaceKey()).build())
            .body(page.getBody())
            .ancestors(page.getAncestors())
            .build();

    final HttpEntity<Page> requestEntity = new HttpEntity<>(page1, httpHeaders);

    final HttpEntity<Page> responseEntity =
        restTemplate.exchange(targetUrl, HttpMethod.POST, requestEntity, Page.class);

    log.debug("Response of creating page: {}", responseEntity.getBody());

    return responseEntity.getBody().getId();
  }

  public String findAncestorId(String title) {
    log.info("Try to find ancestorId by title {}", title);

    Optional<Result> page = findPageByTitle(title);

    if (page.isPresent()) {
      return page.get().getId();
    } else {
      log.info("Try to use page home id as ancestorId {}", confluenceConfig.getSpaceKey());
      return findSpaceHomePageId();
    }
  }

  public Optional<String> getAttachmentId(String pageId, String attachmentFilename) {

    final HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

    URI targetUrl =
        UriComponentsBuilder.fromUriString(confluenceConfig.getRestApiUrl())
            .path("/content/{id}/child/attachment")
            .queryParam("filename", attachmentFilename)
            .buildAndExpand(pageId)
            .toUri();

    final HttpEntity<ConfluenceResponse> responseEntity =
        restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, ConfluenceResponse.class);

    log.debug("Response of creating attachment: {}", responseEntity.getBody());

    return responseEntity.getBody().getResults().stream()
            .filter(result -> result.getTitle().equals(attachmentFilename)).map(Result::getId)
            .findAny();
  }

  public void createAttachment(String pageId, String filePath) {
    URI targetUrl =
        UriComponentsBuilder.fromUriString(confluenceConfig.getRestApiUrl())
            .path("/content/{id}/child/attachment")
            .buildAndExpand(pageId)
            .toUri();
    postAttachment(targetUrl, filePath);
  }

  public void updateAttachment(String pageId, String attachmentId, String filePath) {
    URI targetUrl =
        UriComponentsBuilder.fromUriString(confluenceConfig.getRestApiUrl())
            .path("/content/{pageId}/child/attachment/{attachmentId}/data")
            .buildAndExpand(pageId, attachmentId)
            .toUri();
    postAttachment(targetUrl, filePath);
  }

  public void postAttachment(URI targetUrl, String filePath) {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", new FileSystemResource(filePath));

    HttpEntity<MultiValueMap<String, Object>> multiValueMapHttpEntity =
        new HttpEntity<>(body, httpHeadersForAttachment);
    HttpEntity<Page> responseEntity =
        restTemplate.exchange(targetUrl, HttpMethod.POST, multiValueMapHttpEntity, Page.class);
    log.debug("Response of adding attachment: {}", responseEntity.getBody());
  }
}
