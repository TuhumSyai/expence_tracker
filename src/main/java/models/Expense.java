package models;

import java.time.LocalDate;

// Пункт 2: Наследование
public class Expense extends Transaction {
    private String category;
    private String description; // Твое пожелание по описанию

    public Expense(int userId, double amount, String category, String description, LocalDate date) {
        super(amount, date); // Вызов конструктора родителя
        this.userId = userId;
        this.category = category;
        this.description = description;
    }

    // Геттеры
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public int getUserId() { return userId; }
}