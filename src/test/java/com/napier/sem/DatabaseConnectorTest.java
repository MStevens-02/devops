package com.napier.sem;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

// Tests for DatabaseConnector.
// The first group are unit tests (no database needed).
// The last two need the Docker database, so they are tagged "integration".
public class DatabaseConnectorTest {

    // ===== unit tests =====

    // Before we connect there is no connection yet.
    @Test
    void connectionIsNullBeforeConnecting() {
        // step 1: make a connector but do not connect
        DatabaseConnector db = new DatabaseConnector();
        // step 2: check there is no connection
        assertNull(db.getConnection());
    }

    // Disconnecting when we never connected must not crash.
    @Test
    void disconnectWithoutConnectingDoesNotCrash() {
        // step 1: make a connector but do not connect
        DatabaseConnector db = new DatabaseConnector();
        // step 2: disconnect and check nothing was thrown
        assertDoesNotThrow(db::disconnect);
    }

    // Disconnecting twice must not crash either.
    @Test
    void disconnectingTwiceDoesNotCrash() {
        // step 1: make a connector and disconnect two times
        DatabaseConnector db = new DatabaseConnector();
        // step 2: check nothing was thrown
        assertDoesNotThrow(() -> {
            db.disconnect();
            db.disconnect();
        });
    }

    // If the database is not there, every try fails and we end up with no connection (no crash).
    @Test
    void connectingToNowhereLeavesNoConnection() {
        // step 1: hide the "Failed to connect" messages so the test output stays tidy
        PrintStream original = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        DatabaseConnector db = new DatabaseConnector();
        try {
            // step 2: connect to a port where nothing is running (delay 0 so it is quick)
            db.connect("localhost:1", 0);
        } finally {
            // step 3: always put the normal System.out back
            System.setOut(original);
        }
        // step 4: check we still have no connection
        assertNull(db.getConnection());
    }

    // ===== integration tests =====

    // Connecting to the real database gives a working connection.
    @Test
    @Tag("integration")
    void connectingToTheRealDatabaseWorks() throws SQLException {
        // step 1: connect to the Docker database
        DatabaseConnector db = new DatabaseConnector();
        db.connect("localhost:33060", 30000);
        try {
            // step 2: check we got a connection and it is open
            assertNotNull(db.getConnection());
            assertFalse(db.getConnection().isClosed());
            // step 3: check we can really ask the database a question (the world table has 239 countries)
            try (var stmt = db.getConnection().createStatement();
                 var rs = stmt.executeQuery("SELECT COUNT(*) FROM country")) {
                assertTrue(rs.next());
                assertEquals(239, rs.getInt(1));
            }
        } finally {
            db.disconnect();
        }
    }

    // After disconnect() the connection is closed.
    @Test
    @Tag("integration")
    void disconnectClosesTheConnection() throws SQLException {
        // step 1: connect to the Docker database
        DatabaseConnector db = new DatabaseConnector();
        db.connect("localhost:33060", 30000);
        assertNotNull(db.getConnection());
        // step 2: disconnect
        db.disconnect();
        // step 3: check the connection is now closed
        assertTrue(db.getConnection().isClosed());
    }
}
