/**
 * @author Fernando Aparicio Galisteo UEM - Intelligent System Group
 * (http://orion.esp.uem.es/gsi/)
 */
package com.uem.gsi.cleim.scl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;

import com.uem.gsi.cleim.util.Constants;

public class CallMLP {

    //MEDLINEPLUS Web service parameter defaults
    private String healthTopics = "healthTopics";
    private String term = "";
    private String logic = "+";
    private String retstart = "0";
    private String retmax = "100";

    public String doMLPQuery() {
        String sSource = "";
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod(Constants.MLP_SEARCH);
            String urlenc = URIUtil.encodeQuery("db=" + healthTopics
                    + "&term=" + term + "&retstart=" + retstart + "&retmax=" + retmax);
            urlenc = urlenc.replaceAll("%20", logic);
            method.setQueryString(urlenc);
            // Execute the GET method
            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {
                sSource = method.getResponseBodyAsString();
                method.releaseConnection();
            } else {
                sSource = "<statusCode>" + statusCode + "</statusCode>";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sSource;
    }

    public void setLan(String plan) {
        if (plan.equals("en")) {
            this.healthTopics = "healthTopics";
        } else if (plan.equals("sp")) {
            this.healthTopics = "healthTopicsSpanish";
        }
    }

    public void setTerm(String pterm) {
        this.term = pterm;
    }

    public void setLogic(String plogic) {
        this.logic = plogic;
    }

    public void setRetstart(String pretstart) {
        this.retstart = pretstart;
    }

    public void setRetmax(String pretmax) {
        this.retmax = pretmax;
    }

    public static void main(String[] args) {
        CallMLP cm = new CallMLP();
        cm.setTerm("prostate cancer");
        //cm.setLogic("+OR+");
        System.out.println(cm.doMLPQuery());
    }
}
