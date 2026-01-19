package bookingengine.domain.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EntityNotFoundException Tests")
class EntityNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        String message = "Chambre non trouvée avec l'id: 42";

        EntityNotFoundException exception = new EntityNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should be throwable")
    void shouldBeThrowable() {
        assertThrows(EntityNotFoundException.class, () -> {
            throw new EntityNotFoundException("Entity not found");
        });
    }

    @Test
    @DisplayName("Should extend RuntimeException")
    void shouldExtendRuntimeException() {
        EntityNotFoundException exception = new EntityNotFoundException("test");

        assertTrue(exception instanceof RuntimeException);
    }
}
