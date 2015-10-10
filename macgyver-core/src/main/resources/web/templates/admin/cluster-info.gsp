<%@page import="io.macgyver.core.Kernel" %>
<%@page import="com.google.common.collect.Lists" %>
<%@page import="com.google.common.collect.Ordering" %>
<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>

<section id="cluster-info">
  <h2 class="page-header"><a href="#cluster-info">Cluster Info</a></h2>
<table id="example2" class="table table-bordered table-hover">
                    <thead>
                      <tr>
                        <th>Id</th>
                        <th>Host</th>
						<th>Ignite Version</th>
						<th>Heartbeat</th>
                      </tr>
                    </thead>
                    <tbody>
<%
request.getAttribute("list").each {
%>
                      <tr>
                        <td><%=it.path("id").asText()%></td>
                        <td><%=it.path("host").asText()%></td>
                        <td><%=it.path("igniteVersion").asText()%></td>
						 <td><%=it.path("lastHeartbeatSecs").asText()%></td>
                      </tr>
<%
}
%>
					  </table>
</section>


   </body>
 </html>