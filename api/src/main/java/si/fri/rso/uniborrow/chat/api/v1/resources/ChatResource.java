package si.fri.rso.uniborrow.chat.api.v1.resources;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.metrics.annotation.Counted;
import si.fri.rso.uniborrow.chat.lib.Chat;
import si.fri.rso.uniborrow.chat.services.beans.ChatBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
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

    @Inject
    @DiscoverService(value = "uniborrow-users-service", version = "1.0.0", environment = "dev")
    private Optional<WebTarget> usersService;


    @GET
    @Path("/private")
    public Response getChatForUsers(
            @QueryParam("userOne") Integer userOne,
            @QueryParam("userTwo") Integer userTwo) {
        if (userOne == null || userTwo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Chat> chats = chatBean.getChatsForUsers(userOne, userTwo);

        return Response.status(Response.Status.OK).entity(chats).build();
    }

    @GET
    public Response getUserChats(
            @QueryParam("userId") Integer userId) {
        if (userId == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<Integer> userIds = chatBean.getUserIdsForUser(userId);

        return Response.status(Response.Status.OK).entity(userIds).build();
    }

    @POST
    @Counted(name = "num_created_messages")
    public Response createChat(Chat chat) {
        if (chat.getMessage() == null || chat.getUserFromId() == null || chat.getUserToId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (usersService.isPresent()) {
            WebTarget userFromService = usersService.get().path("/v1/users/" + chat.getUserFromId());
            WebTarget userToService = usersService.get().path("/v1/users/" + chat.getUserToId());

            Response responseFrom;
            Response responseTo;
            try {
                responseFrom = userFromService.request().get();
                responseTo = userToService.request().get();
            } catch (Exception e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }

            if (responseFrom.getStatus() != Response.Status.OK.getStatusCode() ||
                    responseTo.getStatus() != Response.Status.OK.getStatusCode()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Chat createdChat = chatBean.createChat(chat);
            if (createdChat == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            return Response.status(Response.Status.CREATED).entity(createdChat).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/{chatId}")
    @Counted(name = "num_deleted_messages")
    public Response deleteChat(@PathParam("chatId") Integer chatId) {
        boolean isSuccessful = chatBean.deleteChat(chatId);
        return isSuccessful
                ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
