package si.fri.rso.uniborrow.chat.api.v1.resources;

import si.fri.rso.uniborrow.chat.lib.Chat;
import si.fri.rso.uniborrow.chat.services.beans.ChatBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {

    private final Logger log = Logger.getLogger(ChatResource.class.getSimpleName());

    @Inject
    ChatBean chatBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getChatForUsers(
            @QueryParam("userOne") Integer userOne,
            @QueryParam("userTwo") Integer userTwo) {
        if (userOne == null || userTwo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Chat> chats = chatBean.getChatsForUsers(userOne, userTwo);

        return Response.status(Response.Status.OK).entity(chats).build();
    }
}
