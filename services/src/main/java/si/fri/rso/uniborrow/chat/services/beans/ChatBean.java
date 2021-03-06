package si.fri.rso.uniborrow.chat.services.beans;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import si.fri.rso.uniborrow.chat.lib.Chat;
import si.fri.rso.uniborrow.chat.models.converters.ChatConverter;
import si.fri.rso.uniborrow.chat.models.entities.ChatEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ChatBean {

    private final Logger log = Logger.getLogger(ChatBean.class.getSimpleName());

    @Inject
    private EntityManager em;

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getAllChatsFallback")
    public List<Chat> getAllChats() {
        TypedQuery<ChatEntity> query = em.createNamedQuery("ChatEntity.getAllChats", ChatEntity.class);
        List<ChatEntity> resultList = query.getResultList();
        return resultList.stream().map(ChatConverter::toDto).collect(Collectors.toList());
    }

    public List<Chat> getAllChatsFallback() {
        return new ArrayList<Chat>();
    }

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getChatsForUsersFallback")
    public List<Chat> getChatsForUsers(Integer userOne, Integer userTwo) {
        TypedQuery<ChatEntity> query = em.createNamedQuery("ChatEntity.getChatOfUsers", ChatEntity.class)
                .setParameter("userOne", userOne)
                .setParameter("userTwo", userTwo);
        List<ChatEntity> resultList = query.getResultList();
        return resultList.stream().map(ChatConverter::toDto).collect(Collectors.toList());
    }

    public List<Chat> getChatsForUsersFallback(Integer userOne, Integer userTwo) {
        return new ArrayList<Chat>();
    }

    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getIdsForUserFallback")
    public List<Integer> getUserIdsForUser(Integer userId) {
        TypedQuery<Integer> queryFrom = em.createNamedQuery("ChatEntity.getUsersFromForUser", Integer.class)
                .setParameter("userId", userId);
        List<Integer> users = queryFrom.getResultList();
        TypedQuery<Integer> queryTo = em.createNamedQuery("ChatEntity.getUsersToForUser", Integer.class)
                .setParameter("userId", userId);
        users.addAll(queryTo.getResultList());
        return users.stream().distinct().collect(Collectors.toList());
    }

    public List<Integer> getIdsForUserFallback(Integer userId) {
        return new ArrayList<Integer>();
    }

    public Chat createChat(Chat chat) {
        chat.setMessage(sanitizeString(chat.getMessage()));

        Date date = new Date();
        chat.setMsgTimestamp(new Timestamp(date.getTime()));

        var chatEntity = ChatConverter.fromDto(chat);
        try {
            beginTransaction();
            em.persist(chatEntity);
            commitTransaction();
        } catch (Exception e) {
            log.warning("Failed to create a chat entry! Error message: " + e.getMessage());
            rollbackTransaction();
        }

        if (chatEntity.getId() == null) {
            log.warning("Failed to create user!");
            return null;
        }

        return ChatConverter.toDto(chatEntity);
    }

    public boolean deleteChat(Integer chatId) {
        ChatEntity chatEntity = em.find(ChatEntity.class, chatId);
        if (chatEntity == null) {
            return false;
        }
        try {
            beginTransaction();
            em.remove(chatEntity);
            commitTransaction();
        } catch (Exception e) {
            rollbackTransaction();
            log.warning("Deleting chat failed! Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void beginTransaction() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTransaction() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }

    private String sanitizeString(String input) {
        return Jsoup.clean(
                StringEscapeUtils.escapeHtml3(StringEscapeUtils.escapeEcmaScript(StringEscapeUtils.escapeJava(input))),
                Safelist.basic());
    }
}
