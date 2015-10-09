<%@page import="io.macgyver.core.Kernel" %>
<%@page import="com.google.common.collect.Lists" %>
<%@page import="com.google.common.collect.Ordering" %>
<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>

<section id="introduction">
  <h2 class="page-header"><a href="#introduction">Scripts</a></h2>
<table id="example2" class="table table-bordered table-hover">
                    <thead>
                      <tr>
                        <th>Resource</th>
                        <th>Provider</th>
						<th>Action</th>
                      </tr>
                    </thead>
                    <tbody>
<%
request.getAttribute("list").each {
%>
                      <tr>
                        <td><%=it.path("resource").asText()%></td>
                        <td><%=it.path("providerType").asText()%></td>
                       <td>
<%
if (it.path("executeAllowed").asBoolean()) {
%>
<form action="/admin/scripts" method="post" >
<input type="hidden" name="hash" value="<%=it.path("hash").asText()%>" />
<button class="btn btn-block btn-primary btn-xs">Execute</button>
</form>
<%
}
%>
					   </td>
                      </tr>
<%
}
%>
					  </table>
</section><!-- /#introduction -->

	<script>
	$(document).ready(function() {
	    $('#example2').DataTable();
	} );
	</script>
   </body>
 </html>