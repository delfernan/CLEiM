/*
 * UEM.
 */

package com.uem.gsi.cleim.rpd;

import com.uem.gsi.cleim.nlp.DBPediaAnnot;
import com.uem.gsi.cleim.nlp.IntegrateAnnot;
import com.uem.gsi.cleim.util.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Miguel Angel Rosales Navarro
 */
public class DBPediaToFile  extends Thread {
    
    /** Contains the list of languages supported for saving files. **/
    ////public static final List<String> supportedLanguageFiles = new ArrayList<String>() {{
    ////    add("sp");add("en");}};
    
    String filePath = "";
    List<DBPediaAnnot> listAnnotations = new ArrayList<DBPediaAnnot>();
    
    public static final List<String> supportedLanguageFiles = new ArrayList<String>() {{
        add("sp");add("en");add("fr");add("sv");add("pt");
        add("it");add("de");add("nl");add("zh");add("ru");
        add("ja");add("pl");add("da");add("ar");add("cs");
        add("fi");add("he");add("hi");add("nb");add("el");
    }};
    
    /**
     * Constructor.
     * @param filePath - path of DBPedia Files.
     * @param listAnnotations - DBPedia annotations.
     */
    public DBPediaToFile(String pfilePath, List<DBPediaAnnot> plistAnnotations) {
        filePath = pfilePath;
        listAnnotations = plistAnnotations;
    }
    
    /**
     * Start the thread.
     */
    public void run() {
        saveAnnotationsToDBPediaFiles();
    }
    
