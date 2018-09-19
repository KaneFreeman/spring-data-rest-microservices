package kanefreeman.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.rest.core.annotation.RestResource;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String extId;

    private Integer source;

    private String projectType;

    private String name;

    private String status;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @RestResource(exported = false)
    private Project parent;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL)
    @RestResource(exported = false)
    private List<Project> children;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Ticket ticket;
}
