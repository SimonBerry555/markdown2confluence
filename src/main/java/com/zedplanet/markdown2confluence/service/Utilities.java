package com.zedplanet.markdown2confluence.service;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.zedplanet.markdown2confluence.ConfluenceConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Utilities {

    public static String convertMarkdown2HTML(final String markdown) {

        MutableDataSet options = new MutableDataSet();

        // uncomment to set optional extensions
        options.set(
                Parser.EXTENSIONS,
                Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));

        // uncomment to convert soft-breaks to hard breaks
        // options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

        Parser parser = Parser.builder(options).build();
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        return renderer.render(parser.parse(markdown));
    }

    public static String readFile(ConfluenceConfig.Page page) throws IOException {
        File file = Paths.get(page.getFilepath()).toFile();
        return Files.readString(file.toPath());
    }
}
