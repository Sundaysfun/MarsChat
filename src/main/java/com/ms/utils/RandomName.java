package com.ms.utils;

import java.util.Random;

public class RandomName {

    private static String[] firstNameArray = {"张", "赵", "孙", "李", "王"};
    private static String[] middleNameArray = {"小", "二", "老", "大"};
    private static String[] lastNameArray = {"明", "伟", "花", "红", "驴", "鱼"};
    private static String[] shaDiaoNameArray = {"二狗", "狗剩"};

    public static String getRandomName() {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        if (random.nextInt(9) == 0) {
            stringBuilder.append(shaDiaoNameArray[random.nextInt(shaDiaoNameArray.length)]);
        } else {
            stringBuilder.append(firstNameArray[random.nextInt(firstNameArray.length)]);
            stringBuilder.append(middleNameArray[random.nextInt(middleNameArray.length)]);
            stringBuilder.append(lastNameArray[random.nextInt(lastNameArray.length)]);
        }
        return stringBuilder.toString();
    }
}
