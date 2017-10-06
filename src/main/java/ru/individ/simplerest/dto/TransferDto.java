package ru.individ.simplerest.dto;

public class TransferDto {
    public Long recipientId;
    public Double amount;

    public TransferDto() {
    }

    public TransferDto(Long recipientId, Double amount) {
        this.recipientId = recipientId;
        this.amount = amount;
    }
}
