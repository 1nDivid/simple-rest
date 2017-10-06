package ru.individ.simplerest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.individ.simplerest.data.TransactionsDao;
import ru.individ.simplerest.entities.Transaction;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;

/**
 * Controller to handler `/transactions` requests
 * @author Aleksandr Deryugin
 */
public class TransactionsController {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TransactionsDao dao = new TransactionsDao();

    /**
     * POST `/transactions`
     * Create new transaction
     */
    public static String create(Request req, Response res) {
        res.status(201);

        try {
            Transaction input = mapper.readValue(req.body(), Transaction.class);
            Transaction transaction = dao.create(input.senderId, input.recipientId, input.amount);
            res.header("Location", "/transactions/" + transaction.id);
        } catch (IOException ex) {
            res.status(400);
        }
        return "";
    }

    /**
     * GET `/transactions/:id`
     * Get transaction/transactions list (if :id parameter exists and not empty)
     */
    public static String read(Request req, Response res) {
        res.type("application/json");
        res.status(200);
        String answer = "";

        try {
            String id = req.params("id");
            if (id == null || id.trim().length() == 0) {
                List<Transaction> all = dao.findAll();
                answer = mapper.writeValueAsString(all);
            } else {
                Transaction one = dao.findOne(Long.valueOf(id));
                if (one != null) {
                    answer = mapper.writeValueAsString(one);
                } else {
                    res.status(404);
                }
            }
        } catch (NumberFormatException ex) {
            res.status(400);
        } catch (JsonProcessingException ex) {
            res.status(500);
        }
        return answer;
    }

    /**
     * PUT `/transactions/:id`
     * Update transaction
     */
    public static String update(Request req, Response res) {
        res.status(200);

        try {
            String id = req.params("id");
            if (id == null || id.trim().length() == 0) {
                res.status(404);
            } else {
                Transaction input = mapper.readValue(req.body(), Transaction.class);
                input.id = Long.valueOf(id);
                if (dao.update(input) == null) {
                    res.status(404);
                }
            }
        } catch (NumberFormatException | IOException ex) {
            res.status(400);
        }
        return "";
    }

    /**
     * DELETE `/transactions/:id`
     * Delete transaction
     */
    public static String delete(Request req, Response res) {
        res.status(204);

        try {
            String id = req.params("id");
            if (id == null || id.trim().length() == 0) {
                res.status(404);
            } else {
                if (!dao.delete(Long.valueOf(id))) {
                    res.status(404);
                }
            }
        } catch (NumberFormatException ex) {
            res.status(400);
        }
        return "";
    }
}
