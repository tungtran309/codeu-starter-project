package com.google.codeu.servlets;


import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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


@WebServlet("/users/*")
public class UserServlet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() throws ServletException {
        super.init();
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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String requestUrl = request.getRequestURI();

        String urlUserEmail = requestUrl.substring("/users/".length());
        User user = datastore.getUser(urlUserEmail);

        String loggedInUserEmail = getLoggedInUserEmail();

        if (user != null) {// Confirm that user is valid
            // Fetch user messages
            List<Message> messages = datastore.getMessages(urlUserEmail);
            List<Message> messageWithImage = new ArrayList<>();
            messages.forEach(message -> {
                Message replacedMessage = new Message(message.getId(), message.getUser(), ImageReplacement(message.getText()), message.getTimestamp(), message.getVote());
                messageWithImage.add(replacedMessage);
            });

            // Fetch about user
            String aboutUser;
            if (user.getAboutMe() == null) {
                aboutUser = "";
            } else {
                aboutUser = user.getAboutMe();
            }

            // Fetch username
            String displayedName = user.getDisplayedName();
            if (displayedName == null || displayedName.equals(""))
                displayedName = urlUserEmail;

            // Fetch avatar url
            String avatarUrl = user.getAvatarUrl();

            // Add them to the request
            request.setAttribute("loggedInUserEmail", loggedInUserEmail);
            request.setAttribute("urlUserEmail", urlUserEmail);
            request.setAttribute("messages", messageWithImage);
            request.setAttribute("about",
                    aboutUser.equals("") ? "This user has not entered any information yet." : aboutUser);
            request.setAttribute("displayedName", displayedName);
            request.setAttribute("avatarUrl", avatarUrl == null ? "" : avatarUrl);
        } else if (loggedInUserEmail.equals(urlUserEmail)) { // If new user logged in
            datastore.storeUser(new User(urlUserEmail, "", "", ""));
            request.setAttribute("loggedInUserEmail", loggedInUserEmail);
            request.setAttribute("urlUserEmail", urlUserEmail);
            request.setAttribute("messages", new ArrayList<>());
            request.setAttribute("about", "This user has not entered any information yet.");
            request.setAttribute("displayedName", urlUserEmail);
            request.setAttribute("avatarUrl", "");
        } else { // Someone entered a user that does not exist in the database
            response.sendRedirect("/index.html");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/user.jsp").forward(request,response);
    }

    /**
     * Return correct form of image for message
     */
    static String ImageReplacement(String rawMessage) {
        String regex = "(https?://\\S+\\.(png|jpg))";
        String replacement = "<img src=\"$1\" />";

        return rawMessage
                .replaceAll(regex, replacement);
    }
}
