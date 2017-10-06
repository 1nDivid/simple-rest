package ru.individ.simplerest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.individ.simplerest.data.AccountsDao;
import ru.individ.simplerest.dto.AccountDto;
import ru.individ.simplerest.entities.Account;
import ru.individ.simplerest.util.JsonTransformer;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.List;

/**
 * Controller to handler `/accounts` requests
 *
 * @author Aleksandr Deryugin
 */
public class AccountsController {
    private static final ObjectMapper mapper = JsonTransformer.mapper;
    private static final AccountsDao accountsDao = AccountsDao.getInstance();

    /**
     * GET `/account/:accountId`
     * Get account by id
     */
    public static Account read(Request req, Response res) {
        Account response = null;

        Long accountId = Long.valueOf(req.params("accountId"));
        Account account = accountsDao.findOne(accountId);
        if (account != null) {
            response = account;
        } else {
            res.status(404);
        }
        return response;
    }

    /**
     * GET `/accounts`
     * Get all accounts
     */
    public static List<Account> readAll(Request req, Response res) {
        return accountsDao.findAll();
    }

    /**
     * POST `/accounts`
     * Create new account
     */
    public static Account create(Request req, Response res) {
        Account response = null;

        try {
            AccountDto dto = mapper.readValue(req.body(), AccountDto.class);
            if (dto.name != null && !"".equals(dto.name.trim()) && dto.balance != null) {
                Account account = new Account(null, dto.name, dto.balance);
                response = accountsDao.create(account);
            }
        } catch (IOException e) {
            res.status(400);
        }
        return response;
    }

    /**
     * PUT `/accounts/:accountId`
     * Update existing client
     */
    public static Account update(Request req, Response res) {
        Account response = null;

        try {
            AccountDto dto = mapper.readValue(req.body(), AccountDto.class);
            Long accountId = Long.valueOf(req.params("accountId"));
            if (dto.name != null && !"".equals(dto.name.trim()) && dto.balance != null) {
                Account account = accountsDao.update(new Account(accountId, dto.name, dto.balance));
                if (account != null) {
                    response = account;
                } else {
                    res.status(404);
                }
            }
        } catch (NumberFormatException | IOException e) {
            res.status(400);
        }
        return response;
    }

    /**
     * DELETE `/accounts/:accountId`
     * Delete existing client
     */
    public static String delete(Request req, Response res) {
        try {
            Long accountId = Long.valueOf(req.params("accountId"));
            if (!accountsDao.delete(accountId)) {
                res.status(404);
            }
        } catch (NumberFormatException ex) {
            res.status(400);
        }
        return "";
    }
}
