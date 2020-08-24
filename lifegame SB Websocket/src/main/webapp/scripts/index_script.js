'use strict'
jQuery(document).ready(function ($) {

	
    let radius = 10;
    let interval = 200;
    let isGreedEnable = true;
    let array = [];
    let canvasWidth, canvasHeight, widthCenter, heightCenter, offX, offY, dragX, dragY, timer, running,
        generation, aliveCells, isGameOver, savedGame;
    let gameCanvas = $("#gameCanvas");
	let stompClient = null;
	
	connect();
    drawCanvas();
    $(window).resize(drawCanvas);

    function drawCanvas() {
        canvasWidth = $("#container").width();
        canvasHeight = window.innerHeight * 0.80;
        gameCanvas.attr("width", canvasWidth);
        gameCanvas.attr("height", canvasHeight);
        gameCanvas.css("background-color", "#6C757D");
        drawGrid();
    }

    function drawGrid() {
        updateCenter();
        let ctx = gameCanvas[0].getContext("2d");
        ctx.clearRect(0, 0, canvasWidth, canvasHeight);
        let diam = radius * 2;
        let greedX = canvasWidth / diam + diam;
        let greedY = canvasHeight / diam + diam;
        offX = diam - (canvasWidth / 2 - radius) % diam;
        offY = diam - (canvasHeight / 2 - radius) % diam;
        for (let x = 0; x < greedX; x++) {
            let newX = x - widthCenter;
            for (let y = 0; y < greedY; y++) {
                let newY = y - heightCenter
                if (isGreedEnable) {
                    ctx.beginPath();
                    ctx.rect(x * diam - offX, y * diam - offY, diam, diam);
                    ctx.stroke();
                }
                if (array.some(cell => cell.x === newX && cell.y === newY)) {
                    ctx.fillStyle = "orange";
                    ctx.beginPath();
                    ctx.arc(x * diam + radius - offX, y * diam + radius - offY, radius, 0, 2 * Math.PI);
                    ctx.fill();
                }
            }
        }
        $("#gen").text(generation ? generation : 0);
        $("#alive").text(aliveCells ? aliveCells : 0);
    }

    gameCanvas.on("mousewheel", e => {
        if (e.originalEvent.deltaY > 0) {
            radius++;
            drawGrid();
        } else if (radius >= 3) {
            radius--;
            drawGrid();
        }
    });

    gameCanvas.mousemove(e => {
        $("#position").text("x: " + calcX(e.offsetX) + ", y: " + calcY(e.offsetY));
    })

    gameCanvas.mouseout(() => {
        $("#position").text("");
    })

    gameCanvas.contextmenu(e => e.preventDefault())

    gameCanvas.on("mousedown mouseup mouseout", e => {
        if (e.type === "mousedown" && e.button === 0) {
            clickOnCell(e);
            gameCanvas.on("mousemove", e, drawOnLeftDrag);
        } else if (e.type === "mouseup" && e.button === 0) {
            gameCanvas.off("mousemove", drawOnLeftDrag);
        } else if (e.type === "mousedown" && e.button === 2) {
            setDrag(e);
            gameCanvas.on("mousemove", e, moveOnClick);
        } else if (e.type === "mouseup" && e.button === 2) {
            gameCanvas.off("mousemove", moveOnClick);
        } else if (e.type === "mouseout") {
            gameCanvas.off("mousemove", moveOnClick);
            gameCanvas.off("mousemove", drawOnLeftDrag);
        }
    })

    function drawOnLeftDrag(e) {
        if (calcX(e.offsetX) !== dragX || calcY(e.offsetY) !== dragY) {
            clickOnCell(e);
        }
    }

    function setDrag(e) {
        dragX = calcX(e.offsetX);
        dragY = calcY(e.offsetY);
    }

    function updateCenter() {
        widthCenter = Math.round((canvasWidth / 2) / (radius * 2));
        heightCenter = Math.round((canvasHeight / 2) / (radius * 2));
    }

    function calcX(x) {
        return Math.floor((x + offX) / (radius * 2)) - widthCenter;
    }

    function calcY(y) {
        return Math.floor((y + offY) / (radius * 2)) - heightCenter;
    }

    function clickOnCell(e) {
        setDrag(e);
        command("click",{x: dragX, y: dragY})
    }

    function moveOnClick(e) {
        if (calcX(e.offsetX) !== dragX || calcY(e.offsetY) !== dragY) {
            let x = calcX(e.offsetX) - dragX;
            let y = calcY(e.offsetY) - dragY;
            command("move", {x: x, y: y})
            setDrag(e);
        }
    }

    $("#btn_step").click(() => {
        command("step");
    });

    $("#btn_start").click(() => {
        if (!running) {
            running = true;
            loop();
            function loop() {
                if (running && !isGameOver) {
                    window.setTimeout(loop, interval);
                    command("step");
                } else if (isGameOver) {
                    $('#modalCenter').modal('show');
                    isGameOver = 0;
                    running = false;
                }
            }
        }
    });

    $("#btn_stop").click(() => {
        running = false;
    });

    $("#btn_clear").click(() => {
        running = false;
        command("clear")
    });

    $("#btn_grid").change(() => {
        isGreedEnable = !isGreedEnable;
        drawGrid();
    });

    $("#speedRange").change(() => {
        interval = $("#speedRange").val() * 5;
    });
    
    $("#btn_save").click(() => {
        running = false;
        command("save");
    });
    

    let inputFile = $("#inputFile");
    inputFile.change(() => {
        running = false;
        if (inputFile[0].files.length !== 0) {
            clearInterval(timer);
            let reader = new FileReader();
            reader.readAsText(inputFile[0].files[0]);
            reader.onloadend = () => {
                load("load", reader.result)
            }
            inputFile[0].value = null;
        }
    })
   
    function connect() {
    	let socket = new SockJS('/MyWebsocket');
    	stompClient = Stomp.over(socket);
    	stompClient.debug = null;
    	stompClient.connect({}, function (frame) {
    		console.log('Connected: ' + frame);
    		stompClient.subscribe('/user/updatedField', field => {
                parseData(JSON.parse(field.body));
                drawGrid();
    		});
    		stompClient.subscribe('/user/save', txt => {
    			saveGame(txt);
    		});
    		command("getdata")
    	});
    } 
    
    function parseData(data) {
        array = data.cells;
        generation = data.generation;
        aliveCells = data.aliveCells;
        isGameOver = data.isGameOver;
    }
    
    function command(command, data) {
    	if(data) {
    		stompClient.send("/app/"+command, {}, JSON.stringify(data));
    	} else {
    		stompClient.send("/app/"+command);
    	}
    }
    
    function load(command, file) {
    		stompClient.send("/app/"+command,{}, file);
    }
    
    function saveGame (txt) {
		let a = document.createElement('a');
        a.setAttribute('href', 'data:application/txt, ' + encodeURIComponent(txt.body))
        a.setAttribute('download', 'save.lif');
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }
})