package com.process_killer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple cross-platform command-line utility for managing processes by PID or
 * Port.
 * 
 * Features:
 * - List all processes with associated ports.
 * - Find a process running on a specific port.
 * - Kill a process by PID.
 * 
 * Commands:
 * 
 * <pre>
 *   refresh             -> Refresh and display all running processes.
 *   find <by> <port>    -> Find the process ID (PID) using a given port.
 *   kill <pid>          -> Kill a process by its PID.
 *   q                   -> Quit the program.
 * </pre>
 */
public class App {

    /** Current operating system name (lowercase). */
    private static final String SYSTEM = System.getProperty("os.name").toLowerCase();

    /** Scanner to read user input from console. */
    private static final Scanner INPUT = new Scanner(System.in);

    /**
     * Main entry point for the application.
     * Handles command loop until user quits.
     */
    public static void main(String[] args) {
        printProcess(); // Initially print running processes

        while (true) {
            System.out.print(": ");
            var input = INPUT.nextLine();

            // Exit program
            if (input.equalsIgnoreCase("q")) {
                break;
            }

            var arr = input.split(" ");

            // Validate command
            if (!arr[0].equals("kill") && !arr[0].equals("find") && !arr[0].equals("refresh")) {
                System.out.println("Invalid Command!");
                continue;
            }

            // Dispatch command
            if (arr[0].equals("kill")) {
                processKillCommand(arr);
            } else if (arr[0].equals("find")) {
                processFindCommand(arr);
            } else if (arr[0].equals("refresh")) {
                printProcess();
            }
        }
    }

    /**
     * Handles the "find" command.
     * Finds the PID of the process using a specific port.
     * 
     * @param arr command arguments ("find by <port>")
     */
    private static void processFindCommand(String[] arr) {
        if (arr.length != 3) {
            System.out.println("Invalid Command!");
            return;
        }

        var valid = validate(arr[2]);
        if (!valid) {
            System.out.println("Invalid Port!");
            return;
        }

        List<String> findCommand = new ArrayList<>();

        if (SYSTEM.contains("win")) {
            // Windows: use netstat to find PID by port
            findCommand.add("cmd.exe");
            findCommand.add("/c");
            findCommand.add("netstat -ano | findstr :" + arr[2]);
        } else {
            // Linux/macOS: use lsof to find PID by port
            findCommand.add("bash");
            findCommand.add("-c");
            findCommand.add("lsof -t -i :" + arr[2]);
        }

        try {
            Process findProcess = new ProcessBuilder(findCommand).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(findProcess.getInputStream()))) {
                String text = reader.readLine();
                if (text == null || text.isEmpty()) {
                    System.out.println("No process found on port " + arr[2]);
                    return;
                }

                var resArr = text.trim().split(" ");
                System.out.println("Found process on port " + arr[2] + " with PID: "
                        + resArr[resArr.length - 1].trim());
            }
        } catch (IOException ex) {
            System.out.printf("Failed to find Process running on port %s : %s%n", arr[2], ex.getMessage());
        }
    }

    /**
     * Handles the "kill" command.
     * Validates PID before attempting to kill the process.
     *
     * @param arr command arguments ("kill <pid>")
     */
    private static void processKillCommand(String[] arr) {
        var valid = validate(arr[1]);
        if (!valid) {
            System.out.println("Invalid PID!");
            return;
        }
        killProcess(arr[1]);
    }

    /**
     * Kills a process by PID using the appropriate OS command.
     *
     * @param pid process ID to kill
     */
    private static void killProcess(String pid) {
        try {
            List<String> killCommand = new ArrayList<>();

            if (SYSTEM.contains("win")) {
                // Windows: taskkill
                killCommand.add("cmd.exe");
                killCommand.add("/c");
                killCommand.add("taskkill /PID " + pid + " /F");
            } else {
                // Linux/macOS: kill
                killCommand.add("bash");
                killCommand.add("-c");
                killCommand.add("kill -9 " + pid);
            }

            Process killProcess = new ProcessBuilder(killCommand).start();
            killProcess.waitFor();
            System.out.println("Killed process " + pid);

            // Refresh process list after killing
            printProcess();
        } catch (IOException | InterruptedException e) {
            System.out.printf("Failed to kill process %s : %s%n", pid, e.getMessage());
        }
    }

    /**
     * Prints currently running processes and their associated ports.
     * Uses netstat on Windows, lsof on Linux/macOS.
     */
    private static void printProcess() {
        List<String> command = new ArrayList<>();

        if (SYSTEM.contains("win")) {
            // Windows: netstat with PID info
            command.add("cmd.exe");
            command.add("/c");
            command.add("netstat -ano");
        } else {
            // Linux/macOS: lsof with port info
            command.add("bash");
            command.add("-c");
            command.add("lsof -i -P -n");
        }

        try {
            Process process = new ProcessBuilder(command).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            System.out.printf("Sorry internal error occurred : %s%n", e.getMessage());
        }
    }

    /**
     * Validates whether a given string is a number (PID/Port).
     *
     * @param pid string representing a PID or Port number
     * @return true if valid integer, false otherwise
     */
    private static boolean validate(String pid) {
        try {
            Integer.parseInt(pid);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
