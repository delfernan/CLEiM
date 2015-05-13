package com.uem.gsi.cleim.scl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.commons.httpclient.util.URIUtil;

import com.uem.gsi.cleim.util.Constants;

import gov.nih.nlm.ncbi.www.soap.eutils.*;

public class CallPMSoap {

    private String term;
    private String lan = "";
    private String logic = "+";
    private int limit = 20;
    private int pmPage = 0;
    private int count = 0;
    private int totalpages = 0;
    private String idList = "";
    private List<String> lIDList;
    private List<String> lTitle;
    private List<String> lPubDate;
    private List<String> lAuthors;
    private List<String> lAbstractUrl;

    public CallPMSoap() {
        lIDList = new ArrayList<String>();
        lTitle = new ArrayList<String>();
        lPubDate = new ArrayList<String>();
        lAuthors = new ArrayList<String>();
        lAbstractUrl = new ArrayList<String>();
    }

    private void queryPMID() {
        try {
            EUtilsServiceStub stub = new EUtilsServiceStub();
            EUtilsServiceStub.ESearchRequest eSearchRequest = new EUtilsServiceStub.ESearchRequest();
            eSearchRequest.setTool(Constants.ENTREZ_TOOL);
            eSearchRequest.setEmail(Constants.ENTREZ_EMAIL);
            eSearchRequest.setDb("pubmed");
            String termUrlenc = URIUtil.encodeQuery(this.term).replaceAll("%20", this.logic);
            eSearchRequest.setTerm(termUrlenc + this.lan);
            eSearchRequest.setSort("PublicationDate");
            eSearchRequest.setDatetype("edat");
            eSearchRequest.setUsehistory("y");
            eSearchRequest.setRetMax("" + this.limit);
            eSearchRequest.setRetStart("" + this.pmPage * this.limit);
            EUtilsServiceStub.ESearchResult res = stub.run_eSearch(eSearchRequest);
            this.count = new Integer(res.getCount());
            totalpages = new Integer(count / limit);
            if (res.getIdList().getId() != null) {
                int i = 0;
                int resLength = res.getIdList().getId().length;
                for (i = 0; i < resLength - 1; i++) {
                    String id = res.getIdList().getId()[i];
                    lIDList.add(id);
                    idList += id + ",";
                }
                String id = res.getIdList().getId()[resLength - 1];
                lIDList.add(id);
                idList += id;
            }
        } catch (AxisFault e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void queryPMInit() {
        queryPMID();
        if (!idList.equals("")) {
            try {
                EUtilsServiceStub stub = new EUtilsServiceStub();
                EUtilsServiceStub.ESummaryRequest eSummaryRequest = new EUtilsServiceStub.ESummaryRequest();
                eSummaryRequest.setTool(Constants.ENTREZ_TOOL);
                eSummaryRequest.setEmail(Constants.ENTREZ_EMAIL);
                eSummaryRequest.setDb("pubmed");
                eSummaryRequest.setId(this.idList);
				//eSummaryRequest.setRetmax("10");
                //eSummaryRequest.setRetstart(0);
                EUtilsServiceStub.ESummaryResult res = stub.run_eSummary(eSummaryRequest);
                int resLength = res.getDocSum().length;
                for (int i = 0; i < resLength; i++) {
                    String title = "", pubdate = "", abstracturl = "", authors = "";
                    for (int k = 0; k < res.getDocSum()[i].getItem().length; k++) {
                        if (res.getDocSum()[i].getItem()[k].getItem() == null) {
                            String name = res.getDocSum()[i].getItem()[k].getName();
                            String value = res.getDocSum()[i].getItem()[k].getItemContent();
                            if (name.equals("Title")) {
                                title = value;
                            } else if (name.equals("PubDate")) {
                                pubdate = value;
                            } else if (name.equals("HasAbstract") && value.equals("1")) {
                                abstracturl = Constants.ENTREZ_BASE + "efetch.fcgi?db=pubmed&id="
                                        + this.lIDList.get(i) + "&rettype=abstract&retmode=text";
                            }
                        } else {
                            if (res.getDocSum()[i].getItem()[k].getName().equals("AuthorList")) {
                                int subLength = res.getDocSum()[i].getItem()[k].getItem().length;
                                for (int w = 0; w < subLength; w++) {
                                    String value = res.getDocSum()[i].getItem()[k].getItem()[w].getItemContent();
                                    authors += value + ". ";
                                }
                            }
                        }

                    }
                    lTitle.add(title);
                    lPubDate.add(pubdate);
                    lAbstractUrl.add(abstracturl);
                    lAuthors.add(authors);
                }
            } catch (AxisFault e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTerm(String pterm) {
        this.term = pterm;
    }

    public void setLanSpa() {
        this.lan = "+(spa[LANG])";
    }

    public void setLan(String pLan) {
        this.lan = pLan;
    }

    public void setLogic(String plogic) {
        this.logic = plogic;
    }

    public void setLimit(int plimit) {
        this.limit = plimit;
    }

    public void setPage(int pPage) {
        this.pmPage = pPage;
    }

    public int getCount() {
        return this.count;
    }

    public int getTotalPages() {
        return this.totalpages;
    }

    public List<String> getTitles() {
        return this.lTitle;
    }

    public List<String> getPubDates() {
        return this.lPubDate;
    }

    public List<String> getAuthors() {
        return this.lAuthors;
    }

    public List<String> getAbstractUrl() {
        return this.lAbstractUrl;
    }

    public static void main(String[] args) {
        CallPMSoap oPM = new CallPMSoap();
        oPM.setTerm("cancer de prostata");
        oPM.setLanSpa();
        try {
            oPM.setPage(0);
            //oPM.setLanSpa();
            oPM.queryPMInit();
            List<String> l0 = oPM.getTitles();
            List<String> l1 = oPM.getPubDates();
            List<String> l2 = oPM.getAuthors();
            List<String> l3 = oPM.getAbstractUrl();
            System.out.println("Resultados: " + oPM.getCount());
            for (int i = 0; i < l1.size(); i++) {
                System.out.println("<tr>");
                System.out.println("<td>" + l0.get(i) + "<br><a href='" + l3.get(i)
                        + "' target=_blank>Go to Abstract</a></td>"
                        + "<td>" + l1.get(i) + "</td>"
                        + "<td>" + l2.get(i) + "</td>");
                System.out.println("</tr>");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}
