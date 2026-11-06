package de.qaware.cloudcomputing.parser;

import com.github.amsacode.predict4java.*;
import de.qaware.cloudcomputing.tle.TleMember;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Date;
import java.time.LocalDate;

@ApplicationScoped
public class SatPosCalculator {

    @Inject
    TleParser tleParser;

    @WithSpan
    public SatPassTime getNextPass(TleMember tleRecord) throws SatNotFoundException {
        PassPredictor passPredictor = getPassPredictor(tleRecord);
        return passPredictor.nextSatPass(getNow());
    }

    @WithSpan
    public SatPos getSatPos(TleMember tleRecord) throws SatNotFoundException {
        PassPredictor passPredictor = getPassPredictor(tleRecord);
        return passPredictor.getSatPos(getNow());
    }

    private PassPredictor getPassPredictor(TleMember tleRecord) throws SatNotFoundException {
        TLE tle = tleParser.parseTLE(tleRecord);
        GroundStationPosition groundStationPosition = new GroundStationPosition(47, 12, 400, "Rosenheim");
        return new PassPredictor(tle, groundStationPosition);
    }

    private static Date getNow() {
        return Date.valueOf(LocalDate.now());
    }

}
