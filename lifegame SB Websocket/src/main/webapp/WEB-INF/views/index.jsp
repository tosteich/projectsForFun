<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/bootstrap-switch-button.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/bootstrap_4_5_0.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/styles/index_style.css">
<script src="${pageContext.request.contextPath}/scripts/jquery_3_2_1min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/index_script.js"></script>
<script src="${pageContext.request.contextPath}/scripts/bootstrap-switch-button.min.js"></script>
<script src="${pageContext.request.contextPath}/scripts/bootstrap.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/scripts/sockjs.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/scripts/stomp.min.js"></script>
    <title>Game of Life</title>
</head>
<body>
<div class="container-xl" id="container">
    <canvas id="gameCanvas"></canvas>
    <div id="infoDiv" class="row no-gutters pt-1">
        <div class="col-5">
            &nbsp;Pointer position:&nbsp;<span id="position"></span>
        </div>
        <div class="col-5">
            Generation No:&nbsp;<span id="gen"></span>
        </div>
        <div class="col-2">
            Alive cells:&nbsp;<span id="alive"></span>
        </div>
    </div>
    <div class="row pt-2">
        <div class="col-4">
            <a href="save" class="btn btn-secondary mt-1" id="btn_save" download>SAVE</a>
            <label class="btn btn-secondary mt-1" id="fileLabel">LOAD
                <input type="file" accept=".lif" id="inputFile" style="display: none;">
            </label>
        </div>
        <div class="col-4">
            <button class="btn btn-secondary mt-1" id="btn_start">START</button>
            <button class="btn btn-secondary mt-1" id="btn_stop">STOP</button>
            <button class="btn btn-secondary mt-1" id="btn_step">STEP</button>
            <button class="btn btn-secondary mt-1" id="btn_clear">CLEAR</button>
        </div>
        <div class="col-4">
            <div class="float-right ml-1 mt-1">
                <input type="checkbox" id="btn_grid" checked data-toggle="switchbutton"
                       data-onlabel=" GRID"
                       data-offlabel="GRID"
                       data-onstyle="secondary"
                       data-offstyle="outline-secondary">
            </div>
            <div id="progressbar" class="float-right mt-1">
                <label id="speedLabel">SPEED
                    <input type="range" id="speedRange" min="10" max="101" class="slider">
                </label>
            </div>
        </div>
    </div>
    <div class="modal fade" id="modalCenter" tabindex="-1" role="dialog"
         aria-labelledby="modalCenterTitle" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h6 class="modal-title">Game of Life</h6>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <h3 class="modal-title text-center">Game over!</h3>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
