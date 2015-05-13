<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@page import="com.uem.gsi.cleim.util.MyDOMParser"%>
<%@page import="com.uem.gsi.cleim.util.Constants"%>
<%@ page import="javax.xml.parsers.*" %>
<%@ page import="org.w3c.dom.*" %>
<html>
<head>
<title>NCBO OBA Ontologies</title>
<link rel="stylesheet" type="text/css" href="css/style_tablesorter.css">
<script src="js/jquery-1.4.2.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/jquery.tablesorter.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript" src="js/picnet.table.filter.min.js"></script>
<script>
$(document).ready(function() { 
  $("#ontologyTable").tablesorter(); 
  $('#ontologyTable').tableFilter();
});
</script>
</head>

<body>

<h3>NCBO OBA (Open Biomedical Annotator) Ontologies available to Annotate</h3>
<table id="ontologyTable" class="tablesorter"> 
<thead> 
<tr>
 <th>ID</th>
 <th>NAME</th>
 <th>VERSION</th>
 <th>STATUS</th>
 <th>FORMAT</th>
</tr>
</thead> 
<tbody> 

<%
Document doc = com.uem.gsi.cleim.util.MyDOMParser.getDocument(Constants.NCBO_BASE+Constants.NCBO_ONTOL+"?apikey="+Constants.NCBO_APIKEY,0);
traverseTree(doc, out);
%>

<%! private void traverseTree(Node node, JspWriter out) throws Exception {
	    if(node == null) {
	       return;
	    }
	    int type = node.getNodeType();
	     
	    switch (type) {
	      case Node.DOCUMENT_NODE: {
	         traverseTree(((Document)node).getDocumentElement(),out);
	         break;
	      }
	      // handle element nodes
	      case Node.ELEMENT_NODE: {
	        String elementName = node.getNodeName();
	        NodeList childNodes = node.getChildNodes();      
          if(childNodes != null) {
           int length = childNodes.getLength();
           if(elementName.equals("ontologyBean")) {
          	 out.println("<tr>");
          	 paintConcept(node,out);
          	 out.println("</tr>");
           }else{
             for (int loopIndex = 0; loopIndex < length ; loopIndex++)
              traverseTree(childNodes.item(loopIndex),out);
           }
          
	        }
	        break;
	      }
	    }
    }
private void paintConcept(Node node2, JspWriter out2) throws Exception {
	if(node2 == null) {
		return;
	}
  int type = node2.getNodeType();
  switch (type) {
		case Node.ELEMENT_NODE: {
	    NodeList childNodes = node2.getChildNodes();      
      if(childNodes != null) {
       int length = childNodes.getLength();
       for (int loopIndex = 0; loopIndex < length ; loopIndex++)
         paintConcept(childNodes.item(loopIndex),out2);
      }
      break;
    }
	  case Node.TEXT_NODE: {
	    String data = node2.getNodeValue().trim();
      if((data.indexOf("\n")<0) && (data.length()>0)) {
				String parent=node2.getParentNode().getNodeName();
				if ((parent.equals("localOntologyId")) || parent.equals("name")
						|| parent.equals("version") || parent.equals("format") 
						|| parent.equals("status"))
				out2.println("<td>"+data+"</td>");
	    }
      break;
   }
  }
}

%>

</tbody> 
</table>
</body>
</html>
