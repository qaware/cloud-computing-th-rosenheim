package de.qaware.cloudcomputing.tle;

import lombok.Data;

import javax.json.bind.annotation.JsonbProperty;
import java.util.Date;

@Data
public class TleMember {

    @JsonbProperty("@id")
    String id;

    @JsonbProperty("@type")
    String type;

    int satelliteId;

    String name;

    Date date;

    String line1;

    String line2;
}
