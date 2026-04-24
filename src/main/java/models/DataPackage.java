package models;

import java.io.Serializable;
import java.util.ArrayList;

public class DataPackage implements Serializable {
    private String operation; // "LOGIN", "REGISTER", "ADD_EXPENSE", "GET_ALL_EXPENSES"
    private User user;
    private Expense expense;

    // Пункт 4: Используем Коллекции (для отправки списка трат от сервера клиенту)
    private ArrayList<Expense> expenses;

    public DataPackage(String operation) {
        this.operation = operation;
    }

    // Геттеры и сеттеры для всех полей
    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }
    public void setUser(User user) { this.user = user; }
    public User getUser() { return user; }
    public void setExpense(Expense expense) { this.expense = expense; }
    public Expense getExpense() { return expense; }
    public ArrayList<Expense> getExpenses() { return expenses; }
    public void setExpenses(ArrayList<Expense> expenses) { this.expenses = expenses; }
}