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

import java.util.ArrayList;
import java.util.UUID;

/** A single message posted by a user. */
public class Message {
  private UUID id;
  private User user;
  private String text;
  private long timestamp;
  private long vote;
  private ArrayList<String> tags;

  /**
   * Constructs a new {@link Message} posted by {@code user} with {@code text} content. Generates a
   * random ID and uses the current system time for the creation time.
   */
  public Message(User user, String text, long vote) {
      this(UUID.randomUUID(), user, text, System.currentTimeMillis(), vote, null);
  }

  String getImageLink(String text) {
      if (text.indexOf("<img") == -1) {
          return text;
      }
      int pos = text.indexOf("<img");
      int nxtPos = text.indexOf(">", pos + 1);
      if (nxtPos == -1) {
          nxtPos = text.length();
      }
      return text.substring(pos, nxtPos + 1);
  }

  ArrayList<String> getTagsFromText(String text) {
      ArrayList<String> tags = new ArrayList<>();
      int prePos = text.indexOf("<img");
      int pos = text.indexOf("<li>", prePos + 1);
      while (pos != -1) {
          int nxtPos = text.indexOf("</li>", pos + 1);
          String tag = text.substring(pos + 4, nxtPos);
          tag = tag.replace("#", "");
          tag = tag.substring(0, tag.length() - 1);
          pos = text.indexOf("<li>", pos + 1);
          tags.add(tag);
      }
      return tags;
  }

  public Message(UUID id, User user, String text, long timestamp, long vote, ArrayList<String> tags) {
    this.id = id;
    this.user = user;
    if (tags != null && tags.isEmpty()) {
        this.text = getImageLink(text);
    } else {
        this.text = text;
    }

    this.timestamp = timestamp;
    this.vote = vote;
    if (tags == null) {
        this.tags = new ArrayList<>();
        return;
    }
    if (tags.isEmpty()) {
      this.tags = getTagsFromText(text);
    } else {
      this.tags = tags;
    }
  }

  public ArrayList<String> getTags() {
      return tags;
  }
  public ArrayList<String> setTags(ArrayList tags) {
      return this.tags = tags;
  }

  public boolean isContainTag(String searchTag) {
      if (tags == null) {
          return text.contains(searchTag);
      }
      for (String tag : tags) {
          if (searchTag.equals(tag)) {
              return true;
          }
      }
      return false;
  }

  public UUID getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public String getText() {
    return text;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public long getVote() { return vote; }
}
