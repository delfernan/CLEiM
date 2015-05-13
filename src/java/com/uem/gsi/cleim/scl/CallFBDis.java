/**
 * @author Fernando Aparicio Galisteo
 *
 */
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

public class CallFBDis {

    private String disease;
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
    private List<String> lSymptom;
    private List<String> lRisk;
    private List<String> lTreatment;

    public CallFBDis(String pdisease) {
        this.disease = pdisease;
        this.query = o();
        this.envelope = o();
        lIDList = new ArrayList<String>();
        lConcept = new ArrayList<String>();
        lArticle = new ArrayList<String>();
        lImage = new ArrayList<String>();
        lFBUrl = new ArrayList<String>();
        lSymptom = new ArrayList<String>();
        lRisk = new ArrayList<String>();
        lTreatment = new ArrayList<String>();
    }

    private JSON doQuery() {
        Freebase freebase = Freebase.getFreebase();
        JSON mqlJsonResult = freebase.mqlread(this.query, this.envelope, null);
        return mqlJsonResult;
    }

    public void queryFB() {
        //Count
        this.query = o(
                "type", "/medicine/disease", "name~=", "*" + this.disease + "*",
                "return", "count"
        );
        JSON json = doQuery();
        this.count = new Integer(json.get("result").toString());
        totalpages = new Integer(count / limit);

        //Search Result
        this.envelope = o("page", this.page, "cursor", true);
        this.query = a(o(
                "type", "/medicine/disease", "name~=", "*" + this.disease + "*", "id", null, "name", null, "limit", this.limit, "sort", "name",
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
            //Symptoms
            String sSymp = "";
            for (int k = 0; k < jsonDetail.get("symptoms").array().size(); k++) {
                sSymp += (sSymp.equals("")) ? "" : "<span> | </span>";
                sSymp += "<a href=\"symptom.jsp?term="
                        + jsonDetail.get("symptoms").get(k).get("name").value() + "\">"
                        + jsonDetail.get("symptoms").get(k).get("name").value() + "</a>\r\n";
            }
            lSymptom.add(sSymp);
            //Treatments
            String sTreat = "";
            for (int k = 0; k < jsonDetail.get("treatments").array().size(); k++) {
                sTreat += (sTreat.equals("")) ? "" : "<span> | </span>";
                sTreat += "<a href=\"treatment.jsp?term="
                        + jsonDetail.get("treatments").get(k).get("name").value() + "\">"
                        + jsonDetail.get("treatments").get(k).get("name").value() + "</a>\r\n";
            }
            lTreatment.add(sTreat);
            //Risk factors
            String sRisk = "";
            for (int k = 0; k < jsonDetail.get("risk_factors").array().size(); k++) {
                sRisk += (sRisk.equals("")) ? "" : "<span> | </span>";
                sRisk += "<a href=\"" + Constants.FREEBASE_VIEW
                        + jsonDetail.get("risk_factors").get(k).get("id").value() + "\">"
                        + jsonDetail.get("risk_factors").get(k).get("name").value() + "</a>\r\n";
            }
            lRisk.add(sRisk);
        }

    }

    private void queryFBDetail(String pid) {
        this.query = o(
                "type", "/medicine/disease", "id", pid, "name", null,
                "symptoms", a(o("id", null, "index", null, "name", null, "optional", true, "sort", "index", "type", "/medicine/symptom")),
                "treatments", a(o("id", null, "index", null, "name", null, "optional", true, "sort", "index", "type", "/medicine/medical_treatment")),
                "risk_factors", a(o("id", null, "index", null, "name", null, "optional", true, "sort", "index", "type", "/medicine/risk_factor"))
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

    public List<String> getSymptoms() {
        return this.lSymptom;
    }

    public List<String> getRisks() {
        return this.lRisk;
    }

    public List<String> getTreatments() {
        return this.lTreatment;
    }

    public static void main(String[] args) {
        CallFBDis h = new CallFBDis("cancer");
        h.setPage(0);
        h.queryFB();
        System.out.println(h.getCount());
        for (int i = 0; i < h.getIDLists().size(); i++) {
            System.out.println("ID: " + h.getIDLists().get(i));
            System.out.println("CONCEPT: " + h.getConcepts().get(i));
            System.out.println("ARTICLE: " + h.getArticles().get(i));
            System.out.println("IMAGE: " + h.getImages().get(i));
            System.out.println("FREEBASE URL: " + h.getFBUrls().get(i));
            System.out.println("SYMPTOMS: " + h.getSymptoms().get(i));
            System.out.println("RISK FACTORS: " + h.getRisks().get(i));
            System.out.println("TREATMENTS: " + h.getTreatments().get(i));
        }
    }

}
