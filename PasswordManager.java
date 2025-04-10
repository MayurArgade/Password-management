import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import javax.swing.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class PasswordManager {
    private static final String FILE_NAME = "Passwords.txt";
    private static final String MASTER_PASSWORD_FILE = "master.txt"; // File for master password

    public static void main(String[] args) {
        //  Ask for Master Password
        if (!authenticateUser()) {
            JOptionPane.showMessageDialog(null, "Incorrect Password! Exiting...");
            System.exit(0);
        }

        //  Continue to main menu after authentication
        Scanner scanner = new Scanner(System.in);
        String[] options = {"Save Password", "View Passwords"};
        int choice = JOptionPane.showOptionDialog(null, "Choose an option", "Password Manager",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            String website = JOptionPane.showInputDialog("Enter Website:");
            String password = JOptionPane.showInputDialog("Enter Password:");
            savePassword(website, password);
        } else if (choice == 1) {
            viewPasswords();
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Choice! Exiting...");
        }
    }

    // Authentication Method
    private static boolean authenticateUser() {
        String storedPassword = getStoredMasterPassword();
        if (storedPassword == null) {
            JOptionPane.showMessageDialog(null, "Master password file missing!");
            return false;
        }
        String inputPassword = JOptionPane.showInputDialog("Enter Master Password:");
        return inputPassword != null && inputPassword.equals(storedPassword);
    }

    // Retrieve Master Password from File
    private static String getStoredMasterPassword() {
        try (BufferedReader reader = new BufferedReader(new FileReader(MASTER_PASSWORD_FILE))) {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    // Save Password Method
    private static void savePassword(String website, String password) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(website + ":" + password + "\n");
            JOptionPane.showMessageDialog(null, "Password saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while saving: " + e.getMessage());
        }
    }

    // View Passwords Method
    private static void viewPasswords() {
        List<String[]> passwords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    passwords.add(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error while reading passwords: " + e.getMessage());
            return;
        }

        if (passwords.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No stored passwords found!");
        } else {
            showPasswordDialog(passwords, 0);
        }
    }

    private static void showPasswordDialog(List<String[]> passwords, int index) {
        if (index >= passwords.size()) return;

        String website = passwords.get(index)[0];
        String password = passwords.get(index)[1];

        // Show website and password with two buttons: Copy & Next
        Object[] options = {"Copy Password", "Next"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "Website: " + website + "\nPassword: " + password,
                "Stored Passwords",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[1]
        );

        if (choice == 0) {
            copyToClipboard(password);
            JOptionPane.showMessageDialog(null, "Password copied!");
        }

        showPasswordDialog(passwords, index + 1);
    }


    private static void copyToClipboard(String text) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(text), null
        );
    }

}
