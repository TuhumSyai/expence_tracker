package models;

import java.io.Serializable;
import java.time.LocalDate;

public class Transaction implements Serializable {
    protected int id;
    protected int userId;
    protected double amount;
    protected LocalDate date;

    public Transaction(double amount, LocalDate date) {
        this.amount = amount;
        this.date = date;
    }
    // Геттеры...
}