<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<html>
<head>
<title>MedlilePlus RESULTS</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%@ page import="java.util.*" %>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="com.uem.gsi.cleim.scl.*" %>
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
String lan=(request.getParameter("lan")!=null)?request.getParameter("lan"):"";
if (term.equals("")){
  out.println("Term can't be null");    
}else{
  MLPSearch oMLP=new MLPSearch();
  try {
  	//term=URLDecoder.decode(term,"UTF-8");
  	//System.out.println("jsp term: "+term);
    oMLP.runMPLSearch(term,lan);
 %>
<script>
$(document).ready(function() { 
  $("#MLPResult").tablesorter(); 
  //$('#MLPResult').tableFilter();
});
</script>
<style type="text/css">
  .ui-resizable-se {bottom: 17px;}
  body{ font: 55% "Trebuchet MS", sans-serif;margin-top:0;}
  a:LINK {color:#000000;}
</style>
</head>

<body>

<h1 align="center">MEDLINEPLUS RESULTS</h1>
(Total topics: <%=oMLP.getCount()%>)
<table id="MLPResult" class="tablesorter"> 
<thead> 
 <tr>
 <th>TITLE</th>
 <!-- <th>ORGANIZATION NAME</th> -->
 <th>FULL SUMMARY</th>
 <th>MESH (PubMed Search)</th>
 <th>GROUP NAME</th>
 <!-- <th>SNIPPET</th>  -->
 </tr>
</thead> 
<tbody> 

<%
    List<String> l0=oMLP.getUrl();
    List<String> l1=oMLP.getTitles();
    List<String> l2=oMLP.getRank();
    List<String> l3=oMLP.getFullSummary();
    List<String> l4=oMLP.getMesh();
    List<String> l5=oMLP.getGroupName();
    List<String> l6=oMLP.getUrlSp();
    //List<String> l6=oMLP.getSnippet();
    String sOut="";
    for (int i=0;i<l1.size();i++){
      sOut+="<tr>";
      sOut+="<td><b>"+l1.get(i)+"</b>";
      sOut+="<table>";
      sOut+="<tr><td>";
      sOut+="(Rank: "+l2.get(i)+")<br/>";
      sOut+="</td></tr>";
      sOut+="<tr><td>";
      sOut+="<a href='pmresult.jsp?term="+l1.get(i)+
          "' target=_blank>PubMed results</a><br/>";
      sOut+="</td></tr>";
      sOut+="<tr><td>";
      sOut+=(l0.get(i).equals(""))?"":
        "<a href='"+l0.get(i)+
        "' target=_blank>Medlineplus Topics</a><br/>";
      sOut+="</td></tr>";
      sOut+="<tr><td>";
      sOut+=(l6.get(i).equals(""))?"":
        "<a href='"+l6.get(i)+
        "' target=_blank>Tópico en Medlineplus</a><br/>";
      sOut+="</td></tr>";
      sOut+="</table>";  
      sOut+="</td>";
      sOut+="<td>"+l3.get(i)+"</td>";
      sOut+="<td>"+l4.get(i)+"</td>";
      sOut+="<td>"+l5.get(i)+"</td>";
      sOut+="</tr>";
    }
    out.println(sOut);
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
