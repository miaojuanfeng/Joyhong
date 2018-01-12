<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Config management</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=order-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>
		
	</head>

	<body>

		<%@ include file="inc/headerArea.jsp" %>







































	<c:if test="${method == 'insert' || method == 'update'}">
		<div class="content-area">

			<div class="container-fluid">
				<div class="row">

					<h2 class="col-sm-12"><a href="<c:url value="/cms/config/update"></c:url>">Config management</a> > ${method} config</h2>

					<div class="col-sm-12">
						<form method="post">
							<div class="fieldset">
								<div class="row form-group">
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Administrator information</h4>
										<p class="form-group">
											<label for="username">Username <span class="highlight">*</span></label>
											<input id="username" name="username" type="text" class="form-control input-sm required" placeholder="Username" value="${username}" />
										</p>
										<p class="form-group">
											<label for="password">Password <span class="highlight"></span></label>
											<input id="password" name="password" type="password" class="form-control input-sm" placeholder="Password" />
										</p>
										<h4 class="corpcolor-font">Version information</h4>
										<p class="form-group">
											<label for="last_version">Last version <span class="highlight"></span></label>
											<input id="last_version" name="last_version" type="text" class="form-control input-sm" placeholder="Last version" value="${last_version}" />
										</p>
										<p class="form-group">
											<label for="download_link">Download link <span class="highlight"></span></label>
											<input id="download_link" name="download_link" type="text" class="form-control input-sm" placeholder="Download link" value="${download_link}" />
										</p>
									</div>
									<div class="col-sm-8 col-xs-12 pull-right">
										
									</div>
								</div>

								<div class="row">
									<div class="col-xs-4">
										<button type="submit" class="btn btn-sm btn-primary"><i class="glyphicon glyphicon-floppy-disk"></i> Save</button>
									</div>
									<div class="col-xs-8">
									
									</div>
								</div>

							</div>
						</form>
					</div>

				</div>
			</div>




		</div>
	</c:if>	

		










































	<c:if test="${method == 'select'}">
		<div class="content-area">

			<div class="container-fluid">
				<div class="row">

					<h2 class="col-sm-12">Config management</h2>

					<div class="content-column-area col-md-12 col-sm-12">

						<!--div class="fieldset left">
							<div class="search-area">

								<form role="form" method="get">
									<input type="hidden" name="config_id" />
									<table>
										<tbody>
											<tr>
												<td width="90%">
													<div class="row">
														<div class="col-sm-4">
															<input type="text" name="config_id" class="form-control input-sm" placeholder="#" value="" />
														</div>
														<div class="col-sm-4"></div>
														<div class="col-sm-4"></div>
													</div>
												</td>
												<td valign="top" width="10%" class="text-right">
													<button type="submit" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Search">
														<i class="glyphicon glyphicon-search"></i>
													</button>
												</td>
											</tr>
										</tbody>
									</table>
								</form>

							</div>
						</div-->
						<div class="fieldset full">

							<div class="list-area">
								<form name="list" action="<c:url value="/cms/config/delete"></c:url>" method="post">
									<input type="hidden" name="config_id" />
									<table class="list" id="config">
										<tbody>
											<tr>
												<th>#</th>
												<th>Config token</th>
												<th>Config Fcm token</th>
												<th>Create</th>
												<th>Modify</th>
												<th width="40"></th>
												<%-- <th width="40" class="text-right">
													<a href="<c:url value="/cms/config/insert"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Insert">
														<i class="glyphicon glyphicon-plus"></i>
													</a>
												</th> --%>
											</tr>
											<c:forEach items="${config}" var="item">
											<tr id="<?=$value->config_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
												<td title="${item.id}">${item.id}</td>
												<td class="expandable">${item.configToken}</td>
												<td class="expandable">${item.configFcmToken}</td>
												<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="text-right">
													<a href="<c:url value="/cms/config/update/${item.id}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Detail">
														<i class="glyphicon glyphicon glyphicon-hdd"></i>
													</a>
												</td>
												<%-- <td class="text-right">
													<a onclick="check_delete(${item.id});" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Delete">
														<i class="glyphicon glyphicon-remove"></i>
													</a>
												</td> --%>
											</tr>
											</c:forEach>

											<c:if test="${totalRecord == 0}">
											<tr class="list-row">
												<td colspan="10"><a href="#" class="btn btn-sm btn-primary">No record found</a></td>
											</tr>
											</c:if>

										</tbody>
									</table>
									<div class="config-area">
										<span class="btn btn-sm btn-default">${totalRecord}</span>
										<c:if test="${totalRecord > 0}">
										<span class="pagination-area">
											<c:if test="${config-1 > 1}">
												<a href="<c:url value="/cms/config/select/1"></c:url>" class="btn btn-sm btn-primary">&lt;&lt;</a>
											</c:if>
											<c:if test="${config != 1}">
												<a href="<c:url value="/cms/config/select/${config-1}"></c:url>" class="btn btn-sm btn-primary">&lt;</a>
											</c:if>
											<c:if test="${config-1 > 0}">
												<a href="<c:url value="/cms/config/select/${config-1}"></c:url>" class="btn btn-sm btn-primary">${config-1}</a>
											</c:if>
											<a href="<c:url value="/cms/config/select/${config}"></c:url>" class="btn btn-sm btn-primary disabled">${config}</a>
											<c:if test="${config+1 <= totalPage}">
												<a href="<c:url value="/cms/config/select/${config+1}"></c:url>" class="btn btn-sm btn-primary">${config+1}</a>
											</c:if>
											<c:if test="${config != totalPage}">
												<a href="<c:url value="/cms/config/select/${config+1}"></c:url>" class="btn btn-sm btn-primary">&gt;</a>
											</c:if>
											<c:if test="${config+1 < totalPage}">
												<a href="<c:url value="/cms/config/select/${totalPage}"></c:url>" class="btn btn-sm btn-primary">&gt;&gt;</a>
											</c:if>
										</span>
										</c:if>
									</div>
								</form>
							</div> <!-- list-area -->                           
						</div>
					</div>
				</div>
			</div>

		</div>
	</c:if>











































		<%@ include file="inc/footerArea.jsp" %>

	</body>
</html>