/**
 * @author Fernando Aparicio Galisteo
 *
 */
package com.uem.gsi.cleim.nlp;

import gate.Annotation;
import gate.Document;
import gate.Corpus;
import gate.CorpusController;
import gate.AnnotationSet;
import gate.FeatureMap;
import gate.Gate;
import gate.Factory;
import gate.util.*;
import gate.util.persistence.PersistenceManager;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import java.io.File;
import java.io.IOException;

public class GateGazMed {

    /**
     * List of annotation types to write out. If null, write everything as
     * GateXML.
     */
    private String xmlGate = "";
    private String sWebInf = "";
    private List<String> annotTypesToWrite;
    private Vector<String> vAnnotListMinor;
    private Vector<List<String>> vAnnotList;
    private Vector<List<String>> vAnnotListLan; //Language
    private Vector<List<String>> vAnnotListSource; //(Major)
    private Vector<List<String>> vAnnotListGroup;
    private Vector<List<String>> vAnnotListFrom;
    private Vector<List<String>> vAnnotListTo;
    private Vector<List<String>> vAnnotListUrl;

    /**
     * The character encoding to use when loading the docments. If null, the
     * platform default encoding is used.
     */
  //private String encoding = null;
    public GateGazMed(String pWebInf) {
        sWebInf = pWebInf;
        //List of anotations
        annotTypesToWrite = new ArrayList<String>(1);
        annotTypesToWrite.add("Medicine");
        //Init vectors
        vAnnotListMinor = new Vector<String>();
        //Freebase
        vAnnotListMinor.addElement("disease");
        vAnnotListMinor.addElement("symptom");
        vAnnotListMinor.addElement("treatment");
        //Medlineplus
        vAnnotListMinor.addElement("medlineplus");
        //Snomed ct
        vAnnotListMinor.addElement("snomed");
        //DBPedia dbpedia
        vAnnotListMinor.addElement("dbpedia");
        //
        vAnnotList = new Vector<List<String>>(vAnnotListMinor.size());
        vAnnotListLan = new Vector<List<String>>(vAnnotListMinor.size());
        vAnnotListSource = new Vector<List<String>>(vAnnotListMinor.size());
        vAnnotListGroup = new Vector<List<String>>(vAnnotListMinor.size());
        vAnnotListFrom = new Vector<List<String>>(vAnnotListMinor.size());
        vAnnotListTo = new Vector<List<String>>(vAnnotListMinor.size());
        vAnnotListUrl = new Vector<List<String>>(vAnnotListMinor.size());
        for (int i = 0; i < vAnnotListMinor.size(); i++) {
            vAnnotList.insertElementAt(new ArrayList<String>(), i);
            vAnnotListLan.insertElementAt(new ArrayList<String>(), i);
            vAnnotListSource.insertElementAt(new ArrayList<String>(), i);
            vAnnotListGroup.insertElementAt(new ArrayList<String>(), i);
            vAnnotListFrom.insertElementAt(new ArrayList<String>(), i);
            vAnnotListTo.insertElementAt(new ArrayList<String>(), i);
            vAnnotListUrl.insertElementAt(new ArrayList<String>(), i);
        }
    }

