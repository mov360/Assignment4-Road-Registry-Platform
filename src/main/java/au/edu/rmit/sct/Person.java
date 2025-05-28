package au.edu.rmit.sct;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Person{

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private String offensedate;
    private int age;
    private HashMap<Date, Integer> addDemeritPoint = new HashMap<>();
    private boolean isSuspended = false;


//region [Helper Code]

    public String idGenerator() {
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String symbols = "!@#$%^&*()_+-=[]{}|";
        String lettersSymbols = lower + symbols;
        String digits = "23456789";
        String allAllowed = lower + symbols + digits;
        SecureRandom random = new SecureRandom();
        char[] id = new char[10];

    
        for (int i = 0; i < 2; i++)
            id[i] = digits.charAt(random.nextInt(digits.length()));

    
        int count = 0;
    
        while (count < 3) {
            int pos = 2 + random.nextInt(6);
            if (id[pos] == 0) {
                id[pos] = lettersSymbols.charAt(random.nextInt(lettersSymbols.length()));
                count++;
            }
        }
    
        for (int i = 2; i < 8; i++) {
            if (id[i] == 0) id[i] = allAllowed.charAt(random.nextInt(allAllowed.length()));
        }

        id[8] = upper.charAt(random.nextInt(upper.length()));
        id[9] = upper.charAt(random.nextInt(upper.length()));

        return new String(id);
    }
    
    public String address() {
        Scanner scnr = new Scanner(System.in);
    
        System.out.println("Please enter your address:");
    
        System.out.print("Please enter your Street Number: ");
        String num = scnr.nextLine().trim();
    
        System.out.print("Please enter your Street Name: ");
        String name = scnr.nextLine().trim();
    
        System.out.print("Please enter your City: ");
        String city = scnr.nextLine().trim();
        if (city.length() < 4 || city.isBlank()) {
            throw new IllegalArgumentException("City must be at least 4 characters and not blank.");
        }
    
        System.out.print("Please enter your State: ");
        String state = scnr.nextLine().trim();
        if (state.length() < 4 || state.isBlank()) {
            throw new IllegalArgumentException("State must be at least 4 characters and not blank.");
        }
    
        System.out.print("Please enter your Country: ");
        String country = scnr.nextLine().trim();
        if (country.length() < 4 || country.isBlank()) {
            throw new IllegalArgumentException("Country must be at least 4 characters and not blank.");
        }
    
        return num + "|" + name + "|" + city + "|" + state + "|" + country;
    }
    
    public boolean saveToFile() {
        String fileName = "person_data.txt";
        File file = new File(fileName);
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write("Person ID: " + personID);
            writer.newLine();
            writer.write("First Name: " + firstName);
            writer.newLine();
            writer.write("Last Name: " + lastName);
            writer.newLine();
            writer.write("Birthdate: " + birthdate);
            writer.newLine();
            writer.write("Address: " + address);
            writer.newLine();
            writer.write("--------------------------------------------------");
            writer.newLine();
            System.out.println("Person information saved to " + fileName);
            System.out.println("Your Personal ID: " + personID);
            return true;
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            return false;
        }
    }

    private List<String> findPersonBlock(String targetID) {
        List<String> block = new ArrayList<>();
        boolean found = false;
    
        try (Scanner sc = new Scanner(new File("person_data.txt"))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("Person ID: ")) {
                    if (line.contains(targetID)) {
                        found = true;
                        block.add(line);
                    } else {
                        found = false;
                    }
                } else if (found) {
                    block.add(line);
                    if (line.contains("-----")) break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    
        return block;
    }

    private Person parsePersonDetails(List<String> personBlock) {
        Person person = new Person();
        for (String line : personBlock) {
            if (line.startsWith("Person ID: ")) person.personID = line.substring(11).trim();
            else if (line.startsWith("First Name: ")) person.firstName = line.substring(12).trim();
            else if (line.startsWith("Last Name: ")) person.lastName = line.substring(11).trim();
            else if (line.startsWith("Address: ")) person.address = line.substring(9).trim();
            else if (line.startsWith("Birthdate: ")) person.birthdate = line.substring(11).trim();
        }
        return person;
    }

    private Person getUpdatedDetails(Person original) {
        Scanner sc = new Scanner(System.in);
        Person updated = new Person();
        updated.birthdate = original.birthdate;
    
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date birthDate = sdf.parse(original.birthdate);
            this.age = calculateAge(birthDate);
    
            System.out.println("Do you want to change the birthdate? (yes/no)");
            if (sc.nextLine().equalsIgnoreCase("yes")) {
                updated.inputBirthdate(1);
                if (!original.firstName.equals(updated.firstName)
                    || !original.lastName.equals(updated.lastName)
                    || !original.address.equals(updated.address)
                    || !original.personID.equals(updated.personID)) {
                    return null; // Constraint 2 violated
                }
                updated.firstName = original.firstName;
                updated.lastName = original.lastName;
                updated.address = original.address;
                updated.personID = original.personID;
                return updated;
            }
    
            // ID
            if ((original.personID.charAt(0) - '0') % 2 == 0) {
                updated.personID = original.personID;
                System.out.println("ID cannot be changed (first digit is even).");
            } else {
                updated.personID = idGenerator();
            }
    
            System.out.print("Enter new first name (leave blank to keep current): ");
            String fn = sc.nextLine().trim();
            updated.firstName = fn.isEmpty() ? original.firstName : fn;
    
            System.out.print("Enter new last name (leave blank to keep current): ");
            String ln = sc.nextLine().trim();
            updated.lastName = ln.isEmpty() ? original.lastName : ln;
    
            if (age < 18) {
                updated.address = original.address;
                System.out.println("Address cannot be changed (under 18).");
            } else {
                System.out.println("Updating address:");
                updated.address = address();
            }
    
        } catch (ParseException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    
        return updated;
    }

    private int calculateAge(Date birthDate) {
        Date now = new Date();
        long ageInMillis = now.getTime() - birthDate.getTime();
        return (int)(ageInMillis / (1000L * 60 * 60 * 24 * 365));
    }

    private boolean replacePersonInFile(String targetID, Person updated) {
        File inputFile = new File("person_data.txt");
        File tempFile = new File("person_data_temp.txt");
    
        try (Scanner sc = new Scanner(inputFile);
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
    
            boolean insideBlock = false;
    
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("Person ID: ") && line.contains(targetID)) {
                    insideBlock = true;
    
                    writer.write("Person ID: " + updated.personID); writer.newLine();
                    writer.write("First Name: " + updated.firstName); writer.newLine();
                    writer.write("Last Name: " + updated.lastName); writer.newLine();
                    writer.write("Birthdate: " + updated.birthdate); writer.newLine();
                    writer.write("Address: " + updated.address); writer.newLine();

                    //-----------
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    if (updated.addDemeritPoint != null && !updated.addDemeritPoint.isEmpty()) {
                        writer.write("Demerit Points:"); writer.newLine();
                        for (HashMap.Entry<Date, Integer> entry : updated.addDemeritPoint.entrySet()) {
                            String formattedDate = sdf.format(entry.getKey());
                            writer.write(formattedDate + ": " + entry.getValue());
                            writer.newLine();
                        }
                    }


                    //-----------
                    writer.write("--------------------------------------------------"); writer.newLine();
    
                    // skip lines from old block
                    while (sc.hasNextLine()) {
                        String skip = sc.nextLine();
                        if (skip.contains("--------------------------------------------------")) break;
                    }
    
                    insideBlock = false;
                    continue;
                }
    
                if (!insideBlock) {
                    writer.write(line); writer.newLine();
                }
            }
    
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return false;
        }
    
        return tempFile.renameTo(inputFile);
    }

    public void inputBirthdate(int b) {
        Scanner scanner = new Scanner(System.in);
        if(b == 1){
            System.out.println("Please input your birthdate:");
        }else if(b==2){
            System.out.println("Please input your offense date:");
        }


        System.out.print("DD: ");
        String day = scanner.nextLine().trim();

        System.out.print("MM: ");
        String month = scanner.nextLine().trim();

        System.out.print("YYYY: ");
        String year = scanner.nextLine().trim();

        String input = day + "-" + month + "-" + year;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        formatter.setLenient(false);

        if(b == 1){
            try {
                Date parsedDate = formatter.parse(input);
                this.birthdate = formatter.format(parsedDate);
                System.out.println("Birthdate recorded: " + this.birthdate);
            } catch (ParseException e) {
                System.out.println("Invalid date. Please use a valid day, month, and year.");
            }
        }else if(b==2){
            try {
                Date parsedDate = formatter.parse(input);
                this.offensedate = formatter.format(parsedDate);
                System.out.println("Offense recorded: " + this.offensedate);
            } catch (ParseException e) {
                System.out.println("Invalid date. Please use a valid day, month, and year.");
            }
        }
    }



    public void updateFile(String id, Map<Date, Integer> demeritPoints) {
        try {
            List<String> allLines = Files.readAllLines(Paths.get("person_data.txt"));
            List<String> updatedLines = new ArrayList<>();

            List<String> personBlock = findPersonBlock(id);
            if (personBlock.isEmpty()) {
                System.out.println("Person not found.");
                return;
            }

            List<String> newPersonBlock = new ArrayList<>();
            boolean inserted = false;

            for (String line : personBlock) {
                // Remove existing demerit section if any
                if (line.startsWith("Deremit Points:") || line.matches("\\d{4}: \\d+")) {
                    continue;
                }

                if (!inserted && line.contains("-----")) {
                    newPersonBlock.add("Deremit Points:");
                    for (Map.Entry<Date, Integer> entry : demeritPoints.entrySet()) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(entry.getKey());
                        int year = cal.get(Calendar.YEAR);
                        newPersonBlock.add(year + ": " + entry.getValue());
                    }
                    inserted = true;
                }

                newPersonBlock.add(line);
            }

            boolean inTargetBlock = false;
            for (int i = 0; i < allLines.size(); ) {
                String line = allLines.get(i);

                if (line.startsWith("Person ID: ") && line.contains(id)) {
                    inTargetBlock = true;
                    while (i < allLines.size() && !allLines.get(i).contains("-----")) {
                        i++;
                    }
                    if (i < allLines.size()) i++; // skip separator
                    updatedLines.addAll(newPersonBlock);
                } else {
                    if (!inTargetBlock) {
                        updatedLines.add(line);
                        i++;
                    } else {
                        inTargetBlock = false;
                    }
                }
            }

            Files.write(Paths.get("person_data.txt"), updatedLines, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error updating demerit points: " + e.getMessage());
        }
    }


