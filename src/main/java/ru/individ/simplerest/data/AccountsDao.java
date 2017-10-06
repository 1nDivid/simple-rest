package ru.individ.simplerest.data;

import ru.individ.simplerest.entities.Account;

/**
 * Data access object for {@link Account}
 *
 * @author Aleksandr Deryugin
 */
public class AccountsDao extends AbstractDao<Account> {
    private static AccountsDao instance;

    public static AccountsDao getInstance() {
        if (instance == null) {
            instance = new AccountsDao();
        }
        return instance;
    }

    private AccountsDao() {
        super();
    }
}
