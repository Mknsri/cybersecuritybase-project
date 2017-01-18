package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sec.project.domain.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

}
