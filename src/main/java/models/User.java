package models;

import java.io.Serializable;

// Пункт 5: implements Serializable позволяет передавать объект через Сокеты
public class User implements Serializable {
    private static final long serialVersionUID = 1L; // Для стабильной десериализации

    private int id;
    private String login;
    private String password;
    private String fullName; // ФИО

    // Конструктор для регистрации
    public User(String login, String password, String fullName) {
        this.login = login;
        this.password = password;
        this.fullName = fullName;
    }

    // Геттеры и сеттеры (Пункт 1: Инкапсуляция)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
}