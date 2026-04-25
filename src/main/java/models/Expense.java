package models;

import java.io.Serializable;
import java.time.LocalDate;

// Пункт 2: Наследование + Serializable для передачи по сети
public class Expense extends Transaction implements Serializable {
    private int id;
    private String category;
    private String description;
    // userId обычно находится в Transaction, но если нет - добавь его сюда

    // 1. Конструктор для загрузки из БД (с ID)
    // Именно его вызывает DatabaseHandler.getExpenses
    public Expense(int id, int userId, double amount, String category, String description, LocalDate date) {
        super(amount, date);
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.description = description;
    }

    // 2. Конструктор для создания новой траты (без ID)
    // Его вызывает AddExpenseController
    public Expense(int userId, double amount, String category, String description, LocalDate date) {
        super(amount, date);
        this.userId = userId;
        this.category = category;
        this.description = description;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Эти геттеры могут быть в Transaction, проверь
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public int getUserId() { return userId; }
}