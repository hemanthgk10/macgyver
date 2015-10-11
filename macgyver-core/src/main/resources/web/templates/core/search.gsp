<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>

<section id="introduction">

<div class="box">
            <div class="box-header with-border">
              <h3 class="box-title">Search Results</h3>
              <div class="box-tools pull-right">
                <button class="btn btn-box-tool" data-widget="collapse" data-toggle="tooltip" title="Collapse"><i class="fa fa-minus"></i></button>
                
              </div>
            </div>
            <div class="box-body">
              <%
			  request.getAttribute("results").each {
				  def url = it.getUrl()
				  if (url) {
				  %>
				  <p>
				  <a href="<%=it.getUrl()%>"><%=it.getLabel()%></a></p>
				  <%
			  	  }
			  }
				
              %>
            </div><!-- /.box-body -->
           
          </div><!-- /.box -->
</section><!-- /#introduction -->

   </body>
 </html>
