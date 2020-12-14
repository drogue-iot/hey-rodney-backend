package io.drogue.iot.rodney;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.cloudevents.CloudEvent;
import io.drogue.iot.rodney.model.ErrorInformation;

@Path("/")
public class BackendResource {

    @Inject
    Service service;

    @POST
    public Response hey(final CloudEvent event) throws IOException {

        if (event == null || event.getData() == null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(new ErrorInformation("NoData", "No data in cloud event"))
                    .build();
        }

        var data = event.getData();
        service.execute(data.toBytes());

        return Response.accepted().build();
    }
}