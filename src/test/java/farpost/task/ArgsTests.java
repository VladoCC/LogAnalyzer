package farpost.task;

import farpost.task.exceptions.UnexpectedArgumentException;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class ArgsTests {

    @Test
    void argBoundsTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            float available = random.nextFloat() * 99.9f + 0.1f;
            float time = random.nextFloat() * 9999f + 1f;
            String[] array = {"-u", "" + available, "-t", "" + time};
            Args args = new Args(array);
            assertEquals(available, args.getAvailability(), 0.0001f);
            assertEquals(time, args.getResponseTime());

            available = random.nextFloat() * 99.9f + 0.1f;
            time = random.nextFloat() * -9999f - 1f;
            array = new String[]{"-u", "" + available, "-t", "" + time};
            String[] finalArray2 = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray2));

            available = random.nextFloat() * 100f + 100f;
            time = random.nextFloat() * 9999f + 1f;
            array = new String[]{"-u", "" + available, "-t", "" + time};
            String[] finalArray3 = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray3));

            available = random.nextFloat() * 100f - 100f;
            time = random.nextFloat() * 9999f + 1f;
            array = new String[]{"-u", "" + available, "-t", "" + time};
            String[] finalArray4 = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray4));
        }
    }

    @Test
    void argCountTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            float available = random.nextFloat() * 99.9f + 0.1f;
            float time = random.nextFloat() * 9999f + 1f;
            String[] array = {"-u", "" + available, "-t", "" + time};
            Args args = new Args(array);
            assertEquals(available, args.getAvailability(), 0.0001f);
            assertEquals(time, args.getResponseTime());

            array = new String[]{"-t", "" + time, "-u", "" + available, "error"};
            String[] finalArray1 = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray1));

            array = new String[]{"-t", "" + time, "-u"};
            String[] finalArray = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray));

            array = new String[]{"-t", "" + time, "-u", "" + available, "-u", "" + available};
            String[] finalArray5 = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray5));
        }
    }

    @Test
    void argKeysTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            float available = random.nextFloat() * 99.9f + 0.1f;
            float time = random.nextFloat() * 9999f + 1f;
            String[] array = {"-u", "" + available, "-t", "" + time};
            Args args = new Args(array);
            assertEquals(available, args.getAvailability(), 0.0001f);
            assertEquals(time, args.getResponseTime());

            array = new String[]{"-w", "" + available, "-t", "" + time};
            String[] finalArray1 = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray1));

            array = new String[]{"-u", "" + available, "-w", "" + time};
            String[] finalArray = array;
            assertThrowsExactly(UnexpectedArgumentException.class, () -> new Args(finalArray));
        }
    }

    @Test
    void commutativityTest() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            float available = random.nextFloat() * 99.9f + 0.1f;
            float time = random.nextFloat() * 9999f + 1f;
            String[] array = {"-u", "" + available, "-t", "" + time};
            Args args = new Args(array);
            assertEquals(available, args.getAvailability(), 0.0001f);
            assertEquals(time, args.getResponseTime());

            array = new String[]{"-t", "" + time, "-u", "" + available};
            args = new Args(array);
            assertEquals(available, args.getAvailability(), 0.0001f);
            assertEquals(time, args.getResponseTime());
        }
    }
}
