<%@page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@page import="io.macgyver.core.Kernel" %>

<!DOCTYPE html>
<!--
This is a starter template page. Use this page to start your new project from
scratch. This page gets rid of all links and provides the needed markup only.
-->
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>MacGyver</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href="/resources/lte/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="/resources/lte/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. We have chosen the skin-blue for this starter
          page. However, you can choose any other skin. Make sure you
          apply the skin class to the body tag so the changes take effect.
    -->
    <link rel="stylesheet" href="/resources/lte/dist/css/skins/skin-blue.min.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
  </head>
  <!--
  BODY TAG OPTIONS:
  =================
  Apply one or more of the following classes to get the
  desired effect
  |---------------------------------------------------------|
  | SKINS         | skin-blue                               |
  |               | skin-black                              |
  |               | skin-purple                             |
  |               | skin-yellow                             |
  |               | skin-red                                |
  |               | skin-green                              |
  |---------------------------------------------------------|
  |LAYOUT OPTIONS | fixed                                   |
  |               | layout-boxed                            |
  |               | layout-top-nav                          |
  |               | sidebar-collapse                        |
  |               | sidebar-mini                            |
  |---------------------------------------------------------|
  -->
  <body class="hold-transition skin-blue sidebar-mini">
  <!-- jQuery 2.1.4 -->
  <script src="/resources/lte/plugins/jQuery/jQuery-2.1.4.min.js"></script>
  <!-- Bootstrap 3.3.5 -->
  <script src="/resources/lte/bootstrap/js/bootstrap.min.js"></script>


  <script src="/resources/lte/plugins/datatables/jquery.dataTables.min.js"></script>
  <script src="/resources/lte/plugins/datatables/dataTables.bootstrap.min.js"></script>
  <!-- SlimScroll -->
  <script src="/resources/lte/plugins/slimScroll/jquery.slimscroll.min.js"></script>
  <!-- FastClick -->
  <script src="/resources/lte/plugins/fastclick/fastclick.min.js"></script>

  <!-- AdminLTE App -->
  <script src="/resources/lte/dist/js/app.min.js"></script>
    <div class="wrapper">

      <!-- Main Header -->
      <header class="main-header">

        <!-- Logo -->
        <a href="/home" class="logo">
          <!-- mini logo for sidebar mini 50x50 pixels -->
          <span class="logo-mini"><b>Mac</b></span>
          <!-- logo for regular state and mobile devices -->
          <span class="logo-lg"><b>MacGyver</b></span>
        </a>

        <!-- Header Navbar -->
        <nav class="navbar navbar-static-top" role="navigation">
          <!-- Sidebar toggle button-->
          <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
          </a>
          <!-- Navbar Right Menu -->
          <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
              <!-- Messages: style can be found in dropdown.less-->
              

            
              <!-- User Account Menu -->
              <li class="dropdown user user-menu">
                <!-- Menu Toggle Button -->
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                  <!-- The user image in the navbar-->
                  <img src="/resources/images/profile-pic-300px.jpg" class="user-image" alt="User Image">
                  <!-- hidden-xs hides the username on small devices so only the image appears. -->
                  <span class="hidden-xs"><%=SecurityContextHolder.getContext().getAuthentication().getName() %></span>
                </a>
                <ul class="dropdown-menu">
                  <!-- The user image in the menu -->
                  <li class="user-header">
                    <img src="/resources/images/profile-pic-300px.jpg" class="img-circle" alt="User Image">
                    <p>
                     
                    </p>
                  </li>
                  <!-- Menu Body -->
                
                  <!-- Menu Footer-->
                  <li class="user-footer">
                  
                    <div class="pull-right">
                      <a href="/logout" class="btn btn-default btn-flat">Sign out</a>
                    </div>
                  </li>
                </ul>
              </li>
              <!-- Control Sidebar Toggle Button -->
              
            </ul>
          </div>
        </nav>
      </header>
      <!-- Left side column. contains the logo and sidebar -->
      <aside class="main-sidebar">

        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">



        
	      <!-- search form -->
	            <form action="/core/search" method="get" class="sidebar-form">
	              <div class="input-group">
	                <input type="text" name="q" class="form-control" placeholder="Search...">
	                <span class="input-group-btn">
	                  <button type="submit" name="search" id="search-btn" class="btn btn-flat"><i class="fa fa-search"></i></button>
	                </span>
	              </div>
	            </form>
	            <!-- /.search form -->

          <!-- Sidebar Menu -->
          <ul class="sidebar-menu">
		  
<%

def ui = io.macgyver.core.web.UIContext.forCurrentUser()


ui.getRootMenu().getItems().each { 


def styleVal = it.getStyle()

if (!it.getItems().isEmpty()) {

%>
            <li class="treeview <%=it.isSelected() ? "active" : "" %>">
              <a href="#"><i class="<%=styleVal%>"></i> <span><%=it.getLabel()%></span> <i class="fa fa-angle-left pull-right"></i></a>
			  <ul class="treeview-menu">
<%


it.getItems().each { sub ->

%>
<li><a href="<%=sub.getUrl()? sub.getUrl() : "#" %>" class="treeview <%=it.isSelected() ? "active" : "" %>"><i class="fa fa-circle-o"></i><%=sub.getLabel()%></a></li>
<%

}  

%>
			  </ul>
			</li> <!-- treeview -->

<%


}  // is collapsible

else {
%>
<li><a href="<%=it.getUrl()? it.getUrl() : "#" %>"><%=it.getLabel()%></a></li>
<%
}






}






%>


           
             
			
          </ul><!-- /.sidebar-menu -->
        </section>
        <!-- /.sidebar -->
      </aside>

      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
      <!--  <section class="content-header">
          <h1>
            Page Header XXXX
            <small>Optional description</small>
          </h1>-->
        <!--  <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
            <li class="active">Here</li>
          </ol>-->
        </section>

        <!-- Main content -->
        <section class="content">
		<g:layoutBody/>

        </section> <!-- /.content -->
      </div><!-- /.content-wrapper -->

    <!-- <footer class="main-footer">
           <div class="pull-right hidden-xs">
             <b>Version</b> 2.3.0
           </div>
           <strong>Copyright &copy; 2014-2015 <a href="http://almsaeedstudio.com">Almsaeed Studio</a>.</strong> All rights reserved.
         </footer> -->

      <!-- Control Sidebar -->
     
      <!-- Add the sidebar's background. This div must be placed
           immediately after the control sidebar -->
      <div class="control-sidebar-bg"></div>
    </div><!-- ./wrapper -->

    <!-- REQUIRED JS SCRIPTS -->


	

  </body>
</html>
