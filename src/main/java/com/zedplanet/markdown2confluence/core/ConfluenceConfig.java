package com.zedplanet.markdown2confluence.core;


//import groovy.lang.Closure;
//import org.gradle.util.ConfigureUtil;

import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Anton Reshetnikov on 10 Nov 2016.
 */
@Data
public class ConfluenceConfig {

    public static final long DEFAULT_PARSE_TIMEOUT = 2000L;

    private String authentication;
    private String restApiUrl;
    private String spaceKey;
    private boolean sslTrustAll = false;
    private Map<String, String> pageVariables;
    private Long parseTimeout = DEFAULT_PARSE_TIMEOUT;
    private Collection<Page> pages;

    @Data
    public static class Page{
        private String parentTitle;
        private String title;
        private String filepath;
        private Collection<String> labels;
    }
}
