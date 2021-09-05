package dev.demo.scraper.repository;

import dev.demo.scraper.model.jpa.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Page<Link> findAllByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    Optional<Link> findAllByUserIdAndAndHash(String userId, String hash);

    @Query(value = "select * from link l where l.user_id=:userId and :tag in (select t.tag from tags t where t.link_id=l.id) order by l.created_at desc",
            countQuery = "select count(*) from link l where l.user_id=:userId and :tag in (select t.tag from tags t where t.link_id=l.id)",
            nativeQuery = true)
    Page<Link> findByUserIdAndTagContaining(@Param("userId") String userId, @Param("tag") String tag, Pageable pageable);

    @Query(value = "select tc.tag from (select tags.tag as tag, count(tag) as usage " +
            "from tags inner join link  on tags.link_id = link.id " +
            "where link.hash like :hash " +
            "group by tags.tag order by usage desc ) as tc", nativeQuery = true)
    List<String> findTagsByPopularity(@Param("hash") String hash);

    void deleteAllByUserIdAndId(String userId, Long id);

}
