<%@page import="io.macgyver.core.Kernel" %>
<%@page import="com.google.common.collect.Lists" %>
<%@page import="com.google.common.collect.Ordering" %>
<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>

<section id="services">

  <div class="box">
                  <div class="box-header">
              <h3 class="box-title">App Instances</h3>
                  </div><!-- /.box-header -->
                  <div class="box-body">
<table id="example2" class="table table-bordered table-hover">
                    <thead>
                      <tr>
                        <th>Environment</th>
						 <th>App</th>
                        <th>Host</th>
						  
						   <th>Version</th>
						   <th>Revision</th>
						   <th>Branch</th>
						   <th>Last Contact</th>
					   </tr>
                    </thead>
                    <tbody>
					
<%
def pt = new org.ocpsoft.prettytime.PrettyTime()
	request.getAttribute("results").each {
%>
		<tr>
		<td><%=it.path("environment").asText()%></td>
		<td><%=it.path("appId").asText()%></td>
		<td><%=it.path("host").asText()%></td>
		<td><%=it.path("version").asText()%></td>
		<td><%=it.path("scmRevision").asText()%></td>
		<td><%=it.path("scmBranch").asText()%></td>
		<td><%=pt.format(new Date(it.path("lastContactTs").asLong(0)))%></td>
		</tr>
<%	
	}

%>
					</tbody>

					  </table>
				  </div>
			  </div>
		  </section>
    <script>
      $(function () {
        $('#example2').DataTable({
          "paging": true,
          "lengthChange": false,
          "searching": true,
          "ordering": true,
          "info": false,
          "autoWidth": true,
		  "iDisplayLength": 15
        });
      });
    </script>


   </body>
 </html>