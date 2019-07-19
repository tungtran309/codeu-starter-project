<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/feed.css">
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
        <p>Here you can see other users' posts like other normal news feed.</p>
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
                String dateString = date.getDay() + "/" + (date.getMonth()+1) + "/" + (date.getYear() + 1900) + " - " + date.getHours() + ":" + date.getMinutes();
        %>
            <div class="row border-grey">
                <div class="col-2" style="border-right: solid thin darkgray">
                    <div class="card border" style="padding: 5px; margin: 10px;">

                        <img class="card-img-top" src="<%=
                                message.getUser().getAvatarUrl()
                            %>"
                             width="90%"
                             alt="Avatar">

                        <div class="card-body">

                            <h4 id="page-title" class="text-center">
                                <%=message.getUser().getDisplayedName().equals("")?
                                        message.getUser().getEmail() : message.getUser().getDisplayedName()%>
                            </h4>

                            <hr/>

                            <p style="text-align: center"> <%= dateString %> </p>

                        </div>

                    </div>
                </div>
                <div id="meme-content" class="col-10"><%=message.getText()%>
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
</body>
</html>
