/*
 * UEM.
 */

package com.uem.gsi.cleim.rpd;

/**
 *
 * @author Miguel Angel Rosales Navarro
 */
public class FileLineAnnot {
    
    /** Contains the language for this file line. **/
    private String language;
    /** Contains the file line. **/
    private String fileLine;

    public FileLineAnnot(String language, String fileLine) {
        this.language = language;
        this.fileLine = fileLine;
    }

    
    
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFileLine() {
        return fileLine;
    }

    public void setFileLine(String fileLine) {
        this.fileLine = fileLine;
    }
    
    
    
}
