# Histotrek

Histotrek — це настільний додаток на JavaFX для вивчення історичних місць. Користувач може переглядати списки місць, додавати їх до обраного, залишати відгуки, а також генерувати звіти.

## Основні можливості

* **Splash Screen** з анімацією тексту та падаючим піском
* **Меню** з опціями: вхід, реєстрація, гостьовий режим
* **Реєстрація** нових користувачів із валідацією даних
* **Вхід** для зареєстрованих користувачів із перевіркою пароля
* **Гостьовий режим**: перегляд місць без авторизації
* **Списки місць**: перегляд, пошук, фільтрація
* **Обране**: додавання/видалення місць у власний список
* **Відгуки**: залишення оцінок та коментарів
* **Налаштування**: зміна теми, мови, розміру шрифту тощо
* **Адміністративний інтерфейс** (заготовка)

## Технології

* Java 17+
* JavaFX 17+
* JDBC / PostgreSQL
* Maven

## Структура проекту

```
com.agors
├─ application
│  ├─ form      # Форма Login, Signup, UserForm, GuestForm, SettingsForm, AdminForm
│  └─ window    # SplashScreen, MenuScreen, MessageBox
├─ domain
│  ├─ entity    # Сутності: User, Place, Favorite, Review, Report
│  └─ validation# Валідатори: LoginValidator, SignupValidator
├─ infrastructure
│  ├─ persistence
│  │  ├─ contract  # DAO-інтерфейси
│  │  └─ impl      # Реалізації DAO через JDBC
│  └─ util         # Утиліти: ConnectionManager, ConnectionHolder, PropertiesUtil, PasswordUtil, PersistenceInitializer
└─ README.md
```

## Налаштування та запуск

1. **Налаштувати базу** PostgreSQL через скрипти `ddl_postgresql.sql` та `dml_postgresql.sql` у ресурсах.
2. Сконфігурувати `application.properties`:

   ```properties
   db.url=jdbc:postgresql://localhost:5432/histotrek
   db.username=your_user
   db.password=your_password
   db.pool.size=5
   db.run.dml=true
   ```

**Зібрати** та **запустити** проект через Maven:

   ```bash
   mvn clean install
   mvn javafx:run
   ```

## Розширення

* Додати адміністративний інтерфейс у `AdminForm`
* Реалізувати зберігання зображень локально
* Інтегрувати віддалений API для додаткових даних
* Покращити UI анімації

---

© 2025 Histotrek
