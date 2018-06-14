<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Ota management</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=order-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>

		<script>
		function check_delete(id){
			if(confirm("Confirm?")){
				$('input[name="ota_id"]').val(id);
				$('form[name="list"]').submit();
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

					<h2 class="col-sm-12"><a href="<c:url value="/cms/ota/select"></c:url>">Ota management</a> > ${method} ota</h2>

					<div class="col-sm-12">
						<form:form name="update" method="post" modelAttribute="ota" enctype="multipart/form-data">
							<input type="hidden" name="ota_id" value="${ota.id}" />
							<form:input type="hidden" path="orderId" />
							<input type="hidden" name="referer" value="${referer}" />
							<div class="fieldset">
								<div class="row form-group">
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="last_version">Last version <span class="highlight"></span></label>
											<form:input id="last_version" path="lastVersion" type="number" min="0" class="form-control input-sm" placeholder="Last version" />
										</p>
										<p class="form-group">
											<label for="version_desc">Version description <span class="highlight"></span></label>
											<form:textarea id="version_desc" rows="10" path="versionDesc" class="form-control input-sm" placeholder="Version description"></form:textarea>
										</p>
									</div>
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Related information</h4>
										<p class="form-group">
											<label for="ota_file">Ota file <span class="highlight"></span></label>
											<input id="ota_file" name="ota_file" type="file" class="form-control input-sm" placeholder="Ota file" />
										</p>
										<p class="form-group">
											<label for="download_link">Download link <span class="highlight"></span></label>
											<form:input id="download_link" path="downloadLink" type="hidden" class="form-control input-sm" placeholder="Download link" />
											<input id="download_link" name="download_link" type="text" class="form-control input-sm" placeholder="Download link" readonly="true" value="${ossUrl}${ota.downloadLink}" />
										</p>
									</div>
									<div class="col-sm-4 col-xs-12 pull-right"></div>
								</div>

								<div class="row">
									<div class="col-xs-12">
										<button type="submit" class="btn btn-sm btn-primary"><i class="glyphicon glyphicon-floppy-disk"></i> Save</button>
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

					<h2 class="col-sm-12">Ota management</h2>

					<div class="content-column-area col-md-12 col-sm-12">

						<%-- <div class="fieldset">
							<div class="search-area">

								<form role="form" method="get">
									<!-- <input type="hidden" name="ota_id" /> -->
									<table>
										<tbody>
											<tr>
												<td width="90%">
													<div class="row">
														<div class="col-sm-3">
															<input type="text" name="ota_token" class="form-control input-sm" placeholder="Ota token" value="" />
														</div>
														<div class="col-sm-3">
															<input type="text" name="ota_fcm_token" class="form-control input-sm" placeholder="Ota fcm token" value="" />
														</div>
														<div class="col-sm-3"></div>
														<div class="col-sm-3"></div>
													</div>
												</td>
												<td valign="top" width="10%" class="text-right">
													<button type="submit" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Search" name="action" value="search">
														<i class="glyphicon glyphicon-search"></i>
													</button>
												</td>
											</tr>
										</tbody>
									</table>
								</form>

							</div>
						</div> --%>
						<div class="fieldset full">

							<div class="list-area">
								<form name="list" action="<c:url value="/cms/ota/delete"></c:url>" method="post">
									<input type="hidden" name="ota_id" />
									<table class="list" id="ota">
										<tbody>
											<tr>
												<th>#</th>
												<th>Last version</th>
												<th>Download link</th>
												<th>Create</th>
												<th>Modify</th>
												<th width="40"></th>
												<th width="40" class="text-right">
													<a href="<c:url value="/cms/ota/insert${parameters}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Insert">
														<i class="glyphicon glyphicon-plus"></i>
													</a>
												</th>
											</tr>
											<c:forEach items="${ota}" var="item">
											<tr id="<?=$value->ota_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
												<td title="${item.id}">${item.id}</td>
												<td class="expandable">${item.lastVersion}</td>
												<td class="expandable"><a href="${ossUrl}${item.downloadLink}" target="_blank"><c:if test="${item.downloadLink != ''}">${ossUrl}</c:if>${item.downloadLink}</a></td>
												<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="text-right">
													<a href="<c:url value="/cms/ota/update/${item.id}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Update">
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
												<a href="<c:url value="/cms/ota/select/1${parameters}"></c:url>" class="btn btn-sm btn-primary">&lt;&lt;</a>
											</c:if>
											<c:if test="${page != 1}">
												<a href="<c:url value="/cms/ota/select/${page-1}${parameters}"></c:url>" class="btn btn-sm btn-primary">&lt;</a>
											</c:if>
											<c:if test="${page-1 > 0}">
												<a href="<c:url value="/cms/ota/select/${page-1}${parameters}"></c:url>" class="btn btn-sm btn-primary">${page-1}</a>
											</c:if>
											<a href="<c:url value="/cms/ota/select/${page}${parameters}"></c:url>" class="btn btn-sm btn-primary disabled">${page}</a>
											<c:if test="${page+1 <= totalPage}">
												<a href="<c:url value="/cms/ota/select/${page+1}${parameters}"></c:url>" class="btn btn-sm btn-primary">${page+1}</a>
											</c:if>
											<c:if test="${page != totalPage}">
												<a href="<c:url value="/cms/ota/select/${page+1}${parameters}"></c:url>" class="btn btn-sm btn-primary">&gt;</a>
											</c:if>
											<c:if test="${page+1 < totalPage}">
												<a href="<c:url value="/cms/ota/select/${totalPage}${parameters}"></c:url>" class="btn btn-sm btn-primary">&gt;&gt;</a>
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