package edu.boardgames.collections.explorer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/playinfo")
public class PlayInfoResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
	    var name = "Erwin";
        return "hello " + name;
    }
}