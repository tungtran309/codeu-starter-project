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

import com.google.codeu.data.Tag;
import com.google.codeu.data.Message;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;

/** Handles fetching and saving {@link Message} instances. */
@WebServlet("/tag")
public class TagChartServlet extends HttpServlet {

    private JsonArray tagArray;

    @Override
    public void init() {
        tagArray = new JsonArray();
        Gson gson = new Gson();

        Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/tag_data.csv"));
        scanner.nextLine();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] cells = line.split(",");

            String name = cells[0];
            int count = Integer.parseInt(cells[1]);

            tagArray.add(gson.toJsonTree(new Tag(name, count)));
        }

        scanner.close();
    }

    /**
     * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
     * an empty array if the user is not provided.
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.getWriter().println(tagArray.toString());
    }
}
