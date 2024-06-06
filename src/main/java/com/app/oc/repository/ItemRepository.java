package com.app.oc.repository;

import com.app.oc.entity.Item;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long> ,ItemRepositoryCustom{


    @EntityGraph(attributePaths = {"files"})
    Optional<Item> findByIdWithFiles(Long id);

}
