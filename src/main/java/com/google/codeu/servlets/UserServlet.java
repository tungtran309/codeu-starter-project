package com.google.codeu.servlets;


import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.codeu.data.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@WebServlet("users/*")
public class UserServlet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() throws ServletException {
        super.init();
        datastore = new Datastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String requestUrl = request.getRequestURI();

        String user = requestUrl.substring("/users/".length());
        User userData = datastore.getUser(user);

        if (userData != null) {// Confirm that user is valid


            // Fetch user messages
            List<Message> messages = datastore.getMessages(user);
            List<Message> messageWithImage = new ArrayList<>();
            messages.forEach(message -> {
                Message replacedMessage = new Message(message.getUser(), ImageReplacement(message.getText()));
                messageWithImage.add(replacedMessage);
            });

            // Fetch about user
            String aboutUser;
            if (userData.getAboutMe() == null) {
                aboutUser = "";
            } else {
                aboutUser = datastore.getUser(user).getAboutMe();
            }

            // Add them to the request
            request.setAttribute("messages", messageWithImage);
            request.setAttribute("about",
                    aboutUser.equals("")? "This user has not entered any information yet." : aboutUser);

            request.setAttribute("user", user);
        } else { // If new user logged in
            datastore.storeUser(new User(user, ""));
            request.setAttribute("user", user);
            request.setAttribute("messages", new ArrayList<>());
            request.setAttribute("about", "This user has not entered any information yet.");
        }

        request.getRequestDispatcher("/WEB-INF/user.jsp").forward(request,response);
    }

    /**
     * Return correct form of image for message
     */
    private String ImageReplacement(String rawMessage) {
        String regex = "(https?://\\S+\\.(png|jpg))";
        String replacement = "<img src=\"$1\" />";

        return rawMessage
                .replaceAll(regex, replacement);
    }
}
