package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourseConfiguration;
import ru.job4j.dreamjob.model.User;
import java.util.Properties;
import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepository.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");
        var configuration = new DatasourseConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearTable() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("TRUNCATE TABLE users RESTART IDENTITY");
            query.executeUpdate();
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        String email = "petr@mail.ru";
        String password = "petr";
        var user = sql2oUserRepository.save(new User(0, email, "Petr", password)).get();
        var findUser = sql2oUserRepository.findByEmailAndPassword(email, password).get();
        assertThat(findUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenWontSaveTwoUsersWithSameMailThenIsEmpty() {
        String email = "petr@mail.ru";
        String password = "petr";
        sql2oUserRepository.save(new User(0, email, "Petr", password)).get();
        assertThat(sql2oUserRepository.save(new User(0, email, "Anton", "545"))).isEmpty();
    }

    @Test
    public void whenInvalidPasswordThenIsEmpty() {
        String email = "petr@mail.ru";
        String password = "petr";
        sql2oUserRepository.save(new User(0, email, "Petr", password)).get();
        var findUser = sql2oUserRepository.findByEmailAndPassword(email, "88878");
        assertThat(findUser.isEmpty()).isTrue();
    }

    @Test
    public void whenInvalidMailThenIsEmpty() {
        String email = "petr@mail.ru";
        String password = "petr";
        sql2oUserRepository.save(new User(0, email, "Petr", password)).get();
        var findUser = sql2oUserRepository.findByEmailAndPassword("pion@mail.ru", password);
        assertThat(findUser.isEmpty()).isTrue();
    }
}