<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Category management</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=order-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>

		<script>
		function check_delete(id){
			if(confirm("Confirm?")){
				$('input[name="category_id"]').val(id);
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

					<h2 class="col-sm-12"><a href="<c:url value="/cms/category/${type}/select"></c:url>">Category management</a> > ${type} > ${method} category</h2>

					<div class="col-sm-12">
						<form:form name="update" method="post" modelAttribute="category">
							<input type="hidden" name="category_id" value="${category.id}" />
							<input type="hidden" name="referer" value="${referer}" />
							<div class="fieldset">
								<div class="row form-group">
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="name">Name <span class="highlight">*</span></label>
											<form:input id="name" path="name" type="text" class="form-control input-sm required" placeholder="Name" />
										</p>
									</div>
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Related information</h4>
										<p class="form-group">
											<label for="type">Type <span class="highlight">*</span></label>
											<form:select id="type" path="type" data-placeholder="Type" class="chosen-select required">
                                                <option value="sample" <c:if test="${type == 'sample'}">selected</c:if>>Sample</option>
                                                <option value="order" <c:if test="${type == 'order'}">selected</c:if>>Order</option>
										 	</form:select>
										</p>
									</div>
									<div class="col-sm-4 col-xs-12 pull-right">
									
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

					<h2 class="col-sm-12">Category management > ${type}</h2>

					<div class="content-column-area col-md-12 col-sm-12">

						<!--div class="fieldset left">
							<div class="search-area">

								<form role="form" method="get">
									<input type="hidden" name="category_id" />
									<table>
										<tbody>
											<tr>
												<td width="90%">
													<div class="row">
														<div class="col-sm-4">
															<input type="text" name="category_id" class="form-control input-sm" placeholder="#" value="" />
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
								<form name="list" action="<c:url value="/cms/category/delete"></c:url>" method="post">
									<input type="hidden" name="category_id" />
									<table class="list" id="category">
										<tbody>
											<tr>
												<th>#</th>
												<th>Name</th>
												<th>Type</th>
												<th>Create</th>
												<th>Modify</th>
												<th width="40"></th>
												<th width="40"></th>
												<th width="40" class="text-right">
													<a href="<c:url value="/cms/category/insert/${type}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Insert">
														<i class="glyphicon glyphicon-plus"></i>
													</a>
												</th>
											</tr>
											<c:forEach items="${category}" var="item">
											<tr id="<?=$value->category_id?>" class="list-row" onclick=""> <!-- the onclick="" is for fixing the iphone problem -->
												<td title="${item.id}">${item.id}</td>
												<td class="expandable">${item.name}</td>
												<td class="expandable">${item.type}</td>
												<td class="expandable"><fmt:formatDate  value="${item.createDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="expandable"><fmt:formatDate  value="${item.modifyDate}"  pattern="yyyy-MM-dd" /></td>
												<td class="text-right">
													<a href="<c:url value="/cms/order/select?category=${item.id}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Order">
														<i class="glyphicon glyphicon-shopping-cart"></i>
													</a>
												</td>
												<td class="text-right">
													<a href="<c:url value="/cms/category/update/${type}/${item.id}"></c:url>" class="btn btn-sm btn-primary" data-toggle="tooltip" title="Update">
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
												<a href="<c:url value="/cms/category/select/1"></c:url>" class="btn btn-sm btn-primary">&lt;&lt;</a>
											</c:if>
											<c:if test="${page != 1}">
												<a href="<c:url value="/cms/category/select/${page-1}"></c:url>" class="btn btn-sm btn-primary">&lt;</a>
											</c:if>
											<c:if test="${page-1 > 0}">
												<a href="<c:url value="/cms/category/select/${page-1}"></c:url>" class="btn btn-sm btn-primary">${page-1}</a>
											</c:if>
											<a href="<c:url value="/cms/category/select/${page}"></c:url>" class="btn btn-sm btn-primary disabled">${page}</a>
											<c:if test="${page+1 <= totalPage}">
												<a href="<c:url value="/cms/category/select/${page+1}"></c:url>" class="btn btn-sm btn-primary">${page+1}</a>
											</c:if>
											<c:if test="${page != totalPage}">
												<a href="<c:url value="/cms/category/select/${page+1}"></c:url>" class="btn btn-sm btn-primary">&gt;</a>
											</c:if>
											<c:if test="${page+1 < totalPage}">
												<a href="<c:url value="/cms/category/select/${totalPage}"></c:url>" class="btn btn-sm btn-primary">&gt;&gt;</a>
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