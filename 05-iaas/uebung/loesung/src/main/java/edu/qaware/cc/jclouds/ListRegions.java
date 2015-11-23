package edu.qaware.cc.jclouds;

import edu.qaware.cc.jclouds.utils.CloudUtils;
import static edu.qaware.cc.jclouds.utils.CloudUtils.connect;
import java.io.IOException;
import org.jclouds.compute.ComputeService;

/**
 * Gibt die Liste der verfügbaren Regionen aus.
 */
public class ListRegions {
    public static void main(String[] args) throws IOException {        
        ComputeService cs = connect(
                Credentials.USER.get(), Credentials.KEY.get(), Credentials.PROVIDER.get())
                .getComputeService();   
        CloudUtils.printRegions(cs);
        cs.getContext().close();
    }    
}