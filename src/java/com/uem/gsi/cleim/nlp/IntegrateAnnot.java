/**
 *
 */
package com.uem.gsi.cleim.nlp;

import com.uem.gsi.cleim.rpd.DBPediaToFile;
import com.uem.gsi.cleim.scl.CallDBPedia;
import com.uem.gsi.cleim.util.Constants;
import com.uem.gsi.cleim.util.ReplaceDefFile;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author delfernan
 *
 */
public class IntegrateAnnot {

    private String text = "";
    private String gatePath = "";
    private String onts = "";
    private String lev = "";
    private String[] localSrc;
    private String[] localLan;
    private Document document = null;
    private Element root = null;
    private String src = "";
    private boolean localDbpediaSearch = false;

    public IntegrateAnnot(String ptext, String pgatePath, String ponts,
            String plev, String[] plocalSrc, String[] plocalLan, String psrc) {
        text = ptext;
        gatePath = pgatePath;
        onts = ponts;
        lev = plev;
        localSrc = plocalSrc;
        localLan = plocalLan;
        src = psrc;
    }
    
    /** Used only by TestAnnotations. **/
    public void localWithoutFormat() {
        try {
            //First Time = false, we can to test the full search.
            gateWithoutFormat(false);
        } catch (Exception ex) {
            System.out.println("ERROR GETTING LOCAL WITHOUT FORMAT: " + ex.getMessage());
        }
    }

    private GateGazMed gateWithoutFormat(boolean firstSearch) throws Exception {
        GateGazMed gateAnnot = new GateGazMed(gatePath);
        String sGapp = "CLEiMtmp.gapp";
        
        String[] newLocalLan;
        if (localDbpediaSearch) {
            if (firstSearch) {
                //First search we try only with sp and en languages
                List<String> lanList = new ArrayList<String>();
                for (String localSrc1 : localLan) {
                    if ((localSrc1.toLowerCase().equals("sp")) || (localSrc1.toLowerCase().equals("en"))) {
                        lanList.add(localSrc1);
                    }
                }
                newLocalLan = lanList.toArray(new String[0]);
            } else {
                //Second time we use all the languages received
                newLocalLan = localLan;
            }
        } else {
            newLocalLan = localLan;
        }
        ReplaceDefFile rdf = new ReplaceDefFile();
        rdf.saveDefFile(localSrc, newLocalLan, gatePath + Constants.PATH_DEF_TMP);
        gateAnnot.runGazetteer(text, sGapp);
	return gateAnnot;
    }

    /** Used only by TestAnnotations. **/
    public void remoteWithoutFormat() {
        if (src.equals("0") || src.equals("-1")) {
            //NCBO Annotation
            try {
                NCBOAnnot ncboAnnot = new NCBOAnnot();
                ncboAnnot.runNCBOAnnot(text, onts, lev);
            } catch (Exception ex) {
                System.out.println("ERROR GETTING NCBOANNOT: " + ex.getMessage());
            }
            //DBPedia Annotation
            try {
                dbpediaWithoutFormat();
            } catch (Exception ex) {
                System.out.println("ERROR GETTING dbPediaAnnotations: " + ex.getMessage());
            }
            //ADD HERE NEW ANNOTATION SOURCES
        //NCBO Annotation
        } else if (src.equals("2") || src.equals("3")) {
            try {
                NCBOAnnot ncboAnnot = new NCBOAnnot();
                ncboAnnot.runNCBOAnnot(text, onts, lev);
            } catch (Exception ex) {
                System.out.println("ERROR GETTING NCBOANNOT: " + ex.getMessage());
            }
        //DBPedia REMOTE
        } else if (src.equals("4")) {
            try {
                dbpediaWithoutFormat();
            } catch (Exception ex) {
                System.out.println("ERROR GETTING dbPediaAnnotations: " + ex.getMessage());
            }
        }
        //ADD HERE NEW ANNOTATION SOURCES
    }
    
