package sec.project.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
public class Post extends AbstractPersistable<Long> {
    
    @Id
    private Long id;
    @ManyToOne
    private Account owner;
    private String message;    

    public Post() {
        super();
    }

    public Post(Account owner, String message) {
        this();
        this.owner = owner;
        this.message = message;
    }

    public Account getOwner() {
        return owner;
    }

    public String getMessage() {
        return message;
    }

    public String getOwnerName() {
        return owner.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
