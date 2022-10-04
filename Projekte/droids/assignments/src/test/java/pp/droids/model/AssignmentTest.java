package pp.droids.model;

import org.junit.Test;

public class AssignmentTest extends AssignmentTestBase {
    @Test
    public void checkAssignment1() throws Exception {
        loadMap("/maps/testat1.json"); //NON-NLS
        runDestroyTest();
    }

    @Test
    public void checkAssignment2() throws Exception {
        loadMap("/maps/testat2.json"); //NON-NLS
        runDestroyTest();
    }

    @Test
    public void checkAssignment3() throws Exception {
        loadMap("/maps/testat3.json"); //NON-NLS
        runDestroyTest();
    }

    @Test
    public void checkAssignment4() throws Exception {
        loadMap("/maps/testat4.json"); //NON-NLS
        runCaptureFlagTest();
    }

    @Test
    public void checkAssignment5() throws Exception {
        loadMap("/maps/testat5.json"); //NON-NLS
        runCaptureFlagTest();
    }

    @Test
    public void checkAssignment6() throws Exception {
        loadMap("/maps/testat6.json"); //NON-NLS
        runCaptureFlagTest();
    }

    @Test
    public void checkAssignment7() throws Exception {
        loadMap("/maps/testat7.json"); //NON-NLS
        runCaptureFlagTest();
    }

    @Test
    public void checkAssignment8() throws Exception {
        loadMap("/maps/testat8.json"); //NON-NLS
        runCaptureFlagTest();
    }
}
