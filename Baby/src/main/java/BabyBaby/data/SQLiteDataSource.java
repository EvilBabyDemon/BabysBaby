package BabyBaby.data;

import BabyBaby.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SQLiteDataSource.class);
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        try {
            final File dbFile = new File("data/database.db");

            if (!dbFile.exists()) {
                LOGGER.info("database does not exist");
                if (dbFile.createNewFile()) {
                    LOGGER.info("Created database file");
                } else {
                    LOGGER.info("Could not create database file");
                }
            } else {
                LOGGER.info("database already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        config.setJdbcUrl("jdbc:sqlite:data/database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);

        try (final Statement statement = getConnection().createStatement()) {
            final String defaultPrefix = Config.get("prefix");

            // guild_settings Table
            // language=SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guild_id VARCHAR(20) NOT NULL," +
                    "guild_name VARCHAR(20) NOT NULL," +
                    "prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "'" +
                    ");");

            LOGGER.info("guild_settings Table initialised"); // suggestions

            // suggestion Table
            // language-SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS suggestions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_name VARCHAR(20) NOT NULL," +
                    "guild_name VARCHAR(20) NOT NULL," +
                    "suggestion VARCHAR(255) NOT NULL" +
                    ");");

            LOGGER.info("suggestions Table initialised");

            // bug Table
            // language-SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS bugreports (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_name VARCHAR(20) NOT NULL," +
                    "guild_name VARCHAR(20) NOT NULL," +
                    "bug VARCHAR(255) NOT NULL" +
                    ");");

            LOGGER.info("bugreport Table initialised");

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private SQLiteDataSource() {
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}