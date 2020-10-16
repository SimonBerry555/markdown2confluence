package com.zedplanet.markdown2confluence.service;

import com.zedplanet.markdown2confluence.ConfluenceConfig;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileReaderService {

  public String readFile(ConfluenceConfig.Page page) throws IOException {
    File file = Paths.get(page.getFilepath()).toFile();
    return new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
  }
}
