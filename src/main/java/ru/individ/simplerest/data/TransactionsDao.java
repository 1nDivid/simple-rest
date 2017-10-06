package ru.individ.simplerest.data;

import ru.individ.simplerest.entities.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data access object for {@link Transaction}
 *
 * @author Aleksandr Deryugin
 */
public class TransactionsDao extends AbstractDao<Transaction> {
    private static TransactionsDao instance;

    public static TransactionsDao getInstance() {
        if (instance == null) {
            instance = new TransactionsDao();
        }
        return instance;
    }

    private TransactionsDao() {
        super();
    }

    /**
     * Find transactions by account id
     *
     * @param accountId unique account id
     * @return list of transactions where account is sender or recipient
     */
    public List<Transaction> findByAccount(Long accountId) {
        return data.values()
                .stream()
                .filter(t -> (t.senderId.equals(accountId) || t.recipientId.equals(accountId)))
                .sorted(Comparator.comparing((Transaction t) -> t.id).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Find transactions by account id and transaction id
     *
     * @param accountId     unique account id
     * @param transactionId unique transaction id
     * @return transaction where account is sender or recipient / null if nothing found
     */
    public Transaction findByAccountAndId(Long accountId, Long transactionId) {
        Transaction transaction = data.get(transactionId);
        if (transaction != null
                && (transaction.senderId.equals(accountId) || transaction.recipientId.equals(accountId))) {
            return transaction;
        } else {
            return null;
        }
    }
}
