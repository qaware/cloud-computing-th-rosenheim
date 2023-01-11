package de.qaware.cloudcomputing.tle;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

@RegisterRestClient(baseUri = "https://tle.ivanstanojevic.me/api")
public interface TleService {

    @GET
    @Path("/tle/")
    TleSearchResult search(@QueryParam("search") String search);

    @GET
    @Path("/tle/{satelliteId}")
    TleMember getRecord(@PathParam("satelliteId") int satelliteId);
}
