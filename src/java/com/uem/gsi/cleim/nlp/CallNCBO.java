package com.uem.gsi.cleim.nlp;

import java.io.File;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;

import com.uem.gsi.cleim.util.Constants;
import com.uem.gsi.cleim.util.MyDOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class CallNCBO {

    //HTTP REST Web service parameter defaults
    private String text = "";
    //ICD10(44103),Medlineplus(40397),MeSH(42142),Human disease(44172)
    private String ontologiesToKeepInResult = "40397";
    private String levelMax = "0";
    private String longestOnly = "false";
    private String wholeWordOnly = "true";
    private String filterNumber = "false";
    private String stopWords = "null";
    private String withDefaultStopWords = "true";
    private String withSynonyms = "true";
    private String scored = "true";
    private String ontologiesToExpand = "";
    private String semanticTypes = "";
    private String mappingTypes = "all";
    private String format = "xml"; //"text","tabDelimited"

    public CallNCBO() {
    }

    public String doRESTQuery() throws Exception {
        String sSource = "";
        //try {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Constants.NCBO_BASE + Constants.NCBO_ANNOT);

        // Configure the form parameters
        method.addParameter("apikey", Constants.NCBO_APIKEY);
        method.addParameter("email", Constants.NCBO_EMAIL);
        method.addParameter("longestOnly", longestOnly);
        method.addParameter("wholeWordOnly", wholeWordOnly);
        method.addParameter("filterNumber", filterNumber);
        method.addParameter("stopWords", stopWords);
        method.addParameter("withDefaultStopWords", withDefaultStopWords);
        method.addParameter("withSynonyms", withSynonyms);
        method.addParameter("scored", scored);
        method.addParameter("ontologiesToExpand", ontologiesToExpand);
        method.addParameter("ontologiesToKeepInResult", ontologiesToKeepInResult);
        method.addParameter("semanticTypes", semanticTypes);
        method.addParameter("levelMax", levelMax);
        method.addParameter("mappingTypes", mappingTypes);
        method.addParameter("textToAnnotate", text);
      //method.addParameter("format", "asText");
        //method.addParameter("format", "tabDelimited");
        method.addParameter("format", format);

        // Execute the POST method
        int statusCode = client.executeMethod(method);
        if ((statusCode > 199) && (statusCode < 300)) {
            sSource = method.getResponseBodyAsString();
            method.releaseConnection();
            //System.out.println(contents);
        } else {
            //sSource = "<statusCode>" + statusCode + ": " + getStatusCodeDescription(statusCode) + "</statusCode>";
            sSource = statusCode + ": " + getStatusCodeDescription(statusCode);
            throw new Exception(sSource);//System.out.println("status: "+statusCode);
        }
    //} catch( Exception e ) {
        //  e.printStackTrace();
        //}
        return sSource;
    }
    
    private String getStatusCodeDescription(int statusCode) {
        String toReturn = "";
        switch (statusCode) {
            case 200: toReturn="OK; the request was fulfilled.";
            break;
            case 201: toReturn="OK; following a POST command.";
            break;
            case 202: toReturn="OK; accepted for processing, but processing is not completed.";
            break;
            case 203: toReturn="OK; partial information--the returned information is only partial.";
            break;
            case 204: toReturn="OK; no response--request received but no information exists to send back.";
            break;
            case 301: toReturn="Moved--the data requested has a new location and the change is permanent.";
            break;
            case 302: toReturn="Found--the data requested has a different URL temporarily.";
            break;
            case 303: toReturn="Method--under discussion, a suggestion for the client to try another location.";
            break;
            case 304: toReturn="Not Modified--the document has not been modified as expected.";
            break;
            case 400: toReturn="Bad request--syntax problem in the request or it could not be satisfied.";
            break;
            case 401: toReturn="Unauthorized--the client is not authorized to access data.";
            break;
            case 402: toReturn="Payment required--indicates a charging scheme is in effect.";
            break;
            case 403: toReturn="Forbidden--access not required even with authorization.";
            break;
            case 404: toReturn="Not found--server could not find the given resource.";
            break;
            case 500: toReturn="Internal Error--the server could not fulfill the request because of an unexpected condition.";
            break;
            case 501: toReturn="Not implemented--the sever does not support the facility requested.";
            break;
            case 502: toReturn="Server overloaded--high load (or servicing) in progress.";
            break;
            case 503: toReturn="Service Temporarily Unavailable.";
            break;
        }
        return toReturn;
    }

    public void setText(String ptext) {
        this.text = ptext;
    }

    public void setOntInRes(String pontologiesToKeepInResult) {
        this.ontologiesToKeepInResult = pontologiesToKeepInResult;
    }

    public void setLevelMax(String plevelMax) {
        this.levelMax = plevelMax;
    }

    public void setLongestOnly(String plongestOnly) {
        this.longestOnly = plongestOnly;
    }

    public void setWholeWordOnly(String pwholeWordOnly) {
        this.wholeWordOnly = pwholeWordOnly;
    }

    public void setFilterNumber(String pfilterNumber) {
        this.filterNumber = pfilterNumber;
    }

    public void setStopWords(String pstopWords) {
        this.stopWords = pstopWords;
    }

    public void setWithDefaultStopWords(String pwithDefaultStopWords) {
        this.withDefaultStopWords = pwithDefaultStopWords;
    }

    public void setWithSynonyms(String pwithSynonyms) {
        this.withSynonyms = pwithSynonyms;
    }

    public void setScored(String pscored) {
        this.scored = pscored;
    }

    public void setOntologiesToExpand(String pontologiesToExpand) {
        this.ontologiesToExpand = pontologiesToExpand;
    }

    public void setSemanticTypes(String psemanticTypes) {
        this.semanticTypes = psemanticTypes;
    }

    public void setMappingTypes(String pmappingTypes) {
        this.mappingTypes = pmappingTypes;
    }

    public void setFormat(String pformat) {
        this.format = pformat;
    }

    public static void main(String[] args) {
        //System.out.println("********************* NCBO ANNOTATOR CLIENT TEST ************************* \n");
        try {
            CallNCBO oNCBO = new CallNCBO();
            File fClinicalCase = new File("D:/UEM/MedicalMiner/ClinicalCases/CasoClinicoNCBO.txt");
		 //File fClinicalCase=new File("D:/UEM/MedicalMiner/ClinicalCases/CasoClinico20.txt");
            // Read the entire contents of sample.txt
            String text = FileUtils.readFileToString(fClinicalCase);
            oNCBO.setText("diabetes");
		 //System.out.print(oNCBO.doRESTQuery());
            //System.exit(0);
            Document doc = MyDOMParser.getDocument(oNCBO.doRESTQuery());
		 //String elementName = doc.getNodeName();
            //String textInXml=doc.getElementsByTagName("textToAnnotate").item(0).getFirstChild().getNodeValue();
            NodeList listAnnot = doc.getElementsByTagName("annotationBean");

            for (int i = 0; i < listAnnot.getLength(); i++) {
                //Initialize data to show
                String localOntologyId = "", fullId = "", preferredName = "";
                String contextName = "", isDirect = "", from = "", to = "", nameInText = "";
                Node annotNode = listAnnot.item(i);
                //System.out.println(listAnnot.item(i).getNodeName());
                NodeList listChildAnnot = annotNode.getChildNodes();
                for (int j = 0; j < listChildAnnot.getLength(); j++) {
                    Node nodeTemp1 = listChildAnnot.item(j);
				 //System.out.println(nodeTemp1.getNodeName());
                    //Concept
                    if (nodeTemp1.getNodeName().equals("concept")) {
                        //System.out.println(nodeTemp1.getNodeName());
                        NodeList listChildRes = nodeTemp1.getChildNodes();
                        for (int k = 0; k < listChildRes.getLength(); k++) {
                            Node node1 = listChildRes.item(k);
                            String node1name = node1.getNodeName();
                            //Elements selection
                            if (node1name.equals("localOntologyId")) {
                                localOntologyId = node1.getFirstChild().getNodeValue().trim();
                            } else if (node1name.equals("fullId")) {
                                fullId = node1.getFirstChild().getNodeValue().trim();
                            } else if (node1name.equals("preferredName")) {
                                preferredName = node1.getFirstChild().getNodeValue().trim();
                            }
                        }

                    }//Context
                    else if (nodeTemp1.getNodeName().equals("context")) {
                        //System.out.println(nodeTemp1.getNodeName());
                        NodeList listChildRes = nodeTemp1.getChildNodes();
                        for (int k = 0; k < listChildRes.getLength(); k++) {
                            Node node1 = listChildRes.item(k);
                            String node1name = node1.getNodeName();
	  				 //System.out.println(node1.getNodeName()+"="+node1.getNodeValue());
                            //Seleccionar elementos
                            if (node1name.equals("contextName")) {
                                contextName = node1.getFirstChild().getNodeValue().trim();
                            } else if (node1name.equals("isDirect")) {
                                isDirect = node1.getFirstChild().getNodeValue().trim();
                            } else if (node1name.equals("from")) {
                                from = node1.getFirstChild().getNodeValue().trim();
                            } else if (node1name.equals("to")) {
                                to = node1.getFirstChild().getNodeValue().trim();
                            }

                        }/*else if (node1name.equals("term")){
                         if (node1.getFirstChild()!=null && node1.getFirstChild().getFirstChild()!=null)
                         System.out.println("***"+node1.getFirstChild().getNodeName()+"="+node1.getFirstChild().getFirstChild().getNodeValue().trim());
                         }else if (node1name.equals("concept")){
							 
                         }*/

                    }
                }
                nameInText = text.substring(new Integer(from) - 1, new Integer(to));
			 	 //nameInText=textInXml.substring(new Integer(from)-1,new Integer(to));

                System.out.println(localOntologyId + "," + fullId + "," + preferredName + ","
                        + contextName + "," + isDirect + "," + from + "," + to + "," + nameInText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