//endregion

    public boolean addPerson() {

            //TODO: This method adds information about a person to a TXT file.
            //Condition 1: PersonID should be exactly 10 characters long;
            //the first two characters should be numbers between 2 and 9, there should be at least two special characters between characters 3 and 8,
            //and the last two characters should be upper case letters (A - Z). Example: "56s_d%&fAB"|
            //Condition 2: The address of the Person should follow the following format: Street Number|Street|City|State|Country.
            //The State should be only Victoria. Example: 32|Highland Street|Melbourne|Victoria|Australia.
            //Condition 3: The format of the birth date of the person should follow the following format: DD-MM-YYYY. Example: 15-11-1990
            //Instruction: If the Person's information meets the above conditions and any other conditions you may want to consider,
            //the information should be inserted into a TXT file, and the addPerson function should return true.
            //Otherwise, the information should not be inserted into the TXT file, and the addPerson function should return false

        Scanner scnr = new Scanner(System.in);

        try {
            System.out.print("First name: ");
            this.firstName = scnr.nextLine().trim();

            System.out.print("Last name: ");
            this.lastName = scnr.nextLine().trim();

            // Validate name fields
            if (firstName.isBlank() || lastName.isBlank()) {
                System.out.println("First and last name cannot be blank.");
                return false;
            }

            // Input and validate address
            this.address = address();  // may throw exception if invalid

            // Input and validate birthdate
            inputBirthdate(1);
            if (this.birthdate == null || this.birthdate.isBlank()) {
                System.out.println("Invalid birthdate format.");
                return false;
            }

            // Generate and validate ID
            this.personID = idGenerator();

            // Save only if all valid
            return saveToFile();
        } catch (IllegalArgumentException | InputMismatchException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePersonalDetails() {

        //TODO: This method allows updating a given person's ID, firstName, lastName, address and birthday in a TXT file.
        //Changing personal details will not affect their demerit points or the suspension status.
        // All relevant conditions discussed for the addPerson function also need to be considered and checked in the updatPerson function.
        //Condition 1: If a person is under 18, their address cannot be changed.
        //Condition 2: If a person's birthday is going to be changed, then no other personal detail (i.e, person's ID, firstName, lastName,address) can be changed.
        //Condition 3: If the first character/digit of a person's ID is an even number, then their ID cannot be changed.
        //Instruction: If the Person's updated information meets the above conditions and any other conditions you may want to consider,
        //the Person's information should be updated in the TXT file with the updated information, and the updatePersonalDetails function should return true.
        //Otherwise, the Person's updated information should not be updated in the TXT file, and the updatePersonalDetails function should return false

        Scanner scnr = new Scanner(System.in);

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the Person ID of the record to update: ");
            String targetID = scanner.nextLine().trim();
        
            List<String> personBlock = findPersonBlock(targetID);
            if (personBlock == null || personBlock.isEmpty()) {
                System.out.println("Person ID not found.");
                return false;
            }
        
            Person oldPerson = parsePersonDetails(personBlock);
            Person updatedPerson = getUpdatedDetails(oldPerson);
            if (updatedPerson == null) {
                System.out.println("Update canceled due to constraint violation.");
                return false;
            }
        
            boolean success = replacePersonInFile(targetID, updatedPerson);
            if (success) {
                System.out.println("Person details updated successfully.");
                System.out.println("Your new Personal ID: "+personID);
            } else {
                System.out.println("Error updating person details.");
            }
        
            return success;
    }

    public String addDemeritPoints() {

        //TODO: This method adds demerit points for a given person in a TXT file.
        //Condition 1: The format of the date of the offense should follow the following format: DD-MM-YYYY. Example: 15-11-1990
        //Condition 2: The demerit points must be a whole number between 1-6
        //Condition 3: If the person is under 21, the isSuspended variable should be set to true if the total demerit points within two years exceed 6.
        //If the person is over 21, the isSuspended variable should be set to true if the total demerit points within two years exceed 12.
        //Instruction: If the above condiations and any other conditions you may want to consider are met, the demerit points for a person should be inserted into the TXT file,
        //and the addDemeritPoints function should return "Sucess". Otherwise, the addDemeritPoints function should return "Failed"
        Scanner scnr = new Scanner(System.in);
        int demeritpoint;
        int totalDemerit = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    
        // Prompt for person ID
        System.out.print("Enter the Person ID of the record to update: ");
        String targetID = scnr.nextLine().trim();
    
        // Step 1: Find and parse the person block
        List<String> personBlock = findPersonBlock(targetID);
        if (personBlock == null || personBlock.isEmpty()) {
            return "Person ID not found.";
        }
    
        Person person = parsePersonDetails(personBlock);
    
        // Step 2: Get birthdate and calculate age
        Date birthDate;
        try {
            birthDate = sdf.parse(person.birthdate);
        } catch (ParseException e) {
            return "Invalid birthdate format in record.";
        }
        int age = calculateAge(birthDate);  // You already have this method
    
        // Step 3: Input demerit points and offense dates
        for (int i = 0; i < 2; i++) {
            inputBirthdate(2);  // sets this.offensedate
    
            if (this.offensedate == null || this.offensedate.isBlank()) {
                return "Invalid offense date format. Try again.";
            }
    
            Date offenseDateObject;
            try {
                offenseDateObject = sdf.parse(this.offensedate);
            } catch (ParseException e) {
                return "Offense date format error.";
            }
    
            System.out.print("Please enter demerit points (1-6): ");
            try {
                demeritpoint = scnr.nextInt();
            } catch (InputMismatchException e) {
                scnr.next(); // consume invalid input
                return "Invalid input. Must be a number between 1 and 6.";
            }
    
            if (demeritpoint < 1 || demeritpoint > 6) {
                return "Invalid demerit point. Try again.";
            }
    
            addDemeritPoint.put(offenseDateObject, demeritpoint);
            totalDemerit += demeritpoint;
        }

        
    
        // Step 4: Check suspension criteria
        if (age < 21) {
                if (totalDemerit > 6) {
                    isSuspended = true;
                } else {
                    isSuspended = false;
                }
        } else { // age is 21 or older
                if (totalDemerit > 12) {
                    isSuspended = true;
                } else {
                    isSuspended = false;
                }
        }
        
        updateFile(targetID, addDemeritPoint);
        return "Success";
    }


    
}