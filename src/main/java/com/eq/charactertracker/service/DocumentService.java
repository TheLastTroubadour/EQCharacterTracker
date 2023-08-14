package com.eq.charactertracker.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class DocumentService {

    public Document openDocument(String url){
        Document document = null;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("Unable to connect to url: " + url + " IOException: " + e.getMessage());
        }
        return document;
    };
}
