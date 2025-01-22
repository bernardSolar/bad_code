package com.cd.badcode;

import java.util.Scanner;

public class ScannerInput implements Input {
    private Scanner scanner = new Scanner(System.in);

    @Override
    public String next() {
        return scanner.nextLine();
    }
}
