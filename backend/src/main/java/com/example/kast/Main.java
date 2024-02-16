package com.example.kast;


import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Основной класс, отвечающий за запуск приложения
 */
@SpringBootApplication
@EnableMongock
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
