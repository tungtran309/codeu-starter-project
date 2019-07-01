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
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/main.css" rel="stylesheet">
</head>

<body>
    <nav>
        <ul id="navigation">
            <li><a href="/">Home</a></li>
            <li><a href="/aboutus.html">About Our Team</a></li>
            <script>addLoginOrLogoutLinkToNavigation();</script>
        </ul>
    </nav>

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