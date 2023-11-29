package de.qaware.cloudcomputing.parser;

import com.github.amsacode.predict4java.TLE;
import de.qaware.cloudcomputing.tle.TleMember;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TleParser {

    public TLE parseTLE(TleMember tleMember) {
        if (tleMember == null) {
            throw new IllegalArgumentException("tleMember");
        }

        return new TLE(new String[]{tleMember.getName(), tleMember.getLine1(), tleMember.getLine2()});
    }

}
