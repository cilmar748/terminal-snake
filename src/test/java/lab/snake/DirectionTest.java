package lab.snake;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {
    @Test void fromKeyMapsLetters() {
        assertEquals(Direction.UP,Direction.fromKey('W'));
        assertEquals(Direction.DOWN,Direction.fromKey('S'));
        assertEquals(Direction.LEFT,Direction.fromKey('A'));
        assertEquals(Direction.RIGHT,Direction.fromKey('D'));
        assertEquals(Direction.UP,Direction.fromKey('w'));
    }
    @Test void fromKeyRejectsUnknown() {
        assertThrows(IllegalArgumentException.class,()->Direction.fromKey('X'));
        assertThrows(IllegalArgumentException.class,()->Direction.fromKey('1'));
    }
    @Test void oppositesAreMutual() {
        assertTrue(Direction.UP.opposite(Direction.DOWN));
        assertTrue(Direction.LEFT.opposite(Direction.RIGHT));
        assertFalse(Direction.UP.opposite(Direction.LEFT));
        assertFalse(Direction.RIGHT.opposite(Direction.DOWN));
    }
}