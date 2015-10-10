<%@page import="io.macgyver.core.Kernel" %>
<%@page import="com.google.common.collect.Lists" %>
<%@page import="com.google.common.collect.Ordering" %>
<html>
<head>
	<meta name="layout" content="default" />

	</head>
	
<body>

<section id="cluster-info">

<div class="box box-primary" style="width: 800px">
                <div class="box-header with-border">
                  <h3 class="box-title">Encrypt String</h3>
                </div><!-- /.box-header -->
  <form role="form" method="post" action="/core/admin/encrypt-string">
                    <div class="box-body">
                      <div class="form-group">
                        <label for="plaintext1">Plain Text</label>
                        <input type="password" name="plaintext" class="form-control" id="plaintext" placeholder="">
                      </div>
					  <div class="form-group">
					                        <label>Encryption Key</label>
					                        <select class="form-control" name="alias" >
<%
request.getAttribute("aliases").each {
%>
<option><%=it%></option>
<%}%>
					                          
					                        </select>
					                      </div>
					  <div class="form-group">
					                        <label>Textarea</label>
					                        <textarea readonly class="form-control" rows="3" placeholder=""><%=request.getAttribute("ciphertext")%></textarea>
					                      </div>
                    </div><!-- /.box-body -->

                    <div class="box-footer">
                      <button type="submit" class="btn btn-primary">Submit</button>
                    </div>
                  </form>
				  </div>
</section>


   </body>
 </html>