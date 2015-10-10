<%@page import="io.macgyver.core.Kernel" %>
<%@page import="com.google.common.collect.Lists" %>
<%@page import="com.google.common.collect.Ordering" %>
<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>

<section id="introduction">
  <h2 class="page-header"><a href="#introduction">Spring Beans</a></h2>
<table id="example2" class="table table-bordered table-hover">
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Class</th>
                      </tr>
                    </thead>
                    <tbody>
<%
def ctx = Kernel.getApplicationContext()
def list = Lists.newArrayList(ctx.getBeanDefinitionNames())
list.sort(Ordering.usingToString());
list.each {
%>
                      <tr>
                        <td><%=it%></td>
                        <td><%=ctx.getBean(it).getClass().getName()%></td>
                       
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