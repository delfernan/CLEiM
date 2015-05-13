<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<title>DBPedia RESULTS</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.util.List" %>
<%@ page import="com.uem.gsi.cleim.scl.CallDBPedia" %>
<%@ page import="com.uem.gsi.cleim.nlp.DBPediaAnnot" %>
<%@ page import="com.uem.gsi.cleim.nlp.IntegrateAnnot" %>
<link rel="stylesheet" type="text/css" href="css/style_tablesorter.css">
<link rel="stylesheet" type="text/css" href="themes/base/ui.all.css">
<script src="js/jquery-1.4.2.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript" src="js/ui/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.mouse.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.resizable.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.2.custom.min.js"></script>
<script src="js/jquery.tablesorter.min.js" type="text/javascript" charset="utf-8"></script>
<!-- <script type="text/javascript" src="js/picnet.table.filter.min.js"></script> -->
<%
String term=(request.getParameter("term")!=null)?new String(request.getParameter("term").getBytes("UTF-8"),"UTF-8"):"";
String lan=(request.getParameter("lan")!=null)?request.getParameter("lan"):"en";
String type=(request.getParameter("type")!=null)?request.getParameter("type"):"1";
if (term.trim().equals("")){
  out.println("Term can't be null");    
} else {
    try {
        term=URLDecoder.decode(term, "UTF-8");
        List<DBPediaAnnot> annotations = CallDBPedia.getDBPediaAnnotationTopics(term, lan, type);
%>
<script>
$(document).ready(function() { 
  $("#DBPediaResult").tablesorter(); 
});
</script>
<style type="text/css">
  .ui-resizable-se {bottom: 17px;}
  body{ font: 55% "Trebuchet MS", sans-serif;margin-top:0;}
  a:LINK {color:#000000;}
</style>
</head>

<body>

<h1 align="center">DBPEDIA RESULTS</h1>
(Total topics: <% if (annotations == null) {
                      out.println("0");
                  } else { out.println(annotations.size()); } %>)
<table id="DBPediaResult" class="tablesorter"> 
<thead> 
 <tr>
 <th>TITLE</th>
 <th>FULL SUMMARY</th>
 <th>MESH (PubMed Search)</th>
 <th>GROUP NAME</th>
 </tr>
</thead> 
<tbody> 

<%
        String result="";
        if (annotations != null && !annotations.isEmpty()) {
            for (DBPediaAnnot annot : annotations) {
                result += "<tr>";
                result += "<td><b>" + annot.getLabel() + "</b><br/>";
                result += "<br/><a href='" + annot.getWikipediaUrl() + "' target=_blank>";
                result += annot.getAlternativeTextUrl(DBPediaAnnot.PAGE_WIKIPEDIA, annot.getLanguage()) + "</a></td>";
                result += "<td><b>Comment:</b><br/>" + annot.getComment();
                if (!annot.getComment().equals(annot.getAbstract())) {
                    result += "<br/><b>Abstract:</b><br/>" + annot.getAbstract();
                }
                result += "</td>";
                result += "<td><a href='pmresult.jsp?term=" + URLEncoder.encode(annot.getLabel(), "UTF-8") + "' target=_blank>";
                result += annot.getAlternativeTextUrl(DBPediaAnnot.PAGE_PUBMED, annot.getLanguage()) + "</a></td>";
                result += "<td>" + annot.getTypeText() + "</td>";
                result += "</tr>";
            }
        }
        out.println(result);
    } catch (Exception e) {
        out.println(e.toString());
    }
}
%>

<%! 
%>

</tbody> 
</table>
</body>
</html>
