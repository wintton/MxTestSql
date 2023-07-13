package com.mx.deathtrun;

public class Printer {
    public void print(String message)  {
        String[] split = message.split("");
        for (String s : split) {
            System.out.print(s);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
        System.out.println("");
    }
}
