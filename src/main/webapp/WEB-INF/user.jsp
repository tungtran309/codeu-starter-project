<!--
Copyright 2019 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->

<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.codeu.data.Message" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl("/messages");
    List<Message> messages = (List<Message>) request.getAttribute("messages"); %>

<!DOCTYPE html>
<html>
    <head>
        <title><%=request.getAttribute("user")%></title>
        <meta charset="UTF-8">
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/user-page.css" rel="stylesheet">
        <script src="${pageContext.request.contextPath}/js/map-loader.js"></script>
        <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
    </head>
    <body>
        <nav>
            <ul id="navigation">
                <li><a href="${pageContext.request.contextPath}/">Home</a></li>
                <li><a href="${pageContext.request.contextPath}/aboutus.html">About Our Team</a></li>
                <li><a href="${pageContext.request.contextPath}/stats.html">Stats</a></li>
                <li><a href="${pageContext.request.contextPath}/map.html">Map</a></li>
                <li><a href="${pageContext.request.contextPath}/feed.html">Message Feed</a></li>
                <li><a href="${pageContext.request.contextPath}/image.jsp">Image Analysis</a></li>

                <li class="right"><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                <li class="right"><a href="${pageContext.request.contextPath}/setting.html">Setting</a></li>
                <li class="right"><a href="${pageContext.request.contextPath}/users/<%=request.getAttribute("user")%>">Your Page</a></li>
            </ul>
        </nav>
        <div class="jumbotron">
            <h1 id="page-title"><%=request.getAttribute("user")%></h1>
        </div>
        <div class="container-fluid">
            <div id="about-me-container" class="form-group"><%=request.getAttribute("about")%>
            </div>
            <div class="form-group">
                <form id="about-me-form" action="/about" method="POST">
                    <textarea id="about-me-input" name="about-me" placeholder="about me" class="form-control" rows=4
                              required></textarea>
                    <br/>
                    <input type="submit" value="Submit" class="btn btn-primary">
                </form>
            </div>

            <div class="form-group">
                <form id="message-form" action="<%= uploadUrl %>" method="POST" enctype="multipart/form-data">
                    Enter a new message:
                    <br/>
                    <textarea name="text" id="message-input" placeholder="text here" class="form-control"></textarea>
                    <br/>Upload an image:<br/>
                    <br/>
                    <input type="file" name="image" />
                    <br/><br/>
                    <input type="submit" value="Submit" class="btn btn-primary">
                    <script>
                        const config = {removePlugins: ['Heading', 'List', 'ImageUpload']};
                        ClassicEditor.create(document.getElementById('message-input'), config)
                    </script>
                </form>
            </div>
            <hr/>
            <div id="message-container">
                <%
                    if (messages.size() == 0) {
                        %>This user has no posts yet<%
                    } else {
                        for (Message message : messages) {
                            %>
                            <div class="message-div">
                                <div class="message-header"><%=message.getUser()%> - <%= new Date(message.getTimestamp())%>
                                </div>
                                <div class="message-body"><%=message.getText()%>
                                </div>
                            </div>
                            <%
                        }
                    }
                %>
            </div>
        </div>

        <script src="js/jquery-3.4.1.min.js"></script>
        <script src="js/bootstrap.js"></script>
        <script>
            fetch('/login-status')
                .then((response) => {
                    return response.json();
                })
                .then((loginStatus) => {
                    if (loginStatus.isLoggedIn && loginStatus.username == parameterUsername) {
                        $.ajax({
                            url: "https://geoip-db.com/jsonp",
                            jsonpCallback: "callback",
                            dataType: "jsonp",
                            success: function( location ) {
                                postMarker(location.latitude, location.longitude, loginStatus.username);
                            }
                        });
                    }
                });
        </script>
    </body>
</html>
