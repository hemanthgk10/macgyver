<%@page import="io.macgyver.core.Kernel" %>
<%@page import="com.google.common.collect.Lists" %>
<%@page import="com.google.common.collect.Ordering" %>
<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>

<section id="services">
  <h2 class="page-header">Services</h2>
<table id="example2" class="table table-bordered table-hover">
                    <thead>
                      <tr>
                        <th>Service</th>
                        <th>Provider</th>
                    </thead>
                    <tbody>
					
<%

	request.getAttribute("services").each {
%>
		<tr><td><%=it.path("serviceName").asText()%></td><td></td></tr>
<%	
	}

%>
					</tbody>

					  </table>
</section>


   </body>
 </html>