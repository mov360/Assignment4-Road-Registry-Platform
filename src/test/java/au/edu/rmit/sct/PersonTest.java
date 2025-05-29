package au.edu.rmit.sct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PersonTest {

    private final String filePath = "persons.txt";
    private Person validPerson;

    @BeforeAll
    public void cleanBeforeAll() throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
    }

    @BeforeEach
    public void setUp() {
        validPerson = new Person("56s_d%&fAB", "Alice", "Smith",
                "12|Main Road|Melbourne|Victoria|Australia", "01-01-1990");
    }

    // @AfterEach
    // public void cleanUp() throws IOException {
    //     Files.deleteIfExists(Paths.get(filePath));
    // }

    // --------------------- TESTING addPerson() ---------------------

    @Test
    public void testAddPerson_Valid() {
        assertTrue(validPerson.addPerson());
    }

    @Test
    public void testAddPerson_InvalidIDLength() {
        Person p = new Person("56s_d%fA", "Bob", "Jones",
                "32|High Street|Melbourne|Victoria|Australia", "15-08-1992");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidState() {
        Person p = new Person("56!@gh^%IJ", "Cathy", "Brown",
                "20|George St|Sydney|NSW|Australia", "10-10-1991");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_InvalidBirthDateFormat() {
        Person p = new Person("67*&$gf^KL", "David", "White",
                "25|Main St|Melbourne|Victoria|Australia", "1990-11-15");
        assertFalse(p.addPerson());
    }

    @Test
    public void testAddPerson_NotEnoughSpecialChars() {
        Person p = new Person("56abcdfgAB", "Eve", "Green",
                "1|Short Rd|Melbourne|Victoria|Australia", "12-05-1985");
        assertFalse(p.addPerson());
    }

    // --------------------- TESTING updatePersonalDetails() ---------------------

    @Test
    public void testUpdateDetails_ValidNameChange() {
        assertTrue(validPerson.addPerson());
        assertTrue(validPerson.updatePersonalDetails("56s_d%&fAB", "Anna", "Lee",
                validPerson.getAddress(), validPerson.getBirthdate()));
    }

    @Test
    public void testUpdateDetails_AddressChangeUnder18() {
        Person p = new Person("57&^_!gHJK", "Tom", "Young",
                "1|Hill St|Melbourne|Victoria|Australia", "01-01-2010");
        assertTrue(p.addPerson());
        assertFalse(p.updatePersonalDetails("57&^_!gHJK", "Tom", "Young",
                "99|Fake St|Melbourne|Victoria|Australia", "01-01-2010"));
    }

    @Test
    public void testUpdateDetails_MultipleFieldsWithBirthdayChange() {
        assertTrue(validPerson.addPerson());
        assertFalse(validPerson.updatePersonalDetails("56s_d%&fAB", "Alex", "King",
                "12|New Rd|Melbourne|Victoria|Australia", "02-02-1991"));
    }

    @Test
    public void testUpdateDetails_IDChangeEvenFirstDigit() {
        Person p = new Person("24s_d%&fAB", "Zara", "Jones",
                "11|Sunset Blvd|Melbourne|Victoria|Australia", "05-06-1988");
        assertTrue(p.addPerson());
        assertFalse(p.updatePersonalDetails("74s_d%&fAB", "Zara", "Jones",
                p.getAddress(), p.getBirthdate()));
    }

    @Test
    public void testUpdateDetails_OnlyBirthdayChange() {
        assertTrue(validPerson.addPerson());
        assertTrue(validPerson.updatePersonalDetails(validPerson.getPersonID(),
                validPerson.getFirstName(), validPerson.getLastName(), validPerson.getAddress(),
                "10-10-1995"));
    }

    // --------------------- TESTING addDemeritPoints() ---------------------

    @Test
    public void testAddDemeritPoints_ValidMinor() {
        Person minor = new Person("78&^_abCDZ", "Liam", "Gray",
                "14|Tree Ln|Melbourne|Victoria|Australia", "01-01-2007");
        assertTrue(minor.addPerson());
        String result = minor.addDemeritPoints("15-05-2024", 3);
        assertEquals("Success", result);
    }

    @Test
    public void testAddDemeritPoints_InvalidDateFormat() {
        assertTrue(validPerson.addPerson());
        assertEquals("Failed", validPerson.addDemeritPoints("2024-05-15", 4));
    }

    @Test
    public void testAddDemeritPoints_InvalidPointValue() {
        assertTrue(validPerson.addPerson());
        assertEquals("Failed", validPerson.addDemeritPoints("15-05-2024", 10));
    }

    @Test
    public void testAddDemeritPoints_SuspensionUnder21() {
        Person teen = new Person("89!@abGHYZ", "Jake", "Hill",
                "18|Park Rd|Melbourne|Victoria|Australia", "01-01-2008");
        assertTrue(teen.addPerson());
        assertEquals("Success", teen.addDemeritPoints("01-01-2024", 4));
        assertEquals("Success", teen.addDemeritPoints("01-06-2024", 3));
        assertTrue(teen.isSuspended());
    }

    @Test
    public void testAddDemeritPoints_SuspensionOver21() {
        Person adult = new Person("88%$abXZDF", "John", "Stone",
                "20|Main Rd|Melbourne|Victoria|Australia", "01-01-1990");
        assertTrue(adult.addPerson());
        assertEquals("Success", adult.addDemeritPoints("01-01-2024", 6));
        assertEquals("Success", adult.addDemeritPoints("01-06-2024", 6));
        assertEquals("Success", adult.addDemeritPoints("01-07-2024", 1));
        assertTrue(adult.isSuspended());
    }
}
