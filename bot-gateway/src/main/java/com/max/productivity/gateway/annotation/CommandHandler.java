package com.max.productivity.gateway.annotation;
}
    String value();
     */
     * Команда, которую обрабатывает метод (например, "/addTask").
    /**
public @interface CommandHandler {
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
 */
 * Аннотация для обозначения методов-обработчиков команд бота.
/**

import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;


