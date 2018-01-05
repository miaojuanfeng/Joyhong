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
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="order_menu">Menu <span class="highlight">*</span></label>
											<select id="z_order_menu_menu_id" name="z_order_menu_menu_id[]" data-placeholder="Menu" class="chosen-select required" multiple="multiple">
												
											</select>
										</p>
										<?php
										if($this->router->fetch_method() == 'update'){
											$order_photo_link = '/assets/uploads/order/'.$order->order_photo;
											$order_photo = $_SERVER['DOCUMENT_ROOT'].'/minedition'.$order_photo_link;
											if(file_exists($order_photo)){
												echo '<h4 class="corpcolor-font">Order photo</h4>';
												echo '<img class="box-bg" src="'.base_url($order_photo_link).'?'.time().'" />';
											}
											// $order_photos_link = '/assets/images/order_photos/'.$order->order_id.'/';
											// foreach($order_photos as $key => $value){
											// 	echo ($key == 0) ? '<h4 class="corpcolor-font">Photos</h4>' : '';
											// 	echo '<div class="box-bg" id="box_'.$key.'" style="background-image:url('.$order_photos_link.$value.'?'.time().');">';
											// 	echo '<div class="box-function-area">';
											// 	echo '<div class="text-right">';
											// 	echo '<input type="checkbox" id="'.$key.'" name="photos_remove[]" value="'.$value.'" />';
											// 	echo '<a id="a_'.$key.'" onclick="check_photos_delete('.$key.');" class="btn btn-sm btn-primary" data-toggle="tooltip" title="åªé¤">';
											// 	echo '<i class="glyphicon glyphicon-remove"></i>';
											// 	echo '</a>';
											// 	echo '</div>';
											// 	echo '</div>';
											// 	echo '</div>';
											// }
										}
										?>
									</div>
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Basic information</h4>
										<p class="form-group">
											<label for="order_name">Name <span class="highlight">*</span></label>
											<input id="order_name" name="order_name" type="text" class="form-control input-sm required" placeholder="Name" value="<?=$order->order_name?>" />
										</p>
										<p class="form-group">
											<label for="order_author">Author <span class="highlight">*</span></label>
											<select id="order_author" name="order_author" data-placeholder="author" class="chosen-select">
												<?php
												foreach($author as $key => $value){
													$selected = ($value->ai_id == $order->order_author) ? ' selected="selected"' : "" ;
													echo '<option value="'.$value->ai_id.'"'.$selected.'>'.$value->ai_name.'</option>';
												}
												?>
											</select>
										</p>
										<p class="form-group">
											<label for="order_illustrator">Illustrator <span class="highlight">*</span></label>
											<select id="order_illustrator" name="order_illustrator" data-placeholder="illustrator" class="chosen-select">
												<?php
												foreach($illustrator as $key => $value){
													$selected = ($value->ai_id == $order->order_illustrator) ? ' selected="selected"' : "" ;
													echo '<option value="'.$value->ai_id.'"'.$selected.'>'.$value->ai_name.'</option>';
												}
												?>
											</select>
										</p>
										<p class="form-group">
											<label for="order_isbn">ISBN <span class="highlight">*</span></label>
											<input id="order_isbn" name="order_isbn" type="text" class="form-control input-sm required" placeholder="ISBN" value="<?=$order->order_isbn?>" />
										</p>
										<p class="form-group">
											<label for="order_price">Price <span class="highlight">*</span></label>
											<input id="order_price" name="order_price" type="text" class="form-control input-sm required" placeholder="Price" value="<?=$order->order_price?>" />
										</p>
										<p class="form-group">
											<label for="order_pages">Pages <span class="highlight">*</span></label>
											<input id="order_pages" name="order_pages" type="text" class="form-control input-sm required" placeholder="Pages" value="<?=$order->order_pages?>" />
										</p>
										<p class="form-group">
											<label for="order_size">Size <span class="highlight">*</span></label>
											<input id="order_size" name="order_size" type="text" class="form-control input-sm required" placeholder="Size" value="<?=$order->order_size?>" />
										</p>
										<p class="form-group">
											<label for="order_shopping_cart">Shopping cart</label>
											<input id="order_shopping_cart" name="order_shopping_cart" type="text" class="form-control input-sm" placeholder="Shopping cart" value="<?=$order->order_shopping_cart?>" />
											<small>Please type the link with http://</small>
										</p>
										<p class="form-group">
											<label for="order_sort">Sort</label>
											<input id="order_sort" name="order_sort" type="text" class="form-control input-sm" placeholder="Size" value="<?=$order->order_sort?>" />
										</p>
										<p class="form-group">
											<label for="order_hide">Hide?</label>
											<select id="order_hide" name="order_hide" data-placeholder="Hide" class="chosen-select">
												<option value="1" <?php if($order->order_hide == 1) echo "selected='selected'"; ?>>Y</option>
												<option value="0" <?php if($order->order_hide == 0) echo "selected='selected'"; ?>>N</option>
											</select>
										</p>
										<?php
										if( $this->session->userdata('country_id') == 2 ){
										?>
										<p class="form-group">
											<label for="order_sodia_code">Sodia code <span class="highlight">*</span></label>
											<input id="order_sodia_code" name="order_sodia_code" type="text" class="form-control input-sm required" placeholder="Sodia code" value="<?=$order->order_sodia_code?>" />
										</p>
										<?php
										}
										?>
									</div>
									<div class="col-sm-4 col-xs-12">
										<h4 class="corpcolor-font">Related information</h4>
										<p class="form-group">
											<?php if($this->router->fetch_method() == 'update'){ ?>
											<label for="order_photo">Order cover <span class="highlight">(360px * 360px)</span></label>
											<input id="order_photo" name="order_photo" type="file" accept="image/*" />
											<?php }else{ ?>
											<label for="order_photo">Order cover <span class="highlight">* (360px * 360px)</span></label>
											<input id="order_photo" name="order_photo" type="file" accept="image/*" class="required" />
											<?php } ?>
										</p>
										<p class="form-group">
											<?php if($this->router->fetch_method() == 'update'){ ?>
											<label for="order_cover">Order cover (High resolutions) <span class="highlight">!!!</span></label>
											<input id="order_cover" name="order_cover" type="file" accept="image/*" />
											<?php }else{ ?>
											<label for="order_cover">Order cover (High resolutions) <span class="highlight">!!!</span></label>
											<input id="order_cover" name="order_cover" type="file" accept="image/*" class="required" />
											<?php } ?>
										</p>
										<p class="form-group">
											<?php if($this->router->fetch_method() == 'update'){ ?>
											<label for="order_file">Download file <span class="highlight">!!!</span></label>
											<input id="order_file" name="order_file" type="file" accept="image/*" />
											<?php }else{ ?>
											<label for="order_file">Download file <span class="highlight">!!!</span></label>
											<input id="order_file" name="order_file" type="file" accept="image/*" class="required" />
											<?php } ?>
										</p>
										<p class="form-group">
											<label>Flip order link <span class="highlight">!!!</span></label>
											<input type="text" class="form-control input-sm" placeholder="Flip order link" />
											<small>Please type the link with http://</small>
										</p>
										<p class="form-group">
											<label for="order_info">Information</label>
											<textarea id="order_info" name="order_info" class="form-control input-sm" placeholder="Information"><?=$order->order_info?></textarea>
										</p>
										<p class="form-group">
											<label for="order_desc">Description</label>
											<textarea id="order_desc" name="order_desc" class="form-control input-sm summernote" placeholder="Information"><?=$order->order_desc?></textarea>
										</p>
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