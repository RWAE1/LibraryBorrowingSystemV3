package LibraryBorrowingSystem.Database;

import LibraryBorrowingSystem.Model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:library.db";
    private static Connection conn;

    // ── Connection ────────────────────────────────────────────────────────────

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DB_URL);
                try (Statement s = conn.createStatement()) {
                    s.execute("PRAGMA journal_mode=WAL");
                    s.execute("PRAGMA foreign_keys=ON");
                }
            }
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }

    public static void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException ignored) {}
    }

    // ── Schema ────────────────────────────────────────────────────────────────

    public static void initSchema() {
        try (Statement s = getConnection().createStatement()) {
            s.execute("""
                    CREATE TABLE IF NOT EXISTS books (
                        book_id   TEXT PRIMARY KEY,
                        title     TEXT NOT NULL,
                        author    TEXT NOT NULL,
                        genre     TEXT NOT NULL,
                        available INTEGER NOT NULL DEFAULT 1
                    )""");
            s.execute("""
                    CREATE TABLE IF NOT EXISTS multimedia (
                        item_id   TEXT PRIMARY KEY,
                        title     TEXT NOT NULL,
                        type      TEXT NOT NULL,
                        duration  TEXT NOT NULL,
                        available INTEGER NOT NULL DEFAULT 1
                    )""");
            s.execute("""
                    CREATE TABLE IF NOT EXISTS members (
                        member_id TEXT PRIMARY KEY,
                        name      TEXT NOT NULL
                    )""");
            s.execute("""
                    CREATE TABLE IF NOT EXISTS member_borrowed_items (
                        member_id TEXT NOT NULL,
                        item_id   TEXT NOT NULL,
                        PRIMARY KEY (member_id, item_id)
                    )""");
            s.execute("""
                    CREATE TABLE IF NOT EXISTS borrow_records (
                        record_id   TEXT PRIMARY KEY,
                        member_id   TEXT NOT NULL,
                        item_id     TEXT NOT NULL,
                        borrow_date TEXT NOT NULL,
                        return_date TEXT NOT NULL DEFAULT '-',
                        returned    INTEGER NOT NULL DEFAULT 0
                    )""");
            s.execute("""
                    CREATE TABLE IF NOT EXISTS reservations (
                        reservation_id   TEXT PRIMARY KEY,
                        member_id        TEXT NOT NULL,
                        item_id          TEXT NOT NULL,
                        reservation_date TEXT NOT NULL,
                        active           INTEGER NOT NULL DEFAULT 1
                    )""");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize schema: " + e.getMessage(), e);
        }
    }

    public static boolean isTableEmpty(String table) {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return !rs.next() || rs.getInt(1) == 0;
        } catch (SQLException e) {
            return true;
        }
    }

    // ── Books ─────────────────────────────────────────────────────────────────

    public static void insertBook(Books book) {
        String sql = "INSERT OR IGNORE INTO books (book_id, title, author, genre, available) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, book.getBookId());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getAuthor());
            ps.setString(4, book.getGenre());
            ps.setInt(5, book.isAvailable() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertBook failed: " + e.getMessage(), e);
        }
    }

    public static void deleteBook(String bookId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "DELETE FROM books WHERE book_id=?")) {
            ps.setString(1, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteBook failed: " + e.getMessage(), e);
        }
    }

    public static void updateBook(Books book) {
        String sql = "UPDATE books SET title=?, author=?, genre=?, available=? WHERE book_id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getGenre());
            ps.setInt(4, book.isAvailable() ? 1 : 0);
            ps.setString(5, book.getBookId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateBook failed: " + e.getMessage(), e);
        }
    }

    public static void updateItemAvailability(String itemId, boolean available) {
        int val = available ? 1 : 0;
        try {
            int rows;
            try (PreparedStatement ps = getConnection().prepareStatement(
                    "UPDATE books SET available=? WHERE book_id=?")) {
                ps.setInt(1, val);
                ps.setString(2, itemId);
                rows = ps.executeUpdate();
            }
            if (rows == 0) {
                try (PreparedStatement ps = getConnection().prepareStatement(
                        "UPDATE multimedia SET available=? WHERE item_id=?")) {
                    ps.setInt(1, val);
                    ps.setString(2, itemId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("updateItemAvailability failed: " + e.getMessage(), e);
        }
    }

    public static Books[] loadBooks() {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM books ORDER BY book_id")) {
            List<Books> list = new ArrayList<>();
            while (rs.next()) {
                Books b = new Books(rs.getString("book_id"), rs.getString("title"),
                        rs.getString("author"), rs.getString("genre"));
                b.setAvailable(rs.getInt("available") == 1);
                list.add(b);
            }
            return list.toArray(new Books[0]);
        } catch (SQLException e) {
            throw new RuntimeException("loadBooks failed: " + e.getMessage(), e);
        }
    }

    // ── Multimedia ────────────────────────────────────────────────────────────

    public static void insertMultimedia(Multimedia item) {
        String sql = "INSERT OR IGNORE INTO multimedia (item_id, title, type, duration, available) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, item.getItemId());
            ps.setString(2, item.getTitle());
            ps.setString(3, item.getType());
            ps.setString(4, item.getDuration());
            ps.setInt(5, item.isAvailable() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertMultimedia failed: " + e.getMessage(), e);
        }
    }

    public static Multimedia[] loadMultimedia() {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM multimedia ORDER BY item_id")) {
            List<Multimedia> list = new ArrayList<>();
            while (rs.next()) {
                Multimedia m = new Multimedia(rs.getString("item_id"), rs.getString("title"),
                        rs.getString("type"), rs.getString("duration"));
                m.setAvailable(rs.getInt("available") == 1);
                list.add(m);
            }
            return list.toArray(new Multimedia[0]);
        } catch (SQLException e) {
            throw new RuntimeException("loadMultimedia failed: " + e.getMessage(), e);
        }
    }

    // ── Members ───────────────────────────────────────────────────────────────

    public static void insertMember(Member member) {
        String sql = "INSERT OR IGNORE INTO members (member_id, name) VALUES (?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, member.getMemberId());
            ps.setString(2, member.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertMember failed: " + e.getMessage(), e);
        }
    }

    public static void deleteMember(String memberId) {
        try (PreparedStatement ps = getConnection().prepareStatement(
                "DELETE FROM members WHERE member_id=?")) {
            ps.setString(1, memberId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteMember failed: " + e.getMessage(), e);
        }
    }

    public static Member[] loadMembers() {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM members ORDER BY member_id")) {
            List<Member> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Member(rs.getString("member_id"), rs.getString("name")));
            }
            return list.toArray(new Member[0]);
        } catch (SQLException e) {
            throw new RuntimeException("loadMembers failed: " + e.getMessage(), e);
        }
    }

    // ── Member Borrowed Items ─────────────────────────────────────────────────

    public static void insertMemberBorrowedItem(String memberId, String itemId) {
        String sql = "INSERT OR IGNORE INTO member_borrowed_items (member_id, item_id) VALUES (?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertMemberBorrowedItem failed: " + e.getMessage(), e);
        }
    }

    public static void deleteMemberBorrowedItem(String memberId, String itemId) {
        String sql = "DELETE FROM member_borrowed_items WHERE member_id=? AND item_id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("deleteMemberBorrowedItem failed: " + e.getMessage(), e);
        }
    }

    public static String[] loadBorrowedItemIds(String memberId) {
        String sql = "SELECT item_id FROM member_borrowed_items WHERE member_id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                List<String> ids = new ArrayList<>();
                while (rs.next()) ids.add(rs.getString("item_id"));
                return ids.toArray(new String[0]);
            }
        } catch (SQLException e) {
            throw new RuntimeException("loadBorrowedItemIds failed: " + e.getMessage(), e);
        }
    }

    // ── Borrow Records ────────────────────────────────────────────────────────

    public static void insertBorrowRecord(BorrowRecord record) {
        String sql = "INSERT OR IGNORE INTO borrow_records " +
                "(record_id, member_id, item_id, borrow_date, return_date, returned) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, record.getRecordId());
            ps.setString(2, record.getMember().getMemberId());
            ps.setString(3, record.getItem().getItemId());
            ps.setString(4, record.getBorrowDate());
            ps.setString(5, record.getReturnDate());
            ps.setInt(6, record.isReturned() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertBorrowRecord failed: " + e.getMessage(), e);
        }
    }

    public static void markBorrowRecordReturned(String memberId, String itemId, String returnDate) {
        String sql = "UPDATE borrow_records SET return_date=?, returned=1 " +
                "WHERE member_id=? AND item_id=? AND returned=0";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, returnDate);
            ps.setString(2, memberId);
            ps.setString(3, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("markBorrowRecordReturned failed: " + e.getMessage(), e);
        }
    }

    public static int getBorrowRecordCount() {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM borrow_records")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public static List<String[]> loadBorrowRecordRows() {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM borrow_records ORDER BY record_id")) {
            List<String[]> rows = new ArrayList<>();
            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("record_id"),
                        rs.getString("member_id"),
                        rs.getString("item_id"),
                        rs.getString("borrow_date"),
                        rs.getString("return_date"),
                        String.valueOf(rs.getInt("returned"))
                });
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("loadBorrowRecordRows failed: " + e.getMessage(), e);
        }
    }

    // ── Reservations ──────────────────────────────────────────────────────────

    public static void insertReservation(String resId, String memberId, String itemId, String resDate) {
        String sql = "INSERT OR IGNORE INTO reservations " +
                "(reservation_id, member_id, item_id, reservation_date, active) VALUES (?,?,?,?,1)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, resId);
            ps.setString(2, memberId);
            ps.setString(3, itemId);
            ps.setString(4, resDate);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("insertReservation failed: " + e.getMessage(), e);
        }
    }

    public static int getReservationCount() {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM reservations")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            return 0;
        }
    }

    public static void updateReservationStatus(String resId, boolean active) {
        String sql = "UPDATE reservations SET active=? WHERE reservation_id=?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setInt(1, active ? 1 : 0);
            ps.setString(2, resId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("updateReservationStatus failed: " + e.getMessage(), e);
        }
    }

    public static List<String[]> loadReservationRows() {
        try (Statement s = getConnection().createStatement();
             ResultSet rs = s.executeQuery("SELECT * FROM reservations ORDER BY reservation_id")) {
            List<String[]> rows = new ArrayList<>();
            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("reservation_id"),
                        rs.getString("member_id"),
                        rs.getString("item_id"),
                        rs.getString("reservation_date"),
                        String.valueOf(rs.getInt("active"))
                });
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("loadReservationRows failed: " + e.getMessage(), e);
        }
    }
}