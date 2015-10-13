<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>

        <!-- Main content -->
        <section class="content">
<%
def ex = request.getAttribute("javax.servlet.error.exception")
%>      
            <div class="error-content">
              <h3><i class="fa fa-warning text-red"></i> <%=request.getAttribute("status")%> <%=request.getAttribute("exception")%>  <%=request.getAttribute("message")%></h3>
             <p>
<%
if (ex) {
	def sw = new StringWriter()
	def pw = new PrintWriter(sw)
	ex.printStackTrace(pw)
	pw.close()
	%>
	<pre>
	<%=sw%>
	</pre>

	<%
}
	
%>			 
			 </p>
              
            </div>
       
        </section><!-- /.content -->
   </body>
 </html>
 