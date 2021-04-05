import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Lesson6Test {
    private Lesson6 lesson6;

    @BeforeEach
    public void init() {
        lesson6 = new Lesson6();
    }

    @Test
    void getSubArrayWithNumber() {
        Assertions.assertArrayEquals(new int[]{1, 2, 15, 6, 2}, lesson6.getSubArray(new int[]{25, 16, 8, 4, 1, 2, 15, 6, 2}));
        Assertions.assertArrayEquals(new int[]{2, 5, 6}, lesson6.getSubArray(new int[]{6, 12, 4, 8, 9, 4, 2, 5, 6}));
        Assertions.assertArrayEquals(new int[]{}, lesson6.getSubArray(new int[]{2, 5, 12, 4}));
        Assertions.assertArrayEquals(new int[]{}, lesson6.getSubArray(new int[]{4}));
    }

    @Test
    void getSubArrayWithoutNumber() {
        Assertions.assertThrows(RuntimeException.class, () -> lesson6.getSubArray(new int[]{1, 2, 5, 7}));
        Assertions.assertThrows(RuntimeException.class, () -> lesson6.getSubArray(new int[]{}));
        Assertions.assertThrows(RuntimeException.class, () -> lesson6.getSubArray(new int[]{2, 6, 7, 12, 14, 41}));
    }

    @Test
    void checkArrayTrue() {
        Assertions.assertTrue(lesson6.checkArray(new int[]{0, 4, 6, 4, 1}));
        Assertions.assertTrue(lesson6.checkArray(new int[]{0, -1, -4, 1, 5, 7, 4}));
        Assertions.assertTrue(lesson6.checkArray(new int[]{1, 4}));
    }

    @Test
    void checkArrayFalse() {
        Assertions.assertFalse(lesson6.checkArray(new int[]{-1, -4}));
        Assertions.assertFalse(lesson6.checkArray(new int[]{-1, -4, 1, 5, 5}));
        Assertions.assertFalse(lesson6.checkArray(new int[]{4, -1, 0, 11}));
        Assertions.assertFalse(lesson6.checkArray(new int[]{0, 1, 2, 3, -4, 5, 6}));
        Assertions.assertFalse(lesson6.checkArray(new int[]{}));
    }
}