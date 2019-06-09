google.charts.load('current', {packages: ['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart(){
    var tag_data = new google.visualization.DataTable();

    // define columns for the DataTable instance
    tag_data.addColumn('string', 'Tag');
    tag_data.addColumn('number', 'Memes Count');

    // add data to tag_data
    tag_data.addRows([
        ["Research", 60],
        ["Software Engineering", 100],
        ["Programming Language", 50],
        ["Internet Explorer", 30],
        ["Mobile Development", 15]
    ]);

    // custom option for chart
    var chart_options = {
        width: 800,
        height: 400
    };

    // draw chart
    var chart = new google.visualization.BarChart(document.getElementById('tag_chart'));
    chart.draw(tag_data, chart_options);
}