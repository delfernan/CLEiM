/**
 * @author Fernando Aparicio Galisteo
 *
 */
package com.uem.gsi.cleim.ali;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.uem.gsi.cleim.nlp.IntegrateAnnot;

/**
 * Servlet implementation class NLPServ
 */
public class XMLServ extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private String path;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public XMLServ() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        path = servletConfig.getServletContext().getRealPath("/WEB-INF");
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String text = (request.getParameter("text") != null) ? request.getParameter("text") : "";
        String lev = (request.getParameter("lev") != null) ? request.getParameter("lev") : "0";
        String onts = (request.getParameter("onts") != null) ? request.getParameter("onts") : "40397,46116"; //Medlineplus,Snomed
        String src = (request.getParameter("src") != null) ? request.getParameter("src") : "-1";
        String[] localSrc = new String[5];
        if (request.getParameter("cksrcfb") != null) {
            localSrc[0] = request.getParameter("cksrcfb");
        } else {
            localSrc[0] = "";
        }
        if (request.getParameter("cksrcmp") != null) {
            localSrc[1] = request.getParameter("cksrcmp");
        } else {
            localSrc[1] = "";
        }
        if (request.getParameter("cksrcss") != null) {
            localSrc[2] = request.getParameter("cksrcss");
        } else {
            localSrc[2] = "";
        }
        if (request.getParameter("cksrcsc") != null) {
            localSrc[3] = request.getParameter("cksrcsc");
        } else {
            localSrc[3] = "";
        }
        if (request.getParameter("cksrcdb") != null) {
            localSrc[4] = request.getParameter("cksrcdb");
        } else {
            localSrc[4] = "";
        }
        String[] localLan = new String[20];
        localLan[0] = (request.getParameter("cklanen") != null) ? request.getParameter("cklanen") : "";
        localLan[1] = (request.getParameter("cklansp") != null) ? request.getParameter("cklansp") : "";
        localLan[2] = (request.getParameter("cklanfr") != null) ? request.getParameter("cklanfr") : "";
        localLan[3] = (request.getParameter("cklansv") != null) ? request.getParameter("cklansv") : "";
        localLan[4] = (request.getParameter("cklanpt") != null) ? request.getParameter("cklanpt") : "";
        localLan[5] = (request.getParameter("cklanit") != null) ? request.getParameter("cklanit") : "";
        localLan[6] = (request.getParameter("cklande") != null) ? request.getParameter("cklande") : "";
        localLan[7] = (request.getParameter("cklannl") != null) ? request.getParameter("cklannl") : "";
        localLan[8] = (request.getParameter("cklanzh") != null) ? request.getParameter("cklanzh") : "";
        localLan[9] = (request.getParameter("cklanru") != null) ? request.getParameter("cklanru") : "";
        localLan[10] = (request.getParameter("cklanja") != null) ? request.getParameter("cklanja") : "";
        localLan[11] = (request.getParameter("cklanpl") != null) ? request.getParameter("cklanpl") : "";
        localLan[12] = (request.getParameter("cklanda") != null) ? request.getParameter("cklanda") : "";
        localLan[13] = (request.getParameter("cklanar") != null) ? request.getParameter("cklanar") : "";
        localLan[14] = (request.getParameter("cklancs") != null) ? request.getParameter("cklancs") : "";
        localLan[15] = (request.getParameter("cklanfi") != null) ? request.getParameter("cklanfi") : "";
        localLan[16] = (request.getParameter("cklanhe") != null) ? request.getParameter("cklanhe") : "";
        localLan[17] = (request.getParameter("cklanhi") != null) ? request.getParameter("cklanhi") : "";
        localLan[18] = (request.getParameter("cklannb") != null) ? request.getParameter("cklannb") : "";
        localLan[19] = (request.getParameter("cklanel") != null) ? request.getParameter("cklanel") : "";
        
        String xmlResults = "";
        PrintWriter out = response.getWriter();
        if (text.trim().equals("")) {
            xmlResults = "Text to annotate can't be null.";
        } else {
            try {
                if (!text.equals("")) {
                    //NCBO Ontologies
                    if (src.equals("2")) {
                        onts = "40397";
                    } else if (src.equals("3")) {
                        onts = "46116";
                    }

                    IntegrateAnnot ia = new IntegrateAnnot(text, path, onts, lev, localSrc, localLan, src);
                    ia.initXmlDocument();
                    //Local Annotation
                    if (src.equals("-1") || src.equals("1")) {
                        ia.getXmlLocalTree();
                    }

                    //Remote Annotation
                    if (!src.equals("1")) {
                        ia.getXmlRemoteTree();
                    }

                    // Normalizing and get document
                    xmlResults = ia.getNormalizedXml();
                }

                response.setContentType("text/xml");
                out.println(xmlResults);
            } catch (Exception e) {
                xmlResults = e.toString();
            }
        }
        out.println(xmlResults);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
