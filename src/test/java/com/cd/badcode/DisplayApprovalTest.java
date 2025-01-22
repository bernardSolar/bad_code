package com.cd.badcode;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DisplayApprovalTest {
    @Test
    public void shouldDisplayPromptsForInput() throws SQLException {
        LoggingDisplay display = new LoggingDisplay();
        OrderProcessor.processNewOrder(display, new FakeInput());
        Approvals.verify(display.displayedMessages);
    }

    private class LoggingDisplay implements Display {
        private final List<String> displayedMessages = new ArrayList<String>();
        @Override
        public void show(String message) {
            displayedMessages.add(message);
        }
    }

    private class FakeInput implements Input {
        private int count = 0;

        @Override
        public String next() {
            return "x" + count++;
        }
    }
}
