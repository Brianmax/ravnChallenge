package org.ravn.moviescatalogchallenge.utils;

import javax.servlet.http.HttpServletRequest;

public class Utils {
    public static String getLoggedUser(HttpServletRequest request) {
        return request.getUserPrincipal().getName();
    }
}
