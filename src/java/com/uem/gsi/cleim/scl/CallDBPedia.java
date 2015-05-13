package com.uem.gsi.cleim.scl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.uem.gsi.cleim.nlp.DBPediaAnnot;
import com.uem.gsi.cleim.util.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Miguel Angel Rosales Navarro
 */
public class CallDBPedia {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        String searchText = "56 year old man with history of colon and testicular cancer, pons glioma now with significant hilar lymphadeonpathry, pulmonary pathology on CXR. \n" +
//                            "\n" +
//                            "REASON FOR THIS EXAMINATION: extracto de genciana colirio/gotas óticas vagina no hiperestésica\n" +
//                            "\n" +
//                            "56 year old man with history of colon and testicular cancer, pons glioma now Salmonella Irumu SIDA con visión deficiente with significant hilar lymphadeonpathry, pulmonary pathology on CXR. Suture of wound of forelimb Lumbar chemical sympathectomy vía fístula mucosa No contraindications for IV contrast  pain back pain aspirin cáncer de próstata";

        String searchText = "cancer";
        try {
            long startTime = System.currentTimeMillis();
            List<DBPediaAnnot> annotations = getDBPediaAnnotations(searchText);
            long endTime = System.currentTimeMillis();
            int numberOfResult = 0;
            if (annotations != null) {
                for (DBPediaAnnot annotation : annotations) {
                    numberOfResult++;
                    System.out.println("********RESULTADO " + numberOfResult + "*****************");
                    System.out.println("-Language: " + annotation.getLanguage());
                    System.out.println("-Label: " + annotation.getLabel());
                    System.out.println("-Specific URL: " + annotation.getSpecificUrl());
                    System.out.println("-Generic URL: " + annotation.getGenericUrl());
                    System.out.println("-Wikipedia URL: " + annotation.getWikipediaUrl());
                    System.out.println("-Type: " + annotation.getType() + "(" + annotation.getTypeText() + ")");
                    System.out.println("******************************************************");
                }
            }
            System.out.println("");
            System.out.println("SEARCH TEXT = " + searchText);
            System.out.println("EXECUTION TIME ELAPSED = " + (endTime - startTime) + " MILLISECONDS.");
            System.out.println("NUMBER OF RESULTS = " + numberOfResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the DBPedia annotations (search for Medicine and Disease Categories)
     *
     * @param searchText - text to search
     * @return List<DBPediaAnnot> - can be null.
     */
    public static List<DBPediaAnnot> getDBPediaAnnotations(String searchText) throws Exception {
        List<DBPediaAnnot> annotations = new ArrayList<DBPediaAnnot>();
        if (searchText != null && !searchText.trim().equals("")) {
            searchText = searchText.replace("\n", " ");
            searchText = searchText.replace("\r", " ");
            searchText = searchText.replace("\t", " ");
            searchText = searchText.replace(",", " ");
            searchText = searchText.replace(".", " ");
            searchText = searchText.replace("/", " ");
            searchText = searchText.replace("-", " ");
            searchText = searchText.replace("_", " ");
            String[] words = searchText.split(" ");
            List<String> lWords = new ArrayList<String>();
            if (words != null && words.length > 0) {
                for(int i=0;i < words.length; i++) {
                    if ((words[i].length() > 3) && (!lWords.contains(words[i]))) {
                        lWords.add(words[i]);
                    }
                }
            }
            if (!lWords.isEmpty()) {
                List<DBPediaAnnot> medicineList = getDBPediaMedicineList(lWords);
                List<DBPediaAnnot> diseaseList = getDBPediaDiseaseList(lWords);
                if (medicineList != null && !medicineList.isEmpty()) {
                    for (DBPediaAnnot annotation : medicineList) {
                        annotations.add(annotation);
                    }
                }
                if (diseaseList != null && !diseaseList.isEmpty()) {
                    for (DBPediaAnnot annotation : diseaseList) {
                        annotations.add(annotation);
                    }
                }
            }
        }
        return (annotations.isEmpty() ? null : annotations);
    }

    /**
     * Get the topics for an specific DBPedia annotation
     *
     * @param searchText - text to search.
     * @param lan - language.
     * @param type - type to search(disease, medicine,...)
     * @return List<DBPediaAnnot> - can be null.
     */
    public static List<DBPediaAnnot> getDBPediaAnnotationTopics(String searchText, String lan, String type) throws Exception {
        List<DBPediaAnnot> annotationTopics = new ArrayList<DBPediaAnnot>();
        if (searchText != null && !searchText.trim().equals("")) {
            if (Integer.valueOf(type) == DBPediaAnnot.TYPE_MEDICINE) {
                annotationTopics = getDBPediaMedicineTopics(searchText, lan);
            } else if (Integer.valueOf(type) == DBPediaAnnot.TYPE_DISEASE) {
                annotationTopics = getDBPediaDiseaseTopics(searchText, lan);
            }
        }
        return annotationTopics;
    }

    /**
     * Get the DBPedia Medicine annotations.
     * @param searchText - text to search
     * @param normalizedText - text without symbols.
     * @return List<DBPediaAnnot> - can be null.
     */
    private static List<DBPediaAnnot> getDBPediaMedicineList(List<String> lWords) throws Exception {
        List<DBPediaAnnot> annotations = new ArrayList<DBPediaAnnot>();
        String sparqlQueryString
                = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "SELECT DISTINCT ?medicine ?medicine_label  ?medicine_sameas WHERE {\n"
                + "?medicine rdf:type <http://dbpedia.org/ontology/Medicine>.\n"
                + "?medicine foaf:name ?medicine_name.\n"
                + "?medicine rdfs:label ?medicine_label.\n"
                + "?medicine owl:sameAs ?medicine_sameas \n"
                 + "FILTER (";
        for (String word : lWords) {
            sparqlQueryString +="(regex(?disease_label, \"" + word + "\", \"i\")) || (regex(?disease_name, \"" + word + "\", \"i\")) || ";
        }
        //remove last " || "
        sparqlQueryString = sparqlQueryString.substring(0, sparqlQueryString.length() - 4);
        sparqlQueryString +=")\n "
                + "}";

        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(Constants.DBPEDIA_SEARCH, query);

        ResultSet results = qexec.execSelect();
        Map<String, DBPediaAnnot> mapAnnotations = new HashMap<String, DBPediaAnnot>();
        for (; results.hasNext();) {
            QuerySolution sol = (QuerySolution) results.next();
            String label = sol.get("?medicine_label").toString();
            if (label != null && !label.trim().equals("")) {
                DBPediaAnnot annotation = mapAnnotations.get(label);
                if (annotation == null) {
                    annotation = new DBPediaAnnot();
                    String labelFinal = label;
                    String lang = "en";
                    int indexOfLang = label.lastIndexOf("@");
                    if (indexOfLang > 0) {
                        labelFinal = label.substring(0, indexOfLang);
                        lang = label.substring(indexOfLang + 1, label.length());
                    }
                    annotation.setLabel(labelFinal);
                    annotation.setLanguage(lang);
                    annotation.setGenericUrl(sol.get("?medicine").toString());
                    annotation.setType(DBPediaAnnot.TYPE_MEDICINE);
                    mapAnnotations.put(label, annotation);
                    annotations.add(annotation);
                }
                if (annotation.getSpecificUrl() == null || annotation.getSpecificUrl().trim().equals("")) {
                    String specificUrl = sol.get("?medicine_sameas").toString();
                    //COMPABILITY WITH OTHER SOURCES SPANISH LANGUAGE FORMAT
                    String langAux = annotation.getLanguage();
                    if (langAux.equals("sp")) {
                        langAux = "es";
                    }
                    if (specificUrl.startsWith("http://" + langAux + ".dbpedia.org")) {
                        annotation.setSpecificUrl(specificUrl);
                    }
                }
            }
        }
        qexec.close();
        return (annotations.isEmpty() ? null : annotations);
    }

    /**
     * Get the DBPedia Disease annotations.
     *
     * @param searchText - text to search
     * @param normalizedText - text without symbols.
     * @return List<DBPediaAnnot> - can be null.
     */
    private static List<DBPediaAnnot> getDBPediaDiseaseList(List<String> lWords) throws Exception {
        List<DBPediaAnnot> annotations = new ArrayList<DBPediaAnnot>();
        String sparqlQueryString
                = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "SELECT DISTINCT ?disease ?disease_label  ?disease_sameas WHERE {\n"
                + "?disease rdf:type <http://dbpedia.org/ontology/Disease>.\n"
                + "?disease foaf:name ?disease_name.\n"
                + "?disease rdfs:label ?disease_label.\n"
                + "?disease owl:sameAs ?disease_sameas \n"
                + "FILTER (";
        for (String word : lWords) {
            sparqlQueryString +="(regex(?disease_label, \"" + word + "\", \"i\")) || (regex(?disease_name, \"" + word + "\", \"i\")) || ";
        }
        sparqlQueryString = sparqlQueryString.substring(0, sparqlQueryString.length() - 4);
        sparqlQueryString +=")\n "
                + "}";

        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(Constants.DBPEDIA_SEARCH, query);

        ResultSet results = qexec.execSelect();
        Map<String, DBPediaAnnot> mapAnnotations = new HashMap<String, DBPediaAnnot>();
        for (; results.hasNext();) {
            QuerySolution sol = (QuerySolution) results.next();
            String label = sol.get("?disease_label").toString();
            if (label != null && !label.trim().equals("")) {
                DBPediaAnnot annotation = mapAnnotations.get(label);
                if (annotation == null) {
                    annotation = new DBPediaAnnot();
                    String labelFinal = label;
                    String lang = "en";
                    int indexOfLang = label.lastIndexOf("@");
                    if (indexOfLang > 0) {
                        labelFinal = label.substring(0, indexOfLang);
                        lang = label.substring(indexOfLang + 1, label.length());
                    }
                    annotation.setLabel(labelFinal);
                    annotation.setLanguage(lang);
                    annotation.setGenericUrl(sol.get("?disease").toString());
                    annotation.setType(DBPediaAnnot.TYPE_DISEASE);
                    mapAnnotations.put(label, annotation);
                    annotations.add(annotation);
                }
                if (annotation.getSpecificUrl() == null || annotation.getSpecificUrl().trim().equals("")) {
                    String specificUrl = sol.get("?disease_sameas").toString();
                    //COMPABILITY WITH OTHER SOURCES SPANISH LANGUAGE FORMAT
                    String langAux = annotation.getLanguage();
                    if (langAux.equals("sp")) {
                        langAux = "es";
                    }
                    if (specificUrl.startsWith("http://" + langAux + ".dbpedia.org")) {
                        annotation.setSpecificUrl(specificUrl);
                    }
                }
            }
        }
        qexec.close();
        return (annotations.isEmpty() ? null : annotations);
    }

    /**
     * Get the topics for an specific DBPedia Medicine ontology.
     * @param searchText - text to search
     * @param language - language to search.
     * @return List<DBPediaAnnot> - can be null.
     */
    private static List<DBPediaAnnot> getDBPediaMedicineTopics(String searchText, String language) throws Exception {
        if (language == null) {
            language = "";
        }
        if (language.toLowerCase().equals("sp")) {
            language = "es";
        }
        List<DBPediaAnnot> annotations = new ArrayList<DBPediaAnnot>();
        String searchTextFinal = queryText(searchText);
        String sparqlQueryString
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "SELECT DISTINCT ?medicine_label ?medicine_comment ?medicine_abstract WHERE {\n"
                + "?medicine rdf:type <http://dbpedia.org/ontology/Medicine>.\n"
                + "?medicine rdfs:label ?medicine_label.\n"
                + "?medicine rdfs:comment ?medicine_comment.\n"
                + "OPTIONAL { ?medicine dbo:abstract ?medicine_abstract }\n"
                + "FILTER ((regex(?medicine_comment, \"" + searchTextFinal + "\", \"i\") "
                + " && LANG(?medicine_label)='" + language + "' && LANG(?medicine_comment)='" + language + "'"
                + " && LANG(?medicine_abstract)='" + language + "'))\n"
                + "}";

        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(Constants.DBPEDIA_SEARCH, query);

        ResultSet results = qexec.execSelect();
        for (; results.hasNext();) {
            QuerySolution sol = (QuerySolution) results.next();
            DBPediaAnnot annotation = new DBPediaAnnot();
            annotation.setType(DBPediaAnnot.TYPE_MEDICINE);
            String label = sol.get("?medicine_label").toString();
            if (label != null && !label.trim().equals("")) {
                String labelFinal = label;
                String lang = "en";
                int indexOfLang = label.lastIndexOf("@");
                if (indexOfLang > 0) {
                    labelFinal = label.substring(0, indexOfLang);
                    lang = label.substring(indexOfLang + 1, label.length());
                }
                annotation.setLabel(labelFinal);
                annotation.setLanguage(lang);
            }
            if (sol.get("?medicine_comment") != null) {
                String comment = sol.get("?medicine_comment").toString();
                int indexOfLang = comment.lastIndexOf("@");
                if (indexOfLang > 0) {
                    comment = comment.substring(0, indexOfLang);
                }
                annotation.setComment(comment);
            } else {
                annotation.setComment("");
            }
            if (sol.get("?medicine_abstract") != null) {
                String labstract = sol.get("?medicine_abstract").toString();
                int indexOfLang = labstract.lastIndexOf("@");
                if (indexOfLang > 0) {
                    labstract = labstract.substring(0, indexOfLang);
                }
                annotation.setAbstract(labstract);
            } else {
                annotation.setAbstract("");
            }
            annotations.add(annotation);
        }
        qexec.close ();
        return annotations;
    }

    /**
     * Get a complete DBPedia Disease specific annotation.
     * @param searchText - text to search
     * @param language - language to search.
     * @return List<DBPediaAnnot> - can be null.
     */
    private static List<DBPediaAnnot> getDBPediaDiseaseTopics(String searchText, String language) throws Exception {
        if (language == null) {
            language = "";
        }
        if (language.toLowerCase().equals("sp")) {
            language = "es";
        }
        List<DBPediaAnnot> annotations = new ArrayList<DBPediaAnnot>();
        String searchTextFinal = queryText(searchText);
        String sparqlQueryString
                = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "SELECT DISTINCT ?disease_label ?disease_comment ?disease_abstract WHERE {\n"
                + "?disease rdf:type <http://dbpedia.org/ontology/Disease>.\n"
                + "?disease rdfs:label ?disease_label.\n"
                + "?disease rdfs:comment ?disease_comment.\n"
                + "OPTIONAL { ?disease dbo:abstract ?disease_abstract }\n"
                + "FILTER ((regex(?disease_comment, \"" + searchTextFinal + "\", \"i\") "
                + " && LANG(?disease_label)='" + language + "' && LANG(?disease_comment)='" + language + "'"
                + " && LANG(?disease_abstract)='" + language + "'))\n"
                + "}";

        Query query = QueryFactory.create(sparqlQueryString);
        QueryExecution qexec = QueryExecutionFactory.sparqlService(Constants.DBPEDIA_SEARCH, query);

        ResultSet results = qexec.execSelect();
        for (; results.hasNext();) {
            QuerySolution sol = (QuerySolution) results.next();
            DBPediaAnnot annotation = new DBPediaAnnot();
            annotation.setType(DBPediaAnnot.TYPE_DISEASE);
            String label = sol.get("?disease_label").toString();
            if (label != null && !label.trim().equals("")) {
                String labelFinal = label;
                String lang = "en";
                int indexOfLang = label.lastIndexOf("@");
                if (indexOfLang > 0) {
                    labelFinal = label.substring(0, indexOfLang);
                    lang = label.substring(indexOfLang + 1, label.length());
                }
                annotation.setLabel(labelFinal);
                annotation.setLanguage(lang);
            }
            if (sol.get("?disease_comment") != null) {
                String comment = sol.get("?disease_comment").toString();
                int indexOfLang = comment.lastIndexOf("@");
                if (indexOfLang > 0) {
                    comment = comment.substring(0, indexOfLang);
                }
                annotation.setComment(comment);
            } else {
                annotation.setComment("");
            }
            if (sol.get("?disease_abstract") != null) {
                String labstract = sol.get("?disease_abstract").toString();
                int indexOfLang = labstract.lastIndexOf("@");
                if (indexOfLang > 0) {
                    labstract = labstract.substring(0, indexOfLang);
                }
                annotation.setAbstract(labstract);
            } else {
                annotation.setAbstract("");
            }
            annotations.add(annotation);
        }
        qexec.close ();
        return annotations;
    }
    
    /**
     * Converts a text to a new text without '.
     * @param searchText .
     * @return String .
     */
    private static String queryText(String searchText) {
        String queryText = searchText;
        queryText = queryText.replaceAll("'", "\'");
        return queryText;
    }
    
}
