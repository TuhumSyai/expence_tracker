package database;

import models.User;
import models.Expense;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseHandler extends Configs { // Пункт: Наследование
    Connection dbConnection;

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);
        return dbConnection;
    }

    // Регистрация пользователя
    public void signUpUser(User user) throws SQLException, ClassNotFoundException {
        String insert = "INSERT INTO " + Const.USER_TABLE + "(" +
                Const.USERS_LOGIN + "," + Const.USERS_PASS + "," + Const.USERS_FULLNAME + ")" +
                " VALUES(?,?,?)";

        PreparedStatement prSt = getDbConnection().prepareStatement(insert);
        prSt.setString(1, user.getLogin());
        prSt.setString(2, user.getPassword());
        prSt.setString(3, user.getFullName());
        prSt.executeUpdate();
    }

    // Вход пользователя (возвращает ID пользователя, если логин/пароль верны)
    public int loginUser(User user) throws SQLException, ClassNotFoundException {
        String query = "SELECT " + Const.USERS_ID + " FROM " + Const.USER_TABLE +
                " WHERE " + Const.USERS_LOGIN + "=? AND " + Const.USERS_PASS + "=?";

        PreparedStatement prSt = getDbConnection().prepareStatement(query);
        prSt.setString(1, user.getLogin());
        prSt.setString(2, user.getPassword());

        ResultSet resSet = prSt.executeQuery();
        if (resSet.next()) {
            return resSet.getInt(Const.USERS_ID);
        }
        return -1; // Пользователь не найден
    }

    // Добавление траты
    public void addExpense(Expense expense) throws SQLException, ClassNotFoundException {
        String insert = "INSERT INTO " + Const.EXPENSE_TABLE + "(" +
                Const.EXPENSES_USER + "," + Const.EXPENSES_AMOUNT + "," +
                Const.EXPENSES_CAT + "," + Const.EXPENSES_DESC + "," + Const.EXPENSES_DATE + ")" +
                " VALUES(?,?,?,?,?)";

        PreparedStatement prSt = getDbConnection().prepareStatement(insert);
        prSt.setInt(1, expense.getUserId());
        prSt.setDouble(2, expense.getAmount());
        prSt.setString(3, expense.getCategory());
        prSt.setString(4, expense.getDescription());
        prSt.setDate(5, Date.valueOf(expense.getDate())); // Конвертируем LocalDate в SQL Date
        prSt.executeUpdate();
    }
    public ArrayList<Expense> getExpenses(int userId) throws SQLException, ClassNotFoundException {
        ArrayList<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM " + Const.EXPENSE_TABLE + " WHERE " + Const.EXPENSES_USER + "=?";

        PreparedStatement prSt = getDbConnection().prepareStatement(query);
        prSt.setInt(1, userId);

        ResultSet resSet = prSt.executeQuery();
        while (resSet.next()) {
            Expense expense = new Expense(
                    resSet.getInt(Const.EXPENSES_ID),
                    resSet.getInt(Const.EXPENSES_USER),
                    resSet.getDouble(Const.EXPENSES_AMOUNT),
                    resSet.getString(Const.EXPENSES_CAT),
                    resSet.getString(Const.EXPENSES_DESC),
                    resSet.getDate(Const.EXPENSES_DATE).toLocalDate()
            );
            expenses.add(expense);
        }
        return expenses;
    }

    public void deleteExpense(int expenseId) throws SQLException, ClassNotFoundException {
        // Удаляем по первичному ключу ID записи
        String delete = "DELETE FROM " + Const.EXPENSE_TABLE + " WHERE id = ?";

        PreparedStatement prSt = getDbConnection().prepareStatement(delete);
        prSt.setInt(1, expenseId);
        prSt.executeUpdate();
    }


}