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
 * Represents a multimedia item in the library collection.
 * Inherits from LibraryItem — gains itemId, title, and available.
 *
 * A Multimedia item is any non-book borrowable item such as
 * a DVD, CD, or Audiobook. It extends LibraryItem with two
 * additional attributes: type and duration.
 *
 * Inheritance:
 *   Multimedia extends LibraryItem
 *   - Inherits : itemId, title, available (and their getters/setters)
 *   - Adds     : type, duration
 *   - Overrides: getInfo(), toString()
 *
 * Attributes:
 *   - type     : the format of the item (e.g. "DVD", "CD", "Audiobook")
 *   - duration : the running time or length (e.g. "148 min", "15 hrs")
 */
public class Multimedia extends LibraryItem {

    // type stores what kind of multimedia this is, e.g. "DVD", "CD", "Audiobook"
    private String type;

    // duration stores the running time of the item, e.g. "148 min", "Unknown"
    private String duration;

    /**
     * Creates a new Multimedia item with all details provided.
     * Calls the LibraryItem constructor (super) to set itemId, title, available.
     *
     * @param itemId   unique identifier for the item (e.g. "MM001")
     * @param title    title of the multimedia item
     * @param type     format type (e.g. "DVD", "CD", "Audiobook")
     * @param duration running time or length of the item
     */
    public Multimedia(String itemId, String title, String type, String duration) {
        // Call the parent LibraryItem constructor to set itemId, title, and available
        super(itemId, title);

        // Store the format type specific to this multimedia item
        this.type = type;

        // Store the duration specific to this multimedia item
        this.duration = duration;
    }

    /**
     * Returns the pre-defined initial multimedia data for the library.
     * Keeping this data here ensures the Multimedia class owns its own defaults.
     *
     * @return array of Multimedia objects pre-filled with default items
     */
    public static Multimedia[] getInitialMultimedia() {
        // Create an array that can hold 4 Multimedia objects
        Multimedia[] initial = new Multimedia[4];

        // Fill each slot with a pre-defined multimedia item
        initial[0] = new Multimedia("MM001", "Inception",          "DVD",       "148 min");
        initial[1] = new Multimedia("MM002", "Dark Side of Moon",  "CD",        "43 min");
        initial[2] = new Multimedia("MM003", "Sapiens Audiobook",  "Audiobook", "15 hrs");
        initial[3] = new Multimedia("MM004", "Avatar Aang: The Last Airbender",  "DVD", "98 min");

        // Return the completed array
        return initial;
    }

    /**
     * Returns a detailed description of this multimedia item.
     * OVERRIDES the base getInfo() in LibraryItem with multimedia-specific details.
     * The parent version only shows itemId, title, and status.
     * This version also adds type and duration.
     *
     * @return a formatted string with full multimedia details
     */
    @Override
    public String getInfo() {
        // Determine the availability status using if-else
        String status;
        if (available == true) {
            status = "Available";
        } else {
            status = "Borrowed";
        }

        // Build a string that includes the multimedia-specific fields
        // available, itemId, title are accessed directly because they are protected in LibraryItem
        return "[" + itemId + "] \"" + title + "\""
                + " | Type: " + type
                + " | Duration: " + duration
                + " (" + status + ")";
    }

    /**
     * Returns a readable summary of this multimedia item.
     * Calls the overridden getInfo() method from this class.
     */
    public String toString() {
        // Delegates to getInfo() — which is the overridden version in Multimedia
        return getInfo();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** Returns the format type of this multimedia item (e.g. "DVD", "CD"). */
    public String getType() { return type; }

    /** Returns the duration or running time of this multimedia item. */
    public String getDuration() { return duration; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** Updates the format type of this multimedia item. */
    public void setType(String type) { this.type = type; }

    /** Updates the duration of this multimedia item. */
    public void setDuration(String duration) { this.duration = duration; }
}
