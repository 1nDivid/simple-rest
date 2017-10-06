package ru.individ.simplerest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.individ.simplerest.controllers.ErrorsController;
import ru.individ.simplerest.controllers.TransactionsController;
import spark.Request;
import spark.Response;

import static spark.Spark.*;

/**
 * Server launcher
 * @author Aleksandr Deryugin
 */
public class SimpleServer {
    private static Logger logger = LoggerFactory.getLogger(SimpleServer.class);

    public static void main(String[] args) {
        // server configuration
        port(3000);

        // logging requests/responses for debug purpose
        before((request, response) -> logger.debug(requestToString(request)));
        after((request, response) -> logger.debug(responseToString(response)));

        // rest routes
        get("/transactions", TransactionsController::read);
        get("/transactions/:id", TransactionsController::read);
        post("/transactions", TransactionsController::create);
        put("/transactions/:id", TransactionsController::update);
        delete("/transactions/:id", TransactionsController::delete);

        // error routes
        notFound(ErrorsController::notFound);
    }

    /**
     * Get string representation of logging info from {@link Request}
     * @param request request to log
     * @return string to pass to slf4j
     */
    private static String requestToString(Request request) {
        return ">>> " + request.requestMethod() + " " + request.url() + " " + request.body();
    }

    /**
     * Get string representation of logging info from {@link Response}
     * @param response response to log
     * @return string to pass to slf4j
     */
    private static String responseToString(Response response) {
        return "<<< " + response.status() + " " + response.body();
    }
}
