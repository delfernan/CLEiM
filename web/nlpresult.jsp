<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<title>NLP RESULTS</title>
<link rel="stylesheet" type="text/css" href="css/style_tablesorter.css">
<script src="js/jquery-1.4.2.min.js" type="text/javascript" charset="utf-8"></script>
<script src="js/jquery.tablesorter.min.js" type="text/javascript" charset="utf-8"></script>
<script type="text/javascript" src="js/ui/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.mouse.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.resizable.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.draggable.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.position.js"></script>
<script type="text/javascript" src="js/ui/jquery.ui.dialog.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.2.custom.min.js"></script>
<script type="text/javascript" src="js/picnet.table.filter.min.js"></script>

<script>
$(document).ready(function() { 
  $("#nlpResult").tablesorter(); 
  $('#nlpResult').tableFilter();      
});
</script>
<style type="text/css">
  body{ font: 70% "Trebuchet MS", sans-serif;}
  a:link {color: #000000;}
</style>
</head>

<body>
<table id="nlpResult" class="tablesorter"> 
<thead> 
 <tr>
 <th>LAN</th>
 <th>CONCEPT</th>
 <th>SOURCE</th>
 <th>GROUP</th>
 <!-- <th>FROM</th> -->
 <!-- <th>TO</th> -->
 <th>URL</th>
<!-- <th>CONTEXT</th> -->
 </tr>
</thead> 
<tbody> 

<%
out.print(request.getAttribute("nlpResults").toString());
%>

</tbody> 
</table>
</body>
</html>
