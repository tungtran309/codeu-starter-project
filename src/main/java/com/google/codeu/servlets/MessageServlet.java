/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.servlets;

import com.google.codeu.data.User;
import com.google.appengine.api.blobstore.*;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.cloud.vision.v1.EntityAnnotation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {
  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }


  /** Stores a new {@link Message}. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    User user = datastore.getUser(userService.getCurrentUser().getEmail());
    String text = Jsoup.clean(request.getParameter("text"), Whitelist.basic());
    long vote = 0;
    BlobKey blobKey = getBlobKey(request, "image");
    Message message = new Message(user, text + '\n' + getUploadedFileUrlToImageSource(blobKey), vote);
    if (blobKey != null) {
        message.setTags(ImageAnalysisServlet.getTags(blobKey));
    }
    datastore.storeMessage(message);

    response.sendRedirect("/users/" + user.getEmail());
  }

  /**
   * Returns a URL that points to the uploaded file, or null if the user didn't upload a file.
   */

  private BlobKey getBlobKey(HttpServletRequest request, String formInputElementName) throws IOException {
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
      List<BlobKey> blobKeys = blobs.get(formInputElementName);

      // User submitted form without selecting a file, so we can't get a URL. (devserver)
      if (blobKeys == null || blobKeys.isEmpty()) {
        return null;
      }

      // Our form only contains a single file input, so get the first index.
      BlobKey blobKey = blobKeys.get(0);
      return blobKey;
  }

  private String getUploadedFileUrlToImageSource(BlobKey blobKey) throws IOException {
    if (blobKey == null) {
        return "";
    }
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return "";
    }

    // We could check the validity of the file here, e.g. to make sure it's an image file
    // https://stackoverflow.com/q/10779564/873165

    // Use ImagesService to get a URL that points to the uploaded file.
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    ServingUrlOptions options = ServingUrlOptions.Builder.withBlobKey(blobKey);
    return "<img src=\"" + imagesService.getServingUrl(options) + "\">";
  }
}
