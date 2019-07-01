// Fetch stats and display them in the page.
function fetchStats(){
    const url = '/stats';
    fetch(url).then((response) => {
        return response.json();
    }).then((stats) => {
        const statsContainer = document.getElementById('stats-container');
        statsContainer.innerHTML = '';

        statsContainer.appendChild(buildStatElement('Total meme count: ' + stats.messageCount));
        statsContainer.appendChild(buildStatElement('Total user count: ' + stats.totalUserCount));
        statsContainer.appendChild(buildStatElement('Active user* count: ' + stats.activeUserCount));
    });
}

function buildStatElement(statString){
    const statElement = document.createElement('p');
    statElement.appendChild(document.createTextNode(statString));
    return statElement;
}

// Fetch data and populate the UI of the page.
function buildUI(){
    fetchStats();
}