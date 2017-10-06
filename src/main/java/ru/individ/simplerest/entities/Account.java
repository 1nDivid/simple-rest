package ru.individ.simplerest.entities;

/**
 * Data model
 *
 * @author Aleksandr Deryugin
 */
public class Account extends AbstractEntity {
    public String name;
    public Double balance;

    public Account() {
    }

    public Account(Long id, String name, Double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }
}
