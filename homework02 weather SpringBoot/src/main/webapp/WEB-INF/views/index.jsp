<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="my" uri="/WEB-INF/tld/mytags.tld"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/styles.css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/scripts/chartloader.js"></script>
<script src="${pageContext.request.contextPath}/scripts/jquery-3.2.1.min.js"></script>
<script	src="${pageContext.request.contextPath}/scripts/popper.min.js"></script>
<script	src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
<script	src="${pageContext.request.contextPath}/scripts/myScript.js"></script>


<title>Weather Info</title>
</head>
<body>
	<div class="container-xl mt-3">
		<div class="alert alert-secondary" role="alert">
			<h4>Информация о среднесуточной температуре</h4>
		</div>
		<div class="chartWithOverlay">
			<div id="chart_div"></div>
			<div class="card text-right" id="overlay">
				<div class="card-body">
					<my:average days="${daysController.selectedDays}" value="average"></my:average>
					<h6 class="card-title" style="color: grey">Средняя за период:</h6>
					<h3 class="card-text">${average}&#8451;</h3>
				</div>
			</div>
			<div id="selectors" style="width: 150px;">
				<div class="btn-group">
					<button class="btn btn-secondary dropdown-toggle" type="button"
						id="dropdownMenuButton" data-toggle="dropdown"
						aria-haspopup="true" aria-expanded="false">Выбрать месяц</button>
					<div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
						<c:forEach items="${daysController.months}" var="month">
							<a class="dropdown-item"
								href="selection?selection=<fmt:formatDate pattern="MM-yyyy" value="${month}"/>">
								<fmt:formatDate pattern="MMMM" value="${month}" /> <fmt:formatDate
									pattern=" yyyy" value="${month}" />
							</a>
						</c:forEach>
					</div>
				</div>
				<a href="selection?selection=0-0" class="btn btn-secondary col"
					style="margin-top: 6">Показать все</a>
			</div>
		</div>
		<div class="row" style="margin-top: 20">
			<div class="col">
				<button class="btn btn-secondary col" type="button"
					data-toggle="collapse" data-target="#card1" aria-expanded="false"
					aria-controls="card1">Дни с температурой ниже 0</button>
			</div>
			<div class="col">
				<button class="btn btn-secondary col" type="button"
					data-toggle="collapse" data-target="#card2" aria-expanded="false"
					aria-controls="card2">Дни с температурой выше средней</button>
			</div>
			<div class="col">
				<button class="btn btn-secondary col" type="button"
					data-toggle="collapse" data-target="#card3" aria-expanded="false"
					aria-controls="card3">Три самых теплых дня</button>
			</div>
		</div>
		<div class="row">
			<c:forEach var="i" begin="1" end="3">
				<div class="col">
					<div class="collapse multi-collapse" id="card${i}">
						<div class="card card-body scrolled">
							<table class="table table-bordered">
									<c:if test="${i==1}">
										<my:belowZero days="${daysController.selectedDays}" value="value"></my:belowZero>
										<h6 class="card-title" style="color: grey">Всего: ${value.size()}</h6>
        							 </c:if>
        							 <c:if test="${i==2}">
										<my:aboveAverage days="${daysController.selectedDays}" value="value"></my:aboveAverage>
										<h6 class="card-title" style="color: grey">Всего: ${value.size()}</h6>
        							 </c:if>
        							 <c:if test="${i==3}">
										<my:warmestDays days="${daysController.selectedDays}" value="value"></my:warmestDays>
        							 </c:if>
								<c:forEach items="${value}" var="day">
									<tr>
										<td><fmt:formatDate value="${day.toDate()}" pattern="dd MMMM yyyy" /></td>
										<td><fmt:formatNumber value="${day.temp/100}" pattern="##0.00" />&#8451;</td>
									</tr>
								</c:forEach>
							</table>
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</body>
</html>