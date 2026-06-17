/**
 * @author      masjohncook X RWAE 1
 * @version     0.0.2
 * @copyright   (C) Copyright 2026
 * @license     None
 * @maintainer  masjohncook
 * @email       mas.john.cook@gmail.com
 * @status      None
 */
package LibraryBorrowingSystem.Model;

/**
 * Base class representing a person in the library system.
 *
 * Both Member and Librarian inherit from this class.
 * It holds the two attributes that every person in the system shares:
 * a unique ID and a name. It also provides a base getInfo() method
 * that subclasses override with their own specific details.
 *
 * Attributes:
 *   - id   : unique identifier for the person
 *   - name : full name of the person
 */
public class Person {

    // id is the unique code for this person (e.g. "M001" for a member, "L001" for a librarian)
    // protected means this class AND its subclasses (Member, Librarian) can access it directly
    protected String id;

    // name stores the full name of the person
    // protected so subclasses can use it directly without calling a getter
    protected String name;

    /**
     * Creates a new Person with the given ID and name.
     *
     * @param id   unique identifier for the person
     * @param name full name of the person
     */
    public Person(String id, String name) {
        // Store the unique ID for this person
        this.id = id;

        // Store the full name of this person
        this.name = name;
    }

    /**
     * Returns a basic description of this person.
     * This is the BASE version — subclasses Member and Librarian
     * will override this method with their own detailed version.
     *
     * @return a formatted string with the person's ID and name
     */
    public String getInfo() {
        // Base implementation — just shows ID and name
        return "Person[" + id + "] " + name;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** Returns the unique ID of this person. */
    public String getId() { return id; }

    /** Returns the full name of this person. */
    public String getName() { return name; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** Updates the ID of this person. */
    public void setId(String id) { this.id = id; }

    /** Updates the name of this person. */
    public void setName(String name) { this.name = name; }

    /**
     * Returns a readable summary of this person.
     * Calls getInfo() so subclasses that override getInfo()
     * will automatically get the correct output here too.
     */
    public String toString() {
        return getInfo();
    }
}
