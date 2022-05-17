package com.openevents.utils;

public abstract class Numbers {
    public static int generateRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }
}
