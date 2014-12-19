package com.logosprog.kyivguide.app;

/**
 * Created by forando on 18.12.14.
 */
public class Test {
    public static void main(String[] args) {
        String input = "gas|";
        String[] keys = input.split("\\|");
        for (String key : keys) {
            System.out.println("key = " + key);
        }
    }
}
