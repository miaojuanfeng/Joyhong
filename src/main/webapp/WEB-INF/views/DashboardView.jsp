<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="en">
	<head>
		<title>Dashboard</title>

		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

		<%@ include file="inc/headArea.jsp" %>

		<script>
		$(function(){

			/* chart1 */
			var chart1 = echarts.init(document.getElementById('chart1'));
			var option = {
				title: {
					text: ''
				},
				tooltip: {},
				legend: {
					data: ['data1','data2','data3']
				},
				xAxis: {
					data: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一', '十二']
				},
				yAxis: {},
				series: [
					{
						name: 'data1',
						type: 'bar',
						itemStyle: {normal: {color:'rgba(88,151,251,0.7)'}},
						data: [500, 2050, 3600, 1000, 1000, 2000, 500, 2000, 3600, 1000, 1000, 2000]
					},
					{
						name: 'data2',
						type: 'bar',
						itemStyle: {normal: {color:'rgba(00,151,251,0.7)'}},
						data: [2500, 1800, 1100, 1500, 3000, 500, 500, 2500, 3300, 800, 2000, 3000]
					},
					{
						name: 'data3',
						type: 'bar',
						itemStyle: {normal: {color:'rgba(00,151,00,0.7)'}},
						data: [2500, 1800, 1100, 1300, 5000, 100, 520, 500, 5300, 100, 2000, 3000]
					}
				]
			};
			chart1.setOption(option);

			/* chart2 */
			var chart2 = echarts.init(document.getElementById('chart2'));
			option = {
				title : {
					text: 'Platform',
					subtext: '',
					x:'center'
				},
				tooltip : {
					trigger: 'item',
					formatter: "{a} <br/>{b} : {c} ({d}%)"
				},
				legend: {
					orient: 'vertical',
					left: 'left',
					data: ['facebook','twitter','app']
				},
				series : [{
					name: 'platform',
					type: 'pie',
					radius : '55%',
					center: ['50%', '60%'],
					data:[
						{value:'${facebookCount}', name:'facebook'},
						{value:'${twitterCount}', name:'twitter'},
						{value:'${appCount}', name:'app'}
					],
					itemStyle: {
						emphasis: {
							shadowBlur: 10,
							shadowOffsetX: 0,
							shadowColor: 'rgba(0, 0, 0, 0.5)'
						}
					}
				}]
			};
			chart2.setOption(option);

			/* resize */
			$(window).on('resize', function(){
				chart1.resize();
				chart2.resize();
			});
		});
		</script>
	</head>

	<body>

		<%@ include file="inc/headerArea.jsp" %>

		
		<div class="content-area">

			<div class="container-fluid">
				<div class="row">

					<h2 class="col-sm-12">Dashboard</h2>

					<div class="content-column-area col-md-12 col-sm-12">
						<div class="fieldset">

							<div class="row">
								<div class="col-md-4 col-sm-6 bottom-buffer-10">
									<div class="dashboard-box-top">
										<i class="glyphicon glyphicon-tasks pull-left"></i>
										<span class="pull-right">
											<div class="summary text-right"><fmt:formatNumber type="number" value="${orderCount}" /></div>
											<div class="text-right">Order total</div>
										</span>
										<div class="clearfix"></div>
									</div>
									<div class="dashboard-box-bottom">
										<a href="<c:url value="/cms/order/select"></c:url>">
											<span class="pull-left">VIEW MORE</span>
											<span class="pull-right"><i class="glyphicon glyphicon-chevron-right"></i></span>
											<div class="clearfix"></div>
										</a>
									</div>
								</div>
								<div class="col-md-4 col-sm-6 bottom-buffer-10">
									<div class="dashboard-box-top">
										<i class="glyphicon glyphicon-hdd pull-left"></i>
										<span class="pull-right">
											<div class="summary text-right"><fmt:formatNumber type="number" value="${deviceCount}" /></div>
											<div class="text-right">Device total</div>
										</span>
										<div class="clearfix"></div>
									</div>
									<div  class="dashboard-box-bottom">
										<a href="<c:url value="/cms/device/select"></c:url>">
											<span class="pull-left">VIEW MORE</span>
											<span class="pull-right"><i class="glyphicon glyphicon-chevron-right"></i></span>
											<div class="clearfix"></div>
										</a>
									</div>
								</div>
								<div class="col-md-4 col-sm-6 bottom-buffer-10">
									<div class="dashboard-box-top">
										<i class="glyphicon glyphicon-user pull-left"></i>
										<span class="pull-right">
											<div class="summary text-right"><fmt:formatNumber type="number" value="${userCount}" /></div>
											<div class="text-right">User total</div>
										</span>
										<div class="clearfix"></div>
									</div>
									<div class="dashboard-box-bottom">
										<a href="<c:url value="/cms/user/all/select"></c:url>">
											<span class="pull-left">VIEW MORE</span>
											<span class="pull-right"><i class="glyphicon glyphicon-chevron-right"></i></span>
											<div class="clearfix"></div>
										</a>
									</div>
								</div>
							</div>

							<div class="list-area no-overflow-x">
								<div class="row">
									<div class="col-md-6 col-sm-12" style="margin-bottom:10px;">
										<h4 class="corpcolor-font"><i class="glyphicon glyphicon-bullhorn"> Demo</i></h4>
										<div id="chart1" class="chart-area"></div>
									</div>
									<div class="col-md-6 col-sm-12" style="margin-bottom:10px;">
										<h4 class="corpcolor-font"><i class="glyphicon glyphicon-user"> User</i></h4>
										<div id="chart2" class="chart-area"></div>
									</div>
								</div>
								<div class="row">
									<div class="col-md-6 col-sm-12" style="margin-bottom:10px;">
										
									</div>
									<div class="col-md-6 col-sm-12" style="margin-bottom:10px;">
										
									</div>
								</div>
							</div> <!-- list-area -->	

						</div>
					</div>
				</div>
			</div>

		</div>

		<%@ include file="inc/footerArea.jsp" %>

	</body>
</html>