    /**
     * Save the annotations into files, if they are not into them before.
     */
    private void saveAnnotationsToDBPediaFiles() {
        if (listAnnotations != null && !listAnnotations.isEmpty()) {
            List<FileLineAnnot> listFileLines = new ArrayList<FileLineAnnot>();
            // Contains the lines of files with their associate language.
            Map<String, List<String>> mapLines = new HashMap<String, List<String>>();
            for (DBPediaAnnot annot: listAnnotations) {
                //We only can save the lines of supported languages.
                if (supportedLanguageFiles.contains(annot.getLanguage().toLowerCase())) {
                    String fileLine = annot.getLabel() + "&url=";
                    String url = annot.getSpecificUrl();
                    if (url == null || url.trim().equals("") ) {
                        url = annot.getGenericUrl();
                    }
                    fileLine += url + "&groups=" + annot.getTypeText();
                    listFileLines.add(new FileLineAnnot(annot.getLanguage(), fileLine));
                }
            }
            if (!listFileLines.isEmpty()) {
                List<String> languageList =  new ArrayList<String>();
                //1.-First we get all the differents languages we had received.
                for (FileLineAnnot line : listFileLines) {
                    if (line.getLanguage() != null && !line.getLanguage().trim().equals("")) {
                        if (!languageList.contains(line.getLanguage())) {
                            languageList.add(line.getLanguage());
                            mapLines.put(line.getLanguage(), new ArrayList<String>());
                        }
                    }
                }
                if (!languageList.isEmpty()) {
                    //2.-Read the files and put lines into Maps..
                    for (String lan: languageList) {
                        //Open file language.
                        try {
                            String fileName = filePath + Constants.PATH_DBPEDIA_LST + lan + Constants.SUFIX_DBPEDIA_FILE;
                            File fileDir = new File(fileName);
                            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileDir), "UTF8"));
                            String lineLoaded;
                            while ((lineLoaded = in.readLine()) != null) {
                                mapLines.get(lan).add(lineLoaded);
                            }
                            in.close();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    //3. Look each Line received in its language map.
                    //if not fount put in map and write in file. add the language to the listr of languages to write
                    //And add the specif line to the total line we want to write.
                    List<String> fileLanguagesToWrite = new ArrayList<String>();
                    Map<String, String> mapLinesFiles = new HashMap<String, String>();
                    for (FileLineAnnot line : listFileLines) {
                        if (line.getLanguage() != null && !line.getLanguage().trim().equals("")) {
                            if (!mapLines.get(line.getLanguage()).contains(line.getFileLine())) {
                                mapLines.get(line.getLanguage()).add(line.getFileLine());
                                if (!fileLanguagesToWrite.contains(line.getLanguage())) {
                                    fileLanguagesToWrite.add(line.getLanguage());
                                    mapLinesFiles.put(line.getLanguage(), new String(""));
                                }
                                String totalLine = mapLinesFiles.get(line.getLanguage());
                                totalLine += line.getFileLine() + "\n";
                                mapLinesFiles.put(line.getLanguage(), totalLine);
                            }
                        }
                    }
                    if (!fileLanguagesToWrite.isEmpty()) {
                        writeLinesToDBPediaFile(filePath, fileLanguagesToWrite, mapLinesFiles);
                        System.out.println("DBPEDIA FILES WRITE FINISHED AT: " + System.currentTimeMillis());
                    }
                    //4.- Finally, try to destroy these heavy objects (Garbage Collector will do the work).
                    fileLanguagesToWrite = null;
                    mapLinesFiles = null;
                }
                languageList = null;
            }
            mapLines = null;
        }
    }
    
    /**
     * Opens dbpedia files and write new lines.
     * @param filePath - path of the file.
     * @param fileLanguagesToWrite - List of the file languages to open and write.
     * @param mapLinesFiles - map with lines to write to each file list.
     */
    private void writeLinesToDBPediaFile(String filePath, List<String> fileLanguagesToWrite, Map<String, String> mapLinesFiles) {
        if (fileLanguagesToWrite != null && !fileLanguagesToWrite.isEmpty() && mapLinesFiles != null && !mapLinesFiles.isEmpty()) {
            for (String lan : fileLanguagesToWrite) {
                String line = mapLinesFiles.get(lan);
                if (line != null && !line.trim().equals("")) {
                    try {
                        String fileName = filePath + Constants.PATH_DBPEDIA_LST + lan + Constants.SUFIX_DBPEDIA_FILE;
                        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName, true), "UTF-8");
                        BufferedWriter fbw = new BufferedWriter(writer);
                        fbw.write(line);
                        fbw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    //Test DBPedia To File
    public static void main(String[] args) {
        String searchText = "cancer";
        long elapsedTime = testSaveDBPediaToFile(searchText);
        System.out.println("***********************************");
        System.out.println("TEST DBPedia SEARCH AND WRITE FILE.");
        System.out.println("SEARCH TEXT = " + searchText);
        System.out.println("WRITE EXECUTION ELAPSED TIME = " + elapsedTime + " MILLISECONDS");
        System.out.println("***********************************");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DBPediaToFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        elapsedTime = testReadDBPediaFile(searchText);
        System.out.println("***********************************");
        System.out.println("TEST DBPedia SEARCH FROM LOCAL FILES.");
        System.out.println("SEARCH TEXT = " + searchText);
        System.out.println("READ EXECUTION ELAPSED TIME = " + elapsedTime + " MILLISECONDS");
        System.out.println("***********************************");
    }
    
    /**
     * Test find a text from dbpedia and write the ontologies to File.
     * @param searchText - text to Search.
     * @return - long (time elapsed).
     */
    public static long testSaveDBPediaToFile(String searchText) {
        long startTime = System.currentTimeMillis();
        String path = "web/WEB-INF";
        String text = "cancer";
        String onts = "";
        String lev = "0";
        String[] localSrc = new String[]{"", "", "", "", "DBPedia"};
        String[] localLan = DBPediaToFile.supportedLanguageFiles.toArray(new String[0]);
        String src = "4";
        IntegrateAnnot ia = new IntegrateAnnot(text, path, onts, lev, localSrc, localLan, src);
        ia.remoteWithoutFormat();
        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }
    
    /**
     * Text local search from dbpedia files using all available languages
     * @param searchText - text to Search.
     * @return - long (time elapsed).
     */
    public static long testReadDBPediaFile(String searchText) {
        long startTime = System.currentTimeMillis();
        String path = "web/WEB-INF";
        String text = "cancer";
        String onts = "";
        String lev = "0";
        String[] localSrc = new String[]{"", "", "", "", "DBPedia"};
        String[] localLan = DBPediaToFile.supportedLanguageFiles.toArray(new String[0]);
        String src = "1";
        IntegrateAnnot ia = new IntegrateAnnot(text, path, onts, lev, localSrc, localLan, src);
        ia.localWithoutFormat();
        long endTime = System.currentTimeMillis();
        return (endTime - startTime);
    }

}
