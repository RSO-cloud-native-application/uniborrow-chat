package si.fri.rso.uniborrow.chat.api.v1.filters;


import si.fri.rso.uniborrow.chat.services.config.AdminProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
@ApplicationScoped
public class DisableFilter implements ContainerRequestFilter {

    private static final Logger log = Logger.getLogger(DisableFilter.class.getSimpleName());

    @Inject
    private AdminProperties adminProperties;

    @Override
    public void filter(ContainerRequestContext ctx) {
        if (adminProperties.getDisableChat()) {
            ctx.abortWith(Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"Chat service is currently disabled\" }")
                    .build());
        }
    }
}
