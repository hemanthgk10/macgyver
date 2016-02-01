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
              <h3 class="box-title">App Catalog</h3>
                  </div><!-- /.box-header -->
                  <div class="box-body">
<table id="appsTable" class="table table-bordered table-hover">
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Description</th>
                    </thead>
                    <tbody>
					
<%

	request.getAttribute("results").each {
        def id = it.path("appId").asText()
        def name = it.path("name").asText(id)
        def path = "/cmdb/app-definitions/${URLEncoder.encode(id)}"
   
%>
		<tr><td><a href="${path}"><%=name%></a></td><td><%=it.path("description").asText()%></td></tr>
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
        $('#appsTable').DataTable({
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