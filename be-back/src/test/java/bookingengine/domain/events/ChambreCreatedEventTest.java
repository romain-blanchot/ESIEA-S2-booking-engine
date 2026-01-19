package bookingengine.domain.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ChambreCreatedEvent Tests")
class ChambreCreatedEventTest {

    @Test
    @DisplayName("Should create event using factory method")
    void shouldCreateEventUsingFactoryMethod() {
        Instant before = Instant.now();

        ChambreCreatedEvent event = ChambreCreatedEvent.of(1L, "101", "Double", 89.99);

        Instant after = Instant.now();

        assertEquals(1L, event.chambreId());
        assertEquals("101", event.numero());
        assertEquals("Double", event.type());
        assertEquals(89.99, event.prixBase());
        assertNotNull(event.timestamp());
        assertTrue(event.timestamp().isAfter(before) || event.timestamp().equals(before));
        assertTrue(event.timestamp().isBefore(after) || event.timestamp().equals(after));
    }

    @Test
    @DisplayName("Should create event with record constructor")
    void shouldCreateEventWithRecordConstructor() {
        Instant timestamp = Instant.parse("2024-07-15T10:30:00Z");

        ChambreCreatedEvent event = new ChambreCreatedEvent(2L, "202", "Suite", 150.00, timestamp);

        assertEquals(2L, event.chambreId());
        assertEquals("202", event.numero());
        assertEquals("Suite", event.type());
        assertEquals(150.00, event.prixBase());
        assertEquals(timestamp, event.timestamp());
    }

    @Test
    @DisplayName("Should be equal for same values")
    void shouldBeEqualForSameValues() {
        Instant timestamp = Instant.parse("2024-07-15T10:30:00Z");

        ChambreCreatedEvent event1 = new ChambreCreatedEvent(1L, "101", "Double", 89.99, timestamp);
        ChambreCreatedEvent event2 = new ChambreCreatedEvent(1L, "101", "Double", 89.99, timestamp);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    @DisplayName("Should generate valid toString")
    void shouldGenerateValidToString() {
        ChambreCreatedEvent event = ChambreCreatedEvent.of(1L, "101", "Double", 89.99);

        String str = event.toString();
        assertTrue(str.contains("chambreId=1"));
        assertTrue(str.contains("numero=101"));
        assertTrue(str.contains("type=Double"));
    }
}
