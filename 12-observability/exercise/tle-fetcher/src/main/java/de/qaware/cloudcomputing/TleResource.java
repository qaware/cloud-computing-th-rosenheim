package de.qaware.cloudcomputing;

import de.qaware.cloudcomputing.tle.TleMember;
import de.qaware.cloudcomputing.tle.TleSearchResult;
import de.qaware.cloudcomputing.tle.TleService;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@JBossLog
@Path("/tle")
public class TleResource {

    @Inject
    @RestClient
    TleService tleService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @WithSpan
    public TleSearchResult search(@QueryParam("searchString") String searchString) {
        log.tracev("Processing request GET /tle?searchString with parameter {0}", searchString);

        TleSearchResult searchResult = tleService.search(searchString);

        log.debugv("Retrieved search result {0} with {1} items", searchResult.getId(), searchResult.getTotalItems());

        return searchResult;
    }

    @GET
    @Path("/{satelliteId}")
    @WithSpan
    public TleMember getRecord(@PathParam("satelliteId") int satelliteId) {
        log.tracev("Processing request GET /tle/{satelliteId} with parameter {0}", satelliteId);

        TleMember record = tleService.getRecord(satelliteId);

        log.debugv("Retrieved TLE record for satellite {0} (ID {1})", record.getName(), record.getSatelliteId());

        return record;
    }
}
