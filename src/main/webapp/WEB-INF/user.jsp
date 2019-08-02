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
<%@ page import="com.google.common.flogger.FluentLogger" %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String memeUploadUrl = blobstoreService.createUploadUrl("/messages");
    String avatarUploadUrl = blobstoreService.createUploadUrl("/avatar");
    List<Message> messages = (List<Message>) request.getAttribute("messages");

    String urlUserEmail = (String)request.getAttribute("urlUserEmail");
    String loggedInUserEmail = (String)request.getAttribute("loggedInUserEmail");

    String visibilityTag = urlUserEmail.equals(loggedInUserEmail) ? "" : "hidden"; %>

<!DOCTYPE html>
<html>
<head>
    <title><%=urlUserEmail%></title>
    <meta charset="UTF-8">
    <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/user-page.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/js/setting-page-loader.js"></script>
    <script src="https://cdn.ckeditor.com/ckeditor5/12.2.0/classic/ckeditor.js"></script>
</head>
<body>
<nav>
    <ul id="navigation">
        <li><a href="${pageContext.request.contextPath}/">Home</a></li>
        <li><a href="${pageContext.request.contextPath}/aboutus.html">About Our Team</a></li>
        <li><a href="${pageContext.request.contextPath}/stats.html">Stats</a></li>
        <li><a href="${pageContext.request.contextPath}/map.html">Map</a></li>
        <li><a href="${pageContext.request.contextPath}/feed">Message Feed</a></li>

        <li class="right"><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
        <li class="right"><a href="${pageContext.request.contextPath}/users/<%=loggedInUserEmail%>">Your Page</a></li>
    </ul>
</nav>

<div id="container">

    <div class="row" style="margin-left: 10px">
        <div class="col-3" id="user-information-layout">
            <div class="card border" style="padding: 5px; margin-left: 30px; margin: 10px;">

                <img class="card-img-top" src="<%=
                            (request.getAttribute("avatarUrl") == "")?
                                "/images/avatar-placeholder.gif" :
                                request.getAttribute("avatarUrl")
                            %>"
                     width="90%"
                     alt="Avatar">

                <div class="card-body">

                    <h4 id="page-title" class="text-center">
                        <%=request.getAttribute("displayedName")%>
                    </h4>
                    <div class="form-group <%=visibilityTag%>">
                        <form id="about-me-form" action="/about" method="POST">
                            <textarea id="displayed-name-input" name="displayed-name" placeholder="Enter a new name that is displayed to everyone" class="form-control"></textarea>

                            <hr>
                            <p> <%=request.getAttribute("about")%> </p>
                            <textarea id="about-me-input" name="about-me" placeholder="Tell something about yourself" class="form-control"></textarea>
                            <br/>
                            <input type="submit" value="Update" class="btn btn-primary">
                        </form>
                    </div>
                </div>

            </div>

            <div class="<%=visibilityTag%>" style="margin-left: 15px">
                <form method="POST" action="<%= avatarUploadUrl %>" enctype="multipart/form-data">
                    Upload avatar: <input type="file" name="avatar" size="60" />
                    <br/>
                    <br/>
                    <input type="submit" value="Set Avatar" class="btn btn-primary" />
                </form>
            </div>

        </div>

        <div class="col-9" id="meme-list-layout" style="padding-right: 50px">
            <div class="form-group <%=visibilityTag%>">
                <form style="margin-top: 10px" id="message-form" action="<%= memeUploadUrl %>" method="POST" enctype="multipart/form-data">
                    <textarea name="text" id="message-input" class="form-control"></textarea>
                    <br/>
                    Upload meme:
                    <input type="file" name="image" style="margin-left: 15px"/>
                    <br/>
                    <br/>
                    <input type="submit" value="Upload" class="btn btn-primary">
                    <script>
                        const config = {removePlugins: ['ImageUpload'], placeholder: 'Meme title/description'};
                        ClassicEditor.create(document.getElementById('message-input'), config)
                    </script>
                </form>

                <hr/>
            </div>

            <div id="message-container">
                <%
                    if (messages.size() == 0) {
                %>This user has no posts yet<%
            } else {
                for (Message message : messages) {
                    Date date = new Date(message.getTimestamp());
                    String dateString = (date.getMonth()+1) + "/" + date.getDate() + "/" + (date.getYear() + 1900) + "\n" + date.getHours() + ":" + date.getMinutes();
            %>
                <div class="row border-grey">
                    <div class="col-vote">
                        <form action="/vote" method="POST">
                            <input type="hidden" name="id" value="<%=message.getId().toString()%>" />
                            <input type="hidden" name="upvote" value="1">
                            <label class="vote-button">
                                <input id="hidden-submit-button" type="submit">
                                <div class="vote-icon">
                                    <i class="fa fa-icon fa-caret-up pull-left"></i>
                                </div>
                            </label>
                        </form>
                        <div class="vote-content"> <%= message.getVote() %> </div>
                        <form action="/vote" method="POST">
                            <input type="hidden" name="id" value="<%=message.getId().toString()%>" />
                            <input type="hidden" name="downvote" value="1">
                            <label class="vote-button">
                                <div class="vote-icon">
                                    <i class="fa fa-icon fa-caret-down pull-left"></i>
                                </div>
                                <input id="hidden-submit-button" type="submit">
                            </label>
                        </form>
                    </div>
                    <div class="col-2">
                        <div class="card border" id="col-2-border">

                            <img class="card-img-top" src="<%=
                                        message.getUser().getAvatarUrl()
                                    %>"
                                 width="90%"
                                 alt="Avatar">

                            <div class="card-body">

                                <h6 id="page-title" class="text-center">
                                    <%=message.getUser().getDisplayedName().equals("")?
                                            message.getUser().getEmail() : message.getUser().getDisplayedName()%>
                                </h6>

                                <hr/>

                                <p id="contentInTheMiddle"><font size="1"> <%= dateString %> </font> </p>
                            </div>
                        </div>
                        <% if (message.getUser().getEmail().equals(loggedInUserEmail)) { %>
                        <div id="col-2-border">
                            <form method="POST" action="/delete-message">
                                <input type="hidden" name="id" value="<%=message.getId().toString()%>" />
                                <input type="submit" value="Delete" class="btn btn-primary"/>
                            </form>
                        </div>
                        <% } %>
                    </div>
                    <div id="meme-content" class="col-auto"><%=message.getText()%>
                        <ul>
                            <% for(String tag : message.getTags()) { %>
                            <li> <a href=<%="/feed?tags=" + tag.replace(" ", "%20")%>>  <%= tag %> </a> </li>
                            <% } %>
                        </ul>
                    </div>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </div>

    </div>

</div>

<script src="${pageContext.request.contextPath}/js/jquery-3.4.1.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
</body>
</html>