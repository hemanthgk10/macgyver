<html>
<head>
	<meta name="layout" content="default" />


	</head>
	
<body>
<%
        def tokenUrl = request.getAttribute("tokenUrl")
        def secret = request.getAttribute("secret")
        def inputDisabled = ((!secret) && !tokenUrl)
        def expirationMinutes = request.getAttribute("expirationMinutes")
 %>
<section id="introduction">

<%
if (request.getParameter("token") && !secret) {
%>
<div class="alert alert-danger alert-dismissable">
                    <button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
                    <h4><i class="icon fa fa-ban"></i> Not Found!</h4>
                  	The secret you requested has expired or was not found.
                  </div>
<%
}
%>
<%
if (tokenUrl ) {
%>
<div class="callout callout-success">
                  
                    <p>Share this URL with others. They will be able to retreive the secret for <%=expirationMinutes%> minutes.</p>
                  </div>
 <%
 }
 %>
   <div class="col-md-10">

		    <div class="box box-info">
        <div class="box-header with-border">
          <h3 class="box-title">Snap Share</h3>
        </div> <!-- /.box-header -->
        <!-- form start -->
        <form class="form-horizontal" method="post" action="/core/snap-share" role="form">
               <div class="box-body">
        
        <%

        
        if (tokenUrl) {
            %>
            <div class="form-group">
              <label for="token" class="col-sm-2 control-label">Sharing URL</label>
              <div class="col-sm-10">
              <input type="text" class="form-control" id="token" placeholder="Enter text to share securely...." name="tokenUrl" value="<%=tokenUrl%>" readonly>
              </div>
			  
            </div>           
            
            <%
        }
        else {
        %>
            
              <div class="form-group">
                      <label for="secret" class="col-sm-2 control-label">Secret Text</label>
                      <div class="col-sm-10">
                      <textarea id="secret" name="secret" class="form-control" rows="3" placeholder="Enter ..." <%=inputDisabled ? "" : "disabled"%> ><%=secret%></textarea>
                      </div>
              </div>	  
       
        <%
        }
        %>   
           

		   
		   
          </div><!-- /.box-body -->
         
         
         <%
         if ((!secret) && !tokenUrl) {
         %>
          <div class="box-footer">

            <button type="submit" class="btn btn-info pull-right" name="launch">Share</button>
          </div><!-- /.box-footer -->
          <%}%>
         
     		</form>
     	  </div><!-- /.box -->


           
          </div><!-- /.box -->
</section><!-- /#introduction -->

   </body>
 </html>
