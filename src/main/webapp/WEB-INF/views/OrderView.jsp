<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Order management</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=order-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>

		<script>
		$(function(){
			$('input[name="order_id"]').focus();

			/* pagination */
			$('.pagination-area>a, .pagination-area>strong').addClass('btn btn-sm btn-primary');
			$('.pagination-area>strong').addClass('disabled');
		});

		function check_delete(id){
			if(confirm("Confirm?")){
				$('input[name="order_id"]').val(id);
				$('form[name="list"]').submit();
			}else{
				return false;
			}
		}
		</script>
	</head>

	<body>

		<%@ include file="inc/headerArea.jsp" %>

		<script>
		$(function(){
			$('.summernote').summernote({
				toolbar: [
					['style', ['style']],
					['font', ['bold', 'italic', 'underline', 'clear']],
					['color', ['color']],
					['para', ['ul', 'ol', 'paragraph']]
				],
				minHeight: 77
			});

			/* pagination */
			$('.pagination-area>a, .pagination-area>strong').addClass('btn btn-sm btn-primary');
			$('.pagination-area>strong').addClass('disabled');
		});
		</script>







































	<c:if test="${router == 'insert' || router == 'update'}">
		<div class="content-area">

			<div class="container-fluid">
				<div class="row">

					<h2 class="col-sm-12"><a href="<c:url value="/cms/order/select"></c:url>">Order management</a> > ${router} order</h2>

					<div class="col-sm-12">
						<form method="post" enctype="multipart/form-data">
							<input type="hidden" name="order_id" value="<?=$order->order_id?>" />
							<input type="hidden" name="order_country" value="<?=$this->session->userdata('country_id')?>" />
							<input type="hidden" name="referrer" value="<?=$this->agent->referrer()?>" />
							<div class="fieldset">
								<div class="row">
									<div class="col-sm-4 col-xs-12 pull-right">
										
									</div>
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="order_code">Order code <span class="highlight">*</span></label>
											<input id="order_code" name="order_code" type="text" class="form-control input-sm required" placeholder="Order code" value="" maxlength="4" />
										</p>
										<p class="form-group">
											<label for="machine_code">Machine code <span class="highlight">*</span></label>
											<!--  <select id="machine_code" name="machine_code" data-placeholder="Machine code" class="chosen-select">
												<?php
												foreach($author as $key => $value){
													$selected = ($value->ai_id == $order->order_author) ? ' selected="selected"' : "" ;
													echo '<option value="'.$value->ai_id.'"'.$selected.'>'.$value->ai_name.'</option>';
												}
												?>
											</select> -->
											<input id="machine_code" name="machine_code" type="text" class="form-control input-sm required" placeholder="Machine code" value="" maxlength="2" />
										</p>
										<p class="form-group">
											<label for="dealer_code">Dealer code <span class="highlight">*</span></label>
											<!-- <select id="order_illustrator" name="order_illustrator" data-placeholder="illustrator" class="chosen-select">
												<?php
												foreach($illustrator as $key => $value){
													$selected = ($value->ai_id == $order->order_illustrator) ? ' selected="selected"' : "" ;
													echo '<option value="'.$value->ai_id.'"'.$selected.'>'.$value->ai_name.'</option>';
												}
												?>
											</select> -->
											<input id="dealer_code" name="dealer_code" type="text" class="form-control input-sm required" placeholder="Dealer code" value="" maxlength="2" />
										</p>
										<p class="form-group">
											<label for="hardware_code">Hardware code <span class="highlight">*</span></label>
											<input id="hardware_code" name="hardware_code" type="text" class="form-control input-sm required" placeholder="Hardware code" value="" maxlength="2" />
										</p>
										<p class="form-group">
											<label for="order_qty">Order qty <span class="highlight">*</span></label>
											<input id="order_qty" name="order_qty" type="number" min="0" class="form-control input-sm required" placeholder="Order qty" value="" />
										</p>
									</div>
									<div class="col-sm-4 col-xs-12">
										
									</div>
								</div>

								<div class="row">
									<div class="col-xs-12">
										<button type="submit" class="btn btn-sm btn-primary"><i class="glyphicon glyphicon-floppy-disk"></i> Save</button>
									</div>
								</div>

							</div>
						</form>
					</div>

				</div>
			</div>




		</div>
	</c:if>	

		










































	<c:if test="${router == 'select'}">
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
									<input type="hidden" name="order_delete_reason" />
									<table class="list" id="order">
										<tbody>
											<tr>
												<th>#</th>
												<th>
													<%-- <a href="<?=get_order_link('order_isbn')?>">
														Order Token <i class="glyphicon glyphicon-sort corpcolor-font"></i>
													</a> --%>
													Order Token
												</th>
												<th>
													<%-- <a href="<?=get_order_link('order_name')?>">
														FCM Token <i class="glyphicon glyphicon-sort corpcolor-font"></i>
													</a> --%>
													FCM Token
												</th>
												<th>
													<%-- <a href="<?=get_order_link('order_modifydate')?>">
														Create <i class="glyphicon glyphicon-sort corpcolor-font"></i>
													</a> --%>
													Create
												</th>
												<th>
													<%-- <a href="<?=get_order_link('order_modifydate')?>">
														Modify <i class="glyphicon glyphicon-sort corpcolor-font"></i>
													</a> --%>
													Modify
												</th>
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
												<td class="expandable">${item.orderToken}</td>
												<td class="expandable">${item.orderFcmToken}</td>
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

											<?php if(!$orders){ ?>
											<tr class="list-row">
												<td colspan="10"><a href="#" class="btn btn-sm btn-primary">No record found</a></td>
											</tr>
											<?php } ?>

										</tbody>
									</table>
									<%-- <div class="page-area">
										<span class="btn btn-sm btn-default"><?php print_r($num_rows); ?></span>
										<?=$this->pagination->create_links()?>
									</div> --%>
								</form>
							</div> <!-- list-area -->                           
						</div>
					</div>
					<!-- <div class="content-column-area col-md-3 col-sm-12">
						<div class="fieldset right">
							<div class="list-area">
								<table>
									<tbody>
										<tr>
											<th>#</th>
											<th>Name</th>
										</tr>
										<tr class="list-row"> the onclick="" is for fixing the iphone problem
											<td>test</td>
											<td>test</td>
										</tr>
									</tbody>
								</table>
							</div> list-area
						</div>
					</div> -->
				</div>
			</div>

		</div>
	</c:if>











































		<%@ include file="inc/footerArea.jsp" %>

	</body>
</html>