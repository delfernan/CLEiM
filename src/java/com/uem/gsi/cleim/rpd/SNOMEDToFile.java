/**
 * @author Fernando Aparicio Galisteo UEM - Intelligent System Group
 * (http://orion.esp.uem.es/gsi/)
 */
package com.uem.gsi.cleim.rpd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.uem.gsi.cleim.util.Constants;

import java.util.regex.*;

public class SNOMEDToFile {
    /*private String sWebInf="";
     private int count=0;
     private int countSpanish=0;
     private int countEnglish=0;
     private List<String> lTopicLang;
     private List<String> lID;
     private List<String> lTopicName;
     private List<String> lUrl;
     private List<String> lFullSummary;
     private List<String> lGroupNames;
     private List<ArrayList<String>> lSynonyms;
     private ArrayList<String> lSynonym;
	
     public SNOMEDToFile(String pWebInf){
     sWebInf=pWebInf;
     lTopicLang = new ArrayList<String>();
     lID = new ArrayList<String>();
     lTopicName = new ArrayList<String>();
     lUrl = new ArrayList<String>();
     lFullSummary = new ArrayList<String>();
     lGroupNames = new ArrayList<String>();
     lSynonyms = new ArrayList<ArrayList<String>>();
     }
	
     public void textToList(){
     try {
     Document doc;
     //Local
     if (sWebInf!="")
     doc = MyDOMParser.getDocument(sWebInf+Constants.MLP_XML_VOCAB, 1);
     //Remote
     else
     doc = MyDOMParser.getDocument(Constants.MLP_XML_VOCAB, 0);
     this.count=new Integer(doc.getElementsByTagName("MedicalTopics").item(0).getAttributes().getNamedItem("total").getNodeValue());
     this.countEnglish=new Integer(doc.getElementsByTagName("MedicalTopics").item(0).getAttributes().getNamedItem("totalEnglish").getNodeValue());
     this.countSpanish=new Integer(doc.getElementsByTagName("MedicalTopics").item(0).getAttributes().getNamedItem("totalSpanish").getNodeValue());
			
     NodeList listRes=doc.getElementsByTagName("MedicalTopic");
     String id="",name="",url="",fullsum="",groups="";
     for (int j=0;j<listRes.getLength();j++){
     lSynonym = new ArrayList<String>();
     Node annotNode=listRes.item(j);
     //English or spanish
     lTopicLang.add(annotNode.getAttributes().getNamedItem("langcode").getNodeValue());
     NodeList listChildAnnot=annotNode.getChildNodes();
     for (int i=0;i<listChildAnnot.getLength();i++){
     Node nodeContent=listChildAnnot.item(i);
     if (nodeContent.getNodeName().equals("ID"))
     id=nodeContent.getTextContent();
     if (nodeContent.getNodeName().equals("MedicalTopicName"))
     name=nodeContent.getTextContent();
     if (nodeContent.getNodeName().equals("URL"))
     url=nodeContent.getTextContent();
     if (nodeContent.getNodeName().equals("FullSummary"))
     fullsum=nodeContent.getTextContent();
     //Groups names
     if (nodeContent.getNodeName().equals("Groups")){
     NodeList listGroups=nodeContent.getChildNodes();
     groups="";
     for (int k=0;k<listGroups.getLength();k++){
     if (listGroups.item(k).getNodeType()==1)
     groups+=(groups.equals(""))?
     listGroups.item(k).getChildNodes().item(3).getTextContent():
     " | "+listGroups.item(k).getChildNodes().item(3).getTextContent();
     }
     }
     //Synonyms
     if (nodeContent.getNodeName().equals("Synonyms")){
     NodeList listSynonyms=nodeContent.getChildNodes();
     for (int k=0;k<listSynonyms.getLength();k++){
     if (listSynonyms.item(k).getNodeType()==1)
     lSynonym.add(listSynonyms.item(k).getTextContent());
     }
     }
     }
     lID.add(id);
     lTopicName.add(name);
     lUrl.add(url);
     lFullSummary.add(fullsum);
     lGroupNames.add(groups);
     lSynonyms.add(lSynonym);
     }
     } catch (Exception e) {
     System.out.println("Exception reading medlineplus xml: "+e.toString());
     }
     }

     private void loadListFileLan(String filePath,String lan){
     try {
     Writer out = new BufferedWriter(new OutputStreamWriter(
     new FileOutputStream(filePath), "UTF8"));
     // 	Write english data
     for (int i=0;i<lID.size();i++){
     if (lTopicLang.get(i).equals(lan)){
     //out.write(lTopicName.get(i)+"&name="+lTopicName.get(i)+"&url="+lUrl.get(i)+"&groups="+lGroupNames.get(i)+"\n");
     out.write(lTopicName.get(i)+"&url="+lUrl.get(i)+"&groups="+lGroupNames.get(i)+"\n");
     for (int j=0;j<lSynonyms.get(i).size();j++){
     //out.write(lSynonyms.get(i).get(j)+"&name="+lSynonyms.get(i).get(j)+"&url="+lUrl.get(i)+"&groups="+lGroupNames.get(i)+"\n");
     out.write(lSynonyms.get(i).get(j)+"&url="+lUrl.get(i)+"&groups="+lGroupNames.get(i)+"\n");
						
     }
     }
     }
     out.close();
     } catch (IOException e) {
     e.printStackTrace();
     }
     }

     public static void main(String[] args) {
     try{
     // PUT HERE YOUR ABSOLUTE PATH IF YOU WANT LOCAL PROCESS, KEEP EMPTY IF NOT
     String xmlpath="";
     // PUT HERE YOUR LOCAL WEB-INF PATH TO AUTOMATICALLY UPDATE GATE FILES
     String webinf="WebContent/WEB-INF";
     SNOMEDToFile mlp=new SNOMEDToFile(xmlpath);
     System.out.println("READING XML FILES...");
     mlp.xmlToList();
     System.out.println("Topics number: "+mlp.count+
     " In English: "+mlp.countEnglish+". In Spanish: "+mlp.countSpanish);
     for(int i=0; i<mlp.lID.size();i++){
     System.out.println("lang: "+mlp.lTopicLang.get(i));
     System.out.println("ID: "+mlp.lID.get(i));
     System.out.println("Name: "+mlp.lTopicName.get(i));
     System.out.println("Url: "+mlp.lUrl.get(i));
     System.out.println("FullSummary: "+mlp.lFullSummary.get(i));
     System.out.println("Groups: "+mlp.lGroupNames.get(i));
     for (int j=0;j<mlp.lSynonyms.get(i).size();j++)
     System.out.println("Synonyms: "+mlp.lSynonyms.get(i).get(j));
     }
     System.out.println("LOADING GATE LIST FILE...");
     //TO LOAD DISJOINTED LANGUAGE FILES TO THE TERMS
     mlp.loadListFileLan(webinf+Constants.PATH_MLP_LST_EN, "English");
     mlp.loadListFileLan(webinf+Constants.PATH_MLP_LST_SP, "Spanish");
     //USE THIS TO LOAD ENGLISH AND SPANISH TERMS IN THE SAME LIST
     //mlp.loadListFile(Constants.PATH_MLP_LST);
     System.out.println("DONE");
			
     }catch(Exception e){
     System.out.println("Error: "+e.toString());
     }
     }
     */

    public void replacePattern(String filePathInput, String filePathOutput,
            String sPattern, String sReplacement) {
        try {
            FileReader fr = new FileReader(filePathInput);
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(filePathOutput);
            PrintWriter pw = new PrintWriter(fw);
            String line = null;

            while ((line = br.readLine()) != null) {
                line = line.replaceAll(sPattern, sReplacement);
                pw.write(line);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }

    public static void main(String[] args) {

    }
}
