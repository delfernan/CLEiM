<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<title>MEDLINE/PubMed RESULTS</title>
<%@ page import="java.util.*" %>
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
String pg=(request.getParameter("pg")!=null)?request.getParameter("pg"):"0";
int ipg=new Integer(pg);
int totalpg=0;
if (term.equals("")){
  out.println("Term can't be null");    
}else{
  CallPMSoap oPM=new CallPMSoap();
  oPM.setTerm(term);
  try {
  	oPM.setPage(ipg);
    oPM.queryPMInit();
    totalpg=oPM.getTotalPages()+1;
%>
<script>
$(document).ready(function() { 
  $("#PMResult").tablesorter(); 
  //$('#MLPResult').tableFilter();
	$('#bPrev').click(function(){
    document.location.href="?term=<%=term%>&pg=<%=ipg-1%>";
  });
  $('#bNext').click(function(){
	  document.location.href="?term=<%=term%>&pg=<%=ipg+1%>";
  });
});
</script>
<style type="text/css">
  body{ font: 70% "Trebuchet MS", sans-serif;margin-top:0;}
  a:LINK {color:#000000;}
</style>
</head>

<body>
<%if (ipg<=oPM.getTotalPages()){%> 
<h1 align="center">MEDLINE/PubMed RESULTS</h1>
Page <%=ipg+1%> / <%=totalpg%> (Total papers: <%=oPM.getCount()%>)
<%if (ipg>0){%> 
<button id="bPrev" class="ui-button ui-button-text-only ui-widget ui-state-default ui-corner-all">
   <span class="ui-button-text">Prev</span>
</button> 
<%}%>
<%if (ipg!=oPM.getTotalPages()){%> 
<button id="bNext" class="ui-button ui-button-text-only ui-widget ui-state-default ui-corner-all">
   <span class="ui-button-text">Next</span>
</button> 
<%} %>
<table id="PMResult" class="tablesorter"> 
<thead> 
 <tr>
 <th>TITLE</th>
 <!-- <th>ORGANIZATION NAME</th> -->
 <th>PUB. DATE</th>
 <th>AUTHORS</th>
 <!-- <th>GROUP NAME</th> -->
 <!-- <th>SNIPPET</th>  -->
 </tr>
</thead> 
<tbody> 
<%
		List<String> l0=oPM.getTitles();
		List<String> l1=oPM.getPubDates();
		List<String> l2=oPM.getAuthors();
		List<String> l3=oPM.getAbstractUrl();
		String sOut="";
    for (int i=0;i<l1.size();i++){
    	sOut+="<tr>";
    	sOut+="<td>"+l0.get(i);
    	sOut+=(l3.get(i).equals(""))?"":"<br><a href='"+l3.get(i)+"' target=_blank>View Abstract</a>";
    	sOut+="</td>";
    	sOut+="<td>"+l1.get(i)+"</td>";
    	sOut+="<td>"+l2.get(i)+"</td>";
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
</body>
</html>
