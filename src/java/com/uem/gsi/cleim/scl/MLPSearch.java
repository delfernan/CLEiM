/**
 * @author Fernando Aparicio Galisteo UEM - Intelligent System Group
 * (http://orion.esp.uem.es/gsi/)
 */
package com.uem.gsi.cleim.scl;

import java.net.URLDecoder;
import java.util.List;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uem.gsi.cleim.util.MyDOMParser;

public class MLPSearch {

    /**
     * List of annotation
     */
    private List<String> lRank;
    private List<String> lUrl;
    private List<String> lUrlSp;
    private List<String> lTitle;
    private List<String> lOrganizationName;
    private List<String> lFullSummary;
    private List<String> lMesh;
    private List<String> lGroupName;
    private List<String> lSnippet;
    private int page = 0;
    private int count = 0;
    private int totalpages = 0;

    public MLPSearch() {
        //List of anotations
        lRank = new ArrayList<String>();
        lUrl = new ArrayList<String>();
        lUrlSp = new ArrayList<String>();
        lTitle = new ArrayList<String>();
        lOrganizationName = new ArrayList<String>();
        lFullSummary = new ArrayList<String>();
        lMesh = new ArrayList<String>();
        lGroupName = new ArrayList<String>();
        lSnippet = new ArrayList<String>();
    }

    public void runMPLSearch(String term, String lan) throws Exception {
        CallMLP oMLP = new CallMLP();
        oMLP.setTerm(term);
        System.out.println("term=" + term);
        oMLP.setLan(lan);
        System.out.println("lan=" + lan);
        String xml = oMLP.doMLPQuery();
        Document doc = MyDOMParser.getDocument(xml);
        this.count = new Integer(doc.getElementsByTagName("count").item(0).getFirstChild().getNodeValue());
        if (count > 0) {
			//String file=doc.getElementsByTagName("file").item(0).getFirstChild().getNodeValue();
            //String server=doc.getElementsByTagName("server").item(0).getFirstChild().getNodeValue();
            //retstart=doc.getElementsByTagName("retstart").item(0).getFirstChild().getNodeValue();
            //retmax=doc.getElementsByTagName("retmax").item(0).getFirstChild().getNodeValue();

            NodeList listRes = doc.getElementsByTagName("document");
            for (int j = 0; j < listRes.getLength(); j++) {
                Node annotNode = listRes.item(j);
                String rank = annotNode.getAttributes().getNamedItem("rank").getNodeValue();
                lRank.add(rank);
                String url = annotNode.getAttributes().getNamedItem("url").getNodeValue();
                String urlsp = "";
                if (lan.equals("en")) {
                    url = url.replaceAll("http.+url=", "").replaceAll("&.+", "");
                    urlsp = url.replaceFirst("([a-z]*?\\.html)", "spanish/$0");
                } else if (lan.equals("sp")) {
                    urlsp = url.replaceAll("http.+url=", "").replaceAll("&.+", "");
                    url = url.replaceFirst("spanish/", "");
                }
                lUrl.add(URLDecoder.decode(url, "UTF-8"));
                lUrlSp.add(URLDecoder.decode(urlsp, "UTF-8"));
                NodeList listChildAnnot = annotNode.getChildNodes();
                String sMesh = "", sGroupName = "";
                for (int i = 0; i < listChildAnnot.getLength(); i++) {
                    Node nodeContent = listChildAnnot.item(i);
                    //Concept
                    int type = nodeContent.getNodeType();
                    if (nodeContent.getNodeName().equals("content") && type != Node.TEXT_NODE) {
                        Node nAttr = listChildAnnot.item(i).getAttributes().getNamedItem("name");
                        String attr = nAttr.getNodeValue();
                        String value = (listChildAnnot.item(i).getFirstChild() != null)
                                ? listChildAnnot.item(i).getFirstChild().getNodeValue() : "";
                        //Elements selection
                        if (attr.equals("title")) {
                            value = value.replaceAll("<span class=\"qt[0-9]\">", "").replaceAll("</span>", "");
                            lTitle.add(value);
                        } else if (attr.equals("organizationName")) {
                            lOrganizationName.add(value);
                        } else if (attr.equals("FullSummary")) {
                            lFullSummary.add(value);
                        } else if (attr.equals("mesh")) {
                            value = value.replaceAll("<span class=\"qt[0-9]\">", "").replaceAll("</span>", "");
                            value = "<a href=\"pmresult.jsp?term=" + value + "\" target=_blank>" + value + "</a>";
                            sMesh += (sMesh.equals("")) ? value : "<span> | </span>" + value;
                        } else if (attr.equals("groupName")) {
                            sGroupName = (sGroupName.equals("")) ? value : sGroupName + ", " + value;
                        } else if (attr.equals("snippet")) {
                            lSnippet.add(value);
                        }
                    }
                }
                lMesh.add(sMesh);
                lGroupName.add(sGroupName);
            }
        }
    }

    public int getCount() {
        return this.count;
    }

    public List<String> getRank() {
        return this.lRank;
    }

    public List<String> getUrl() {
        return this.lUrl;
    }

    public List<String> getUrlSp() {
        return this.lUrlSp;
    }

    public List<String> getTitles() {
        return this.lTitle;
    }

    public List<String> getOrganizationName() {
        return this.lOrganizationName;
    }

    public List<String> getFullSummary() {
        return this.lFullSummary;
    }

    public List<String> getMesh() {
        return this.lMesh;
    }

    public List<String> getGroupName() {
        return this.lGroupName;
    }

    public List<String> getSnippet() {
        return this.lSnippet;
    }

    public static void main(String args[]) {
        MLPSearch oMLP = new MLPSearch();
        try {
            oMLP.runMPLSearch("cancer de prostata", "sp");
            List<String> l0 = oMLP.getUrl();
            List<String> l1 = oMLP.getTitles();
            List<String> l2 = oMLP.getRank();
            List<String> l3 = oMLP.getFullSummary();
            List<String> l4 = oMLP.getMesh();
            List<String> l5 = oMLP.getGroupName();
            List<String> l6 = oMLP.getUrlSp();
            //List<String> l6=oMLP.getSnippet();
            String sOut = "";
            for (int i = 0; i < l1.size(); i++) {
                sOut += "<tr>";
                sOut += "<td><b>" + l1.get(i) + "</b>";
                sOut += "<table>";
                sOut += "<tr><td>";
                sOut += "(Rank: " + l2.get(i) + ")<br/>";
                sOut += "</td></tr>";
                sOut += "<tr><td>";
                sOut += "<a href='pmresult.jsp?term=" + l1.get(i)
                        + "' target=_blank>PubMed results</a><br/>";
                sOut += "</td></tr>";
                sOut += "<tr><td>";
                sOut += (l0.get(i).equals("")) ? ""
                        : "<a href='" + l0.get(i)
                        + "' target=_blank>Medlineplus Topics</a><br/>";
                sOut += "</td></tr>";
                sOut += "<tr><td>";
                sOut += (l6.get(i).equals("")) ? ""
                        : "<a href='" + l6.get(i)
                        + "' target=_blank>Topico en Medlineplus</a><br/>";
                sOut += "</td></tr>";
                sOut += "</table>";
                sOut += "</td>";
                sOut += "<td>" + l3.get(i) + "</td>";
                sOut += "<td>" + l4.get(i) + "</td>";
                sOut += "<td>" + l5.get(i) + "</td>";
                sOut += "</tr>";
            }
            System.out.println(sOut);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
