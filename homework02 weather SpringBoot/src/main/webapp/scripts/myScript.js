"use strict";
jQuery(document).ready(function ($) {
    let array = [];
    getData();
    google.charts.load('current', { 'packages': ['line'] });
    google.charts.setOnLoadCallback(drawChart);

    function drawChart() {
        let data = new google.visualization.DataTable();
        data.addColumn('date', '');
        data.addColumn('number', "Температура");
        data.addRows(array);
        let options = {
            series: { 0: { axis: 'Temps' } },
            axes: { y: { Temps: { label: 'Температура (\u2103)' } } },
        };
        let chart = new google.charts.Line(document.getElementById('chart_div'));
        chart.draw(data, options);
        $(window).resize(() => { chart.draw(data, options) });
    }

    function getData() {
        $.ajax({
            url: 'weather/getData',
            success: function (data) {
            	$.each(data, function(index, day){
            		array.push([new Date(day.year, (day.month - 1), day.dayOfMonth), (day.temp/100)]);
            	});
            }
        })
    }
})