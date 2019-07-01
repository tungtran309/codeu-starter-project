<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    String uploadUrl = blobstoreService.createUploadUrl("/image-analysis"); %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Image Upload Analysis</title>
        <link href="https://fonts.googleapis.com/css?family=Roboto&display=swap" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/bootstrap.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/main.css" rel="stylesheet">
        <link href="${pageContext.request.contextPath}/css/user-page.css" rel="stylesheet">
        <script src="${pageContext.request.contextPath}/js/map-loader.js"></script>
        <script src="https://cdn.ckeditor.com/ckeditor5/11.2.0/classic/ckeditor.js"></script>
    </head>

    <body>
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

        <div id="content">
            <div class="jumbotron">
                <h1>Image Analysis</h1>
            </div>
            <div class="container-fluid">
                This feature will analysis the uploaded image and extract labels from it.
            </div>

            <div class="container-fluid">
                <form method="POST" enctype="multipart/form-data" action="<%= uploadUrl %>">
                    <p>Upload an image:</p>
                    <input type="file" name="image">
                    <br/><br/>
                    <button>Submit</button>
                </form>
            </div>
        </div>
    </body>
</html>