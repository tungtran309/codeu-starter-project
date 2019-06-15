package com.google.codeu.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.codeu.data.Marker;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/markers")
public class MarkerServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lng = Double.parseDouble(request.getParameter("lng"));
        String content = Jsoup.clean(request.getParameter("content"), Whitelist.none());

        Marker marker = new Marker(lat, lng, content);
        storeMarker(marker);
    }

    public void storeMarker(Marker marker) {
        Entity markerEntity = new Entity("Marker");
        markerEntity.setProperty("lat", marker.getLat());
        markerEntity.setProperty("lng", marker.getLng());
        markerEntity.setProperty("content", marker.getContent());

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(markerEntity);
    }
}
