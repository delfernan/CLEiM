package com.uem.gsi.cleim.util;

import com.uem.gsi.cleim.rpd.DBPediaToFile;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ReplaceDefFile {

    /**
     *
     * @param sources Freebase,MedlinePlus,Snomed
     * @param lan en,sp
     * @param filePath Path to def GATE file
     */
    public void saveDefFile(String[] sources, String[] lan, String filePath) {
        try {
            Writer out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filePath, false), "UTF8"));
            //Cleans the file
            out.write("");
            if (lan != null && lan.length > 0) {
                for (int i = 0; i < lan.length; i++) {
                    if (lan[i] != "") {
                        for (int j = 0; j < sources.length; j++) {
                            if (sources[j].toLowerCase().equals("freebase")) {
                                if ((lan[i].toLowerCase().equals("sp")) || (lan[i].toLowerCase().equals("en"))) {
                                    out.write("disease" + lan[i] + ".lst:" + sources[j] + ":disease:" + lan[i] + "\n");
                                    out.write("symptom" + lan[i] + ".lst:" + sources[j] + ":symptom:" + lan[i] + "\n");
                                    out.write("treatment" + lan[i] + ".lst:" + sources[j] + ":treatment:" + lan[i] + "\n");
                                }
                            } else if (sources[j].toLowerCase().equals("medlineplus")) {
                                if ((lan[i].toLowerCase().equals("sp")) || (lan[i].toLowerCase().equals("en"))) {
                                    out.write("mlp" + lan[i] + ".lst:" + sources[j] + ":medlineplus:" + lan[i] + "\n");
                                }
                            } else if (sources[j].toLowerCase().equals("snomedcore")) {
                                if ((lan[i].toLowerCase().equals("sp")) || (lan[i].toLowerCase().equals("en"))) {
                                    out.write("sctcore" + lan[i] + ".lst:" + sources[j] + ":snomed:" + lan[i] + "\n");
                                }
                            }  else if (sources[j].toLowerCase().equals("dbpedia")) {
                                //dbpedia supports all languages
                                if (DBPediaToFile.supportedLanguageFiles.contains(lan[i].toLowerCase())) {
                                    out.write("dbpedia" + lan[i] + ".lst:" + sources[j] + ":dbpedia:" + lan[i] + "\n");
                                }
                            }
                        }
                    }
                }
            }
            // Close file writer
            out.close();
        } catch (IOException e) {
            System.out.println("Error generando fichero: " + filePath);
            System.out.println(e.toString());
        }
    }
}
