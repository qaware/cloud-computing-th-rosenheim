package de.qaware.cloudcomputing.tle;

import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/tle")
@RegisterRestClient(configKey="tle-fetcher")
public interface TleClient {

    @GET
    @Path("/{satelliteId}")
    @WithSpan
    TleMember getRecord(@PathParam("satelliteId") @SpanAttribute int satelliteId);
}
