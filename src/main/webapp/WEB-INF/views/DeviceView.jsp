<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Device management</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=order-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>

		<script>
		function check_delete(id){
			if(confirm("Confirm?")){
				$('input[name="device_id"]').val(id);
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

					<h2 class="col-sm-12"><a href="<c:url value="/cms/device/select"></c:url>">Device management</a> > ${method} device</h2>

					<div class="col-sm-12">
						<form:form name="update" method="post" modelAttribute="device">
							<input type="hidden" name="device_id" value="${device.id}" />
							<input type="hidden" name="referer" value="${referer}" />
							<div class="fieldset">
								<div class="row form-group">
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="device_token">Device token <span class="highlight">*</span></label>
											<form:input id="device_token" path="deviceToken" type="text" class="form-control input-sm required" placeholder="Device token" />
										</p>
										<p class="form-group">
											<label for="device_fcm_token">Device fcm token <span class="highlight"></span></label>
											<form:input id="device_fcm_token" path="deviceFcmToken" type="text" class="form-control input-sm" placeholder="Device fcm token" />
										</p>
										<p class="form-group">
											<label for="login_time">Login time <span class="highlight"></span></label>
											<input id="login_time" name="loginTime" type="text" class="form-control input-sm" placeholder="Login time" value="<fmt:formatDate  value="${device.loginTime}"  pattern="yyyy-MM-dd HH:mm:ss" />" />
										</p>
										<p class="form-group">
											<label for="heartbeat_time">Heartbeat time <span class="highlight"></span></label>
											<input id="heartbeat_time" name="heartbeatTime" type="text" class="form-control input-sm" placeholder="Heartbeat time" value="<fmt:formatDate  value="${device.heartbeatTime}"  pattern="yyyy-MM-dd HH:mm:ss" />" />
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
														<th>Username</th>
														<th>Account</th>
														<th>Nickname</th>
														<th>Platform</th>
														<th>Create</th>
														<th>Modify</th>
													</tr>
													<c:forEach items="${user}" var="item">
													<tr id="<?=$value->device_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
														<td title="${item.id}">${item.id}</td>
														<td class="expandable">${item.username}</td>
														<td class="expandable">${item.number}</td>
														<td class="expandable">${item.nickname}</td>
														<td class="expandable">${item.platform}</td>
														<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
														<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
													</tr>
													</c:forEach>
		
													<c:if test="${userTotal==0}">
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
										<button type="button" class="btn btn-sm btn-warning" onclick="check_generate(${device.id});"><i class="glyphicon glyphicon-list"></i> Generate device token</button>
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

					<h2 class="col-sm-12">Device management</h2>

					<div class="content-column-area col-md-12 col-sm-12">

						<!--div class="fieldset left">
							<div class="search-area">

								<form role="form" method="get">
									<input type="hidden" name="device_id" />
									<table>
										<tbody>
											<tr>
												<td width="90%">
													<div class="row">
														<div class="col-sm-4">
															<input type="text" name="device_id" class="form-control input-sm" placeholder="#" value="" />
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
								<form name="list" action="<c:url value="/cms/device/delete"></c:url>" method="post">
									<input type="hidden" name="device_id" />
									<table class="list" id="device">
										<tbody>
											<tr>
												<th>#</th>
												<th>Device token</th>
												<th>Device Fcm token</th>
												<th>Alive time / s</th>
												<th>Create</th>
												<th>Modify</th>
												<th width="40"></th>
												<%-- <th width="40" class="text-right">
													<a href="<c:url value="/cms/device/insert"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Insert">
														<i class="glyphicon glyphicon-plus"></i>
													</a>
												</th> --%>
											</tr>
											<c:forEach items="${device}" var="item">
											<tr id="<?=$value->device_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
												<td title="${item.id}">${item.id}</td>
												<td class="expandable">${item.deviceToken}</td>
												<td class="expandable">${item.deviceFcmToken}</td>
												<c:set var="aliveTime" value="${item.heartbeatTime.time - item.loginTime.time}"/>
												<td class="expandable"><fmt:formatNumber value="${aliveTime/1000}" pattern="#0"/></td>
												<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="text-right">
													<a href="<c:url value="/cms/device/update/${item.id}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Detail">
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
									<div class="page-area">
										<span class="btn btn-sm btn-default">${totalRecord}</span>
										<c:if test="${totalRecord > 0}">
										<span class="pagination-area">
											<c:if test="${page-1 > 1}">
												<a href="<c:url value="/cms/device/select/1"></c:url>" class="btn btn-sm btn-primary">&lt;&lt;</a>
											</c:if>
											<c:if test="${page != 1}">
												<a href="<c:url value="/cms/device/select/${page-1}"></c:url>" class="btn btn-sm btn-primary">&lt;</a>
											</c:if>
											<c:if test="${page-1 > 0}">
												<a href="<c:url value="/cms/device/select/${page-1}"></c:url>" class="btn btn-sm btn-primary">${page-1}</a>
											</c:if>
											<a href="<c:url value="/cms/device/select/${page}"></c:url>" class="btn btn-sm btn-primary disabled">${page}</a>
											<c:if test="${page+1 <= totalPage}">
												<a href="<c:url value="/cms/device/select/${page+1}"></c:url>" class="btn btn-sm btn-primary">${page+1}</a>
											</c:if>
											<c:if test="${page != totalPage}">
												<a href="<c:url value="/cms/device/select/${page+1}"></c:url>" class="btn btn-sm btn-primary">&gt;</a>
											</c:if>
											<c:if test="${page+1 < totalPage}">
												<a href="<c:url value="/cms/device/select/${totalPage}"></c:url>" class="btn btn-sm btn-primary">&gt;&gt;</a>
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