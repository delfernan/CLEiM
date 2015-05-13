<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*" %>
<%@ page import="com.uem.gsi.cleim.scl.*" %>
<html>
<head>
<title>FREEBASE DISEASE RESULTS</title>
<link rel="stylesheet" type="text/css" href="css/style_tablesorter.css">
<link rel="stylesheet" type="text/css" href="themes/base/ui.all.css">
<script src="js/jquery-1.4.2.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript" src="js/ui/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.mouse.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.resizable.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.2.custom.min.js"></script>
<script src="js/jquery.tablesorter.min.js" type="text/javascript" charset="utf-8"></script>

<%
String term=(request.getParameter("term")!=null)?request.getParameter("term"):"";
String pg=(request.getParameter("pg")!=null)?request.getParameter("pg"):"0";
int ipg=-1;
try{
	ipg=new Integer(pg);
}catch(Exception e){}

int totalpg=0;
if (term.equals("")||ipg<0){
  out.println("Incorrect parameters");    
}else{
  CallFBDis oFB=new CallFBDis(term);
  try {
  	oFB.setPage(ipg);
    oFB.queryFB();
    totalpg=oFB.getTotalPages()+1;
%>

<script>
$(document).ready(function() { 
  $("#FBResult").tablesorter(); 
  //$('#FBResult').tableFilter();
	$('#bPrev').click(function(){
    document.location.href="?term=<%=term%>&pg=<%=ipg-1%>";
  });
  $('#bNext').click(function(){
	  document.location.href="?term=<%=term%>&pg=<%=ipg+1%>";
  });
});
</script>
<style type="text/css">
  body{ font: 65% "Trebuchet MS", sans-serif;margin-top:0;}
  a:link {color: #000000;}
</style>
</head>

<body>
<div id="cambiarTamanyo"></div>
<h1 align="center">FREEBASE DISEASE RESULTS</h1>
<div>
<%if (ipg<=oFB.getTotalPages()){%> 
Page <%=ipg+1%> / <%=totalpg%> (Total topics: <%=oFB.getCount()%>)
<%if (ipg>0){%> 
<button id="bPrev" class="ui-button ui-button-text-only ui-widget ui-state-default ui-corner-all">
   <span class="ui-button-text">Prev</span>
</button> 
<%}%>
<%if (ipg!=oFB.getTotalPages()){%> 
<button id="bNext" class="ui-button ui-button-text-only ui-widget ui-state-default ui-corner-all">
   <span class="ui-button-text">Next</span>
</button> 
<%} %>
</div>
<div id="output">
<table id="FBResult" class="tablesorter"> 
<thead> 
 <tr>
 <th>CONCEPT(DISEASE)</th>
 <th>IMAGE</th>
 <th>DESCRIPTION</th>
 <th>SYMPTOMS</th>
 <th>TREATMENTS</th>
 <th>RISK FACTORS</th>
 </tr>
</thead> 
<tbody> 
<%
		List<String> l0=oFB.getConcepts();
		List<String> l1=oFB.getImages();
		List<String> l2=oFB.getArticles();
		List<String> l3=oFB.getSymptoms();
		List<String> l4=oFB.getTreatments();
		List<String> l5=oFB.getRisks();
		List<String> l6=oFB.getFBUrls();
    String sOut="";
    for (int i=0;i<l1.size();i++){
    	sOut+="<tr>";
    	sOut+="<td><h2>"+l0.get(i)+"</h2>";
    	sOut+="<p align=center><a href='pmresult.jsp?term="+l0.get(i)+
    			"' target=_blank>PubMed results</a></p>";
      sOut+=(l6.get(i).equals(""))?"":
    		"<p align=center><a href='"+l6.get(i)+
    		"' target=_blank>Freebase Topics</a></p>";
    	sOut+="</td>";
    	sOut+="<td>";
    	sOut+=(l1.get(i).equals(""))?"":"<img src='"+l1.get(i)+"?maxwidth=200'>";
    	sOut+="</td>";
    	sOut+="<td>"+l2.get(i)+"</td>";
    	sOut+="<td>"+l3.get(i)+"</td>";
    	sOut+="<td>"+l4.get(i)+"</td>";
    	sOut+="<td>"+l5.get(i)+"</td>";
      sOut+="</tr>";
    }
    out.println(sOut);
	}else{
	  out.println("Incorrect parameters");
	}
  } catch (Exception e) {
  	out.println("Some error occurred");
  }
}
%>

<%! 
%>

</tbody> 
</table>
</div>
</body>
</html>
