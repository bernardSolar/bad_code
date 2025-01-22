package com.cd.badcode;

import java.util.*;
import java.sql.*;
public class OrderProcessor {

    static Connection dbConnection;
    static double totalAmount = 0;
    static ArrayList<String> orderItems = new ArrayList<>();
    static String customerName;
    static String customerEmail;
    static boolean isVIP;

    public static void main(String[] args) {
        ProcessNewOrder(new Display(), new ScannerInput());
    }

    private static void ProcessNewOrder(Display display, Input input) {
        try {
            dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/store", "root", "password123"
            );

            display.show("Enter customer name:");
            customerName = input.next();
            display.show("Enter customer email:");
            customerEmail = input.next();

            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT is_vip FROM customers WHERE email='" + customerEmail + "'"
            );
            if (rs.next()) {
                isVIP = rs.getBoolean("is_vip");
            }

            while (true) {
                display.show("Enter item (or 'done' to finish):");
                String item = input.next();
                if (item.equals("done")) {
                    break;
                }

                rs = stmt.executeQuery(
                    "SELECT price FROM items WHERE name='" + item + "'"
                );

                if (rs.next()) {
                    double price = rs.getDouble("price");
                    if (isVIP) {
                        price = price * 0.9; // VIP discount
                    }
                    if (totalAmount > 100) {
                        price = price * 0.95; // Bulk discount
                    }
                    totalAmount += price;
                    orderItems.add(item);
                }
            }

            processOrder();
            sendConfirmationEmail(display);
            printReceipt(display);

        } catch (Exception e) {
            display.show("Error: " + e.getMessage());
        }
    }

    private static void processOrder() throws SQLException {
        String items = String.join(",", orderItems);
        Statement stmt = dbConnection.createStatement();
        stmt.executeUpdate(
            "INSERT INTO orders (customer_name, items, total) VALUES ('"
            + customerName + "','" + items + "'," + totalAmount + ")"
        );
        
        for (String item : orderItems) {
            stmt.executeUpdate(
                "UPDATE inventory SET quantity = quantity - 1 WHERE item='" + item + "'"
            );
        }
    }
    
    private static void sendConfirmationEmail(Display display) {
        display.show("Sending email to " + customerEmail);
        display.show("Order confirmation sent!");
    }
    
    private static void printReceipt(Display display) {
        display.show("=== RECEIPT ===");
        display.show("Customer: " + customerName);
        for (String item : orderItems) {
            display.show("Item: " + item);
        }
        display.show("Total: $" + totalAmount);
        if (isVIP) {
            display.show("VIP discount applied");
        }
    }
}
