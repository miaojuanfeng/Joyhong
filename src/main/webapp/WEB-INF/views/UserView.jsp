<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>User management</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=order-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>

		<script>
		function check_delete(id){
			if(confirm("Confirm?")){
				$('input[name="user_id"]').val(id);
				$('form[name="list"]').submit();
			}else{
				return false;
			}
		}
		
		function check_back(){
			var referer = $('input[name="referer"]').val();
			window.location.href = referer;
		}
		</script>
	</head>

	<body>

		<%@ include file="inc/headerArea.jsp" %>







































	<c:if test="${method == 'insert' || method == 'update'}">
		<div class="content-area">

			<div class="container-fluid">
				<div class="row">

					<h2 class="col-sm-12"><a href="<c:url value="/cms/user/select"></c:url>">User management</a> > ${method} user</h2>

					<div class="col-sm-12">
						<form:form name="update" method="post" modelAttribute="user">
							<input type="hidden" name="user_id" value="${user.id}" />
							<input type="hidden" name="referer" value="${referer}" />
							<div class="fieldset">
								<div class="row form-group">
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="username">Username <span class="highlight">*</span></label>
											<form:input id="username" path="username" type="text" class="form-control input-sm required" placeholder="Username" />
										</p>
										<p class="form-group">
											<label for="number">Account <span class="highlight">*</span></label>
											<form:input id="number" path="number" type="number" class="form-control input-sm required" placeholder="Number" />
										</p>
										<p class="form-group">
											<label for="nickname">Nickname <span class="highlight">*</span></label>
											<form:input id="nickname" path="nickname" type="text" class="form-control input-sm required" placeholder="Nickname" />
										</p>
										<p class="form-group">
											<label for="profile_image">Profile image <span class="highlight"></span></label>
											<form:input id="profile_image" path="profileImage" type="text" class="form-control input-sm" placeholder="Profile image" />
										</p>
										<p class="form-group">
											<label for="platform">Platform <span class="highlight">*</span></label>
											<form:input id="platform" path="platform" type="text" class="form-control input-sm required" placeholder="Platform" />
										</p>
										<p class="form-group">
											<label for="accepted">Accepted <span class="highlight">*</span></label>
											<form:input id="accepted" path="accepted" type="text" class="form-control input-sm" placeholder="Accepted" />
										</p>
									</div>
									<div class="col-sm-8 col-xs-12 pull-right">
										<c:if test="${method == 'update'}">
										<h4 class="corpcolor-font">Related information</h4>
										<div class="list-area">
											<table class="list" id="device">
												<tbody>
													<tr>
														<th>#</th>
														<th>Device token</th>
														<th>Device name</th>
														<th>Create</th>
														<th>Modify</th>
													</tr>
													<c:forEach items="${device}" var="item">
													<tr id="<?=$value->device_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
														<td title="${item.id}">${item.id}</td>
														<td class="expandable">${item.deviceToken}</td>
														<td class="expandable">${item.deviceName}</td>
														<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
														<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
													</tr>
													</c:forEach>
		
													<c:if test="${deviceTotal==0}">
													<tr class="list-row">
														<td colspan="10"><a href="#" class="btn btn-sm btn-primary">No record found</a></td>
													</tr>
													</c:if>
		
												</tbody>
											</table>
										</div>
										</c:if>
									</div>
								</div>

								<div class="row">
									<div class="col-xs-4">
										<button type="button" class="btn btn-sm btn-primary" onclick="check_back();"><i class="glyphicon glyphicon-chevron-left"></i> Back</button>
									</div>
									<div class="col-xs-8">
										<%-- <c:if test="${method == 'update'}">
										<button type="button" class="btn btn-sm btn-warning" onclick="check_generate(${user.id});"><i class="glyphicon glyphicon-list"></i> Generate device token</button>
										</c:if> --%>
									</div>
								</div>

							</div>
						</form:form>
					</div>

				</div>
			</div>




		</div>
	</c:if>	

		










































	<c:if test="${method == 'select'}">
		<div class="content-area">

			<div class="container-fluid">
				<div class="row">

					<h2 class="col-sm-12">User management</h2>

					<div class="content-column-area col-md-12 col-sm-12">

						<!--div class="fieldset left">
							<div class="search-area">

								<form role="form" method="get">
									<input type="hidden" name="user_id" />
									<table>
										<tbody>
											<tr>
												<td width="90%">
													<div class="row">
														<div class="col-sm-4">
															<input type="text" name="user_id" class="form-control input-sm" placeholder="#" value="" />
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
								<form name="list" action="<c:url value="/cms/user/delete"></c:url>" method="post">
									<input type="hidden" name="user_id" />
									<table class="list" id="user">
										<tbody>
											<tr>
												<th>#</th>
												<th>Username</th>
												<th>Account</th>
												<th>Nickname</th>
												<th>Profile image</th>
												<th>Platform</th>
												<th>Accepted</th>
												<th>Create</th>
												<th>Modify</th>
												<th width="40"></th>
												<%-- <th width="40" class="text-right">
													<a href="<c:url value="/cms/user/insert"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Insert">
														<i class="glyphicon glyphicon-plus"></i>
													</a>
												</th> --%>
											</tr>
											<c:forEach items="${user}" var="item">
											<tr id="<?=$value->user_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
												<td title="${item.id}">${item.id}</td>
												<td class="expandable">${item.username}</td>
												<td class="expandable">${item.number}</td>
												<td class="expandable">${item.nickname}</td>
												<td class="expandable"><c:if test="${item.profileImage != ''}"><a href="${item.profileImage}" target="_blank">[Show]</a></c:if></td>
												<td class="expandable">${item.platform}</td>
												<td class="expandable">${item.accepted}</td>
												<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="text-right">
													<a href="<c:url value="/cms/user/update/${item.id}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Detail">
														<i class="glyphicon glyphicon-user"></i>
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
									<div class="page-area">
										<span class="btn btn-sm btn-default">${totalRecord}</span>
										<c:if test="${totalRecord > 0}">
										<span class="pagination-area">
											<c:if test="${page-1 > 1}">
												<a href="<c:url value="/cms/user/select/${type}/1"></c:url>" class="btn btn-sm btn-primary">&lt;&lt;</a>
											</c:if>
											<c:if test="${page != 1}">
												<a href="<c:url value="/cms/user/select/${type}/${page-1}"></c:url>" class="btn btn-sm btn-primary">&lt;</a>
											</c:if>
											<c:if test="${page-1 > 0}">
												<a href="<c:url value="/cms/user/select/${type}/${page-1}"></c:url>" class="btn btn-sm btn-primary">${page-1}</a>
											</c:if>
											<a href="<c:url value="/cms/user/select/${type}/${page}"></c:url>" class="btn btn-sm btn-primary disabled">${page}</a>
											<c:if test="${page+1 <= totalPage}">
												<a href="<c:url value="/cms/user/select/${type}/${page+1}"></c:url>" class="btn btn-sm btn-primary">${page+1}</a>
											</c:if>
											<c:if test="${page != totalPage}">
												<a href="<c:url value="/cms/user/select/${type}/${page+1}"></c:url>" class="btn btn-sm btn-primary">&gt;</a>
											</c:if>
											<c:if test="${page+1 < totalPage}">
												<a href="<c:url value="/cms/user/select/${type}/${totalPage}"></c:url>" class="btn btn-sm btn-primary">&gt;&gt;</a>
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