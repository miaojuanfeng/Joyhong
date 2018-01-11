<div class="header-area">

			<h1 class="btn btn-primary btn-md"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;&nbsp;JoyHong CMS</h1>
			<div class="shadow-area"></div>

			<div class="menu-area">
				<div class="container-fluid">
					<div class="row">
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<h3 class="corpcolor-font">Order / Device</h3>
							<ul>
								<li><a href="<c:url value="/cms/order/select"></c:url>">Order</a></li>
								<li><a href="<c:url value="/cms/device/select"></c:url>">Device</a></li>
							</ul>
						</div>
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<h3 class="corpcolor-font">User</h3>
							<ul>
								<li><a href="<c:url value="/cms/user/all/select"></c:url>">All</a></li>
								<li><a href="<c:url value="/cms/user/app/select"></c:url>">App</a></li>
							</ul>
						</div>
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<h3 class="corpcolor-font">User</h3>
							<ul>
								<li><a href="<c:url value="/cms/user/facebook/select"></c:url>">Facebook</a></li>
								<li><a href="<c:url value="/cms/user/twitter/select"></c:url>">Twitter</a></li>
							</ul>
						</div>
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<h3 class="corpcolor-font">System</h3>
							<ul>
								<li><a href="<c:url value="/cms/config/update"></c:url>">Config</a></li>
							</ul>
						</div>

					</div>
				</div>
			</div>

		</div> <!-- header-area -->


		<div class="btn-group show-myself">
			<a href="#" class="btn btn-primary btn-sm dropdown-toggle" data-toggle="dropdown">
				<i class="glyphicon glyphicon-user"> ${user_nickname}</i>
			</a>
			<ul class="dropdown-menu dropdown-menu-right" role="menu">
				<li><a href="<c:url value="/cms/user/logout"></c:url>">Logout: ${user_nickname}</a></li>
			</ul>
		</div>