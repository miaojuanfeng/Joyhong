<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Login</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

		<link rel="stylesheet" href="<%=request.getContextPath() %>/assets/css/jquery-ui.css">
		<link rel="stylesheet" href="<%=request.getContextPath() %>/assets/css/bootstrap.css">
		<link rel="stylesheet" href="<%=request.getContextPath() %>/assets/css/chosen.css">
		<link rel="stylesheet" href="<%=request.getContextPath() %>/assets/css/cms-style.css">
		<link rel="stylesheet" href="<%=request.getContextPath() %>/assets/css/login.css">

		<script src="<%=request.getContextPath() %>/assets/js/jquery-1.11.3.min.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/jquery-ui.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/jquery-ui.multidatespicker.js"></script>
		<!-- <script src="assets/js/modernizr-custom.min.js"></script> -->
		<script src="<%=request.getContextPath() %>/assets/js/bootstrap.min.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/chosen.jquery.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/jquery.maskedinput.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/accounting.min.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/jquery.validate.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/additional-methods.min.js"></script>
		<script src="<%=request.getContextPath() %>/assets/js/cms-function.js"></script>
	</head>

	<body>
		<div class="login-area">
			<div class="error">${error}</div>
			<div class="container-fluid">
				<div class="row">
					<div class="content-column-area col-sm-7 col-xs-12">
						<div class="fieldset left">
							<h2 class="corpcolor-font"><span>Login</span></h2>
							<form method="post">
								<table>
									<tbody>
										<tr>
											<td><label for="user_username">Username</label></td>
											<td><input type="text" id="user_username" name="user_username" class="form-control input-sm required" placeholder="Username" value="${user_username}" /></td>
										</tr>
										<tr>
											<td><label for="user_password">Password</label></td>
											<td><input type="password" id="user_password" name="user_password" class="form-control input-sm required" placeholder="Password" value="${user_password}" /></td>
										</tr>
										<tr>
											<td></td>
											<td><button type="submit" class="btn-login btn btn-sm btn-primary"><i class="glyphicon glyphicon-send"></i> Login</button></td>
										</tr>
									</tbody>
								</table>
							</form>
						</div>
					</div>
					<div class="content-column-area col-sm-5 col-xs-12">
						<div class="fieldset right">
							<p>Please use IE9.0 version or later / Firefox, Chrome, Safari 2015-01-01 version or later</p>
							<p>Javascript is required</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>