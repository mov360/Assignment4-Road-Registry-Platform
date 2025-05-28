package au.edu.rmit.sct;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Person person = new Person();
        Scanner scnr = new Scanner(System.in);

        while (true) {
            System.out.println("Please Choose option (Type 1-3 or \"exit\" to quit):");
            System.out.println("1) Add person");
            System.out.println("2) Update person details");
            System.out.println("3) Add demerit points");
            System.out.print("Please enter the option number or type \"exit\" to quit: ");

            String input = scnr.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting...");
                break;
            }

            
            switch (input) {
                case "1":
                System.out.println("Add new personal details result: ");
                    person.addPerson();
                    break;
                case "2":
                    System.out.println("Update personal details result: ");
                    person.updatePersonalDetails();
                    break;
                case "3":
                    System.out.println("Add demerit points result: ");
                    person.addDemeritPoints();
                    break;
                default:
                    System.out.println("Invalid key. Try again.");
                    continue;  // ask again
            }

            // After executing chosen option, quit the program
            break;
        }

        scnr.close();
    }
}
