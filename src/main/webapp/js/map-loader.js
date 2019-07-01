let map;
let editMarker;

function createMap() {
    map = new google.maps.Map(document.getElementById('map'),{
        center: {lat: 16.3290223, lng: 106.3898982},
        zoom: 6
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

function createLink(url) {
    const linkElement = document.createElement('a');
    linkElement.href = url;
    return linkElement;
}

// create a marker that shows read-only info window when clicked
function createMarkerForDisplay(lat, lng, content) {
    const marker = new google.maps.Marker({
        position: {lat: lat, lng: lng},
        map: map
    });

    marker.addListener('click', () => {
        window.location.href = createLink('/users/' + content);
    });
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