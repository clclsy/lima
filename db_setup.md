# Database Setup Guide

This project uses a MySQL database and loads credentials from an environment file.

## 1. Copy the Example Environment File

Copy `example.env` to a new file named `.env` in the root of the project (or wherever your Java code expects it):

```sh
cp example.env .env
```

## 2. Edit Your `.env` File

Open `.env` and fill in your database credentials:

```
DB_URL=jdbc:mysql://localhost:3306/your_db_name
DB_USER=your_mysql_user
DB_PASSWORD=your_mysql_password
```

- `DB_URL` should match your MySQL instance and database name.
- `DB_USER` and `DB_PASSWORD` should be your MySQL credentials.

## 3. Make Sure MySQL is Running

Start your MySQL server if it's not already running.

## 4. Install Dependencies

Make sure you have the required Java dependencies. This project uses the `mysql-connector-j` driver and `dotenv` for Java.

- The MySQL connector JAR is already included in `lib/`.
- The code uses `io.github.cdimascio.dotenv` for loading environment variables.

## 5. Run the Importer or Test

You can run the database importer or test classes from `src/nutrisci/db/`:

```sh
javac -cp "lib/mysql-connector-j-9.3.0.jar:." src/nutrisci/db/CSVToDBImporter.java
java -cp "lib/mysql-connector-j-9.3.0.jar:src" nutrisci.db.CSVToDBImporter
```
