<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<title>FREEBASE TREATMENT RESULTS</title>
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
  CallFBTre oFB=new CallFBTre(term);
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
  .ui-resizable-se {bottom: 17px;}
  body{ font: 65% "Trebuchet MS", sans-serif;margin-top:0;}
  a:LINK {color:#000000;}
  /*.demoHeaders { margin-top: 2em; }
  #dialog_link {padding: .4em 1em .4em 20px;text-decoration: none;position: relative;}
  #dialog_link span.ui-icon {margin: 0 5px 0 0;position: absolute;left: .2em;top: 50%;margin-top: -8px;}
  ul#icons {margin: 0; padding: 0;}
  ul#icons li {margin: 2px; position: relative; padding: 4px 0; cursor: pointer; float: left;  list-style: none;}
  ul#icons span.ui-icon {float: left; margin: 0 4px;}*/
</style>
</head>

<body>
<%if (ipg<=oFB.getTotalPages()){%> 
<h1 align="center">FREEBASE TREATMENT RESULTS</h1>
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
<table id="FBResult" class="tablesorter"> 
<thead> 
 <tr>
 <th>CONCEPT(TREATMENT)</th>
 <th>IMAGE</th>
 <th>DESCRIPTION</th>
 <!-- <th>CONTRAINDICATIONS</th> -->
 <th>USED TO TREAT</th>
 <th>SIDE EFFECT</th>
 <!-- <th>TRIALS</th> -->
 </tr>
</thead> 
<tbody> 
<%
		List<String> l0=oFB.getConcepts();
		List<String> l1=oFB.getImages();
		List<String> l2=oFB.getArticles();
		//List<String> l3=oFB.getContras();
		List<String> l4=oFB.getUsedTo();
		List<String> l5=oFB.getSideEf();
		//List<String> l5=oFB.getTrials();
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
    	//sOut+="<td>"+l3.get(i)+"</td>";
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
</body>
</html>
