google.charts.load('current', {packages: ['corechart']});
google.charts.setOnLoadCallback(drawChart);

function drawChart() {
    fetch("/tag")
    .then((response) => {
        return response.json();
    })
    .then((tagJson) => {
        var tagData = new google.visualization.DataTable();

        //define columns for the DataTable instance
        tagData.addColumn('string', 'Tag Name');
        tagData.addColumn('number', 'Count');

        for(i = 0; i < tagJson.length; ++i) {
            tagRow = [];

            var name = tagJson[i].name;
            var count = tagJson[i].count;
            tagRow.push(name, count);

            tagData.addRow(tagRow);
        }

        var chartOptions = {
            width: 800,
            height: 400
        };

        var tagChart = new google.visualization.BarChart(document.getElementById('tag_chart'));

        tagChart.draw(tagData, chartOptions);
    });
}