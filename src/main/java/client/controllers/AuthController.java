package client.controllers;

import client.ClientSocket;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import models.DataPackage;
import models.User;

public class AuthController {

    // Элементы Авторизации (левая часть)
    @FXML private TextField loginField;
    @FXML private PasswordField passField; // в XML это TextField
    @FXML private Button signin;
    @FXML private Label loginErrorLabel;

    // Элементы Регистрации (правая часть)
    @FXML private TextField regFullName;
    @FXML private TextField regLogin;
    @FXML private PasswordField regpass;      // в XML с маленькой буквы
    @FXML private PasswordField checkregPass; // в XML такое имя для повтора пароля
    @FXML private Button signup;
    @FXML private Label regErrorLabel;

    @FXML
    void initialize() {
        // Скрываем ошибки при запуске
        showError(loginErrorLabel, false);
        showError(regErrorLabel, false);
    }

    @FXML
    void handleSignIn() {
        showError(loginErrorLabel, false);

        String login = loginField.getText().trim();
        String pass = passField.getText().trim();

        if (login.isEmpty() || pass.isEmpty()) {
            loginErrorLabel.setText("Заполните все поля");
            showError(loginErrorLabel, true);
            return;
        }

        DataPackage request = new DataPackage("LOGIN");
        request.setUser(new User(login, pass, ""));

        DataPackage response = ClientSocket.sendRequest(request);

        if (response != null && "SUCCESS".equals(response.getOperation())) {
            System.out.println("Вход выполнен! Добро пожаловать, " + response.getUser().getFullName());
            // TODO: Переход к главному окну
        } else {
            loginErrorLabel.setText("Неверный логин или пароль");
            showError(loginErrorLabel, true);
        }
    }

    @FXML
    void handleSignUp() {
        showError(regErrorLabel, false);
        regErrorLabel.setStyle("-fx-text-fill: red;");

        String fullName = regFullName.getText().trim();
        String login = regLogin.getText().trim();
        String pass = regpass.getText().trim();
        String checkPass = checkregPass.getText().trim();

        // 1. Проверка на пустые поля
        if (fullName.isEmpty() || login.isEmpty() || pass.isEmpty()) {
            regErrorLabel.setText("Заполните все поля!");
            showError(regErrorLabel, true);
            return;
        }

        // 2. Проверка совпадения паролей
        if (!pass.equals(checkPass)) {
            regErrorLabel.setText("Пароли не совпадают!");
            showError(regErrorLabel, true);
            return;
        }

        DataPackage request = new DataPackage("REGISTER");
        request.setUser(new User(login, pass, fullName));

        DataPackage response = ClientSocket.sendRequest(request);

        if (response != null) {
            if ("SUCCESS".equals(response.getOperation())) {
                regErrorLabel.setStyle("-fx-text-fill: green;");
                regErrorLabel.setText("Успех! Теперь войдите.");
                showError(regErrorLabel, true);
            } else if ("LOGIN_TAKEN".equals(response.getOperation())) {
                regErrorLabel.setText("Логин уже занят!");
                showError(regErrorLabel, true);
            } else {
                regErrorLabel.setText("Ошибка регистрации");
                showError(regErrorLabel, true);
            }
        }
    }

    private void showError(Label label, boolean isVisible) {
        label.setVisible(isVisible);
        label.setManaged(isVisible);
    }
}