package si.fri.rso.uniborrow.chat.models.converters;

import si.fri.rso.uniborrow.chat.lib.Chat;
import si.fri.rso.uniborrow.chat.models.entities.ChatEntity;

public class ChatConverter {

    public static Chat toDto(ChatEntity chatEntity) {
        var chat = new Chat();

        chat.setChatId(chatEntity.getId());
        chat.setUserFromId(chatEntity.getUserFrom());
        chat.setUserToId(chatEntity.getUserTo());
        chat.setMessage(chatEntity.getMessage());
        chat.setMsgTimestamp(chatEntity.getMsgTimestamp());

        return chat;
    }

    public static ChatEntity fromDto(Chat chat) {
        var chatEntity = new ChatEntity();

        chatEntity.setId(chat.getChatId());
        chatEntity.setUserTo(chat.getUserToId());
        chatEntity.setUserFrom(chat.getUserFromId());
        chatEntity.setMessage(chat.getMessage());
        chatEntity.setMsgTimestamp(chat.getMsgTimestamp());

        return chatEntity;
    }
}
