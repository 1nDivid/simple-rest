package ru.individ.simplerest.data;

import ru.individ.simplerest.entities.Transaction;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Data access object for {@link Transaction}
 * @author Aleksandr Deryugin
 */
public class TransactionsDao {
    // in-memory store
    private final ConcurrentMap<Long, Transaction> data;
    // track last entity id
    private Long lastId = 0L;

    public TransactionsDao() {
        data = new ConcurrentHashMap<>();
    }

    /**
     * Find transaction by id
     * @param id unique id of transaction
     * @return matching transaction or null if nothing found
     */
    public Transaction findOne(Long id) {
        return data.get(id);
    }

    /**
     * Get list of all transactions
     * @return list of transactions
     */
    public List<Transaction> findAll() {
        return data.values()
                .stream()
                .sorted(Comparator.comparing((Transaction t) -> t.id))
                .collect(Collectors.toList());
    }

    /**
     * Create and store new transaction
     * @param senderId unique id of sender account
     * @param recipientId unique id of recipient account
     * @param amount transfer sum
     * @return newly created transaction
     */
    public Transaction create(Long senderId, Long recipientId, Double amount) {
        lastId++;

        Transaction transaction = new Transaction(lastId, senderId, recipientId, amount);
        data.put(lastId, transaction);
        return transaction;
    }

    /**
     * Update existing transaction, completely rewrites old one
     * @param entity updated transaction
     * @return update transaction or null if nothing to update
     */
    public Transaction update(Transaction entity) {
        if (data.replace(entity.id, entity) == null) {
            return null;
        }
        return entity;
    }

    /**
     * Delete existing transaction
     * @param id unique id of transaction
     * @return true if transaction has been deleted, false if transaction not exists
     */
    public boolean delete(Long id) {
        return data.remove(id) != null;
    }
}
