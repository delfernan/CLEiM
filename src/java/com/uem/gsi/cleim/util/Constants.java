/**
 * @author Fernando Aparicio Galisteo
 *
 */
package com.uem.gsi.cleim.util;

public final class Constants {

    //FREEBASE
    public static final String FREEBASE_VIEW = "http://www.freebase.com/view";
    //GATE
    public static final String PATH_DEF_TMP = "/plugins/ANNIE/resources/gazetteer/medicinetmp.def";
    public static final String PATH_DISEASE_LST_EN = "/plugins/ANNIE/resources/gazetteer/diseaseen.lst";
    public static final String PATH_SYMPTOM_LST_EN = "/plugins/ANNIE/resources/gazetteer/symptomen.lst";
    public static final String PATH_TREATMENT_LST_EN = "/plugins/ANNIE/resources/gazetteer/treatmenten.lst";
    public static final String PATH_DISEASE_LST_SP = "/plugins/ANNIE/resources/gazetteer/diseasesp.lst";
    public static final String PATH_SYMPTOM_LST_SP = "/plugins/ANNIE/resources/gazetteer/symptomsp.lst";
    public static final String PATH_TREATMENT_LST_SP = "/plugins/ANNIE/resources/gazetteer/treatmentsp.lst";
    
    //MEDLINEPLUS
    public static final String MLP_SEARCH = "http://wsearch.nlm.nih.gov/ws/query";
    public static final String MLP_XML_VOCAB = "http://www.nlm.nih.gov/medlineplus/xml/vocabulary/mplus_vocab_2012-02-18.xml";
    public static final String MLP_CONNECT_SNOMED = "http://apps.nlm.nih.gov/medlineplus/services/mpconnect.cfm?mainSearchCriteria.v.cs=2.16.840.1.113883.6.96";
    public static final String MLP_CONNECT_TERM_PARAM = "mainSearchCriteria.v.c";
    public static final String MLP_CONNECT_LAN_PARAM = "informationRecipient.languageCode.c";
    public static final String PATH_MLP_LST_EN = "/plugins/ANNIE/resources/gazetteer/mlpen.lst";
    public static final String PATH_MLP_LST_SP = "/plugins/ANNIE/resources/gazetteer/mlpsp.lst";
    //ENTREZ
    public static final String ENTREZ_BASE = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";
    public static final String ENTREZ_TOOL = "CLEiM";
    public static final String ENTREZ_EMAIL = "fernando.aparicio@uem.es";
    //NCBO
    public static final String NCBO_BASE = "http://rest.bioontology.org/obs";
    public static final String NCBO_EMAIL = "fernando.aparicio@uem.es";
    public static final String NCBO_ANNOT = "/annotator";
    public static final String NCBO_ONTOL = "/ontologies";
    public static final String NCBO_APIKEY = "fc382edb-681e-4132-84b4-1b1dc3697e76";
    //DBPEDIA
    public static final String DBPEDIA_SEARCH = "http://dbpedia.org/sparql";
    public static final String PATH_DBPEDIA_LST = "/plugins/ANNIE/resources/gazetteer/dbpedia";
    public static final String SUFIX_DBPEDIA_FILE = ".lst";

    public Constants() {
        // Avoid calling this constructor
        throw new AssertionError();
    }

}
