/*
 * UEM
 */

package com.uem.gsi.cleim.nlp;

import java.net.URLEncoder;

/**
 *
 * @author Miguel Angel Rosales Navarro
 */
public class DBPediaAnnot {
    
    public static final String PAGE_DBPEDIA = "DBPedia";
    
    public static final String PAGE_WIKIPEDIA = "WikiPedia";
    
    public static final String PAGE_PUBMED = "PubMed";
    
    /** Constant for Disease Type. **/
    public static final int TYPE_DISEASE = 1;
    
    /** Constant for Medicine Type. **/
    public static final int TYPE_MEDICINE = 2;
    
    /** Label of concept (without @language). **/
    private String label;
    
    /** URL of english concept. **/
    private String genericUrl;
    
    /** URL of concept with its specific language. **/
    private String specificUrl;
    
    /** Language for the concept (for retrocompabilty it uses "SP" for spanish instead of "ES"). **/
    private String language;
    
    /** Type of the concept (Disease or Medicine) **/
    private int type;
    
    /** Abstract of concept. **/
    private String iabstract;
    
    /** Comment of concept. **/
    private String comment;
    
    /** Generic Constructor. **/
    public DBPediaAnnot() {
        
    }

    public String getLabel() {
        if (label == null) {
            return "";
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGenericUrl() {
        if (genericUrl == null) {
            return "";
        }
        String url = genericUrl;
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception ex) {
            url = genericUrl;
        }
        url = url.replace("+", "_");
        url = url.replace("%3A", ":");
        url = url.replace("%2F", "/");
        return url;
    }

    public void setGenericUrl(String genericUrl) {
        this.genericUrl = genericUrl;
    }

    public String getSpecificUrl() {
        if (specificUrl == null) {
            return "";
        }
        String url = specificUrl;
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception ex) {
            url = specificUrl;
        }
        url = url.replace("+", "_");
        url = url.replace("%3A", ":");
        url = url.replace("%2F", "/");
        return url;
    }

    public void setSpecificUrl(String specificUrl) {
        this.specificUrl = specificUrl;
    }

