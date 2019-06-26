let map;
let editMarker;

function createMap() {
    map = new google.maps.Map(document.getElementById('map'),{
        center: {lat: 16.3290223, lng: 106.3898982},
        zoom: 6
    });

    fetch('/login-status')
        .then((response) => {
            return response.json();
        })
        .then((loginStatus) => {
            if (loginStatus.isLoggedIn) {
            // when user clicks in the map, show a marker with text box
            map.addListener('click', (event) => {
                createMarkerForEdit(event.latLng.lat(), event.latLng.lng());
            });
        }
    });

    fetchMarkers();
}

// fetches markers from backend
function fetchMarkers() {
    fetch('/markers')
        .then((response) => {
            return response.json();
        })
        .then ((markers) => {
            markers.forEach((marker) =>{
                createMarkerForDisplay(marker.lat, marker.lng, marker.content)
            });
        });
}

// create a marker that shows read-only info window when clicked
function createMarkerForDisplay(lat, lng, content) {
    const marker = new google.maps.Marker({
        position: {lat: lat, lng: lng},
        map: map
    });

    var infoWindow = new google.maps.InfoWindow({
        content: content
    });

    marker.addListener('click', () => {
        infoWindow.open(map, marker);
    });
}

// creates a marker that shows a textbox the user can edit
function createMarkerForEdit(lat, lng) {
    // remove if we're already showing an editable marker
    if (editMarker) {
        editMarker.setMap(null);
    }
    editMarker = new google.maps.Marker({
        position: {lat: lat, lng: lng},
        map: map
    });

    const infoWindow = new google.maps.InfoWindow({
        content: buildInfoWindowInput(lat, lng)
    });

    // if the user closes the editable info window, remove the marker
    google.maps.event.addListener(infoWindow, 'closeclick', () => {
        editMarker.setMap(null);
    });

    infoWindow.open(map, editMarker);
}

// builds and returns HTML elements that show an editable textbox and a submit button
function buildInfoWindowInput(lat, lng) {
    const textBox = document.createElement('textarea');
    const button = document.createElement('button');
    button.appendChild(document.createTextNode('Submit'));

    button.onclick = () => {
        postMarker(lat, lng, textBox.value);
        createMarkerForDisplay(lat, lng, textBox.value);
        editMarker.setMap(null);
    };

    const containerDiv = document.createElement('div');
    containerDiv.appendChild(textBox);
    containerDiv.appendChild(document.createElement('br'));
    containerDiv.appendChild(button);

    return containerDiv;
}

// send a marker to the backend for saving
function postMarker(lat, lng, content) {
    const params = new URLSearchParams();
    params.append('lat', lat);
    params.append('lng', lng);
    params.append('content', content);

    fetch('/markers', {
        method: 'POST',
        body: params
    });
}