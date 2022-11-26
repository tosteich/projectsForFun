 let tick = 0;
  let animation = { easing: 'easeOutBounce' };
  if ('wakeLock' in navigator) {
    let wakeLock = null;
    const requestWakeLock = async () => {
      try {
        wakeLock = await navigator.wakeLock.request();
        wakeLock.addEventListener('release', () => {
          console.log('Screen Wake Lock released:', wakeLock.released);
        });
        console.log('Screen Wake Lock released:', wakeLock.released);
      } catch (err) {
        console.error(`${err.name}, ${err.message}`);
      }
    };

    window.setTimeout(() => {
      wakeLock.release();
      wakeLock = null;
    }, 20 * 60 * 1000);

    const handleVisibilityChange = async () => {
      if (wakeLock !== null && document.visibilityState === 'visible') {
        await requestWakeLock();
      }
    };

    const activateWakeLock = async () => {
      if (wakeLock == null) {
        await requestWakeLock();
      }
    };

    document.addEventListener('click', activateWakeLock);
    document.addEventListener('visibilitychange', handleVisibilityChange);
  }
  const hostAddr = '10.0.0.166';
  let lastMessageTime;

  const gateway = `ws://${hostAddr}/ws`;
  let websocket;
  function initWebSocket() {
    console.log('Trying to open a WebSocket connection...');
    websocket = new WebSocket(gateway);
    websocket.onopen = onOpen;
    websocket.onclose = onClose;
    websocket.onmessage = onMessage;
  }
  function onOpen(event) {
    console.log('Connection opened');
    websocket.send('initInfo');
  }
  function onClose(event) {
    console.log('Connection closed');
    setTimeout(initWebSocket, 2000);
  }
  window.addEventListener('load', initWebSocket);
  function onMessage(event) {
    lastMessageTime = new Date().getTime();
    indicator.classList.remove("red");
    indicator.classList.add("green");
    let reply = JSON.parse(event.data);
    if (reply.name == "updateInfo") {
      let currTemp = Math.round(reply.currentTemp*100)/100;
      let currPress = Math.round(reply.currentPress*100)/100;
      let currPower = parseInt(reply.currentPower);
      let isFull = tick > 300 ? true : false;
      tempGaugeChart.series[0].points[0].update(currTemp);
      pressGaugeChart.series[0].points[0].update(currPress);
      let currTime = new Date().getTime();
      calcStandBy(reply.currentTime);
      newChart.series[0].addPoint([currTime, currTemp], true, isFull);
      newChart.series[1].addPoint([currTime, currPress * 10], true, isFull);
      newChart.series[2].addPoint([currTime, currPower], true, isFull);
      tick++;
      return;
    }   
    if (reply.name == "time") {
      timerGaugeChart.yAxis[0].setTitle({ text: reply.state });
      let time = parseFloat(parseFloat(reply.value).toFixed(2));
      let second = timerGaugeChart.get('second');
      second.update(time, true, animation);
      return;
    }
    if (reply.name == "targetTemp") {
      document.getElementById("temps").value = reply.value;
      temps.classList.add("conf-text");
      setTimeout(function () {
        temps.classList.remove("conf-text");
      }, 3000);
      return;
    }
    if (reply.name == "pidKp") {
      let figure = parseFloat(reply.value);
      pidKp.value = figure.toFixed(2);
      pidKp.classList.add("conf-text");
      setTimeout(function () {
        pidKp.classList.remove("conf-text");
      }, 3000);
      return;
    }
    if (reply.name == "pidKi") {
      let figure = parseFloat(reply.value);
      pidKi.value = figure.toFixed(2);
      pidKi.classList.add("conf-text");
      setTimeout(function () {
        pidKi.classList.remove("conf-text");
      }, 3000);
      return;
    }
    if (reply.name == "pidKd") {
      let figure = parseFloat(reply.value);
      pidKd.value = figure.toFixed(2);
      pidKd.classList.add("conf-text");
      setTimeout(function () {
        pidKd.classList.remove("conf-text");
      }, 3000);
      return;
    }
    if (reply.name == "initInfo") {
      let currTemp = parseFloat(reply.currentTemp);
      let currPress = parseFloat(reply.currentPress);
      let targTemp = reply.targetTemp;
      let Kp = parseFloat(reply.pidKp);
      let Ki = parseFloat(reply.pidKi);
      let Kd = parseFloat(reply.pidKd);
      tempGaugeChart.series[0].points[0].update(parseFloat(currTemp.toFixed(2)));
      pressGaugeChart.series[0].points[0].update(parseFloat(currPress.toFixed(2)));
      temps.value = targTemp;
      pidKp.value = Kp.toFixed(2);
      pidKi.value = Ki.toFixed(2);
      pidKd.value = Kd.toFixed(2);
      return;
    }
  }

  let pingTimer = setTimeout(function ping() {
    let pingTime = new Date().getTime();
    if ((pingTime - lastMessageTime) > 2000) {
      indicator.classList.remove("green");
      indicator.classList.add("red");
    }
    pingTimer = setTimeout(ping, 1500);
  }, 1500);

  function calcStandBy(currTime) {
     let timeInsec = parseInt(currTime);
     currentTime.innerHTML = "standby " + addZero(Math.floor(timeInsec / 60)) + ":" + addZero(timeInsec % 60); 
  }

  function addZero(number) {
     return ('0' + number).slice(-2);
  }

  function changeTemp() {
    var xhttp = new XMLHttpRequest();
    let url = "http://" + hostAddr + "/targetTemp?temp=" + document.getElementById("temps").value
    xhttp.open("GET", url, true);
    xhttp.send();
  }
  function changePidKp() {
    var xhttp = new XMLHttpRequest();
    let url = "http://" + hostAddr + "/changePidKp?Kp=" + pidKp.value
    xhttp.open("GET", url, true);
    xhttp.send();
  }
  function changePidKi() {
    var xhttp = new XMLHttpRequest();
    let url = "http://" + hostAddr + "/changePidKi?Ki=" + pidKi.value
    xhttp.open("GET", url, true);
    xhttp.send();
  }
  function changePidKd() {
    var xhttp = new XMLHttpRequest();
    let url = "http://" + hostAddr + "/changePidKd?Kd=" + pidKd.value
    xhttp.open("GET", url, true);
    xhttp.send();
  }
  function reboot() {
    websocket.send('reboot');
  }

  let newChart = Highcharts.chart('container', {
    chart: {
        zoomType: 'x',
        type: 'spline',
        animation: Highcharts.svg,
    },

    time: {
        useUTC: false
    },
    title: false,
    subtitle: false,
    xAxis: {
        type: 'datetime',
        tickPixelInterval: 150,
        labels: {
            formatter: function() {
              return Highcharts.dateFormat('%H:%M:%S', this.value)
            }
        }
    },

    yAxis: {
        title: false
    },

    tooltip: {
        shared: true,
        crosshairs: true,
        formatter: function () {
            return this.points.reduce(function (s, point) {
                switch (point.series.name) {
                    case 'Temperature':
                        return s + '<br/><span style="color:'+ point.series.color + '"><b>' + point.series.name + '</b></span>: <b>' +
                            point.y.toFixed(2) + '</b> \xB0C';
                    case 'Pressure':
                        return s + '<br/><span style="color:'+ point.series.color + '"><b>' + point.series.name + '</b></span>: <b>' +
                            (point.y/10).toFixed(2) + '</b> Bars';
                    case 'Boiler Power':
                        return s + '<br/><span style="color:'+ point.series.color + '"><b>' + point.series.name + '</b></span>: <b>' +
                            point.y + '</b> %';
                    default:
                        break;
                }

            }, Highcharts.dateFormat('%A, %b %e %H:%M:%S', this.x));
        },
    },

    dataLabels: {
        enabled: true,
        formatter: function () {
            return Highcharts.numberFormat(this.y, 2);
        }
    },

    exporting: {
        enabled: false
    },
    plotOptions: {
        spline: {
            marker: {
                enabled: false
            },
        }
    },
    credits: {
        enabled: false
    },
    series: [{
        name: 'Temperature',
        color: 'rgb(124, 181, 236)',
    }, {
        name: 'Pressure',
        color: 'rgb(144, 237, 125)',
    }, {
        name: 'Boiler Power',
        color: 'rgb(251, 155, 145)',
    }]
});

  let pressGaugeChart = Highcharts.chart('pressGauge', {

    chart: {
      type: 'gauge',
      plotBackgroundColor: null,
      plotBackgroundImage: null,
      plotBorderWidth: 0,
      plotShadow: false
    },

    title: false,
    credits: {
      enabled: false
    },

    pane: {
      startAngle: -150,
      endAngle: 150,
      background: [{
        backgroundColor: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0, '#FFF'],
            [1, '#333']
          ]
        },
        borderWidth: 0,
        outerRadius: '109%'
      }, {
        backgroundColor: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0, '#333'],
            [1, '#FFF']
          ]
        },
        borderWidth: 1,
        outerRadius: '107%'
      }, {
        // default background
      }, {
        backgroundColor: '#DDD',
        borderWidth: 0,
        outerRadius: '105%',
        innerRadius: '103%'
      }]
    },

    // the value axis
    yAxis: {
      min: 0,
      max: 12,

      minorTickInterval: 'auto',
      minorTickWidth: 1,
      minorTickLength: 10,
      minorTickPosition: 'inside',
      minorTickColor: '#666',

      tickPixelInterval: 30,
      tickWidth: 2,
      tickPosition: 'inside',
      tickLength: 14,
      tickColor: '#666',
      labels: {
        step: 1,
        rotation: 'auto'
      },
      title: {
        text: 'Pressure',
        style: {
          fontWeight: 'normal',
          fontSize: '12px',
          lineHeight: '12px'
        },
        y: 10
      },
      plotBands: [{
        from: 0,
        to: 6,
        color: '#E1E1E1' // green
      }, {
        from: 6,
        to: 9.6,
        color: '#55BF3B' // green
      }, {
        from: 9.6,
        to: 10.6,
        color: '#DDDF0D' // yellow
      }, {
        from: 10.6,
        to: 12,
        color: '#DF5353' // red
      }]
    },
    plotOptions: {
      series: {
        dataLabels: {
          enabled: true,
          y: 40,
          className: 'gaugeLabel',
          format: '{point.y:,.2f}'
        }
      }
    },

    series: [{
      name: 'Pressure',
      data: [2.42],
      tooltip: {
        valueSuffix: ' Bar'
      }
    }]
  });

  let tempGaugeChart = Highcharts.chart('tempGauge', {

    chart: {
      type: 'gauge',
      plotBackgroundColor: null,
      plotBackgroundImage: null,
      plotBorderWidth: 0,
      plotShadow: false
    },

    title: false,
    credits: {
      enabled: false
    },

    pane: {
      startAngle: -150,
      endAngle: 150,
      background: [{
        backgroundColor: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0, '#FFF'],
            [1, '#333']
          ]
        },
        borderWidth: 0,
        outerRadius: '109%'
      }, {
        backgroundColor: {
          linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1 },
          stops: [
            [0, '#333'],
            [1, '#FFF']
          ]
        },
        borderWidth: 1,
        outerRadius: '107%'
      }, {
        // default background
      }, {
        backgroundColor: '#DDD',
        borderWidth: 0,
        outerRadius: '105%',
        innerRadius: '103%'
      }]
    },

    // the value axis
    yAxis: {
      min: 0,
      max: 150,

      minorTickInterval: 'auto',
      minorTickWidth: 1,
      minorTickLength: 10,
      minorTickPosition: 'inside',
      minorTickColor: '#666',

      tickPixelInterval: 30,
      tickWidth: 2,
      tickPosition: 'inside',
      tickLength: 14,
      tickColor: '#666',
      labels: {
        step: 2,
        rotation: 'auto'
      },
      title: {
        text: 'Temp, \xB0C',
        style: {
          fontWeight: 'normal',
          fontSize: '12px',
          lineHeight: '12px'
        },
        y: 10
      },
      plotBands: [{
        from: 0,
        to: 20,
        color: '#E1E1E1' // grey
      }, {
        from: 20,
        to: 80,
        color: '#55BF3B' // green
      }, {
        from: 80,
        to: 100,
        color: '#DDDF0D' // yellow
      }, {
        from: 100,
        to: 150,
        color: '#DF5353' // red
      }]
    },
    plotOptions: {
      series: {
        dataLabels: {
          enabled: true,
          y: 40,
          className: 'gaugeLabel',
          format: '{point.y:,.2f}'
        }
      }
    },

    series: [{
      name: 'Temp',
      data: [20],
      tooltip: {
        valueSuffix: ' \xB0C'
      }
    }]
  });

  let timerGaugeChart = Highcharts.chart('timerGauge', {

    chart: {
      type: 'gauge',
      plotBackgroundColor: null,
      plotBackgroundImage: null,
      plotBorderWidth: 0,
      plotShadow: false,
    },

    credits: {
      enabled: false
    },

    title: false,

    pane: {
      background: [{
        // default background
      }, {
        // reflex for supported browsers
        backgroundColor: Highcharts.svg ? {
          radialGradient: {
            cx: 0.5,
            cy: -0.4,
            r: 1.9
          },
          stops: [
            [0.5, 'rgba(255, 255, 255, 0.2)'],
            [0.5, 'rgba(200, 200, 200, 0.2)']
          ]
        } : null
      }]
    },

    yAxis: {
      labels: {
        distance: -20
      },
      min: 0,
      max: 60,
      lineWidth: 0,
      showFirstLabel: false,

      minorTickInterval: 'auto',
      minorTickWidth: 1,
      minorTickLength: 5,
      minorTickPosition: 'inside',
      minorGridLineWidth: 0,
      minorTickColor: '#666',

      tickInterval: 5,
      tickWidth: 2,
      tickPosition: 'inside',
      tickLength: 10,
      tickColor: '#666',
      title: {
        text: 'Timer',
        style: {
          fontWeight: 'normal',
          fontSize: '12px',
          lineHeight: '12px'
        },
        y: 10
      }
    },

    tooltip: {
      formatter: function () {
        return Highcharts.numberFormat(this.y, 2);
      }
    },
    plotOptions: {
      series: {
        dataLabels: {
          enabled: true,
          format: '{point.y:,.2f}'
        }
      }
    },

    series: [{
      data: [{
        id: 'second',
        y: 0,
        dial: {
          radius: '100%',
          baseWidth: 1,
          rearLength: '15%'
        }
      }],
      animation: false,
    }]
  },
  );

  /**
  * Easing function from https://github.com/danro/easing-js/blob/master/easing.js
  */
  Math.easeOutBounce = function (pos) {
    if ((pos) < (1 / 2.75)) {
      return (7.5625 * pos * pos);
    }
    if (pos < (2 / 2.75)) {
      return (7.5625 * (pos -= (1.5 / 2.75)) * pos + 0.75);
    }
    if (pos < (2.5 / 2.75)) {
      return (7.5625 * (pos -= (2.25 / 2.75)) * pos + 0.9375);
    }
    return (7.5625 * (pos -= (2.625 / 2.75)) * pos + 0.984375);
  };