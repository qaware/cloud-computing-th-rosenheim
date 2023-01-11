package de.qaware.cloudcomputing;

import de.qaware.cloudcomputing.tle.TleMember;
import de.qaware.cloudcomputing.tle.TleSearchResult;
import de.qaware.cloudcomputing.tle.TleService;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@JBossLog
@Path("/tle")
public class TleResource {

    @Inject
    @RestClient
    TleService tleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TleSearchResult search(@QueryParam("searchString") String searchString) {
        log.tracev("Processing request GET /tle?searchString with parameter {0}", searchString);

        TleSearchResult searchResult = tleService.search(searchString);

        log.debugv("Retrieved search result {0} with {1} items", searchResult.getId(), searchResult.getTotalItems());

        return searchResult;
    }

    @GET
    @Path("/{satelliteId}")
    public TleMember getRecord(@PathParam("satelliteId") int satelliteId) {
        log.tracev("Processing request GET /tle/{satelliteId} with parameter {0}", satelliteId);

        TleMember record = tleService.getRecord(satelliteId);

        log.debugv("Retrieved TLE record for satellite {0} (ID {1})", record.getName(), record.getSatelliteId());

        return record;
    }
}