    public String getLanguage() {
        if (language == null || language.trim().equals("")) {
            return "en";
        //COMPABILITY WITH FORMTAT OF OTHER SOURCES
        } else if (language.equals("es")) {
            return "sp";
        }
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAbstract() {
        if (iabstract == null) {
            return "";
        }
        return iabstract;
    }

    public void setAbstract(String iabstract) {
        this.iabstract = iabstract;
    }

    public String getComment() {
        if (comment == null) {
            return "";
        }
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Return the text of the type of the concept in specific language (or english if there is no traduction available).
     * @return String - text type.
     */
    public String getTypeText() {
        String type = "";
        if (this.getType() == TYPE_DISEASE) { type = "Disease"; }
        else { type = "Medicine"; }
        if (this.getLanguage() != null && !this.getLanguage().trim().equals("")) {
            if (this.getLanguage().toLowerCase().equals("sp")) {
                if (this.getType() == TYPE_DISEASE) { type = "Enfermedad"; }
                else { type = "Medicina"; }
            } else if (this.getLanguage().toLowerCase().equals("en")) {
                if (this.getType() == TYPE_DISEASE) { type = "Disease"; }
                else { type = "Medicine"; }
            } else if (this.getLanguage().toLowerCase().equals("fr")) {
                if (this.getType() == TYPE_DISEASE) { type = "Maladie"; }
                else { type = "Médecine"; }
            } else if (this.getLanguage().toLowerCase().equals("sv")) {
                if (this.getType() == TYPE_DISEASE) { type = "Sjukdom"; }
                else { type = "Medicin"; }
            } else if (this.getLanguage().toLowerCase().equals("pt")) {
                if (this.getType() == TYPE_DISEASE) { type = "Doença"; }
                else { type = "Medicina"; }
            } else if (this.getLanguage().toLowerCase().equals("it")) {
                if (this.getType() == TYPE_DISEASE) { type = "Malattia"; }
                else { type = "Medicina"; }
            } else if (this.getLanguage().toLowerCase().equals("de")) {
                if (this.getType() == TYPE_DISEASE) { type = "Krankheit"; }
                else { type = "Medizin"; }
            } else if (this.getLanguage().toLowerCase().equals("nl")) {
                if (this.getType() == TYPE_DISEASE) { type = "Ziekte"; }
                else { type = "Geneeskunde"; }
            } else if (this.getLanguage().toLowerCase().equals("zh")) {
                if (this.getType() == TYPE_DISEASE) { type = "病"; }
                else { type = "医药"; }
            } else if (this.getLanguage().toLowerCase().equals("ru")) {
                if (this.getType() == TYPE_DISEASE) { type = "Болезнь"; }
                else { type = "Медицина"; }
            } else if (this.getLanguage().toLowerCase().equals("ja")) {
                if (this.getType() == TYPE_DISEASE) { type = "病気"; }
                else { type = "医学"; }
            } else if (this.getLanguage().toLowerCase().equals("pl")) {
                if (this.getType() == TYPE_DISEASE) { type = "Choroba"; }
                else { type = "Medycyna"; }
            } else if (this.getLanguage().toLowerCase().equals("da")) {
                if (this.getType() == TYPE_DISEASE) { type = "Sygdom"; }
                else { type = "Medicin"; }
            } else if (this.getLanguage().toLowerCase().equals("ar")) {
                if (this.getType() == TYPE_DISEASE) { type = "المرض"; }
                else { type = "الطب"; }
            } else if (this.getLanguage().toLowerCase().equals("cs")) {
                if (this.getType() == TYPE_DISEASE) { type = "Nemoc"; }
                else { type = "Medicína"; }
            } else if (this.getLanguage().toLowerCase().equals("fi")) {
                if (this.getType() == TYPE_DISEASE) { type = "Tauti"; }
                else { type = "Lääketiede"; }
            } else if (this.getLanguage().toLowerCase().equals("he")) {
                if (this.getType() == TYPE_DISEASE) { type = "מחלה"; }
                else { type = "רפואה"; }
            } else if (this.getLanguage().toLowerCase().equals("hi")) {
                if (this.getType() == TYPE_DISEASE) { type = "रोग"; }
                else { type = "चिकित्सा"; }
            } else if (this.getLanguage().toLowerCase().equals("nb")) {
                if (this.getType() == TYPE_DISEASE) { type = "Sykdom"; }
                else { type = "Medisin"; }
            } else if (this.getLanguage().toLowerCase().equals("el")) {
                if (this.getType() == TYPE_DISEASE) { type = "Ασθένεια"; }
                else { type = "Ιατρική"; }
            }
        }
        return type;
    }
    
    /**
     * Return the type of a concept.
     * @param textType .
     * @return int .
     */
    public static int getType(String textType) {
        int toReturn = TYPE_DISEASE;
        if (textType == null || textType.trim().equals("")) {
            textType = "Disease";
        }
        if ((textType.equals("Disease")) || (textType.equals("Enfermedad")) || (textType.equals("Maladie")) || 
            (textType.equals("Doença")) || (textType.equals("Malattia")) || (textType.equals("Krankheit")) || 
            (textType.equals("Sjukdom")) || (textType.equals("Ziekte")) || (textType.equals("病")) || 
            (textType.equals("Болезнь")) || (textType.equals("病気")) || (textType.equals("Choroba")) || 
            (textType.equals("Sygdom")) || (textType.equals("المرض")) || (textType.equals("Nemoc")) || 
            (textType.equals("Tauti")) || (textType.equals("מחלה")) || (textType.equals("रोग")) || 
            (textType.equals("Sykdom")) || (textType.equals("Ασθένεια"))) {
            toReturn = TYPE_DISEASE;
        } else { 
            toReturn = TYPE_MEDICINE;
        }
        return toReturn;
    }
    
    /**
     * Return the alternative text of the URL of the concept in specific language (or english if there is no traduction is available).
     * @param pageName - Name of the Page.
     * @param lan - language
     * @return String - Alternative Text.
     */
    public static String getAlternativeTextUrl(String pageName, String lan) {
        String text = "Go to " + pageName;
        if (lan != null && !lan.trim().equals("")) {
            if (lan.toLowerCase().equals("es")) {
                lan = "sp";
            }
            if (lan.toLowerCase().equals("sp")) {
                text = "Ir a " + pageName;
            } else if (lan.toLowerCase().equals("en")) {
                text = "Go to " + pageName;
            } else if (lan.toLowerCase().equals("fr")) {
                text = "Allez dans " + pageName;
            } else if (lan.toLowerCase().equals("sv")) {
                text = "Gå till " + pageName;
            } else if (lan.toLowerCase().equals("pt")) {
                text = "Ir para " + pageName;
            } else if (lan.toLowerCase().equals("it")) {
                text = "Vai a " + pageName;
            } else if (lan.toLowerCase().equals("de")) {
                text = "Zum " + pageName;
            } else if (lan.toLowerCase().equals("nl")) {
                text = "Ga naar " + pageName;
            } else if (lan.toLowerCase().equals("zh")) {
                text = "去" + pageName + "中";
            } else if (lan.toLowerCase().equals("ru")) {
                text = "К " + pageName;
            } else if (lan.toLowerCase().equals("ja")) {
                text = pageName + "のに行く";
            } else if (lan.toLowerCase().equals("pl")) {
                text = "Idź do " + pageName;
            }  else if (lan.toLowerCase().equals("da")) {
                text = "Gå til " + pageName;
            } else if (lan.toLowerCase().equals("ar")) {
                text = "الذهاب إلى " + pageName;
            } else if (lan.toLowerCase().equals("cs")) {
                text = "Přejít na " + pageName;
            } else if (lan.toLowerCase().equals("fi")) {
                text = "Siirry " + pageName;
            } else if (lan.toLowerCase().equals("he")) {
                text = "עבור ל" + pageName;
            } else if (lan.toLowerCase().equals("hi")) {
                text = pageName + " पर जाएँ";
            } else if (lan.toLowerCase().equals("nb")) {
                text = "Gå til " + pageName;
            } else if (lan.toLowerCase().equals("el")) {
                text = "Πηγαίνετε στο " + pageName;
            }else {
                text += "(" + lan + ")";
            }
        }
        return text;
    }
    
    /**
     * Return the alternative text of the URL of the concept in english language.
     * @param pageName - Name of the Page.
     * @return String - Alternative Text.
     */
    public static String getAlternativeTextUrlEnglish(String pageName) {
        String text = "Go to " + pageName;
        return text;
    }
    
    /**
     * Returns the Wikipedia url of the concept.
     * @return String .
     */
    public String getWikipediaUrl() {
        String url = "http://";
        if (this.getLanguage() == null || this.getLanguage().trim().equals("")) {
            url += "wikipedia.org/wiki/";
        } else {
            if (this.getLanguage().toLowerCase().equals("sp")) {
                url += "es";
            } else {
                url += this.getLanguage();
            }
            url += ".wikipedia.org/wiki/";
        }
        try {
            url += URLEncoder.encode(this.getLabel(), "UTF-8");
        } catch (Exception ex) {
            url += this.getLabel();
        }
        url = url.replace("+", "_");
        return url;
    }
    
}
