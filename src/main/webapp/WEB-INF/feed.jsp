<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.common.flogger.FluentLogger" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    UserService userService = UserServiceFactory.getUserService();

    List<Message> messages = (List<Message>) request.getAttribute("messages");
    String loggedInUserEmail = (String)request.getAttribute("loggedInUserEmail");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Message Feed</title>
    <meta charset="UTF-8">
    <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/navigation-loader.js"></script>
</head>
<body>
<nav>
    <ul id="navigation">
        <li><a href="${pageContext.request.contextPath}/">Home</a></li>
        <li><a href="${pageContext.request.contextPath}/aboutus.html">About Our Team</a></li>
        <li><a href="${pageContext.request.contextPath}/stats.html">Stats</a></li>
        <li><a href="${pageContext.request.contextPath}/map.html">Map</a></li>
        <li><a href="${pageContext.request.contextPath}/feed">Message Feed</a></li>

        <%
            if (userService.isUserLoggedIn()) {
        %>
        <li class="right"><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
        <li class="right"><a href="${pageContext.request.contextPath}/setting.html">Settings</a></li>
        <li class="right"><a href="${pageContext.request.contextPath}/users/<%=loggedInUserEmail%>">Your Page</a></li>
        <%
        } else {
        %>
        <li class="right"><a href="${pageContext.request.contextPath}/login">Login</a></li>
        <%
            }
        %>

    </ul>
</nav>

<div id="content">
    <div class="jumbotron">
        <h1>A Meme Feed</h1>
    </div>

    <div class="container-fluid">
        <p>Here you can see other users' posts like other normal news feed. You can click on a name to go to that user's profile.</p>
    </div>

    <div class="container-fluid">
        <hr/>
        <div id="message-container">
            <%
                if (messages.size() == 0) {
            %>There are no post yet!<%
        } else {
            for (Message message : messages) {
                Date date = new Date(message.getTimestamp());
                String dateString = (date.getMonth()+1) + "/" + date.getDate() + "/" + (date.getYear() + 1900) + "\n" + date.getHours() + ":" + date.getMinutes();
        %>
            <div class="row border-grey">
                <div class="col-vote">
                    <form action="/upvote" method="POST">
                        <label class="switch">
                            <input type="checkbox" id="upvote" onclick="checkUpvote()">
                            <div class="fa fa-icon fa-caret-up fa-2x pull-left"></div>
                        </label>
                    </form>
                    <div class="vote-content"> <%= message.getVote() %> </div>
                    <label class="switch">
                        <input type="checkbox" id="downvote" onclick="checkDownvote()">
                        <div class="fa fa-icon fa-caret-down fa-2x pull-left"></div>
                    </label>
                </div>

                <div class="col-2">
                    <div class="card border" id="card-border">

                        <img class="card-img-top" src="<%=
                                message.getUser().getAvatarUrl()
                            %>"
                             width="90%"
                             alt="Avatar">

                        <div class="card-body">
                            <h4 id="page-title" class="text-center">
                                <a href=<%="/users/" + message.getUser().getEmail()%>>
                                    <%=message.getUser().getDisplayedName().equals("")?
                                            message.getUser().getEmail() : message.getUser().getDisplayedName()%>
                                </a>
                            </h4>

                            <hr/>

                            <p id="contentInTheMiddle"> <%= dateString %> </p>

                            <% if (message.getUser().getEmail().equals(loggedInUserEmail)) { %>
                            <div>
                                <form method="POST" action="/delete-message">
                                    <input type="hidden" name="id" value="<%=message.getId().toString()%>" />
                                    <input type="submit" value="Delete" class="btn btn-primary"/>
                                </form>
                            </div>
                            <% } %>
                        </div>

                    </div>
                </div>
                <div id="meme-content" class="col-auto"><%=message.getText()%>
                </div>
            </div>
            <%
                    }
                }
            %>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
<script>
    function checkUpvote() {
        var checkbox = document.getElementById("upvote");
        if (checkbox.checked == true){
            $('.fa-caret-up').css("color", "mediumpurple");
            $('.fa-caret-down').css("color", "grey");
            $('#downvote').prop("checked", false);
        }
        else{
            $('.fa').css("color", "black");
        }
    }
    function checkDownvote() {
        var checkbox = document.getElementById("downvote");
        if (checkbox.checked == true){
            $('.fa-caret-up').css("color", "grey");
            $('.fa-caret-down').css("color", "mediumpurple");
            $('#upvote').prop("checked", false);
        }
        else{
            $('.fa').css("color", "black");
        }
    }
</script>
</body>
</html>
