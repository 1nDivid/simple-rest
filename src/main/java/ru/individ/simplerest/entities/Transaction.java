package ru.individ.simplerest.entities;

/**
 * Data model
 *
 * @author Aleksandr Deryugin
 */
public class Transaction extends AbstractEntity {
    public Long senderId;
    public Long recipientId;
    public Double amount;

    public Transaction() {
    }

    public Transaction(Long id, Long senderId, Long recipientId, Double amount) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.amount = amount;
    }
}
