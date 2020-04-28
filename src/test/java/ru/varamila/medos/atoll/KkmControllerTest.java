package ru.varamila.medos.atoll;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import ru.varamila.medos.atoll.entity.Kkm;
import test.SimpleTest;

import static org.junit.jupiter.api.Assertions.*;

class KkmControllerTest extends SimpleTest {

    @Test
    void test1() {
        KkmController controller = new KkmController();
        assertEquals( "Hello, test!",controller.test());
    }

    @Test
    void makeResponse() {
        KkmController controller = new KkmController();
        Kkm kkm = new Kkm();
        kkm.setFunction("not exist");
        Gson g = new GsonBuilder().create();
        String res = controller.makeResponse(g.toJson(kkm));
        assertEquals("I don't know wheat to do :-(", res);

    }
}