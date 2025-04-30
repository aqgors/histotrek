package com.agors.infrastructure.repository;

import com.agors.domain.dao.UserDao;
import com.agors.domain.entity.User;

import java.util.List;
import java.util.Scanner;

public class UserDaoTest {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserDao userDao = new UserDao();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n==== Меню ====");
            System.out.println("1. Додати користувача");
            System.out.println("2. Показати всіх користувачів");
            System.out.println("3. Знайти користувача за ID");
            System.out.println("4. Оновити користувача");
            System.out.println("5. Видалити користувача");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> addUser();
                case 2 -> listUsers();
                case 3 -> getUserById();
                case 4 -> updateUser();
                case 5 -> deleteUser();
                case 0 -> {
                    System.out.println("Вихід...");
                    return;
                }
                default -> System.out.println("Невідомий вибір.");
            }
        }
    }

    private static void addUser() {
        System.out.print("Ім'я користувача: ");
        String username = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Пароль (хеш): ");
        String password = scanner.nextLine();

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password);
        userDao.addUser(user);
        System.out.println("Користувача додано.");
    }

    private static void listUsers() {
        List<User> users = userDao.getAllUsers();
        if (users.isEmpty()) {
            System.out.println("Немає користувачів.");
        } else {
            for (User user : users) {
                System.out.printf("ID: %d | Username: %s | Email: %s%n",
                    user.getId(), user.getUsername(), user.getEmail());
            }
        }
    }

    private static void getUserById() {
        System.out.print("ID користувача: ");
        int id = Integer.parseInt(scanner.nextLine());
        User user = userDao.getUserById(id);
        if (user != null) {
            System.out.printf("ID: %d | Username: %s | Email: %s%n",
                user.getId(), user.getUsername(), user.getEmail());
        } else {
            System.out.println("Користувача не знайдено.");
        }
    }

    private static void updateUser() {
        System.out.print("ID користувача для оновлення: ");
        int id = Integer.parseInt(scanner.nextLine());
        User existing = userDao.getUserById(id);
        if (existing == null) {
            System.out.println("Користувача не знайдено.");
            return;
        }

        System.out.print("Нове ім'я: ");
        existing.setUsername(scanner.nextLine());
        System.out.print("Новий email: ");
        existing.setEmail(scanner.nextLine());
        System.out.print("Новий пароль (хеш): ");
        existing.setPasswordHash(scanner.nextLine());

        userDao.updateUser(existing);
        System.out.println("Користувача оновлено.");
    }

    private static void deleteUser() {
        System.out.print("ID користувача для видалення: ");
        int id = Integer.parseInt(scanner.nextLine());
        userDao.deleteUser(id);
        System.out.println("Користувача видалено.");
    }
}
