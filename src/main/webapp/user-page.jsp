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
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl("/messages"); %>

<!DOCTYPE html>
<html>
    <head>
        <title>User Page</title>
        <meta charset="UTF-8">
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <link href="css/bootstrap.css" rel="stylesheet">
        <link href="css/main.css" rel="stylesheet">
        <link href="/css/user-page.css" rel="stylesheet">
        <script src="/js/navigation-loader.js??v=AUTO_INCREMENT_VERSION"></script>
        <script src="/js/user-page-loader.js"></script>
        <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
    </head>
    <body>
        <nav>
            <ul id="navigation">
                <li><a href="/">Home</a></li>
                <li><a href="/aboutus.html">About Our Team</a></li>
                <script>addLoginOrLogoutLinkToNavigation();</script>
            </ul>
        </nav>
        <div class="jumbotron">
            <h1 id="page-title">User Page</h1>
        </div>
        <div class="container-fluid">
            <div id="about-me-container" class="form-group">Loading...</div>
            <div class="form-group">
                <form id="about-me-form" action="/about" method="POST" class="hidden">
                    <textarea id="about-me-input" name="about-me" placeholder="about me" class="form-control" rows=4 required></textarea>
                    <br/>
                    <input type="submit" value="Submit" class="btn btn-primary">
                </form>
            </div>

            <div class="form-group">
                <form id="message-form" action="<%= uploadUrl %>" method="POST" class="hidden" enctype="multipart/form-data">
                    Enter a new message:
                    <br/>
                    <textarea name="text" id="message-input" placeholder="text here" class="form-control"></textarea>
                    <br/>
                    <br/>Upload an image:<br/>
                    <div class="custom-file">
                        <input type="file" name="image" class="custom-file-input" aria-describedby="inputFile">
                        <label class="custom-file-label" for="inputFile">Choose file</label>
                    </div>
                    <br/><br/>
                    <input type="submit" value="Submit" class="btn btn-primary">
                    <br/>
                    <script>
                        const config = {removePlugins: [ 'Heading', 'List', 'ImageUpload']};
                        ClassicEditor.create(document.getElementById('message-input'), config)
                    </script>
                </form>
            </div>
            <hr/>

            <div id="message-container">Loading...</div>
            <script>buildUI();</script>
        </div>

        <script src="js/jquery-3.4.1.min.js"></script>
        <script src="js/bootstrap.js"></script>
    </body>
</html>
