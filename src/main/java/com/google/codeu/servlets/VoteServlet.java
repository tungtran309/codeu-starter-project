package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Message;
import com.google.codeu.data.Datastore;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/vote")
public class VoteServlet extends HttpServlet {
    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }

        String messageId = request.getParameter("id");
        Message message = datastore.getMessage(messageId);

        if (message == null) {
            // TODO: Put 'message not found' notification
        }
        else if (request.getParameter("upvote") != null) {
            datastore.adjustVote(message, 1);
        }
        else if (request.getParameter("downvote") != null) {
            datastore.adjustVote(message, -1);
        }

        response.sendRedirect(request.getHeader("referer"));
    }
}
