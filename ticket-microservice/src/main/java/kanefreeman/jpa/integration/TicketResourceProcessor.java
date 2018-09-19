package kanefreeman.jpa.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import kanefreeman.jpa.model.Ticket;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.hypermedia.DiscoveredResource;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.UriTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TicketResourceProcessor implements ResourceProcessor<Resource<Ticket>> {

    private final @NonNull
    DiscoveredResource projectsResource;

    /*
     * (non-Javadoc)
     * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
     */
    @Override
    public Resource<Ticket> process(Resource<Ticket> resource) {
        Ticket ticket = resource.getContent();

        Optional<Link> link = Optional.ofNullable(projectsResource.getLink());

        link.ifPresent(it -> {
            Link templatedLink = new Link(new UriTemplate(it.getHref()).with("ticket_id", TemplateVariable.VariableType.REQUEST_PARAM), "projects");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ticket_id", ticket.getId());

            resource.add(templatedLink.expand(parameters));
        });

        return resource;
    }
}
