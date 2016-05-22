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
                        <th>Status</th>
						<th>Last Heartbeat (secs ago)</th>
                        
                      </tr>
                    </thead>
                    <tbody>
<%
request.getAttribute("list").each {
%>
                      <tr>
                        <td><%=it.getId()%><%=it.isSelf()? "  (This Node)" : ""%></td>
                        <td><%=it.getHost()%></td>
                        <td><%=it.isPrimary() ? "PRIMARY" : "SECONDARY"%></td>
						 <td><%=java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-it.getUpdateTs())%></td>
                      </tr>
<%
}
%>
					  </table>
</section>


   </body>
 </html>