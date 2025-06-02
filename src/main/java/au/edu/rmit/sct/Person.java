package au.edu.rmit.sct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Person {

    private String personID;
    private String firstName;
    private String lastName;
    private String address;
    private String birthdate;
    private String offensedate;
    private int age;
    private HashMap<Date, Integer> addDemeritPoint = new HashMap<>();
    private boolean isSuspended = false;

    private static final String FILE_NAME = "persons.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public Person(String personID, String firstName, String lastName, String address, String birthdate) {
        this.personID = personID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthdate = birthdate;
        this.age = calculateAge(birthdate);
    }

    public String getPersonID() {
    return personID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthdate() {
        return birthdate;
    }


    private boolean isValidPersonID(String id) {
        if (id.length() != 10) return false;
        try {
            int firstDigit = Integer.parseInt(id.substring(0, 1));
            int secondDigit = Integer.parseInt(id.substring(1, 2));
            if (firstDigit < 2 || firstDigit > 9 || secondDigit < 2 || secondDigit > 9) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        String middle = id.substring(2, 8);
        int specialCharCount = 0;
        for (char c : middle.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) specialCharCount++;
        }
        if (specialCharCount < 2) return false;
        String lastTwo = id.substring(8);
        return lastTwo.chars().allMatch(Character::isUpperCase);
    }

    private boolean isValidAddress(String address) {
        String[] parts = address.split("\\|");
        return parts.length == 5 && parts[3].equalsIgnoreCase("Victoria");
    }

    private boolean isValidDate(String date) {
        try {
            DATE_FORMAT.setLenient(false);
            DATE_FORMAT.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private int calculateAge(String birthdate) {
        try {
            Date birth = DATE_FORMAT.parse(birthdate);
            Calendar dob = Calendar.getInstance();
            dob.setTime(birth);
            Calendar today = Calendar.getInstance();
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) age--;
            return age;
        } catch (ParseException e) {
            return 0;
        }
    }

    public boolean addPerson() {

        //TODO: This method adds information about a person to a TXT file.
        //Condition 1: PersonID should be exactly 10 characters long;
        //the first two characters should be numbers between 2 and 9, there should be at least two special characters between characters 3 and 8,
        //and the last two characters should be upper case letters (A - Z). Example: "56s_d%&fAB"
        //Condition 2: The address of the Person should follow the following format: Street Number|Street|City|State|Country.
        //The State should be only Victoria. Example: 32|Highland Street|Melbourne|Victoria|Australia.
        //Condition 3: The format of the birth date of the person should follow the following format: DD-MM-YYYY. Example: 15-11-1990
        //Instruction: If the Person's information meets the above conditions and any other conditions you may want to consider,
        //the information should be inserted into a TXT file, and the addPerson function should return true.
        //Otherwise, the information should not be inserted into the TXT file, and the addPerson function should return false

        if (!isValidPersonID(personID) || !isValidAddress(address) || !isValidDate(birthdate)) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(String.format("%-12s: %s", "ID", personID));
            writer.newLine();
            writer.write(String.format("%-12s: %s", "FirstName", firstName));
            writer.newLine();
            writer.write(String.format("%-12s: %s", "LastName", lastName));
            writer.newLine();
            writer.write(String.format("%-12s: %s", "Address", address));
            writer.newLine();
            writer.write(String.format("%-12s: %s", "Birthdate", birthdate));
            writer.newLine();
            writer.write("-----------------------------------------------------------------");
            writer.newLine();
            writer.newLine();
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean updatePersonalDetails(String newID, String newFirstName, String newLastName, String newAddress, String newBirthdate) {

        //TODO: This method allows updating a given person's ID, firstName, lastName, address and birthday in a TXT file.
        //Changing personal details will not affect their demerit points or the suspension status.
        //All relevant conditions discussed for the addPerson function also need to be considered and checked in the updatePersonalDetails function.
        //Condition 1: If a person is under 18, their address cannot be changed.
        //Condition 2: If a person's birthday is going to be changed, then no other personal detail (i.e, person's ID, firstName, lastName,address) can be changed.
        //Condition 3: If the first character/digit of a person's ID is an even number, then their ID cannot be changed.
        //Instruction: If the Person's updated information meets the above conditions and any other conditions you may want to consider,
        //the Person's information should be updated in the TXT file with the updated information, and the updatePersonalDetails function should return true.
        //Otherwise, the Person's updated information should not be updated in the TXT file, and the updatePersonalDetails function should return false

        boolean changeBirthday = !birthdate.equals(newBirthdate);
        boolean changeAddress = !address.equals(newAddress);
        boolean changeID = !personID.equals(newID);
        boolean isUnder18 = calculateAge(birthdate) < 18;

        if (changeBirthday && (changeID || changeAddress || !firstName.equals(newFirstName) || !lastName.equals(newLastName))) {
            return false;
        }

        if (isUnder18 && changeAddress) return false;

        if (changeID && (personID.charAt(0) - '0') % 2 == 0) return false;

        if (!isValidPersonID(newID) || !isValidAddress(newAddress) || !isValidDate(newBirthdate)) return false;

        try {
            File inputFile = new File(FILE_NAME);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(personID + ",")) {
                    writer.write(String.join(",", newID, newFirstName, newLastName, newAddress, newBirthdate));
                } else {
                    writer.write(currentLine);
                }
                writer.newLine();
            }

            reader.close();
            writer.close();

            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                return false;
            }

            this.personID = newID;
            this.firstName = newFirstName;
            this.lastName = newLastName;
            this.address = newAddress;
            this.birthdate = newBirthdate;
            this.age = calculateAge(newBirthdate);

            return true;

        } catch (IOException e) {
            return false;
        }
    }