    /**
     * Get DBPedia annotations without format (and try to write new lines in files).
     * @return List<DBPediaAnnot> .
     * @throws Exception .
     */
    private List<DBPediaAnnot> dbpediaWithoutFormat() throws Exception {
        List<DBPediaAnnot> annotations = CallDBPedia.getDBPediaAnnotations(text);
        if (annotations != null && !annotations.isEmpty()) {
            //Call to DBPediaToFile using a thread (heavy process)
            DBPediaToFile dbpFile = new DBPediaToFile(gatePath, annotations);
            dbpFile.start();
        }
        return annotations;
    }
    
    private NCBOAnnot ncboWithoutFormat() throws Exception {
        NCBOAnnot ncboAnnot = new NCBOAnnot();
        ncboAnnot.runNCBOAnnot(text, onts, lev);
	return ncboAnnot;
    }

    public String getHtmlLocalTree() {
        String result = "";
        localDbpediaSearch = false;
        if (localSrc != null && localSrc.length > 0) {
            for (String localSrc1 : localSrc) {
                if (localSrc1.toLowerCase().equals("dbpedia")) {
                    localDbpediaSearch = true;
                }
            }
            
            try {
                boolean dbPediaFound = false;
                //(firstTime=true)
                GateGazMed gateAnnot = gateWithoutFormat(true);
                Vector<String> vMinor = gateAnnot.getMinor();
                Vector<List<String>> vAnnot = gateAnnot.getAnnot();
                Vector<List<String>> vAnnotLan = gateAnnot.getAnnotLan();
                Vector<List<String>> vAnnotSource = gateAnnot.getAnnotSource();
                Vector<List<String>> vAnnotGroup = gateAnnot.getAnnotGroup();
                Vector<List<String>> vAnnotUrl = gateAnnot.getAnnotUrl();
                for (int i = 0; i < vMinor.size(); i++) {
                    //Repeated
                    List<Integer> lNoRepeated = remRepeated(vAnnot.get(i));
                    for (int k = 0; k < lNoRepeated.size(); k++) {
                        int j = lNoRepeated.get(k);
                        String url = "";
                        if (vMinor.get(i).contains("dbpedia")) {
                            dbPediaFound = true;
                            url+= "dbpedia.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8") + "&lan=" + vAnnotLan.get(i).get(j) + "&type=" + DBPediaAnnot.getType(vAnnotGroup.get(i).get(j));
                        } else {
                            url += "medlineplus.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8") + "&lan=" + vAnnotLan.get(i).get(j);
                        }
                        result += insertHtmlItem(vAnnot.get(i).get(j), vAnnotLan.get(i).get(j), vAnnotSource.get(i).get(j),
                                vAnnotGroup.get(i).get(j), vAnnotUrl.get(i).get(j), url);
                    }
                }
                //if there is a search over dbpedia and first seacrh (en and sp) not found anything
                //try to look up a second time using all languages.
                if (localDbpediaSearch && !dbPediaFound) {
                    result = "";
                    //SecondSearch (firstTime=false)
                    gateAnnot = gateWithoutFormat(false);
                    vMinor = gateAnnot.getMinor();
                    vAnnot = gateAnnot.getAnnot();
                    vAnnotLan = gateAnnot.getAnnotLan();
                    vAnnotSource = gateAnnot.getAnnotSource();
                    vAnnotGroup = gateAnnot.getAnnotGroup();
                    vAnnotUrl = gateAnnot.getAnnotUrl();
                    for (int i = 0; i < vMinor.size(); i++) {
                        //Repeated
                        List<Integer> lNoRepeated = remRepeated(vAnnot.get(i));
                        for (int k = 0; k < lNoRepeated.size(); k++) {
                            int j = lNoRepeated.get(k);
                            String url = "";
                            if (vMinor.get(i).contains("dbpedia")) {
                                int type = DBPediaAnnot.getType(vAnnotGroup.get(i).get(j));
                                url+= "dbpedia.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8") + "&lan=" + vAnnotLan.get(i).get(j) + "&type=" + String.valueOf(type);
                            } else {
                                url += "medlineplus.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8") + "&lan=" + vAnnotLan.get(i).get(j);
                            }
                            result += insertHtmlItem(vAnnot.get(i).get(j), vAnnotLan.get(i).get(j), vAnnotSource.get(i).get(j),
                                    vAnnotGroup.get(i).get(j), vAnnotUrl.get(i).get(j), url);
                        }
                    }
                }
            } catch (Exception ex) {
                result += "<tr><td></td><td>" + ex.getMessage()+ "</td><td>LOCAL</td><td></td><td></td><tr>";
            }
        }
        return result;
    }

    /**
     * Exposes one method for all annotations type, returning an HTML result.
     * @return String with <tr>'s html rows.
     * @throws Exception .
     */
    public String getHtmlRemoteTree() {
        String nlpResults = "";
        //ALL REMOTE
        if (src.equals("0") || src.equals("-1")) {
            //NCBO Annotation
            try {
                nlpResults += getHtmlNCBOTree();
            } catch (Exception ex) {
                nlpResults += "<tr><td></td><td>" + ex.getMessage()+ "</td><td>NCBO</td><td></td><td></td><tr>";
            }
            //DBPedia Annotation
            try {
                nlpResults += getHtmlDBPediaTree();
            } catch (Exception ex) {
                nlpResults += "<tr><td></td><td>" + ex.getMessage()+ "</td><td>DBPedia</td><td></td><td></td><tr>";
            }
            //ADD HERE NEW ANNOTATION SOURCES
        //NCBO Annotation
        } else if (src.equals("2") || src.equals("3")) {
            try {
                nlpResults += getHtmlNCBOTree();
            } catch (Exception ex) {
                String source = "MedlinePlus";
                if (src.equals("3")) {
                    source = "Snomed";
                }
                nlpResults += "<tr><td></td><td>" + ex.getMessage()+ "</td><td>" + source + "</td><td></td><td></td><tr>";
            }
        //DBPedia REMOTE
        } else if (src.equals("4")) {
            try {
                nlpResults += getHtmlDBPediaTree();
            } catch (Exception ex) {
                nlpResults += "<tr><td></td><td>" + ex.getMessage()+ "</td><td>DBPedia</td><td></td><td></td><tr>";
            }
        }
        //ADD HERE NEW ANNOTATION SOURCES
        return nlpResults;
    }
    
    private String getHtmlNCBOTree() throws Exception {
        String result = "";
        NCBOAnnot ncboAnnot = ncboWithoutFormat();
        List<String> lConcept = ncboAnnot.getConcept();
        List<String> lGroup = ncboAnnot.getGroup();
        List<String> lLocalOntologyId = ncboAnnot.getLocalOntologyId();
        List<String> lFullId = ncboAnnot.getFullId();
        List<String> lIsDirect = ncboAnnot.getIsDirect();
        List<String> lPreferredName = ncboAnnot.getPreferredName();
        //Add value just if it is not repeated
        List<Integer> lNoRepeated;
        if (lev.equals("0")) {
            List<String> ltemp = new ArrayList<String>();
            for (int j = 0; j < lConcept.size(); j++) {
                ltemp.add(lConcept.get(j) + " " + lLocalOntologyId.get(j));
            }
            lNoRepeated = remRepeated(ltemp);
            //Paint results		
            for (int k = 0; k < lNoRepeated.size(); k++) {
                int i = lNoRepeated.get(k);
                //source, localUrl, url
                String[] data = compoundNCBOData(lConcept.get(i), lLocalOntologyId.get(i), lFullId.get(i));
                result += insertHtmlItem(lConcept.get(i), "en", data[0], lGroup.get(i),
                        data[2], data[1]);
            }
        } else {
            List<String> ltemp = new ArrayList<String>();
            for (int j = 0; j < lPreferredName.size(); j++) {
                ltemp.add(lPreferredName.get(j) + " " + lLocalOntologyId.get(j));
            }
            lNoRepeated = remRepeated(ltemp);
            //Paint results		
            for (int k = 0; k < lNoRepeated.size(); k++) {
                int i = lNoRepeated.get(k);
                String[] data = compoundNCBOData(lPreferredName.get(i), lLocalOntologyId.get(i), lFullId.get(i));
                if (lIsDirect.get(i).equals("true")) {
                    result += insertHtmlItem(lPreferredName.get(i) + " <- " + lConcept.get(i),
                            "en", data[0], lGroup.get(i), data[2], data[1]);
                } else {
                    result += insertHtmlItem(lPreferredName.get(i) + " -> " + lConcept.get(i),
                            "en", data[0], lGroup.get(i), data[2], data[1]);
                }
            }
        }
        return result;
    }
    
    /**
     * Get html formated results from dbpedia query
     * @return String (html, tr of table)
     * @throws Exception .
     */
    private String getHtmlDBPediaTree() throws Exception {
        String result = "";
        List<DBPediaAnnot> dbPediaAnnotations = dbpediaWithoutFormat();
        if (dbPediaAnnotations != null && !dbPediaAnnotations.isEmpty()) {
            for (DBPediaAnnot annot : dbPediaAnnotations) {
                 String url = "";
                if (!annot.getLanguage().toLowerCase().equals("en")) {
                    url += annot.getSpecificUrl();
                } else {
                    url += annot.getGenericUrl();
                }
                if (url == null || url.trim().equals("")) {
                    url += annot.getGenericUrl();
                }
                result += insertHtmlItem(annot.getLabel(), annot.getLanguage(), DBPediaAnnot.PAGE_DBPEDIA, annot.getTypeText(),
                        url, "dbpedia.jsp?term=" + URLEncoder.encode(annot.getLabel(), "UTF-8")+ "&lan=" + annot.getLanguage());
            }
        }
        return result;
    }

    private String insertHtmlItem(String name, String lan, String src,
            String group, String url, String localUrl) {
        String result = "<tr>";
        result += "<td>" + lan + "</td>";
        result += "<td>";
        result += "<a href='" + localUrl + "' target=_blank>" + name + "</a>";
        result += "</td>";
        result += "<td>" + src + "</td>";
        result += "<td>" + group + "</td>";
        result += "<td>";
        if (src.contains("Snomed")) {
            String result1 = "<a href='" + Constants.MLP_CONNECT_SNOMED + "&"
                    + Constants.MLP_CONNECT_TERM_PARAM + "=" + url + "&"
                    + Constants.MLP_CONNECT_LAN_PARAM + "=en"
                    + "' target=_blank>Go to MP Connect (" + src + ")</a>";
            String result2 = "<a href='" + Constants.MLP_CONNECT_SNOMED + "&"
                    + Constants.MLP_CONNECT_TERM_PARAM + "=" + url + "&"
                    + Constants.MLP_CONNECT_LAN_PARAM + "=sp"
                    + "' target=_blank>Ir a MP Connect (" + src + ")</a>";
            result += (lan.equals("en")) ? result1 + " | " + result2 : result2 + " | " + result1;
        } else if (src.equals("MedlinePlus")) {
            if (lan.equals("en")) {
                result += "<a href='" + url + "' target=_blank>Go to " + src + "</a>";
                String urlsp = url.replaceFirst("([a-z]*?\\.html)", "spanish/$0");
                result += " | <a href='" + urlsp + "' target=_blank>Ir a " + src + "</a>";
            } else {
                result += "<a href='" + url + "' target=_blank>Ir a " + src + "</a>";
                result += " | <a href='" + url.replace("/spanish", "") + "' target=_blank>Go to " + src + "</a>";
            }
        } else if (src.equals("DBPedia")) {
            result += "<a href='" + url + "' target=_blank>" + DBPediaAnnot.getAlternativeTextUrl(DBPediaAnnot.PAGE_DBPEDIA, lan) + "</a>";
        } else {
            result += "<a href='" + url + "' target=_blank>Go to " + src;
        }
        result += "</td>";
        result += "</tr>";
        return result;
    }

    public void getXmlLocalTree() throws Exception {
        localDbpediaSearch = false;
        if (localSrc != null && localSrc.length > 0) {
            for (String localSrc1 : localSrc) {
                if (localSrc1.toLowerCase().equals("dbpedia")) {
                    localDbpediaSearch = true;
                }
            }
            boolean dbPediaFound = false;
            //SecondSearch (firstTime=true)
            GateGazMed gateAnnot = gateWithoutFormat(true);
            Vector<String> vMinor = gateAnnot.getMinor();
            Vector<List<String>> vAnnot = gateAnnot.getAnnot();
            Vector<List<String>> vAnnotLan = gateAnnot.getAnnotLan();
            Vector<List<String>> vAnnotSource = gateAnnot.getAnnotSource();
            Vector<List<String>> vAnnotGroup = gateAnnot.getAnnotGroup();
            Vector<List<String>> vAnnotFrom = gateAnnot.getAnnotFrom();
            Vector<List<String>> vAnnotTo = gateAnnot.getAnnotTo();
            Vector<List<String>> vAnnotUrl = gateAnnot.getAnnotUrl();
            for (int i = 0; i < vMinor.size(); i++) {
                for (int j = 0; j < vAnnot.get(i).size(); j++) {
                    String url = "";
                    if (vMinor.get(i).contains("dbpedia")) {
                        dbPediaFound = true;
                        url += "dbpedia.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8") + "&lan=" + vAnnotLan.get(i).get(j) + "&type=" + DBPediaAnnot.getType(vAnnotGroup.get(i).get(j));
                    } else {
                        url += "medlineplus.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8");
                    }
                    insertXmlItem(vAnnotSource.get(i).get(j), vAnnot.get(i).get(j),
                        vAnnot.get(i).get(j), "true", vAnnotGroup.get(i).get(j), vAnnotLan.get(i).get(j),
                        vAnnotFrom.get(i).get(j), vAnnotTo.get(i).get(j), vAnnotUrl.get(i).get(j), url);
                }
            }
            //if there is a search over dbpedia and first seacrh (en and sp) not found anything
            //try to look up a second time using all languages.
            if (localDbpediaSearch && !dbPediaFound) {
                initXmlDocument();
                //SecondSearch (firstTime=false)
                gateAnnot = gateWithoutFormat(false);
                vMinor = gateAnnot.getMinor();
                vAnnot = gateAnnot.getAnnot();
                vAnnotLan = gateAnnot.getAnnotLan();
                vAnnotSource = gateAnnot.getAnnotSource();
                vAnnotGroup = gateAnnot.getAnnotGroup();
                vAnnotFrom = gateAnnot.getAnnotFrom();
                vAnnotTo = gateAnnot.getAnnotTo();
                vAnnotUrl = gateAnnot.getAnnotUrl();
                for (int i = 0; i < vMinor.size(); i++) {
                    for (int j = 0; j < vAnnot.get(i).size(); j++) {
                        String url = "";
                        if (vMinor.get(i).contains("dbpedia")) {
                            url += "dbpedia.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8") + "&lan=" + vAnnotLan.get(i).get(j) + "&type=" + DBPediaAnnot.getType(vAnnotGroup.get(i).get(j));
                        } else {
                            url += "medlineplus.jsp?term=" + URLEncoder.encode(vAnnot.get(i).get(j), "UTF-8");
                        }
                        insertXmlItem(vAnnotSource.get(i).get(j), vAnnot.get(i).get(j),
                            vAnnot.get(i).get(j), "true", vAnnotGroup.get(i).get(j), vAnnotLan.get(i).get(j),
                            vAnnotFrom.get(i).get(j), vAnnotTo.get(i).get(j), vAnnotUrl.get(i).get(j), url);
                    }
                }
            }
        }
    }

    /**
     * Get the XmlRemoteTree depending on the source.
     **/ 
    public void getXmlRemoteTree() throws Exception {
        //ALL REMOTE
        if (src.equals("0") || src.equals("-1")) {
            //NCBO Annotation
            try {
                getXmlNCBOTree();
            } catch (Exception ex) {
                System.out.println("ERROR GETTING XML NCBO TREE " + ex.getMessage());
            }
            //DBPedia Annotation
            try {
                getXmlDBPediaTree();
            } catch (Exception ex) {
                System.out.println("ERROR GETTING XML DBPEDIA TREE " + ex.getMessage());
            }
            //ADD HERE NEW ANNOTATION SOURCES
        //NCBO Annotation
        } else if (src.equals("2") || src.equals("3")) {
            getXmlNCBOTree();
        //DBPedia REMOTE
        } else if (src.equals("4")) {
            getXmlDBPediaTree();
        }
        //ADD HERE NEW ANNOTATION SOURCES
    }
    
    /**
     * Get the XML DBPedia Tree.
     * 
     **/
    private void getXmlDBPediaTree() throws Exception {
        List<DBPediaAnnot> dbPediaAnnotations = dbpediaWithoutFormat();
        if (dbPediaAnnotations != null && !dbPediaAnnotations.isEmpty()) {
            for (DBPediaAnnot annot : dbPediaAnnotations) {
                String url = "";
                if (!annot.getLanguage().toLowerCase().equals("en")) {
                    url += annot.getSpecificUrl();
                } else {
                    url += annot.getGenericUrl();
                }
                if (url == null || url.trim().equals("")) {
                    url += annot.getGenericUrl();
                }
                insertXmlItem("DBPedia", annot.getLabel(),
                        annot.getLabel(), "true", annot.getTypeText(), annot.getLanguage(),
                        "0", "6", url, "dbpedia.jsp?term=" + URLEncoder.encode(annot.getLabel(), "UTF-8")
                                + "&lan=" + annot.getLanguage() + "&type=" + String.valueOf(annot.getType()));
            }
        }
    }
    
    private void getXmlNCBOTree() throws Exception {
        NCBOAnnot ncboAnnot = ncboWithoutFormat();
        List<String> lConcept = ncboAnnot.getConcept();
        List<String> lGroup = ncboAnnot.getGroup();
        List<String> lLocalOntologyId = ncboAnnot.getLocalOntologyId();
        List<String> lFullId = ncboAnnot.getFullId();
        List<String> lIsDirect = ncboAnnot.getIsDirect();
        List<String> lFrom = ncboAnnot.getFrom();
        List<String> lTo = ncboAnnot.getTo();
        List<String> lPreferredName = ncboAnnot.getPreferredName();
        int size = lConcept.size();
        for (int i = 0; i < size; i++) {
            String[] data = compoundNCBOData(lPreferredName.get(i), lLocalOntologyId.get(i),
                    lFullId.get(i));
            insertXmlItem(data[0], lConcept.get(i), lPreferredName.get(i), lIsDirect.get(i),
                    lGroup.get(i), "en", lFrom.get(i), lTo.get(i), data[2], data[1]);
        }
    }

    private void insertXmlItem(String annotType, String concept, String preferred,
            String direct, String groups, String lan, String from, String to, String url,
            String localUrl) {
        // Insert child Item annotType
        Node itemChild = document.createElement("annotation");
        root.appendChild(itemChild);
        //Source attribute
        Attr source = document.createAttribute("source");
        source.setValue(annotType);
        itemChild.getAttributes().setNamedItem(source);
        //Language attribute
        Attr language = document.createAttribute("language");
        language.setValue(lan);
        itemChild.getAttributes().setNamedItem(language);
        // Insert child from with text
        Node item = document.createElement("concept");
        itemChild.appendChild(item);
        Attr neg = document.createAttribute("neg");
        neg.setValue("0");
        item.getAttributes().setNamedItem(neg);
        Node value = document.createTextNode(concept);
        item.appendChild(value);
    //document.createAttribute("neg").setValue("0");
        // Insert child from with text
        item = document.createElement("from");
        itemChild.appendChild(item);
        value = document.createTextNode(from);
        item.appendChild(value);
        // Insert child to with text
        item = document.createElement("to");
        itemChild.appendChild(item);
        value = document.createTextNode(to);
        item.appendChild(value);
        // Insert child preferred with text
        item = document.createElement("preferred");
        itemChild.appendChild(item);
        Attr dir = document.createAttribute("direct");
        dir.setValue(direct);
        item.getAttributes().setNamedItem(dir);
        value = document.createTextNode(preferred);
        item.appendChild(value);
        // Insert child local url with text
        item = document.createElement("localurl");
        itemChild.appendChild(item);
        value = document.createTextNode(localUrl);
        item.appendChild(value);
        // External URLs
        String urlen = url;
        String urlsp = "";
        if (annotType.contains("Snomed")) {
            String urltemp = Constants.MLP_CONNECT_SNOMED + "&"
                    + Constants.MLP_CONNECT_TERM_PARAM + "=" + url + "&" + Constants.MLP_CONNECT_LAN_PARAM;
            urlen = urltemp + "=en";
            urlsp = urltemp + "=sp";
        } else if (annotType.equals("MedlinePlus")) {
            if (lan.equals("en")) {
                urlsp = url.replaceFirst("([a-z]*?\\.html)", "spanish/$0");
            } else {
                urlen = url.replace("/spanish", "");
                urlsp = url;
            }
        }
        // Insert child urlen with text
        item = document.createElement("urlen");
        itemChild.appendChild(item);
        value = document.createTextNode(urlen);
        item.appendChild(value);
        // Insert child urlsp with text
        item = document.createElement("urlsp");
        itemChild.appendChild(item);
        value = document.createTextNode(urlsp);
        item.appendChild(value);

        // Insert child groups with text
        item = document.createElement("groups");
        itemChild.appendChild(item);
        value = document.createTextNode(groups);
        item.appendChild(value);
    }

    public void initXmlDocument() throws Exception {
        document = null;
        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        document = builder.newDocument();

        // Root tmt results
        root = (Element) document.createElement("cleim");
        document.appendChild(root);
        // Insert input fields
        Node itemInput = document.createElement("input");
        root.appendChild(itemInput);
        // -> Text
        Node item = document.createElement("text");
        itemInput.appendChild(item);
        Node value = document.createTextNode(text);
        item.appendChild(value);
        // -> onts
        item = document.createElement("remoteonts");
        itemInput.appendChild(item);
        value = document.createTextNode(onts);
        item.appendChild(value);
        // -> lev
        item = document.createElement("lev");
        itemInput.appendChild(item);
        value = document.createTextNode(lev);
        item.appendChild(value);
        // -> localSrc
        item = document.createElement("localSrc");
        itemInput.appendChild(item);
        value = document.createTextNode(Arrays.toString(localSrc));
        item.appendChild(value);
        // -> localLan
        item = document.createElement("localLan");
        itemInput.appendChild(item);
        value = document.createTextNode(Arrays.toString(localLan));
        item.appendChild(value);

    }

    public String getNormalizedXml() throws Exception {
        // Normalizing the DOM
        document.getDocumentElement().normalize();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
        return result.getWriter().toString();
    }

    private String[] compoundNCBOData(String concept, String localOnt, String fullId) {
        String source = "OBA";
        String localUrl = "medlineplus.jsp?term=" + concept;
        String url = fullId;
        if (localOnt.equals("40397")) {
            source += " Medlineplus";
			//localUrl="medlineplus.jsp?term="+concept;
            //url=fullId;
        } else if (localOnt.equals("46116")) {
            source += " Snomed";
            //localUrl="medlineplus.jsp?term="+concept;
            url = fullId.replaceAll(".*/", "");
        }
        source += " (" + localOnt + ")";
        return new String[]{source, localUrl, url};
    }

    private List<Integer> remRepeated(List<String> lOrig) {
        int size = lOrig.size();
        List<Integer> lRes = new ArrayList<Integer>();
        List<String> lResTemp = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            if (!lResTemp.contains(lOrig.get(i))) {
                lRes.add(new Integer(i));
                lResTemp.add(lOrig.get(i));
            }
        }
        return lRes;
    }
}
