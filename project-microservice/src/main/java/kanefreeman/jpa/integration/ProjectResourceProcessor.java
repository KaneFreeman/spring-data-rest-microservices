package kanefreeman.jpa.integration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import kanefreeman.jpa.model.Project;
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
public class ProjectResourceProcessor implements ResourceProcessor<Resource<Project>> {

    private final @NonNull
    DiscoveredResource ticketResource;

    /*
     * (non-Javadoc)
     * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
     */
    @Override
    public Resource<Project> process(Resource<Project> resource) {
        Project project = resource.getContent();
        Ticket ticket = project.getTicket();

        // Define ticket link
        Optional<Link> link = Optional.ofNullable(ticketResource.getLink());
        link.ifPresent(it -> {
            if (ticket == null) {
                return;
            }

            Link templatedLink = new Link(new UriTemplate(it.getHref()).with("ticket_id", TemplateVariable.VariableType.SEGMENT), "ticket");

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ticket_id", ticket.getId());

            resource.add(templatedLink.expand(parameters));
        });

        String projectUrl = null;

        try {
            // Get base project url
            URI uri = new URI(resource.getId().getHref());

            projectUrl = uri.getScheme() + "://" + uri.getHost();
            int port = uri.getPort();
            if (port > 0 && port != 80 && port != 443) {
                projectUrl += ":" + port;
            }

            projectUrl += "/projects";
        } catch (URISyntaxException ex) {
            Logger.getLogger(ProjectResourceProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (projectUrl != null) {
            // Define parent link
            Project parent = project.getParent();
            if (parent != null) {
                Link templatedLink = new Link(new UriTemplate(projectUrl).with("parent_id", TemplateVariable.VariableType.SEGMENT), "parent");

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("parent_id", parent.getId());

                resource.add(templatedLink.expand(parameters));
            }

            // Define children link
            List<Project> children = project.getChildren();
            if (children != null && children.size() > 0) {
                Link templatedLink = new Link(new UriTemplate(projectUrl).with("parent", TemplateVariable.VariableType.REQUEST_PARAM), "children");

                Map<String, Object> parameters = new HashMap<>();
                parameters.put("parent", project.getId());

                resource.add(templatedLink.expand(parameters));
            }
        }

        return resource;
    }
}
