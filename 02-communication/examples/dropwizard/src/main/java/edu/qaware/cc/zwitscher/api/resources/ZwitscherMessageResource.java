package edu.qaware.cc.zwitscher.api.resources;

import edu.qaware.cc.zwitscher.api.entities.ZwitscherMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.ws.rs.*;

import java.util.ArrayList;
import java.util.List;

@Path("/messages")
public class ZwitscherMessageResource {
    
    @Path("/random")
    @GET
    @Produces("application/json")
    @Operation(
            summary = "Eine beliebige Nachricht zurückgeben", 
            description = "Diese Methode dient nur zu Demonstrationszwecken. Sie gibt eine beliebige Nachricht zurück."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Es kann keine vernünftige Nachricht generiert werden"),
        @ApiResponse(responseCode = "404", description = "Die generierte Nachricht ist unvernünftig") 
    })    
    public ZwitscherMessage getRandomMessage(){
        return new ZwitscherMessage("YO!");
    }
    
    @GET
    @Produces("application/json")  
    @Operation(summary = "Den aktuellen Nachrichtenstrom zurückgeben")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ZwitscherMessage.class)))),
        @ApiResponse(responseCode = "404", description = "Es können keine Nachrichten geholt werden") 
    })    
    public List<ZwitscherMessage> getMessageStream(
            @DefaultValue("")
            @QueryParam("keyword")
            String keyword
    ){
        List<ZwitscherMessage> messages = new ArrayList<>();
        messages.add(new ZwitscherMessage("yo"));
        return messages;
    }
}