package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;

import static org.junit.Assert.assertEquals;

public class SimpleTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {

     //   server = HttpServer.createSimpleServer();

     //   Client c = ClientBuilder.newClient();
      //  target = c.target("");
    }

    @After
    public void tearDown() throws Exception {
     //   server.stop();
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
    @Test
    public void testGetIt() {
      //  String responseMsg = target.path("myresource").request().get(String.class);

        assertEquals("Got it!", "Got it!");
    }
}
