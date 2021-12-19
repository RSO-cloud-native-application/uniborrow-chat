package si.fri.rso.uniborrow.chat.services.beans;

import si.fri.rso.uniborrow.chat.lib.Chat;
import si.fri.rso.uniborrow.chat.models.converters.ChatConverter;
import si.fri.rso.uniborrow.chat.models.entities.ChatEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ChatBean {

    private final Logger log = Logger.getLogger(ChatBean.class.getSimpleName());

    @Inject
    private EntityManager em;

    public List<Chat> getChatsForUsers(Integer userOne, Integer userTwo) {
        TypedQuery<ChatEntity> query = em.createNamedQuery("ChatEntity.getChatOfUsers", ChatEntity.class)
                .setParameter("userOne", userOne)
                .setParameter("userTwo", userTwo);
        List<ChatEntity> resultList = query.getResultList();
        return resultList.stream().map(ChatConverter::toDto).collect(Collectors.toList());
    }
}
