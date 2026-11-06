package de.qaware.cloudcomputing.satellite;

import com.github.amsacode.predict4java.SatPassTime;
import com.github.amsacode.predict4java.SatPos;
import de.qaware.cloudcomputing.parser.SatPosCalculator;
import de.qaware.cloudcomputing.tle.TleClient;
import de.qaware.cloudcomputing.tle.TleMember;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;

@JBossLog
@Consumes(MediaType.APPLICATION_JSON)
@Path("/satellite")
@ApplicationScoped
public class SatelliteResource {

    @Inject
    @RestClient
    TleClient tleClient;

    @Inject
    SatPosCalculator satPosCalculator;

    @GET
    @Path("/{satelliteId}/pass/next")
    @WithSpan
    public RestResponse<SatPassTime> getNextPass(@PathParam("satelliteId") int satelliteId) {
        try {
            TleMember tleRecord = tleClient.getRecord(satelliteId);
            logResponse(tleRecord);
            return RestResponse.ok(satPosCalculator.getNextPass(tleRecord));
        } catch (Exception e) {
            log.error(e);
            return RestResponse.serverError();
        }
    }

    @GET
    @Path("/{satelliteId}/pos")
    @WithSpan
    public RestResponse<SatPos> getSatPosition(@PathParam("satelliteId") int satelliteId) {
        try {
            TleMember tleRecord = tleClient.getRecord(satelliteId);
            logResponse(tleRecord);
            SatPos satPos = satPosCalculator.getSatPos(tleRecord);
            return RestResponse.ok(satPos);
        } catch (Exception e) {
            log.error(e);
            return RestResponse.serverError();
        }
    }

    private void logResponse(TleMember tleMember) {
        log.info(tleMember);

    }
}
