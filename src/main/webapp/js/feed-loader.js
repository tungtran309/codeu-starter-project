// Fetch messages and add them to the page.

function fetchMessages() {
    const url = '/feed';
    fetch(url).then(response => {
        return response.json();
    }).then((messages) => {
        const messageContainer = document.getElementById('message-container');
        if (messages.length == 0) {
            messageContainer.innerHTML = '<p>There are no post yet.</p>';
        } else {
            messageContainer.innerHTML = '';
        }

        messages.forEach(message => {
            const messageDiv = buildMessageDiv(message);
            messageContainer.appendChild(messageDiv);
        });
    })
}


function buildDateString(date) {
    return date.getDate() + "/" + (date.getMonth() + 1) + "/" + date.getFullYear() + " " + date.getHours() + ":" + date.getMinutes();
}

function buildMessageDiv(message) {
    var usernameDiv = document.createElement('a');
    usernameDiv.classList.add("top-align");
    usernameDiv.appendChild(document.createTextNode(message.user.email));
    usernameDiv.title = message.user.email;
    usernameDiv.href = 'users/' + message.user.email;
    var avatar = document.createElement("img");
    avatar.setAttribute('src', message.user.avatarUrl);
    avatar.setAttribute('alt', 'na');
    avatar.setAttribute('height', '40px');
    avatar.setAttribute('width', '40px');
 
    const timeDiv = document.createElement('div');
    timeDiv.classList.add('top-align');
    timeDiv.appendChild(document.createTextNode(buildDateString(new Date(message.timestamp))));
 
    const headerDiv = document.createElement('div');
    headerDiv.classList.add('message-header');
    headerDiv.appendChild(avatar);
    headerDiv.appendChild(document.createElement('br'));
    headerDiv.appendChild(usernameDiv);
    headerDiv.appendChild(timeDiv);
 
    const bodyDiv = document.createElement('div');
    bodyDiv.classList.add('message-body');
    $(message.text).appendTo(bodyDiv);
 
    const messageDiv = document.createElement('div');
    messageDiv.classList.add('message-div');
    messageDiv.appendChild(headerDiv);
    messageDiv.appendChild(bodyDiv);
  
    return messageDiv;
}

// Fetch data and populate the UI of the page.
function buildUI() {
    fetchMessages();
}
