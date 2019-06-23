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

package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the User in Datastore. */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    userEntity.setProperty("displayedName", user.getDisplayedName());
    datastore.put(userEntity);
  }

  public void Test() {
    Query query = new Query("User");
    PreparedQuery results = datastore.prepare(query);
    for(Entity entity : results.asIterable()) {
      System.out.println((String)entity.getProperty("email") + " - " + (String)entity.getProperty("aboutMe") + " - " + (String)entity.getProperty("displayedName"));
    }
  }

  /**
   * Returns the User owned by the email address, or
   * null if no matching User was found.
   */
  public User getUser(String email) {
    Test();

    Query query = new Query("User")
            .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();

    if(userEntity == null) {
      return null;
    }

    String aboutMe = (String) userEntity.getProperty("aboutMe");
    String displayedName = (String) userEntity.getProperty("displayedName");
    User user = new User(email, aboutMe, displayedName);

    return user;
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity("Message", message.getId().toString());
    messageEntity.setProperty("user", message.getUser());
    // styled text part 1 here. Temporary remove it
    /*Parser parser = Parser.builder().build();
    Node document = parser.parse(message.getText());
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    messageEntity.setProperty("text", renderer.render(document));*/
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());

    datastore.put(messageEntity);
  }

  /** Returns the total number of messages for all users. */
  public int getTotalMessageCount(){
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withDefaults());
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String user) {
    Query query =
        new Query("Message")
            .setFilter(new Query.FilterPredicate("user", FilterOperator.EQUAL, user))
            .addSort("timestamp", SortDirection.DESCENDING);

    return getMessagesFromQuery(query);
  }

  /**
   * Gets messages posted by all users.
   *
   * @return a list of messages posted by all users, or empty list if no users has posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getAllMessages() {
    Query query =
            new Query("Message")
                    .addSort("timestamp", SortDirection.DESCENDING);

    return getMessagesFromQuery(query);
  }

  /**
   * Gets messages from a Datastore query
   *
   * @return a list of messages from the specified query. List is sorted by time descending.
   */
  private List<Message> getMessagesFromQuery(Query query) {
    PreparedQuery results = datastore.prepare(query);

    List<Message> messages = new ArrayList<>();

    for (Entity entity : results.asIterable()) {
      try {
        String idString = entity.getKey().getName();
        UUID id = UUID.fromString(idString);
        String user = (String) entity.getProperty("user");
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");

        Message message = new Message(id, user, text, timestamp);
        messages.add(message);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }

    return messages;
  }

  public Set<String> getUsers(){
    Set<String> users = new HashSet<>();
    Query query = new Query("Message");
    PreparedQuery results = datastore.prepare(query);
    for(Entity entity : results.asIterable()) {
      users.add((String) entity.getProperty("user"));
    }
    return users;
  }
}
