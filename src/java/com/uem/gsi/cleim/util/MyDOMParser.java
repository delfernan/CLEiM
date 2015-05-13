package com.uem.gsi.cleim.util;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.*;

public class MyDOMParser implements java.io.Serializable {

    public MyDOMParser() {
    }

    /**
     *
     * @param url URL or FILE depending on method param
     * @param method 0 (URL) 1 (FILE)
     * @return
     * @throws Exception
     */
    public static Document getDocument(String url, int method) throws Exception {

        // Step 1: create a DocumentBuilderFactory
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();

     // Step 2: create a DocumentBuilder
        //df.setNamespaceAware(true);
        DocumentBuilder db = df.newDocumentBuilder();

        // Step 3: parse the input file to get a Document object
        Document doc;
        if (method == 1) {
            File file = new File(url);
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            doc = db.parse(is);
        } else {
            doc = db.parse(url);
        }
        return doc;
    }

    public static Document getDocument(String xml) throws Exception {

        // Step 1: create a DocumentBuilderFactory
        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();

     // Step 2: create a DocumentBuilder
        //df.setNamespaceAware(true);
        DocumentBuilder db = df.newDocumentBuilder();
        // Step 3: parse the input text to get a Document object
        InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Document doc = db.parse(is);

        return doc;
    }
}
