package edu.qaware.cc.zwitscher;

import edu.qaware.cc.zwitscher.core.ZwitscherApplication;
import edu.qaware.cc.zwitscher.core.ZwitscherConfiguration;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(DropwizardExtensionsSupport.class)
public class TestApi {
    
    public static final DropwizardAppExtension<ZwitscherConfiguration> app = new DropwizardAppExtension<>(ZwitscherApplication.class, "./src/main/resources/zwitscher-config.yml");
    
    @Test
    public void testRandomMessage(){
        get("http://localhost:2890/messages/random")
                .then().body("message", equalTo("YO!"));
    }
    
}
