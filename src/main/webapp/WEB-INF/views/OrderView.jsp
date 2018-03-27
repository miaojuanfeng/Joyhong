<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Order management</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=order-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>

		<script>
		function check_delete(id){
			if(confirm("Confirm?")){
				$('input[name="order_id"]').val(id);
				$('form[name="list"]').submit();
			}else{
				return false;
			}
		}
		
		function check_generate(id){
			if(confirm("Confirm?")){
				$('input[name="order_id"]').val(id);
				$('form[name="update"]').attr("action", "<c:url value="/cms/order/generate"></c:url>");
				$('form[name="update"]').submit();
			}else{
				return false;
			}
		}
		</script>
	</head>

	<body>

		<%@ include file="inc/headerArea.jsp" %>







































	<c:if test="${method == 'insert' || method == 'update'}">
		<div class="content-area">

			<div class="container-fluid">
				<div class="row">

					<h2 class="col-sm-12"><a href="<c:url value="/cms/order/select"></c:url>">Order management</a> > ${method} order</h2>

					<div class="col-sm-12">
						<form:form name="update" method="post" modelAttribute="order">
							<%-- <input type="hidden" name="order_id" value="${order.id}" /> --%>
							<input type="hidden" name="referer" value="${referer}" />
							<div class="fieldset">
								<div class="row form-group">
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="order_code">Order code <span class="highlight">*</span></label>
											<form:input id="order_code" path="orderCode" type="text" class="form-control input-sm required" placeholder="Order code" minlength="4" maxlength="4" />
										</p>
										<p class="form-group">
											<label for="machine_code">Machine code <span class="highlight">*</span></label>
											<form:input id="machine_code" path="machineCode" type="text" class="form-control input-sm required" placeholder="Machine code" minlength="2" maxlength="2" />
										</p>
										<p class="form-group">
											<label for="dealer_code">Dealer code <span class="highlight">*</span></label>
											<form:input id="dealer_code" path="dealerCode" type="text" class="form-control input-sm required" placeholder="Dealer code" minlength="2" maxlength="2" />
										</p>
										<p class="form-group">
											<label for="hardware_code">Hardware code <span class="highlight">*</span></label>
											<form:input id="hardware_code" path="hardwareCode" type="text" class="form-control input-sm required" placeholder="Hardware code" minlength="2" maxlength="2" />
										</p>
										<p class="form-group">
											<label for="order_qty">Order qty <span class="highlight">*</span></label>
											<form:input id="order_qty" path="orderQty" type="number" min="0" class="form-control input-sm required" placeholder="Order qty" />
										</p>
										<p class="form-group">
											<label for="last_version">Last version <span class="highlight"></span></label>
											<form:input id="last_version" path="lastVersion" type="number" min="0" class="form-control input-sm" placeholder="Last version" />
										</p>
										<p class="form-group">
											<label for="download_link">Download link <span class="highlight"></span></label>
											<form:input id="download_link" path="downloadLink" type="text" class="form-control input-sm" placeholder="Download link" />
										</p>
										<p class="form-group">
											<label for="version_desc">Version Description <span class="highlight"></span></label>
											<form:textarea id="version_desc" rows="10" path="versionDesc" class="form-control input-sm" placeholder="Version Description"></form:textarea>
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
														<th>
															Device token
														</th>
														<th>
															FCM token
														</th>
														<th>
															Create
														</th>
														<th>
															Modify
														</th>
													</tr>
													<c:forEach items="${device}" var="item">
													<tr id="<?=$value->device_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
														<td title="${item.id}">${item.id}</td>
														<td class="expandable">${item.deviceToken}</td>
														<td class="expandable">${item.deviceFcmToken}</td>
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
										<button type="submit" class="btn btn-sm btn-primary"><i class="glyphicon glyphicon-floppy-disk"></i> Save</button>
									</div>
									<div class="col-xs-8">
										<c:if test="${method == 'update'}">
										<button type="button" class="btn btn-sm btn-warning" onclick="check_generate(${order.id});"><i class="glyphicon glyphicon-list"></i> Generate device token</button>
										</c:if>
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

					<h2 class="col-sm-12">Order management</h2>

					<div class="content-column-area col-md-12 col-sm-12">

						<!--div class="fieldset left">
							<div class="search-area">

								<form role="form" method="get">
									<input type="hidden" name="order_id" />
									<table>
										<tbody>
											<tr>
												<td width="90%">
													<div class="row">
														<div class="col-sm-4">
															<input type="text" name="order_id" class="form-control input-sm" placeholder="#" value="" />
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
								<form name="list" action="<c:url value="/cms/order/delete"></c:url>" method="post">
									<input type="hidden" name="order_id" />
									<table class="list" id="order">
										<tbody>
											<tr>
												<th>#</th>
												<th>Order code</th>
												<th>Machine code</th>
												<th>Dealer code</th>
												<th>Hardware code</th>
												<th>Order qty</th>
												<th>Create</th>
												<th>Modify</th>
												<th width="40"></th>
												<th width="40" class="text-right">
													<a href="<c:url value="/cms/order/insert"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Insert">
														<i class="glyphicon glyphicon-plus"></i>
													</a>
												</th>
											</tr>
											<c:forEach items="${order}" var="item">
											<tr id="<?=$value->order_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
												<td title="${item.id}">${item.id}</td>
												<td class="expandable">${item.orderCode}</td>
												<td class="expandable">${item.machineCode}</td>
												<td class="expandable">${item.dealerCode}</td>
												<td class="expandable">${item.hardwareCode}</td>
												<td class="expandable">${item.orderQty}</td>
												<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="text-right">
													<a href="<c:url value="/cms/order/update/${item.id}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Update">
														<i class="glyphicon glyphicon-pencil"></i>
													</a>
												</td>
												<td class="text-right">
													<a onclick="check_delete(${item.id});" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Delete">
														<i class="glyphicon glyphicon-remove"></i>
													</a>
												</td>
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
												<a href="<c:url value="/cms/order/select/1"></c:url>" class="btn btn-sm btn-primary">&lt;&lt;</a>
											</c:if>
											<c:if test="${page != 1}">
												<a href="<c:url value="/cms/order/select/${page-1}"></c:url>" class="btn btn-sm btn-primary">&lt;</a>
											</c:if>
											<c:if test="${page-1 > 0}">
												<a href="<c:url value="/cms/order/select/${page-1}"></c:url>" class="btn btn-sm btn-primary">${page-1}</a>
											</c:if>
											<a href="<c:url value="/cms/order/select/${page}"></c:url>" class="btn btn-sm btn-primary disabled">${page}</a>
											<c:if test="${page+1 <= totalPage}">
												<a href="<c:url value="/cms/order/select/${page+1}"></c:url>" class="btn btn-sm btn-primary">${page+1}</a>
											</c:if>
											<c:if test="${page != totalPage}">
												<a href="<c:url value="/cms/order/select/${page+1}"></c:url>" class="btn btn-sm btn-primary">&gt;</a>
											</c:if>
											<c:if test="${page+1 < totalPage}">
												<a href="<c:url value="/cms/order/select/${totalPage}"></c:url>" class="btn btn-sm btn-primary">&gt;&gt;</a>
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