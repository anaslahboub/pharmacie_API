package pharmacie;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")

public class testt {
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String testEndpoint() {
        return "Test successful";

	}
}