public String addDemeritPoints(String offenseDateStr, int points) {

    // TODO: This method adds demerit points for a given person in a TXT file.
    // Condition 1: The format of the date of the offense should follow the following format: DD-MM-YYYY. Example: 15-11-1990
    // Condition 2: The demerit points must be a whole number between 1-6
    // Condition 3: If the person is under 21, the isSuspended variable should be set to true if the total demerit points within two years exceed 6.
    // If the person is over 21, the isSuspended variable should be set to true if the total demerit points within two years exceed 12.
    // Instruction: If the above conditions and any other conditions you may want to consider are met, the demerit points for a person should be inserted into the TXT file,
    // and the addDemeritPoints function should return "Success". Otherwise, the addDemeritPoints function should return "Failed".

    if (!isValidDate(offenseDateStr) || points < 1 || points > 6) {
        return "Failed";
    }

    try {
        Date offenseDate = DATE_FORMAT.parse(offenseDateStr);
        addDemeritPoint.put(offenseDate, points);

        Date now = new Date();
        // Collect offenses in the last 2 years
        List<Map.Entry<Date, Integer>> recentOffenses = new ArrayList<>();
        int totalPoints = 0;

        for (Map.Entry<Date, Integer> entry : addDemeritPoint.entrySet()) {
            long diff = now.getTime() - entry.getKey().getTime();
            long days = diff / (1000L * 60 * 60 * 24);
            if (days <= 730) {
                recentOffenses.add(entry);
                totalPoints += entry.getValue();
            }
        }

        if ((age < 21 && totalPoints > 6) || (age >= 21 && totalPoints > 12)) {
            isSuspended = true;
        } else {
            isSuspended = false;
        }

        // Write formatted log to a separate file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("demerit_log.txt", true))) {
            writer.write(String.format("%-20s:%s", "ID", personID));
            writer.newLine();

            for (int i = 0; i < recentOffenses.size(); i++) {
                Map.Entry<Date, Integer> entry = recentOffenses.get(i);
                String dateStr = DATE_FORMAT.format(entry.getKey());
                int pts = entry.getValue();

                writer.write(String.format("%d)", i + 1));
                writer.newLine();
                writer.write(String.format("%-20s:%s", "OffenseDate", dateStr));
                writer.newLine();
                writer.write(String.format("%-20s:%d", "Points", pts));
                writer.newLine();
            }

            if (recentOffenses.isEmpty()) {
                writer.write("1) N/A");
                writer.newLine();
            }

            writer.write(String.format("Status:%sSuspended", isSuspended ? "  " : " Not "));
            writer.newLine();
            writer.write("-----------------------------------------------------------------");
            writer.newLine();
        }

        return "Success";

    } catch (ParseException | IOException e) {
        return "Failed";
    }
}

    

    public boolean isSuspended() {
        return isSuspended;
    }
}
