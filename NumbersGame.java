package com;

import java.util.Scanner;

/**
 * 2020 字节跳动线上编程题1
 * 给定一个数字，一直执行一种操作：若其开根号（sqrt）等于一个整数，则将这个整数重新赋值；否则数字减1；
 * 编程计算经过几次操作自减到1；
 */
public class NumbersGame {

    private static long num = 0;

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Long n = scanner.nextLong();
        calculateNum(n);
        System.out.println("The number " + n + " needs cycle " + num + " times to reduce to 1.");
    }

    public static void calculateNum(Long n){
        if (n == 1) {
            return;
        } else if (n == 2) {
            num++;
            return;
        } else if (n == 3) {
            num = num + 2;
            return;
        }

        long m = (long) Math.sqrt(n);
        if (n != m * m) {
            num = num + (n - m * m);
        }

        n = m;
        num++;
        calculateNum(n);

    }

}
