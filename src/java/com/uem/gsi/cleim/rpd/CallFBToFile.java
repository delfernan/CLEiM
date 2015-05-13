package com.uem.gsi.cleim.rpd;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import net.sf.json.*;

import com.freebase.api.Freebase;
import com.freebase.json.JSON;
import com.uem.gsi.cleim.util.Constants;

import static com.freebase.json.JSON.o;
import static com.freebase.json.JSON.a;

public class CallFBToFile {

    private JSON query;
    private JSON envelope;
    //Disease, symptom or medical_treatment
    private String fbparam;
    //Search limit
    private int limit;
    //Pagination
    private int page;
    //Language
    private String lan;

    public CallFBToFile(String pfbparam) {
        this.fbparam = pfbparam;
        this.query = o();
        this.envelope = o();
        this.limit = 2000;
        this.page = 0;
        this.lan = "en";
    }

    public String doQuery(String fbtype) {
        if (fbtype.equals("Disease")) {
            queryFBDisease();
        } else if (fbtype.equals("Symptom")) {
            queryFBSymptom();
        } else if (fbtype.equals("Treatment")) {
            queryFBTreatment();
        }

        Freebase freebase = Freebase.getFreebase();
        JSON mqlJsonResult = freebase.mqlread(this.query, this.envelope, null);
        return mqlJsonResult.toJSONString();
    }

    public void queryFBDisease() {
        this.envelope = o("page", this.page, "cursor", true, "lang", "/lang/" + this.lan);
        this.query = a(o(
                "type", "/medicine/disease", "name~=", "*" + this.fbparam + "*",
                "id", null, "name", null, "optional", false, "limit", this.limit, "sort", "name",
                "/common/topic/article", o("guid", null, "limit", 1, "optional", true)
        )
        );
    }

    public void queryFBSymptom() {
        this.envelope = o("page", this.page, "cursor", true, "lang", "/lang/" + this.lan);
        this.query = a(o(
                "type", "/medicine/symptom", "name~=", "*" + this.fbparam + "*",
                "id", null, "name", null, "limit", this.limit, "sort", "name",
                "/common/topic/article", o("guid", null, "limit", 1, "optional", true))
        );
    }

    public void queryFBTreatment() {
        this.envelope = o("page", this.page, "cursor", true, "lang", "/lang/" + this.lan);
        this.query = a(o(
                "type", "/medicine/medical_treatment", "name~=", "*" + this.fbparam + "*",
                "id", null, "name", null, "limit", this.limit, "sort", "name",
                "/common/topic/article", o("guid", null, "limit", 1, "optional", true))
        );
    }

    public void loadListFile(String filePath, JSONArray jsonres, String group, boolean append) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, append), "UTF8"));
            // 	Write diseases to the file
            for (int i = 0; i < jsonres.size(); i++) {
                JSONObject jsonresobj = jsonres.getJSONObject(i);
                //out.write(jsonresobj.get("name")+"&name="+jsonresobj.get("name")+"&url="+Constants.FREEBASE_VIEW+jsonresobj.get("id")+"&groups="+group+"\n");
                out.write(jsonresobj.get("name") + "&url=" + Constants.FREEBASE_VIEW + jsonresobj.get("id") + "&groups=" + group + "\n");
            }
            // Close file writer
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadResults(String fbpath, String fbtype) {
        String jsonquery = "";
        JSONArray jsonres;
        try {
            this.page = 0;
            jsonquery = doQuery(fbtype);
            jsonres = JSONObject.fromObject(jsonquery).getJSONArray("result");
            System.out.println("Writing " + fbtype + " to file '" + fbpath + "'");
            loadListFile(fbpath, jsonres, fbtype, false);
            while (JSONObject.fromObject(jsonquery).getString("cursor") != "false") {
                this.page++;
                try {
                    jsonquery = doQuery(fbtype);
                    jsonres = JSONObject.fromObject(jsonquery).getJSONArray("result");
                    System.out.println("Appending " + fbtype + " to file '" + fbpath + "'");
                    loadListFile(fbpath, jsonres, fbtype, true);
                } catch (Exception ex) {
                    System.out.println("Exception: " + ex.toString());
                    this.page--;
                    System.out.println("Retrying page: " + this.page);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }

    public static void main(String[] args) {
        String fbparam = "";
        String webinf = "WebContent/WEB-INF";
        CallFBToFile h = new CallFBToFile(fbparam);
        try {
            //English files
            h.lan = "en";
            System.out.println("Quering Freebase diseases in english");
            h.loadResults(webinf + Constants.PATH_DISEASE_LST_EN, "Disease");
            System.out.println("Quering Freebase symptoms in english");
            h.loadResults(webinf + Constants.PATH_SYMPTOM_LST_EN, "Symptom");
            System.out.println("Quering Freebase treatments in english");
            h.loadResults(webinf + Constants.PATH_TREATMENT_LST_EN, "Treatment");
            //Spanish files
            h.lan = "es";
            System.out.println("Quering Freebase diseases in spanish");
            h.loadResults(webinf + Constants.PATH_DISEASE_LST_SP, "Disease");
            System.out.println("Quering Freebase symptoms in english");
            h.loadResults(webinf + Constants.PATH_SYMPTOM_LST_SP, "Symptom");
            System.out.println("Quering Freebase treatments in spanish");
            h.loadResults(webinf + Constants.PATH_TREATMENT_LST_SP, "Treatment");
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        System.out.println("done");
    }

}
