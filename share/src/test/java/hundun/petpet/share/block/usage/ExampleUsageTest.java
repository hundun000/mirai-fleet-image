package hundun.petpet.share.block.usage;

import hundun.petpet.share.block.PetpetBlockException;
import org.junit.Test;

import java.io.IOException;

public class ExampleUsageTest {
    static ExampleUsage exampleUsage = new ExampleUsage();

    @Test
    public void test_testcase0() throws PetpetBlockException, IOException {
        exampleUsage.work("./example-data/petpetBlock/testcase0");
    }

    @Test
    public void test_yosuganosora() throws PetpetBlockException, IOException {
        exampleUsage.work("./example-data/petpetBlock/yosuganosora");
    }
}