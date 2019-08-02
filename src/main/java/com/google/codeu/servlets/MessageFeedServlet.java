package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles fetching all messages for the public feed
 */
@WebServlet("/feed")
public class MessageFeedServlet extends HttpServlet {
    private Datastore datastore;
    private static final String SEARCH_TAG = "tags";

    @Override
    public void init() throws ServletException {
        datastore = new Datastore();
    }

    private String getLoggedInUserEmail() {
        UserService userService = UserServiceFactory.getUserService();

        String loggedInUserEmail;
        if (userService.isUserLoggedIn()) {
            loggedInUserEmail = userService.getCurrentUser().getEmail();
        } else {
            loggedInUserEmail = "";
        }

        return loggedInUserEmail;
    }

    /**
     * Responds with a JSON representation of Message data for all users.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String loggedInUserEmail = getLoggedInUserEmail();
        String searchTag = request.getParameter(SEARCH_TAG);
        List<Message> messages = datastore.getAllMessages(searchTag);
        List<Message> messagesWithImage = new ArrayList<>();
        messages.forEach(message -> {
            Message replacedMessage = new Message(message.getId(), message.getUser(), UserServlet.ImageReplacement(message.getText()), message.getTimestamp(), message.getVote(), message.getTags());
            messagesWithImage.add(replacedMessage);
        });

        request.setAttribute("messages", messagesWithImage);
        request.setAttribute("loggedInUserEmail", loggedInUserEmail);

        request.getRequestDispatcher("/WEB-INF/feed.jsp").forward(request,response);
    }
}
