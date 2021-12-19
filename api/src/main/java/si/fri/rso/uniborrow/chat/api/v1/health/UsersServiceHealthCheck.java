package si.fri.rso.uniborrow.chat.api.v1.health;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import java.util.Optional;

@Readiness
@ApplicationScoped
public class UsersServiceHealthCheck implements HealthCheck {

    @Inject
    @DiscoverService(value = "uniborrow-users-service", version = "1.0.0", environment = "dev")
    private Optional<WebTarget> usersService;

    @Override
    public HealthCheckResponse call() {
        return usersService.isPresent()
                ? HealthCheckResponse.up(UsersServiceHealthCheck.class.getSimpleName())
                : HealthCheckResponse.down(UsersServiceHealthCheck.class.getSimpleName());
    }
}
