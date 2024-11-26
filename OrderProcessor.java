import java.util.*;
import java.sql.*;

public class OrderProcessor {
    // Global variables everywhere!
    static Connection dbConnection;
    static double totalAmount = 0;
    static ArrayList<String> orderItems = new ArrayList<>();
    static String customerName;
    static String customerEmail;
    static boolean isVIP;
    
    public static void main(String[] args) {
        // Everything in one giant method
        try {
            // Hardcoded database connection
            dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/store", "root", "password123"
            );
            
            // Mixing UI with business logic
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter customer name:");
            customerName = scanner.nextLine();
            System.out.println("Enter customer email:");
            customerEmail = scanner.nextLine();
            
            // Direct database query in business logic
            Statement stmt = dbConnection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT is_vip FROM customers WHERE email='" + customerEmail + "'"
            );
            if (rs.next()) {
                isVIP = rs.getBoolean("is_vip");
            }
            
            // Infinite loop with nested condition hell
            while (true) {
                System.out.println("Enter item (or 'done' to finish):");
                String item = scanner.nextLine();
                if (item.equals("done")) {
                    break;
                }
                
                // More SQL injection vulnerability
                rs = stmt.executeQuery(
                    "SELECT price FROM items WHERE name='" + item + "'"
                );
                
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    // Business logic scattered throughout
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
            
            // Mixed responsibilities - processing, email, and printing
            processOrder();
            sendConfirmationEmail();
            printReceipt();
            
        } catch (Exception e) {
            // Generic exception handling
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    // Method doing too many things
    private static void processOrder() throws SQLException {
        String items = String.join(",", orderItems);
        // More SQL injection
        Statement stmt = dbConnection.createStatement();
        stmt.executeUpdate(
            "INSERT INTO orders (customer_name, items, total) VALUES ('"
            + customerName + "','" + items + "'," + totalAmount + ")"
        );
        
        // Inventory management mixed with order processing
        for (String item : orderItems) {
            stmt.executeUpdate(
                "UPDATE inventory SET quantity = quantity - 1 WHERE item='" + item + "'"
            );
        }
    }
    
    // Method with hard-coded email logic
    private static void sendConfirmationEmail() {
        System.out.println("Sending email to " + customerEmail);
        // Pretend to send email
        System.out.println("Order confirmation sent!");
    }
    
    // Method mixing business logic with presentation
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
