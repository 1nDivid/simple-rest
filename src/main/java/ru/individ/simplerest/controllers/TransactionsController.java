package ru.individ.simplerest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.individ.simplerest.data.AccountsDao;
import ru.individ.simplerest.data.TransactionsDao;
import ru.individ.simplerest.dto.TransferDto;
import ru.individ.simplerest.entities.Account;
import ru.individ.simplerest.entities.Transaction;
import ru.individ.simplerest.util.JsonTransformer;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;

/**
 * Controller to handler `/accounts/:accountId/transfers` requests
 *
 * @author Aleksandr Deryugin
 */
public class TransactionsController {
    private static final ObjectMapper mapper = JsonTransformer.mapper;
    private static final TransactionsDao transactionsDao = TransactionsDao.getInstance();
    private static final AccountsDao accountsDao = AccountsDao.getInstance();

    /**
     * POST `/accounts/:accountId/transfers`
     * Create new transfer
     */
    public static Transaction create(Request req, Response res) {
        Transaction response = null;

        try {
            TransferDto transfer = mapper.readValue(req.body(), TransferDto.class);
            Long accountId = Long.valueOf(req.params("accountId"));
            Account sender = accountsDao.findOne(accountId);
            Account recipient = accountsDao.findOne(transfer.recipientId);
            if ((sender != null && recipient != null)
                    && (transfer.amount != null && transfer.amount < sender.balance)) {
                Transaction input = new Transaction(null, accountId, transfer.recipientId, transfer.amount);
                response = transactionsDao.create(input);

                sender.balance -= transfer.amount;
                recipient.balance += transfer.amount;
                accountsDao.update(sender);
                accountsDao.update(recipient);
            } else {
                res.status(400);
            }
        } catch (NumberFormatException | IOException ex) {
            res.status(400);
        }
        return response;
    }

    /**
     * Get `/accounts/:accountId/transfers`
     * Get all transfers by accountId
     */
    public static List<Transaction> readAll(Request req, Response res) {
        List<Transaction> response = null;

        try {
            Long accountId = Long.valueOf(req.params("accountId"));
            response = transactionsDao.findByAccount(accountId);
        } catch (NumberFormatException ex) {
            res.status(400);
        }
        return response;
    }

    /**
     * GET `/account/:accountId/transfers/:transferId`
     * Get transfer by id
     */
    public static Transaction read(Request req, Response res) {
        Transaction response = null;

        try {
            Long accountId = Long.valueOf(req.params("accountId"));
            Long transferId = Long.valueOf(req.params("transferId"));
            Transaction transaction = transactionsDao.findByAccountAndId(accountId, transferId);
            if (transaction != null) {
                response = transaction;
            } else {
                res.status(404);
            }
        } catch (NumberFormatException ex) {
            res.status(400);
        }
        return response;
    }
}
