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
        try {
            dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/store", "root", "password123"
            );
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter customer name:");
            customerName = scanner.nextLine();
            System.out.println("Enter customer email:");
            customerEmail = scanner.nextLine();
            
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT is_vip FROM customers WHERE email='" + customerEmail + "'"
            );
            if (rs.next()) {
                isVIP = rs.getBoolean("is_vip");
            }
            
            while (true) {
                System.out.println("Enter item (or 'done' to finish):");
                String item = scanner.nextLine();
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
            sendConfirmationEmail();
            printReceipt();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
    
    private static void sendConfirmationEmail() {
        System.out.println("Sending email to " + customerEmail);
        System.out.println("Order confirmation sent!");
    }
    
    private static void printReceipt() {
        System.out.println("=== RECEIPT ===");
        System.out.println("Customer: " + customerName);
        for (String item : orderItems) {
            System.out.println("Item: " + item);
        }
        System.out.println("Total: $" + totalAmount);
        if (isVIP) {
            System.out.println("VIP discount applied");
        }
    }
}
