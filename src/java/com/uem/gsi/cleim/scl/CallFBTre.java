package com.uem.gsi.cleim.scl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;
import com.uem.gsi.cleim.util.Constants;

import static com.freebase.json.JSON.o;
import static com.freebase.json.JSON.a;

public class CallFBTre {

    private String treatment;
    private JSON query;
    private JSON envelope;
    private int limit = 10;
    private int page = 0;
    private int count = 0;
    private int totalpages = 0;
    private List<String> lIDList;
    private List<String> lConcept;
    private List<String> lArticle;
    private List<String> lImage;
    private List<String> lFBUrl;
    //private List<String> lContra;
    private List<String> lSideEf;
    private List<String> lUsedTo;
    //private List<String> lTrials;

    public CallFBTre(String ptreatment) {
        this.treatment = ptreatment;
        this.query = o();
        this.envelope = o();
        lIDList = new ArrayList<String>();
        lConcept = new ArrayList<String>();
        lArticle = new ArrayList<String>();
        lImage = new ArrayList<String>();
        lFBUrl = new ArrayList<String>();
        //lContra = new ArrayList<String>();
        lSideEf = new ArrayList<String>();
        lUsedTo = new ArrayList<String>();
        //lTrials = new ArrayList<String>();
    }

    private JSON doQuery() {
        Freebase freebase = Freebase.getFreebase();
        JSON mqlJsonResult = freebase.mqlread(this.query, this.envelope, null);
        return mqlJsonResult;
    }

    public void queryFB() {
        //Count
        this.query = o(
                "type", "/medicine/medical_treatment", "name~=", "*" + this.treatment + "*",
                "return", "count"
        );
        JSON json = doQuery();
        this.count = new Integer(json.get("result").toString());
        totalpages = new Integer(count / limit);

        //Search Result
        this.envelope = o("page", this.page, "cursor", true);
        this.query = a(o(
                "type", "/medicine/medical_treatment", "name~=", "*" + this.treatment + "*", "id", null, "name", null, "limit", this.limit, "sort", "name",
                "/common/topic/article", a(o("guid", null, "limit", 1, "optional", true)),
                "/common/topic/image", a(o("id", null, "limit", 1, "optional", true)))
        );
        JSON jsontemp = doQuery();
        json = jsontemp.get("result");
        int size = json.array().size();
        for (int i = 0; i < size; i++) {
            String id = (String) json.get(i).get("id").value();
            lIDList.add(id);
            lConcept.add((String) json.get(i).get("name").value());
            //get raw data if exists
            lArticle.add(
                    (json.get(i).get("/common/topic/article").array().size() > 0)
                    ? doRawQuery("http://api.freebase.com/api/trans/raw/"
                            + ((String) json.get(i).get("/common/topic/article").get(0).get("guid").value()).substring(1)) : "");
            lImage.add(
                    (json.get(i).get("/common/topic/image").array().size() > 0)
                    ? "http://api.freebase.com/api/trans/image_thumb"
                    + json.get(i).get("/common/topic/image").get(0).get("id").value()
                    : "");
            lFBUrl.add(Constants.FREEBASE_VIEW + id);
            //Semantic detail
            queryFBDetail(id);
            JSON jsonDetail = doQuery().get("result");
			//Contraindications
			/*String sContra="";
             for (int k=0;k<jsonDetail.get("contraindications").array().size();k++){
             sContra+="<a href=\""+Constants.FREEBASE_VIEW+
             jsonDetail.get("contraindications").get(k).get("id").value()+
             "\" target=_blank>"+
             jsonDetail.get("contraindications").get(k).get("name").value()+
             "</a><br>\r\n";
             }
             lContra.add(sContra);
             */
            //Side Effects
            String sSideEf = "";
            for (int k = 0; k < jsonDetail.get("side_effects").array().size(); k++) {
                sSideEf += (sSideEf.equals("")) ? "" : "<span> | </span>";
                sSideEf += "<a href=\"symptom.jsp?term="
                        + jsonDetail.get("side_effects").get(k).get("name").value() + "\">"
                        + jsonDetail.get("side_effects").get(k).get("name").value() + "</a>\r\n";
            }
            lSideEf.add(sSideEf);
            //Used to treat
            String sUsedTo = "";
            for (int k = 0; k < jsonDetail.get("used_to_treat").array().size(); k++) {
                sUsedTo += (sUsedTo.equals("")) ? "" : "<span> | </span>";
                sUsedTo += "<a href=\"disease.jsp?term="
                        + jsonDetail.get("used_to_treat").get(k).get("name").value() + "\">"
                        + jsonDetail.get("used_to_treat").get(k).get("name").value() + "</a>\r\n";
            }
            lUsedTo.add(sUsedTo);
			//Trials
			/*String sTrials="";
             for (int k=0;k<jsonDetail.get("trials").array().size();k++){
             sTrials+="<a href=\""+Constants.FREEBASE_VIEW+
             jsonDetail.get("trials").get(k).get("id").value()+
             "\" target=_blank>"+
             jsonDetail.get("trials").get(k).get("name").value()+
             "</a><br>\r\n";
             }
             lTrials.add(sTrials);
             */

        }

    }

    private void queryFBDetail(String pid) {
        this.query = o(
                "type", "/medicine/medical_treatment", "id", pid, "name", null,
                "side_effects", a(o("id", null, "index", null, "name", null, "optional", true, "sort", "index", "type", "/medicine/symptom")),
                "used_to_treat", a(o("id", null, "index", null, "name", null, "optional", true, "sort", "index", "type", "/medicine/disease"))
        );
    }

    private String doRawQuery(String url) {
        String sSource = "";
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(url);
            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {
                sSource = method.getResponseBodyAsString();
                method.releaseConnection();
            } else {
                sSource += statusCode;
            }
        } catch (Exception e) {
            sSource += e.toString();
        }
        return sSource;
    }

    public void setPage(int pPage) {
        this.page = pPage;
    }

    public int getCount() {
        return this.count;
    }

    public int getTotalPages() {
        return this.totalpages;
    }

    public List<String> getIDLists() {
        return this.lIDList;
    }

    public List<String> getConcepts() {
        return this.lConcept;
    }

    public List<String> getArticles() {
        return this.lArticle;
    }

    public List<String> getImages() {
        return this.lImage;
    }

    public List<String> getFBUrls() {
        return this.lFBUrl;
    }
    /*public List<String> getContras(){
     return this.lContra;
     }*/

    public List<String> getSideEf() {
        return this.lSideEf;
    }

    public List<String> getUsedTo() {
        return this.lUsedTo;
    }
    /*public List<String> getTrials(){
     return this.lTrials;
     }*/

    public static void main(String[] args) {
        CallFBTre h = new CallFBTre("chemotherapy");
        h.setPage(0);
        h.queryFB();
        System.out.println(h.getCount());
        for (int i = 0; i < h.getIDLists().size(); i++) {
            System.out.println("ID: " + h.getIDLists().get(i));
            System.out.println("CONCEPT: " + h.getConcepts().get(i));
            System.out.println("ARTICLE: " + h.getArticles().get(i));
            System.out.println("IMAGE: " + h.getImages().get(i));
            System.out.println("FREEBASE URL: " + h.getFBUrls().get(i));
            //System.out.println("CONTRAINDICATIONS: "+h.getContras().get(i));
            System.out.println("EFFECT OF: " + h.getSideEf().get(i));
            System.out.println("USED TO: " + h.getUsedTo().get(i));
            //System.out.println("TRIALS: "+h.getTrials().get(i));
        }
    }

}
