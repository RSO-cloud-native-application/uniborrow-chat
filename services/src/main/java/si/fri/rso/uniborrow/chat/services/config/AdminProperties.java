package si.fri.rso.uniborrow.chat.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;

import javax.enterprise.context.ApplicationScoped;

@ConfigBundle("admin-properties")
@ApplicationScoped
public class AdminProperties {

    @ConfigValue(watch = true)
    private Boolean disableChat;

    public Boolean getDisableChat() {
        return disableChat;
    }

    public void setDisableChat(Boolean disableChat) {
        this.disableChat = disableChat;
    }
}
