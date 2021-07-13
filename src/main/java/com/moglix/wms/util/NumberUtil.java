package com.moglix.wms.util;

/**
 * @author pankaj on 29/4/19
 */
public class NumberUtil {

    private static String[] TENS  = { "", "Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety" };
    private static String[] UNITS = new String[] {
            "",
            " One",
            " Two",
            " Three",
            " Four",
            " Five",
            " Six",
            " Seven",
            " Eight",
            " Nine",
            " Ten",
            " Eleven",
            " Twelve",
            " Thirteen",
            " Fourteen",
            " Fifteen",
            " Sixteen",
            " Seventeen",
            " Eighteen",
            " Nineteen"          };
    private static String[] DIGIT = { "", " Hundred", " Thousand", " Lakh", " Crore" };

    public static double round4(double d) {
        long result = Math.round((d * 10000));
        return (result / 10000.00);
    }

    public static double round3(double d) {
        long result = Math.round((d * 1000));
        return (result / 1000.00);
    }

    public static double roundToDecimals(double d, int c) {
        long result = Math.round(d * Math.pow(10, c));
        return (result / Math.pow(10, c));
    }

    public static String toWords(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Zero or Negative number not for conversion");
        } else if (number == 0) {
            return "Zero";
        }

        StringBuilder builder = new StringBuilder();
        WHILE: while (number > 0) {
            int len = digitCount(number);
            switch (len) {
                case 8:
                case 7:
                case 6:
                    builder.append(toWordsTwoDigit(number / 100000) + DIGIT[3]);
                    number = number % 100000;
                    break;
                case 5:
                case 4:
                    builder.append(toWordsTwoDigit(number / 1000) + DIGIT[2]);
                    number = number % 1000;
                    break;
                case 3:
                    builder.append(toWordsThreeDigit(number));
                    break WHILE;
                case 2:
                    builder.append(toWordsTwoDigit(number));
                    break WHILE;
                case 1:
                    builder.append(UNITS[number]);
                    break WHILE;
                default:
                    builder.append(" ").append(toWords(number / 10000000)).append(DIGIT[4]);
                    number = number % 10000000;
                    break;
            }
        }
        return builder.deleteCharAt(0).toString();
    }

    private static String toWordsTwoDigit(int number) {
        if (number > 19) {
            return TENS[number / 10] + UNITS[number % 10];
        } else {
            return UNITS[number];
        }
    }

    private static String toWordsThreeDigit(int numq) {
        int numr, nq;
        nq = numq / 100;
        numr = numq % 100;
        if (numr == 0) {
            return UNITS[nq] + DIGIT[1];
        } else {
            return UNITS[nq] + DIGIT[1] + " and" + toWordsTwoDigit(numr);
        }
    }

    private static int digitCount(int num) {
        int cnt = 0;
        while (num > 0) {
            num = num / 10;
            cnt++;
        }
        return cnt;
    }

    /*public static String generateRandomString(Integer length, boolean useLetters, boolean useNumbers) {
        if(length == null) {
            length = 10;
        }
        String randomString = RandomStringUtils.random(length, useLetters, useNumbers);
        return randomString;
    }*/

    public static void main(String[] arg) {
        /*System.out.println("No." + roundToDecimals(100.436,2));
        System.out.println("No. 12345 in words: " + toWords(12345));*/
    }
}
