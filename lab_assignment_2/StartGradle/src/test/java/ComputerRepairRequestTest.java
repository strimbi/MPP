import model.ComputerRepairRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComputerRepairRequestTest {
    @Test
    @DisplayName("Test 1")
    public void testExample1() {
        ComputerRepairRequest request1 = new ComputerRepairRequest();
        ComputerRepairRequest request = new ComputerRepairRequest(41, "Daria",
                "Str. Eminescu 1", "078672912",
                "Asus", "10.04.2021", "Displayul nu functioneaza");
        assertEquals(41, request.getID());
        assertEquals("Daria", request.getOwnerName());
        assertEquals("Str. Eminescu 1", request.getOwnerAddress());
        assertEquals("078672912", request.getPhoneNumber());
        assertEquals("Asus", request.getModel());
        assertEquals("10.04.2021", request.getDate());
        assertEquals("Displayul nu functioneaza", request.getProblemDescription());

        assertEquals("", request1.getOwnerName());
        assertEquals("", request1.getOwnerAddress());
    }

    @Test
    @DisplayName("Test 2")
    public void testExample2() {
        assertEquals(2,2,"Numele ar trebui sa fie egale");
    }


}