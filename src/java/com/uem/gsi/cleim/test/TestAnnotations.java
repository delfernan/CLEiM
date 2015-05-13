package com.uem.gsi.cleim.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.FileUtils;
import com.uem.gsi.cleim.nlp.IntegrateAnnot;

public class TestAnnotations {

    private String path = "web/WEB-INF";
    private String text = "cancer";
    private String onts = "";
    private String lev = "0";
    private String[] localSrc = new String[]{"", "", "", "", ""};
    private String[] localLan = new String[]{"", ""};
    private IntegrateAnnot ia;
    private String src = "4";

    public TestAnnotations() {

    }

    public void setText(String ptext) {
        text = ptext;
    }

    public void setOnts(String ponts) {
        onts = ponts;
    }

    public void setLev(String plev) {
        lev = plev;
    }

    public void setLocalSrc(String[] plocalSrc) {
        localSrc = plocalSrc;
    }

    public void setLocalLan(String[] plocalLan) {
        localLan = plocalLan;
    }

    public void setSrc(String psrc) {
        src = psrc;
    }

    public void initIntegrateAnnot() {
        ia = new IntegrateAnnot(text, path, onts, lev, localSrc, localLan, src);
    }

    private long takeTime() {
        return System.currentTimeMillis();
    }

    private void avoidInitialization() {
        try {
            ia.localWithoutFormat();
            ia.remoteWithoutFormat();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    /**
     * @param formatType (html,xml)
     */
    private long testGate(String formatType, String testVars) {
        String output = "";
        long res = 0;
        try {
            if (formatType.equals("")) {
                long start = takeTime();
                ia.localWithoutFormat();
                long end = takeTime();
                res = end - start;
            } else if (formatType.equals("html")) {
                long start = takeTime();
                output = ia.getHtmlLocalTree();
                long end = takeTime();
                res = end - start;
            } else if (formatType.equals("xml")) {
                long start = takeTime();
                try {
                    ia.initXmlDocument();
                    ia.getXmlLocalTree();
                    output = ia.getNormalizedXml();
                } catch (Exception ex) {
                    System.out.println("ERROR GETTING XML LOCAL TREE: " + ex.getMessage());
                }
                long end = takeTime();
                res = end - start;
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        System.out.println(testVars + " Local Time (format=" + formatType + "): " + res);
        //System.out.println(output);
        return res;
    }

    public long testOba(String formatType, String testVars) {
        String output = "";
        long res = 0;
        try {
            if (formatType.equals("")) {
                long start = takeTime();
                ia.remoteWithoutFormat();
                long end = takeTime();
                res = end - start;
            } else if (formatType.equals("html")) {
                long start = takeTime();
                output = ia.getHtmlRemoteTree();
                long end = takeTime();
                res = end - start;
            } else if (formatType.equals("xml")) {
                long start = takeTime();
                try {
                    ia.initXmlDocument();
                    ia.getXmlRemoteTree();
                    output = ia.getNormalizedXml();
                } catch (Exception ex) {
                    System.out.println("ERROR GETTING XML REMOTE TREE: " + ex.getMessage());
                }
                long end = takeTime();
                res = end - start;
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
        System.out.println(testVars + " Remote Time (format=" + formatType + "): " + res);
        //System.out.println(output);
        return res;
    }

    public void testHTTPGATE(String src) {
        String sSource = "";
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod("http://orion.esp.uem.es:8080/CLEiM/NLPServ");
        // Configure the form parameters
        method.addParameter("text", text);
        method.addParameter("src", src);
        try {
            long start = takeTime();
            int statusCode = client.executeMethod(method);
            sSource = method.getResponseBodyAsString();
            long end = takeTime();
            System.out.println("GATE HTTP Time (src=" + src + "): " + (end - start));
            if ((statusCode > 199) && (statusCode < 300)) {
                //sSource = method.getResponseBodyAsString();
                method.releaseConnection();
                //System.out.println(contents);
            } else {
                System.out.println("ERROR, statusCode = " + statusCode);
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }

    public static void main(String[] args) {
        String text = "cancer";
        int testNumber = 3;
        int longTestNumber = 3;
        int caseNumber = 8;
        int longCaseNumber = 8;
        String testCase = "";
        String testVars = "";
        String[] sResults;
        TestAnnotations ta = new TestAnnotations();
        ta.setLev("0");
        String[] texts = createArrayText();

        //Test UPMC cases
        sResults = new String[testNumber + 1];
        for (int i = 0; i < sResults.length; i++) {
            sResults[i] = "";
        }

        for (int k = 0; k < caseNumber; k++) {
            //Text and ontologies
            switch (k) {
                //0-3: Remote --- 4-7: local
                case 0:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "", "", ""});
                    ta.setLocalLan(new String[]{"en", ""});
                    ta.setOnts("40397");
                    ta.setSrc("2"); //Remote MedilinePlus
                    testCase = "P1;P2;P3";
                    testVars = "[MedlinePlus] [en]";
                    break;
                case 1:
                    ta.setLocalSrc(new String[]{"", "", "Snomed", "", ""});
                    ta.setLocalLan(new String[]{"en", ""});
                    ta.setOnts("46116");
                    ta.setSrc("3"); //Remote Snomed
                    testCase = "P4;P5;P6";
                    testVars = "[Snomed] [en]";
                    break;
                case 2:
                    ta.setLocalSrc(new String[]{"", "", "", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", ""});
                    ta.setOnts("");
                    ta.setSrc("4"); //Remote DBPedia
                    testCase = "P7;P8;P9";
                    testVars = "[DBPedia] [en]";
                    break;
                case 3:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "Snomed", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", ""});
                    ta.setOnts("40397,46116");
                    ta.setSrc("0"); //All Remote
                    testCase = "P10;P11;P12";
                    testVars = "[MedlinePlus, Snomed, DBPedia] [en]";
                    break;
                case 4:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "", "", ""});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("40397");
                    ta.setSrc("2"); //Local MedLinePlus
                    testCase = "P13;P14;P15";
                    testVars = "[MedlinePlus] [en, sp]";
                    break;
                case 5:
                    ta.setLocalSrc(new String[]{"", "", "Snomed", "", ""});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("46116");
                    ta.setSrc("3"); //Local Snomed
                    testCase = "P16;P17;P18";
                    testVars = "[Snomed] [en, sp]";
                    break;
                case 6:
                    ta.setLocalSrc(new String[]{"", "", "", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("");
                    ta.setSrc("4"); //Local DBPedia
                    testCase = "P19;P20;P21";
                    testVars = "[DBPedia] [en, sp]";
                    break;
                case 7:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "Snomed", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setSrc("1"); //Local ALL
                    testCase = "P22;P23;P24";
                    testVars = "[MedlinePlus, Snomed, DBPedia] [en, sp]";
                    break;
            }

            System.out.println("********************************");
            System.out.println("TEST CASE: " + testCase + " " + testVars);
            System.out.println("********************************");

            //for (int i = 715; i < 716; i++) {
            for (int i = 715; i < 727; i++) {
                //Text from file
////                try {
////                    File fClinicalCase = new File("D:/UEM/MedicalMiner/ClinicalCases/upmc_2012/" + i);
////                    text = FileUtils.readFileToString(fClinicalCase);
////                } catch (IOException e1) {
////                    System.out.println("Exception: " + e1.toString());
////                }
                //Text from variables (if no files are available)
                text = texts[i];
                ta.setText(text);
                ta.initIntegrateAnnot();
                //Avoid initialization times
                ta.avoidInitialization();

                System.out.println(" -> CLINICAL CASE " + i);
                System.out.println("----------------------");
                sResults[0] += (sResults[0].equals("")) ? testCase : ";" + testCase;
                //Repeat Test testNumber times
                for (int n = 0; n < testNumber; n++) {
                    System.out.println("+++++++++++++++++++++");
                    System.out.println("TEST NUMBER: N=" + n);
                    System.out.println("+++++++++++++++++++++");
                    if (k > 3) {
                        //Test GATE
                        System.gc();
                        sResults[n + 1] += (sResults[n + 1].equals("")) ? ta.testGate("", testVars) : ";" + ta.testGate("", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testGate("html", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testGate("xml", testVars);
                    } else {
                        System.gc();
                        sResults[n + 1] += ";" + ta.testOba("", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testOba("html", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testOba("xml", testVars);
                    }
                }
            }
            Writer out;
            try {
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("data/case" + k + ".txt", false), "UTF8"));

                for (int y = 0; y < sResults.length; y++) {
                    out.write(sResults[y] + "\n");
                    sResults[y] = "";
                }
                out.close();
            } catch (Exception e) {
                System.out.println("Exception: " + e.toString());
            }

        }

        //Test long texts
        /*sResults = new String[longTestNumber + 1];
        for (int i = 0; i < sResults.length; i++) {
            sResults[i] = "";
        }

        for (int k = 0; k < longCaseNumber; k++) {
            //Text and ontologies
            switch (k) {
                //0-3: Remote --- 4-7: local
                case 0:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "", "", ""});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("40397");
                    ta.setSrc("2"); //Remote MedilinePlus
                    testCase = "P1;P2;P3";
                    testVars = "[MedlinePlus] [en, sp]";
                    break;
                case 1:
                    ta.setLocalSrc(new String[]{"", "", "Snomed", "", ""});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("46116");
                    ta.setSrc("3"); //Remote Snomed
                    testCase = "P4;P5;P6";
                    testVars = "[Snomed] [en, sp]";
                    break;
                case 2:
                    ta.setLocalSrc(new String[]{"", "", "", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("");
                    ta.setSrc("4"); //Remote DBPedia
                    testCase = "P7;P8;P9";
                    testVars = "[DBPedia] [en, sp]";
                    break;
                case 3:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "Snomed", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("40397,46116");
                    ta.setSrc("0"); //All Remote
                    testCase = "P10;P11;P12";
                    testVars = "[MedlinePlus, Snomed, DBPedia] [en, sp]";
                    break;
                case 4:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "", "", ""});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("40397");
                    ta.setSrc("2"); //Local MedLinePlus
                    testCase = "P13;P14;P15";
                    testVars = "[MedlinePlus] [en, sp]";
                    break;
                case 5:
                    ta.setLocalSrc(new String[]{"", "", "Snomed", "", ""});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("46116");
                    ta.setSrc("3"); //Local Snomed
                    testCase = "P16;P17;P18";
                    testVars = "[Snomed] [en, sp]";
                    break;
                case 6:
                    ta.setLocalSrc(new String[]{"", "", "", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setOnts("");
                    ta.setSrc("4"); //Local DBPedia
                    testCase = "P19;P20;P21";
                    testVars = "[DBPedia] [en, sp]";
                    break;
                case 7:
                    ta.setLocalSrc(new String[]{"", "MedlinePlus", "Snomed", "", "DBPedia"});
                    ta.setLocalLan(new String[]{"en", "sp"});
                    ta.setSrc("1"); //Local ALL
                    testCase = "P22;P23;P24";
                    testVars = "[MedlinePlus, Snomed, DBPedia] [en, sp]";
                    break;
            }
            System.out.println("********************************");
            System.out.println("TEST CASE: " + testCase + " " + testVars);
            System.out.println("********************************");
            for (int i = 0; i < 3; i++) {
                //Text from file
//                try {
//                    File fClinicalCase = new File("D:/UEM/MedicalMiner/ClinicalCases/upmc_2012/long" + i);
//                    text = FileUtils.readFileToString(fClinicalCase);
//                } catch (IOException e1) {
//                    System.out.println("Exception: " + e1.toString());
//                }
                //Text from variables (if no files are available)
                text = texts[i];
                //Text
                ta.setText(text);
                ta.initIntegrateAnnot();
                //Avoid initialization times
                ta.avoidInitialization();

                System.out.println(" -> CLINICAL CASE LONG " + i);
                System.out.println("----------------------");
                sResults[0] += (sResults[0].equals("")) ? testCase : ";" + testCase;
                for (int n = 0; n < longTestNumber; n++) {
                    System.out.println("+++++++++++++++++++++");
                    System.out.println("LONG TEST NUMBER: N=" + n);
                    System.out.println("+++++++++++++++++++++");
                    if (k > 3) {
                        //Test GATE
                        System.gc();
                        sResults[n + 1] += (sResults[n + 1].equals("")) ? ta.testGate("", testVars) : ";" + ta.testGate("", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testGate("html", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testGate("xml", testVars);
                    } else {
                        //Test OBA
                        System.gc();
                        sResults[n + 1] += ";" + ta.testOba("", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testOba("html", testVars);
                        System.gc();
                        sResults[n + 1] += ";" + ta.testOba("xml", testVars);
                    }
                }
            }
            Writer out;
            try {
                out = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("caseLong" + k + ".txt", false), "UTF8"));

                for (int y = 0; y < sResults.length; y++) {
                    out.write(sResults[y] + "\n");
                    sResults[y] = "";
                }
                out.close();
            } catch (Exception e) {
                System.out.println("Exception: " + e.toString());
            }
        }*/
    }

    /**
     * @return String[727] - text array.
     */
    public static String[] createArrayText() {
        String[] texts = new String[727];
        texts[0] = "56 year old man with history of colon and testicular cancer, pons glioma now with significant hilar lymphadeonpathry, pulmonary pathology on CXR.";
        texts[1] = "REASON FOR THIS EXAMINATION: extracto de genciana colirio/gotas óticas vagina no hiperestésica";
        texts[2] = "56 year old man with history of colon and testicular cancer, pons glioma now Salmonella Irumu SIDA con visión deficiente with significant hilar lymphadeonpathry, pulmonary pathology on CXR. Suture of wound of forelimb Lumbar chemical sympathectomy vía fístula mucosa No contraindications for IV contrast  pain back pain aspirin cáncer de próstata";
        for (int i = 3; i < 715; i++) {
            texts[i] = "cancer";
        }
        texts[715] = "cancer";
        texts[716] = "alergia";
        texts[717] = "cáncer";
        texts[718] = "allergy";
        texts[719] = "testicular";
        texts[720] = "prostate cancer";
        texts[721] = "cáncer de próstata";
        texts[722] = "allergie";
        texts[723] = "candidiasis";
        texts[724] = "thyroid";
        texts[725] = "Vaginal";
        texts[726] = "Lung";
        return texts;
    }

}
