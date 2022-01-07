package si.fri.rso.uniborrow.chat.api.v1.resources;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import si.fri.rso.uniborrow.chat.lib.Chat;
import si.fri.rso.uniborrow.chat.services.beans.ChatBean;
import si.fri.rso.uniborrow.chat.services.clients.UniborrowUserApi;
import si.fri.rso.uniborrow.chat.services.dtos.UniborrowUserRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Log
@ApplicationScoped
@Path("/chat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ChatResource {

    private final Logger log = Logger.getLogger(ChatResource.class.getSimpleName());

    @Inject
    ChatBean chatBean;

    private UniborrowUserApi uniborrowUserApi;

    @Inject
    @DiscoverService(value = "uniborrow-users-service", version = "1.0.0", environment = "dev")
    private Optional<URL> usersServiceUrl;

    @PostConstruct
    private void init() {
        if (usersServiceUrl != null && usersServiceUrl.isPresent()) {
            uniborrowUserApi = RestClientBuilder
                    .newBuilder()
                    .baseUrl(usersServiceUrl.get())
                    .build(UniborrowUserApi.class);
        }
    }

    @GET
    @Path("/private")
    @Operation(description = "Get chats between users.", summary = "Get chats between users.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Messages for chat between two users.",
                    content = @Content(schema = @Schema(implementation = Chat.class, type = SchemaType.ARRAY))
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "No messages found."
            )
    })
    public Response getChatForUsers(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "User ID",
                    required = true
            ) @QueryParam("userOne") Integer userOne,
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "User ID",
                    required = true
            ) @QueryParam("userTwo") Integer userTwo) {
        if (userOne == null || userTwo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        List<Chat> chats = chatBean.getChatsForUsers(userOne, userTwo);

        return Response.status(Response.Status.OK).entity(chats).build();
    }

    @GET
    @Operation(description = "Get all chats or user IDs associated with user.", summary = "Get chats or user IDs for user")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "All chat messages",
                    content = @Content(schema = @Schema(implementation = Chat.class, type = SchemaType.ARRAY))
            ),
            @APIResponse(
                    responseCode = "200",
                    description = "All User IDs that chat with a specific user.",
                    content = @Content(schema = @Schema(implementation = Integer.class, type = SchemaType.ARRAY))
            )
    })
    public Response getUserChats(
            @Parameter(
                    in = ParameterIn.QUERY,
                    description = "User ID"
            ) @QueryParam("userId") Integer userId) {
        if (userId == null) {
            List<Chat> chats = chatBean.getAllChats();
            return Response.status(Response.Status.OK).entity(chats).build();
        } else {
            List<Integer> userIds = chatBean.getUserIdsForUser(userId);
            return Response.status(Response.Status.OK).entity(userIds).build();
        }
    }

    @POST
    @Counted(name = "num_created_messages")
    @Operation(description = "Create new chat message.", summary = "Create new chat message")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Chat message successfully created.",
                    content = @Content(schema = @Schema(implementation = Chat.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Bad request"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Wrong user ID."
            )
    })
    public Response createChat(
            @RequestBody(
                    description = "DTO for chat message.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Chat.class))
            ) Chat chat) {
        if (chat.getMessage() == null || chat.getUserFromId() == null || chat.getUserToId() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (uniborrowUserApi == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }

        UniborrowUserRequest fromUser = uniborrowUserApi.getById(chat.getUserFromId());
        UniborrowUserRequest toUser = uniborrowUserApi.getById(chat.getUserToId());
        if (fromUser != null && toUser != null) {
            Chat createdChat = chatBean.createChat(chat);
            if (createdChat == null) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
            return Response.status(Response.Status.CREATED).entity(createdChat).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{chatId}")
    @Counted(name = "num_deleted_messages")
    @Operation(description = "Delete a chat message.", summary = "Delete chat message")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Successfully delete a chat message"
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Chat message not found."
            )
    })
    public Response deleteChat(
            @Parameter(
                    description = "Chat message ID.",
                    required = true
            ) @PathParam("chatId") Integer chatId) {
        boolean isSuccessful = chatBean.deleteChat(chatId);
        return isSuccessful
                ? Response.status(Response.Status.NO_CONTENT).build()
                : Response.status(Response.Status.NOT_FOUND).build();
    }
}
