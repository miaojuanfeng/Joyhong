		

		<div class="header-area">

			<h1 class="btn btn-primary btn-md"><i class="glyphicon glyphicon-check"></i>&nbsp;&nbsp;&nbsp;JoyHong CMS</h1>
			<div class="shadow-area"></div>

			<div class="menu-area">
				<div class="container-fluid">
					<div class="row">
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<h3 class="corpcolor-font">Link</h3>
							<ul>
								<li><a href="<?=base_url('cms/dashboard')?>">Dashboard</a></li>
								<li><a href="<?=base_url('main')?>" target="_blank">Website</a></li>
							</ul>
						</div>
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<h3 class="corpcolor-font">Content</h3>
							<ul>
								<li><a href="<?=base_url('cms/main')?>">Main</a></li>
								<li><a href="<?=base_url('cms/page')?>">Page</a></li>
								<li><a href="<?=base_url('cms/submenu')?>">Sub Menu</a></li>
								<li><a href="<?=base_url('cms/menu/select/order/menu_sort/ascend/desc')?>">Menu</a></li>
								<li><a href="<?=base_url('cms/news')?>">News</a></li>
							</ul>
						</div>
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<h3 class="corpcolor-font">Data preset</h3>
							<ul>
								<li><a href="<?=base_url('cms/catalogues')?>">Catalogues</a></li>
								<li><a href="<?=base_url('cms/book/select/order/book_sort/ascend/desc')?>">Book</a></li>
								<li><a href="<?=base_url('cms/banner')?>">Banner</a></li>
								<li><a href="<?=base_url('cms/ai')?>">Author/Illustrator</a></li>
								<li><a href="<?=base_url('cms/email')?>">Email</a></li>
							</ul>
						</div>
						
						<div class="menu-column-area col-lg-3 col-md-3 col-sm-6 col-xs-6 col-ms-6">
							<!-- <h3 class="corpcolor-font">System setting / log</h3>
							<ul>
								<li><a href="<?=base_url('cms/log')?>">System log</a></li>
							</ul> -->
							<h3 class="corpcolor-font">Permission / role / user</h3>
							<ul>
								<li><a href="<?=base_url('cms/role')?>">Role</a></li>
								<li><a href="<?=base_url('cms/user')?>">User</a></li>
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
				<li><a href="<%=request.getContextPath() %>/cms/user/logout">Logout: ${user_nickname}</a></li>
			</ul>
		</div>


