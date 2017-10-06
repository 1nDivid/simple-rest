package ru.individ.simplerest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.individ.simplerest.controllers.AccountsController;
import ru.individ.simplerest.controllers.ErrorsController;
import ru.individ.simplerest.controllers.TransactionsController;
import ru.individ.simplerest.util.JsonTransformer;
import spark.ResponseTransformer;

import static spark.Spark.*;

/**
 * Server launcher
 * @author Aleksandr Deryugin
 */
public class SimpleServer {
    private static Logger logger = LoggerFactory.getLogger(SimpleServer.class);
    private static ResponseTransformer transformer = new JsonTransformer();

    public static void main(String[] args) {
        // server configuration
        port(3000);

        // rest routes
        get("/accounts", AccountsController::readAll, transformer);
        get("/accounts/:accountId", AccountsController::read, transformer);
        post("/accounts", AccountsController::create, transformer);
        put("/accounts/:accountId", AccountsController::update, transformer);
        delete("/accounts/:accountId", AccountsController::delete, transformer);

        get("/accounts/:accountId/transfers", TransactionsController::readAll, transformer);
        get("/accounts/:accountId/transfers/:transferId", TransactionsController::read, transformer);
        post("/accounts/:accountId/transfers", TransactionsController::create, transformer);

        after((req, res) -> res.type("application/json"));

        // error routes
        notFound(ErrorsController::notFound);
    }
}
