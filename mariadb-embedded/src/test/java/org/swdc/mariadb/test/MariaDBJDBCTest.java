package org.swdc.mariadb.test;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.swdc.mariadb.embed.EmbeddedMariaDB;
import org.swdc.mariadb.embed.jdbc.EmbedMariaDBDriver;

import javax.persistence.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MariaDBJDBCTest {

    public static void main(String[] args) throws SQLException, InterruptedException {
        //DriverManager.registerDriver(new EmbedMariaDBDriver());
        /*Connection connection = DriverManager.getConnection(
                "jdbc:mysql://dbForTest?basedir=./mysqlData&datadir=./mysqlData/data&autocreate=true"
        );
        boolean init = connection.createStatement().execute("CREATE TABLE IF NOT EXISTS testuser (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255)," +
                "age INT" +
                ")");
        if (init) {
            System.err.println("init table ok");
        }
        System.err.println("ok");
        connection.close();*/


        System.err.println(ZonedDateTime.now().getOffset().toString());
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.url", "jdbc:mysql://dbForTest?basedir=./mysqlData&datadir=./mysqlData/data&autocreate=true&timeZone=+08:00");
        properties.setProperty("hibernate.connection.driver_class", EmbedMariaDBDriver.class.getName());
        properties.setProperty("hibernate.dialect", org.hibernate.dialect.MariaDBDialect.class.getName());
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.format_sql", "true");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.put(org.hibernate.jpa.AvailableSettings.LOADED_CLASSES, Arrays.asList(
                EntUser.class
        ));

        EntityManagerFactory entityFactory = Persistence.createEntityManagerFactory("default-test", properties);

        EntityManager em = entityFactory.createEntityManager();



        for (int i=0 ; i < 8; i++) {
            List<EntUser> users = em.createQuery("FROM EntUser").getResultList();
            if (users != null && users.size() == 0) {

                EntityTransaction tx = em.getTransaction();
                tx.begin();

                EntUser user = new EntUser();
                user.setName("张三");
                user.setAge(24);
                user.setSource(8.2);
                user.setNextAim(6.1f);
                user.setCreatedOn(LocalDate.now());
                user.setCreatedAt(LocalDateTime.now());
                user.setState(true);
                em.persist(user);
                tx.commit();

            } else {
                EntUser user = users.get(0);
                System.err.println(user);
            }
        }
        em.close();
        entityFactory.close();

    }

}
