package si.fri.rso.uniborrow.chat.api.v1;


import com.kumuluz.ee.discovery.annotations.RegisterService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@RegisterService(value = "uniborrow-chat-service", environment = "dev", version = "1.0.0")
@ApplicationPath("/v1")
public class ChatApplication extends Application {
}