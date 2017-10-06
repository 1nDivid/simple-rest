package ru.individ.simplerest.dto;

public class AccountDto {
    public String name;
    public Double balance;

    public AccountDto() {
    }

    public AccountDto(String name, Double balance) {
        this.name = name;
        this.balance = balance;
    }
}
