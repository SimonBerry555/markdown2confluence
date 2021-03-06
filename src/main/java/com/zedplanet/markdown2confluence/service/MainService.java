package com.zedplanet.markdown2confluence.service;

import com.zedplanet.markdown2confluence.ConfluenceConfig;
import com.zedplanet.markdown2confluence.ConfluenceConfig.Page;
import com.zedplanet.markdown2confluence.service.confluence.AttachmentService;
import com.zedplanet.markdown2confluence.service.confluence.ConfluenceService;
import com.zedplanet.markdown2confluence.service.confluence.PageService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static com.zedplanet.markdown2confluence.service.Utilities.convertMarkdown2HTML;
import static com.zedplanet.markdown2confluence.service.Utilities.readFile;

@Service
public class MainService {

  private final PageService pageService;
  private final AttachmentService attachmentService;
  private final ConfluenceService confluenceService;

  public MainService(
      PageService pageService,
      AttachmentService attachmentService,
      ConfluenceService confluenceService) {
    this.pageService = pageService;
    this.attachmentService = attachmentService;
    this.confluenceService = confluenceService;
  }

  public static List<Page> order(Collection<Page> pages) {
    if (pages.isEmpty()) return Collections.EMPTY_LIST;

    Collection<Page> roots = new ArrayList<>();
    Collection<Page> children = new ArrayList<>();
    for (Page page : pages) {
      if (hasParent(page, pages)) {
        children.add(page);
      } else {
        roots.add(page);
      }
    }

    LinkedList<Page> linkedList = new LinkedList<>();

    linkedList.addAll(roots);
    linkedList.addAll(order(children));
    return linkedList;
  }

  private static boolean hasParent(Page page, Collection<Page> pages) {
    boolean res = false;
    for (Page curr : pages) {
      if (curr.getTitle().equals(page.getParentTitle())) {
        return true;
      }
    }

    return res;
  }

  public void processAll(ConfluenceConfig confluenceConfig) throws IOException {

    confluenceService.setConfluenceConfig(confluenceConfig);

    List<Page> orderedList = order(confluenceConfig.getPages());
    for (Page page : orderedList) {
      String markdownText = readFile(page);
      String html = convertMarkdown2HTML(markdownText);

      // Holds the path to the file
      ArrayList<Path> imageFilePaths = new ArrayList<>();

      html = processInlineImages(html, imageFilePaths);

      String pageId = pageService.postWikiPageToConfluence(page, html);

      imageFilePaths.forEach(
          imagePath -> attachmentService.postAttachmentToPage(pageId, imagePath));
    }
  }

  // Any images in the markdown need to be published as attachments to the page.
  // An inline image in markdown looks like this:
  // ![alt text](uri "Title")
  // uri can be an http reference (e.g. https://zedplanet.com/images/logo.png) or a file path
  // e.g. (./docs/images/logo.png or /Users/user/home/avatar.png)
  // Any files need to be uploaded as attachments, the attachment has the filename not the full
  // path so we need to replace the full path in the wikiText
  private String processInlineImages(String wikiText, ArrayList<Path> imageFilePaths) {

    Document doc = Jsoup.parse(wikiText, "", Parser.xmlParser());

    doc.select("img")
        .forEach(
            img -> {
              Attributes attributes = img.attributes();
              if (!attributes.get("src").startsWith("http")) {
                new Element(Tag.valueOf("ac:image"), "");
                imageFilePaths.add(Path.of(attributes.get("src")));
                var filename =
                    attributes.get("src").substring(attributes.get("src").lastIndexOf("/") + 1);
                img.replaceWith(
                    new Element(Tag.valueOf("ac:image"), "")
                        .insertChildren(
                            0,
                            new Element(Tag.valueOf("ri:attachment"), "")
                                .attr("ri:filename", filename)));
              }
            });

    return doc.html();
  }
}
