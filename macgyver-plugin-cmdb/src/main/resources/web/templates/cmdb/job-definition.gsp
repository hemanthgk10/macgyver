<%@page import="io.macgyver.core.Kernel" %>
<%@page import="com.google.common.collect.Lists" %>
<%@page import="com.google.common.collect.Ordering" %>
<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>
<%
def job = request.getAttribute("job")
def name = job.path("name").asText(job.path("id").asText())

%>
<a href="/cmdb/job-definitions">Back to Job Catalog</a>
<section id="services">

  <div class="box">
                  <div class="box-header">
                      <h3 class="box-title"><%=name%></h3>
                  </div><!-- /.box-header -->
                  <div class="box-body">
<table class="table table-bordered">
                  <%
                  job.fieldNames().sort().each { it->
                      def attr = it
                      def val = job.get(attr)
                      boolean display = true;
                      
                      if (attr.equals("id")) {
                          display = false
                      }
                      if (attr.equals("updateTs")) {
                          display = false
                      }
                      
                      if (display) {
                          def printValue = val.asText()
                          if (val.isArray()) {
                              printValue = ""
                              val.each { x ->
                                  if (printValue) {
                                      printValue += "<br/>"
                                  }
                                  printValue += x.asText()
                              }
                        
                          }
                      
                      %>
                    <tr>
                      <td style="width: 100px"><%=attr%></td>
                      <td><%=printValue%></td>
                     
                    </tr>
                    <% } }%>
                  </table>
                  </div>
  </div>
</section>

  
   </body>
 </html>