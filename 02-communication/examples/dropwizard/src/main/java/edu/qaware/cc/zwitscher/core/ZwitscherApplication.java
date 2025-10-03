package edu.qaware.cc.zwitscher.core;

import edu.qaware.cc.zwitscher.api.resources.ZwitscherMessageResource;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class ZwitscherApplication extends Application<ZwitscherConfiguration> {    
    
    public static void main(String[] args) throws Exception {
        new ZwitscherApplication().run(
                "server",
                "./src/main/resources/zwitscher-config.yml"
        ); 
    }

    @Override
    /**
     * Prüft die Konfiguration
     */
    public void initialize(Bootstrap<ZwitscherConfiguration> bootstrap) {
        bootstrap.addBundle(new SwaggerBundle<>() {
            @Override
            protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(ZwitscherConfiguration configuration) {
                return configuration.swaggerBundleConfiguration;
            }
        });
    }

    @Override
    /**
     * Komponenten registrieren und konfigurieren
     * In unserem Fall: REST Ressourcen registrieren
     */
    public void run(ZwitscherConfiguration t, Environment e) {
        e.jersey().register(ZwitscherMessageResource.class);
    }
    
}