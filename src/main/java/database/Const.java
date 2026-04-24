package database;

public class Const {
    // Таблица пользователей
    public static final String USER_TABLE = "users";
    public static final String USERS_ID = "id";
    public static final String USERS_LOGIN = "login";
    public static final String USERS_PASS = "password";
    public static final String USERS_FULLNAME = "full_name";

    // Таблица трат
    public static final String EXPENSE_TABLE = "expenses";
    public static final String EXPENSES_ID = "id";
    public static final String EXPENSES_USER = "user_id";
    public static final String EXPENSES_AMOUNT = "amount";
    public static final String EXPENSES_CAT = "category";
    public static final String EXPENSES_DESC = "description";
    public static final String EXPENSES_DATE = "date";
}