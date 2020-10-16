package com.zedplanet.markdown2confluence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.zedplanet.markdown2confluence.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Slf4j
@SpringBootApplication
public class Markdown2confluenceApplication implements CommandLineRunner {

  @Autowired MainService mainService;

  public static void main(String[] args) {
    SpringApplication.run(Markdown2confluenceApplication.class, args).close();
  }

  public static ConfluenceConfig getConfluenceConfig() throws IOException {

    if (System.getenv("CONFLUENCE_USER") == null) {
      throw new IllegalArgumentException("CONFLUENCE_USER is not defined in the environment.");
    }

    if (System.getenv("CONFLUENCE_API_KEY") == null) {
      throw new IllegalArgumentException("CONFLUENCE_API_KEY is not defined in the environment.");
    }

    File file = Paths.get("md2confluence.yaml").toFile();
    ObjectMapper om = new ObjectMapper(new YAMLFactory());

    ConfluenceConfig confluenceConfig = om.readValue(file, ConfluenceConfig.class);

    var basicAuthString =
        System.getenv("CONFLUENCE_USER") + ':' + System.getenv("CONFLUENCE_API_KEY");

    byte[] bytesEncoded = Base64.encodeBase64(basicAuthString.getBytes());

    confluenceConfig.setAuthentication(new String(bytesEncoded));

    return confluenceConfig;
  }

  @Override
  public void run(String... args) throws Exception {

    System.out.println("Running");

    mainService.processAll(getConfluenceConfig());

    System.out.println("Done");
  }
}
