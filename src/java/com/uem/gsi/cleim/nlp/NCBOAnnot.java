package com.uem.gsi.cleim.nlp;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uem.gsi.cleim.util.MyDOMParser;

public class NCBOAnnot {

    /**
     * List of annotation
     */
    //private List<String> lOntoAnnot;
    private List<String> lConcept;
    private List<String> lGroup;
    private List<String> lLocalOntologyId;
    private List<String> lFullId;
    private List<String> lIsDirect;
    private List<String> lFrom;
    private List<String> lTo;
    private List<String> lPreferredName;
    private List<String> lContextName;

    public NCBOAnnot() {
  	//List of anotations
        //lOntoAnnot = new ArrayList<String>();
        lConcept = new ArrayList<String>();
        lGroup = new ArrayList<String>();
        lLocalOntologyId = new ArrayList<String>();
        lFullId = new ArrayList<String>();
        lIsDirect = new ArrayList<String>();
        lFrom = new ArrayList<String>();
        lTo = new ArrayList<String>();
        lPreferredName = new ArrayList<String>();
        lContextName = new ArrayList<String>();
    }

    public void runNCBOAnnot(String text, String onts, String lev) throws Exception {
        CallNCBO oNCBO = new CallNCBO();
        oNCBO.setText(text);
        oNCBO.setOntInRes(onts);
        oNCBO.setLevelMax(lev);
        String xml = oNCBO.doRESTQuery();
        //System.out.println(xml);
        org.w3c.dom.Document doc = MyDOMParser.getDocument(xml);
        NodeList listAnnot = doc.getElementsByTagName("annotationBean");
        for (int i = 0; i < listAnnot.getLength(); i++) {
            String from = "", to = "";
            Node annotNode = listAnnot.item(i);
            NodeList listChildAnnot = annotNode.getChildNodes();
            for (int j = 0; j < listChildAnnot.getLength(); j++) {
                Node nodeTemp1 = listChildAnnot.item(j);
                //Concept
                if (nodeTemp1.getNodeName().equals("concept")) {
                    NodeList listChildRes = nodeTemp1.getChildNodes();
                    for (int k = 0; k < listChildRes.getLength(); k++) {
                        Node node1 = listChildRes.item(k);
                        String node1name = node1.getNodeName();
                        //Elements selection
                        if (node1name.equals("localOntologyId")) {
                            lLocalOntologyId.add(node1.getFirstChild().getNodeValue().trim());
                        } else if (node1name.equals("fullId")) {
                            lFullId.add(node1.getFirstChild().getNodeValue().trim());
                        } else if (node1name.equals("preferredName")) {
                            lPreferredName.add(node1.getFirstChild().getNodeValue().trim().toUpperCase());
                        } else if (node1name.equals("semanticTypes")) {
                            NodeList lsemantictypes = node1.getChildNodes();
                            for (int m = 0; m < lsemantictypes.getLength(); m++) {
                                if (lsemantictypes.item(m).getNodeType() == 1) {
                                    NodeList lsemanticDesc = lsemantictypes.item(m).getChildNodes();
                                    for (int n = 0; n < lsemanticDesc.getLength(); n++) {
                                        if (lsemanticDesc.item(n).getNodeName().equals("description")) {
                                            lGroup.add(lsemanticDesc.item(n).getTextContent());
                                        }
                                    }
                                }
                            }
                        }
                    }

                }//Context
                else if (nodeTemp1.getNodeName().equals("context")) {
                    NodeList listChildRes = nodeTemp1.getChildNodes();
                    for (int k = 0; k < listChildRes.getLength(); k++) {
                        Node node1 = listChildRes.item(k);
                        String node1name = node1.getNodeName();
                        //Elements
                        if (node1name.equals("contextName")) {
                            lContextName.add(node1.getFirstChild().getNodeValue().trim());
                        } else if (node1name.equals("isDirect")) {
                            lIsDirect.add(node1.getFirstChild().getNodeValue().trim());
                        } else if (node1name.equals("from")) {
                            from = node1.getFirstChild().getNodeValue().trim();
                        } else if (node1name.equals("to")) {
                            to = node1.getFirstChild().getNodeValue().trim();
                        }

                    }
                }
            }
            int iFrom = new Integer(from) - 1;
            //lConcept.add(text.substring(iFrom,new Integer(to)).toUpperCase());
            lConcept.add(text.substring(iFrom, new Integer(to)).toUpperCase());
            lFrom.add("" + iFrom);
            lTo.add(to);
        }
    }
    /*public List<String> getAnnots(){
     return this.lOntoAnnot;
     }*/

    public List<String> getConcept() {
        return this.lConcept;
    }

    public List<String> getGroup() {
        return this.lGroup;
    }

    public List<String> getLocalOntologyId() {
        return this.lLocalOntologyId;
    }

    public List<String> getFullId() {
        return this.lFullId;
    }

    public List<String> getIsDirect() {
        return this.lIsDirect;
    }

    public List<String> getFrom() {
        return this.lFrom;
    }

    public List<String> getTo() {
        return this.lTo;
    }

    public List<String> getPreferredName() {
        return this.lPreferredName;
    }

    public List<String> getContextName() {
        return this.lContextName;
    }

    public static void main(String args[]) {
        String onts = "40397,46116";
        String lev = "";
        String text = "UNDERLYING MEDICAL CONDITION: "
                + " 56 year old man with history of colon and testicular cancer, pons glioma now"
                + " with significant hilar lymphadeonpathry, pulmonary pathology on CXR."
                + " REASON FOR THIS EXAMINATION: extracto de genciana colirio/gotas oticas"
                + " vagina no hiperestesica  56 year old man with history of colon and testicular cancer, pons glioma now"
                + " Salmonella Irumu SIDA con vision deficiente with significant hilar lymphadeonpathry, pulmonary pathology on CXR."
                + " Suture of wound of forelimb Lumbar chemical sympathectomy"
                + " via fistula mucosa No contraindications for IV contrast  pain back pain aspirin cancer de prostata";
        NCBOAnnot oNcbo = new NCBOAnnot();
        try {
            oNcbo.runNCBOAnnot(text, onts, lev);
            List<String> lConcept = oNcbo.getConcept();
            List<String> lGroup = oNcbo.getGroup();
            List<String> lLocalOntologyId = oNcbo.getLocalOntologyId();
            List<String> lFullId = oNcbo.getFullId();
            List<String> lIsDirect = oNcbo.getIsDirect();
            List<String> lFrom = oNcbo.getFrom();
            List<String> lTo = oNcbo.getTo();
            List<String> lPreferredName = oNcbo.getPreferredName();
            List<String> lContextName = oNcbo.getContextName();

            for (int i = 0; i < lConcept.size(); i++) {
                System.out.println("->Concept: " + lConcept.get(i));
                System.out.println("->Group: " + lGroup.get(i));
                System.out.println("->LocalOntologyId: " + lLocalOntologyId.get(i));
                System.out.println("->FullId: " + lFullId.get(i));
                System.out.println("->IsDirect: " + lIsDirect.get(i));
                System.out.println("->From: " + lFrom.get(i));
                System.out.println("->To: " + lTo.get(i));
                System.out.println("->PreferredName: " + lPreferredName.get(i));
                System.out.println("->ContextName: " + lContextName.get(i));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
