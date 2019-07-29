package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.User;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Handles fetching and saving user data.
 */
@WebServlet("/setting")
public class SettingServlet extends HttpServlet {

    private Datastore datastore;

    @Override
    public void init() {
        datastore = new Datastore();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        JsonObject jsonObject = new JsonObject();

        UserService userService = UserServiceFactory.getUserService();

        if (userService.isUserLoggedIn()) {
            String userEmail = userService.getCurrentUser().getEmail();
            User user = datastore.getUser(userEmail);
            jsonObject.addProperty("displayedName", user.getDisplayedName());
        } else {
            response.getWriter().println("{}");
            return;
        }

        response.setContentType("application/json");
        response.getWriter().println(jsonObject.toString());
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        UserService userService = UserServiceFactory.getUserService();
        if (!userService.isUserLoggedIn()) {
            response.sendRedirect("/index.html");
            return;
        }

        String userEmail = userService.getCurrentUser().getEmail();
        String aboutMe = datastore.getUser(userEmail).getAboutMe();
        String avatarUrl = datastore.getUser(userEmail).getAvatarUrl();
        String displayedName = Jsoup.clean(request.getParameter("displayed-name"), Whitelist.none());

        User user = new User(userEmail, aboutMe, displayedName, avatarUrl);
        datastore.storeUser(user);

        response.sendRedirect("/setting.html");
    }
}