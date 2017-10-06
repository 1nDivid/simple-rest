package ru.individ.simplerest.controllers;

import spark.Request;
import spark.Response;

/**
 * Controller to handler errors
 * @author Aleksandr Deryugin
 */
public class ErrorsController {
    /**
     * Simple 404 handler
     */
    public static String notFound(Request req, Response res) {
        res.status(404);
        return "";
    }
}