    private void initGate() {
        try {
            //System.out.println("gate.home: "+Gate.getGateHome());
            if (Gate.getGateHome() == null) {
                //Gate home, config -> webapp/WEB-INF/gate.xml, plugins -> webapp/WEB-INF/plugins
                File gateHome = new File(sWebInf);
                Gate.setGateHome(gateHome);
                // user config -> webapp/WEB-INF/user-gate.xml 
                Gate.setUserConfigFile(new File(gateHome, "user-gate.xml"));
            }
            //System.out.println("Is Gate initialized? "+Gate.isInitialised());
            if (!Gate.isInitialised()) {
                Gate.init();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runGazetteer(String sDocText, String sGapp) {
        try {
            initGate();
            // load the gapp application
            CorpusController application
                    = //(CorpusController)PersistenceManager.loadObjectFromFile(new File(sWebInf+"/CLEiM.gapp"));
                    (CorpusController) PersistenceManager.loadObjectFromFile(new File(sWebInf + "/" + sGapp));
            // Create a temporally Corpus, process and clear
            Corpus corpus = Factory.newCorpus("Medicine Corpus");
            application.setCorpus(corpus);
            Document doc = Factory.newDocument(sDocText);
            corpus.add(doc);
            application.execute();
            // remove the document from the corpus again
            corpus.clear();

            // Extract annotations
            Iterator<String> annotTypesIt = annotTypesToWrite.iterator();
            while (annotTypesIt.hasNext()) {
                String annotSelected = annotTypesIt.next();
                AnnotationSet annotsOfThisType = doc.getAnnotations(annotSelected);
                if (annotsOfThisType != null) {
                    for (int i = 0; i < annotsOfThisType.size(); i++) {
                        Annotation annot = annotsOfThisType.get(i);
                        if (annot != null) {
                            FeatureMap features = annot.getFeatures();
                            //String anotName=(String)features.get("name");
                            String anotLan = (String) features.get("language");
                            String minorType = (String) features.get("minorType");
                            String majorType = (String) features.get("majorType");
                            String groups = (String) features.get("groups");
                            String url = (String) features.get("url");
                            String from = annot.getStartNode().getOffset().toString();
                            String to = annot.getEndNode().getOffset().toString();
                            String anotName = sDocText.substring(new Integer(from), new Integer(to));
                            int j = vAnnotListMinor.indexOf(minorType);
                            if (j > -1) {
                                vAnnotList.get(j).add(anotName.toUpperCase());
                                vAnnotListLan.get(j).add(anotLan);
                                vAnnotListSource.get(j).add(majorType);
                                vAnnotListGroup.get(j).add(groups);
                                vAnnotListFrom.get(j).add(from);
                                vAnnotListTo.get(j).add(to);
                                vAnnotListUrl.get(j).add(url);
                            }
                        }
                    }
                }
            }
            Factory.deleteResource(doc);
            Factory.deleteResource(application);
        } catch (GateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Vector<String> getMinor() {
        return this.vAnnotListMinor;
    }

    public Vector<List<String>> getAnnot() {
        return this.vAnnotList;
    }

    public Vector<List<String>> getAnnotLan() {
        return this.vAnnotListLan;
    }

    public Vector<List<String>> getAnnotSource() {
        return this.vAnnotListSource;
    }

    public Vector<List<String>> getAnnotGroup() {
        return this.vAnnotListGroup;
    }

    public Vector<List<String>> getAnnotFrom() {
        return this.vAnnotListFrom;
    }

    public Vector<List<String>> getAnnotTo() {
        return this.vAnnotListTo;
    }

    public Vector<List<String>> getAnnotUrl() {
        return this.vAnnotListUrl;
    }

    public String getXmlGate() {
        return this.xmlGate;
    }

    public static void main(String args[]) {
        String path = "WebContent/WEB-INF";
        String sGapp = "CLEiM.gapp";
        String text = "UNDERLYING MEDICAL CONDITION: "
                + " 56 year old man with history of colon and testicular cancer, pons glioma now"
                + " with significant hilar lymphadeonpathry, pulmonary pathology on CXR."
                + " REASON FOR THIS EXAMINATION: extracto de genciana colirio/gotas óticas"
                + " vagina no hiperestésica  56 year old man with history of colon and testicular cancer, pons glioma now"
                + " Salmonella Irumu SIDA con visión deficiente with significant hilar lymphadeonpathry, pulmonary pathology on CXR."
                + " Suture of wound of forelimb Lumbar chemical sympathectomy"
                + " vía fístula mucosa No contraindications for IV contrast  pain back pain aspirin cáncer de próstata";
        GateGazMed gateMed = new GateGazMed(path);
        gateMed.runGazetteer(text, sGapp);
        Vector<String> vMinor = gateMed.getMinor();
        Vector<List<String>> vAnnot = gateMed.getAnnot();
        Vector<List<String>> vAnnotSource = gateMed.getAnnotSource();
        Vector<List<String>> vAnnotGroup = gateMed.getAnnotGroup();
        Vector<List<String>> vAnnotFrom = gateMed.getAnnotFrom();
        Vector<List<String>> vAnnotTo = gateMed.getAnnotTo();
        Vector<List<String>> vAnnotUrl = gateMed.getAnnotUrl();
        for (int i = 0; i < vMinor.size(); i++) {
            System.out.println("Minor: " + vMinor.get(i));
            for (int j = 0; j < vAnnot.get(i).size(); j++) {
                System.out.println("->Annot: " + vAnnot.get(i).get(j));
                System.out.println("->AnnotSource: " + vAnnotSource.get(i).get(j));
                System.out.println("->AnnotGroup: " + vAnnotGroup.get(i).get(j));
                System.out.println("->AnnotFrom: " + vAnnotFrom.get(i).get(j));
                System.out.println("->AnnotTo: " + vAnnotTo.get(i).get(j));
                System.out.println("->AnnotUrl: " + vAnnotUrl.get(i).get(j));
            }
        }
    }
}
