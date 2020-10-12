package com.zedplanet.markdown2confluence.core.service;

import com.zedplanet.markdown2confluence.core.ConfluenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Anton Reshetnikov on 15 Nov 2016.
 */
@Service
public class FileReaderService  {

    public String readFile(ConfluenceConfig.Page page) throws IOException {
        File file = Paths.get(page.getFilepath()).toFile();
        return new String(Files.readAllBytes(file.toPath()), Charset.forName("UTF-8"));
    }
}
