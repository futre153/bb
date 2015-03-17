<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
  <head>
  	<META http-equiv=Content-Type content="text/html; charset=utf-8" />
  	<%@ page language="java" session="false" isThreadSafe="true" isErrorPage="false" contentType="text/html;charset=UTF-8"%>
	<%@ taglib uri = "WEB-INF/TabularDataHandler.tld" prefix = "tdh" %>
    <title>RMA autorizácie</title>
    <link rel="stylesheet" type="text/css" href="styles/correspondents.css" />
    <style>
    	
    </style>
    <script type='text/javascript' src='scripts/correspondents.js' charset='utf-8'></script>
  </head>
  <body onload="return win_onload();">
  <div class="content">
  	<h1>Zoznam vymenených RMA autorizácií<br />
  	BIC: <%=request.getParameter("srcFile").substring(request.getParameter("srcFile").indexOf("POBN")<0?0:request.getParameter("srcFile").indexOf("POBN"),request.getParameter("srcFile").indexOf("POBN")<0?0:request.getParameter("srcFile").indexOf("POBN")+8)%><br />
  	platnosť od: <tdh:modify sourceFile='<%=request.getParameter("srcFile")%>' /></h1>
  	<tdh:plaintable columns = "Correspondent,Service,R. Status,R. Validity,S. Status,S. Validity,Stav" className="list_of_correspondent" sourceFile='<%=request.getParameter("srcFile")%>' columnSeparator="	" hasHeader='true'>
  	</tdh:plaintable>
	</div>
  </body>
</html>