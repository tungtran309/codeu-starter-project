package com.google.codeu.servlets;

import com.google.api.client.json.Json;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Handles fetching all messages for the public feed
 */
@WebServlet("/feed")
public class MessageFeedServlet extends HttpServlet {
    private Datastore datastore;

    @Override
    public void init() throws ServletException {
        datastore = new Datastore();
    }

    /**
     * Responds with a JSON representation of Message data for all users.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");

        List<Message> messages = datastore.getAllMessages();
        Gson gson = new Gson();
        String json = gson.toJson(messages);

        response.getOutputStream().println(json);
    }
}
