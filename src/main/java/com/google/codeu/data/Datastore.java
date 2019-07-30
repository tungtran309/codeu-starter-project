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

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

/** Provides access to the data stored in Datastore. */
public class Datastore {

  public static final String KIND_USER = "User";
  public static final String KIND_MESSAGE = "Message";
  private DatastoreService datastore;

  public Datastore() {
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the User in Datastore. */
  public void storeUser(User user) {
    Entity userEntity = new Entity(KIND_USER, user.getEmail());
    userEntity.setProperty("email", user.getEmail());
    userEntity.setProperty("aboutMe", user.getAboutMe());
    userEntity.setProperty("displayedName", user.getDisplayedName());
    userEntity.setProperty("avatarUrl", user.getAvatarUrl());
    datastore.put(userEntity);
  }

  /**
   * Returns the User owned by the email address, or
   * null if no matching User was found.
   */
  public User getUser(String email) {
    Query query = new Query(KIND_USER)
            .setFilter(new Query.FilterPredicate("email", FilterOperator.EQUAL, email));
    PreparedQuery results = datastore.prepare(query);
    Entity userEntity = results.asSingleEntity();

    if(userEntity == null) {
      return null;
    }

    String aboutMe = (String) userEntity.getProperty("aboutMe");
    String displayedName = (String) userEntity.getProperty("displayedName");
    String avatarUrl = (String) userEntity.getProperty("avatarUrl");

    return new User(email, aboutMe, displayedName, avatarUrl);
  }

  /** Stores the Message in Datastore. */
  public void storeMessage(Message message) {
    Entity messageEntity = new Entity(KIND_MESSAGE, message.getId().toString());

    messageEntity.setProperty("user", message.getUser().getEmail());
    // styled text part 1 here. Temporary remove it
    /*Parser parser = Parser.builder().build();
    Node document = parser.parse(message.getText());
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    messageEntity.setProperty("text", renderer.render(document));*/
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("vote", message.getVote());

    datastore.put(messageEntity);
  }

  /** Returns the total number of messages for all users. */
  public int getTotalMessageCount(){
    Query query = new Query(KIND_MESSAGE);
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withDefaults());
  }

  /** Returns the total number of users. */
  public int getTotalUserCount(){
    Query query = new Query(KIND_USER);
    PreparedQuery results = datastore.prepare(query);
    return results.countEntities(FetchOptions.Builder.withDefaults());
  }

  /** Returns the number of active users (posted at least one meme). */
  public int getActiveUserCount(){
    Set<String> users = getUsers();
    return users.size();
  }

  /**
   * Gets message with a specific id.
   *
   * @return a message of that id, or null if no message with that id was found.
   */
  public Message getMessage(String id) {
    Key messageKey = KeyFactory.createKey(KIND_MESSAGE, id);

    Query query =
            new Query(KIND_MESSAGE)
                    .setFilter(new Query.FilterPredicate("__key__", FilterOperator.EQUAL, messageKey));

    List<Message> messages = getMessagesFromQuery(query);
    return messages.isEmpty() ? null : messages.get(0);
  }

  /**
   * Deletes message with a specific id, or do nothing if message not found.
   */
  public void deleteMessage(String id) {
    Key messageKey = KeyFactory.createKey(KIND_MESSAGE, id);

    Query query =
            new Query(KIND_MESSAGE)
                    .setKeysOnly()
                    .setFilter(new Query.FilterPredicate("__key__", FilterOperator.EQUAL, messageKey));

    PreparedQuery result = datastore.prepare(query);

    for (Entity en : result.asIterable()) {
      // delete each entity
      datastore.delete(en.getKey());
    }
  }

  public void adjustVote(Message message, long vote) {
    Entity messageEntity = new Entity(KIND_MESSAGE, message.getId().toString());

    messageEntity.setProperty("user", message.getUser().getEmail());
    // styled text part 1 here. Temporary remove it
    /*Parser parser = Parser.builder().build();
    Node document = parser.parse(message.getText());
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    messageEntity.setProperty("text", renderer.render(document));*/
    messageEntity.setProperty("text", message.getText());
    messageEntity.setProperty("timestamp", message.getTimestamp());
    messageEntity.setProperty("vote", message.getVote() + vote);

    datastore.put(messageEntity);
  }

  /**
   * Gets messages posted by a specific user.
   *
   * @return a list of messages posted by the user, or empty list if user has never posted a
   *     message. List is sorted by time descending.
   */
  public List<Message> getMessages(String user) {
    Query query =
        new Query(KIND_MESSAGE)
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
        new Query(KIND_MESSAGE)
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
        User user = getUser((String) entity.getProperty("user"));
        String text = (String) entity.getProperty("text");
        long timestamp = (long) entity.getProperty("timestamp");
        long vote = (long) entity.getProperty("vote");

        Message message = new Message(id, user, text, timestamp, vote);
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
    Query query = new Query(KIND_MESSAGE);
    PreparedQuery results = datastore.prepare(query);
    for(Entity entity : results.asIterable()) {
      users.add((String) entity.getProperty("user"));
    }
    return users;
  }
}
