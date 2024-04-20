package com.app.oc.repository.repositoryImpl;

import com.app.oc.entity.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventRepositoryImpl {

    @PersistenceContext
    private EntityManager em;

    /**
     * 최근에 작성한 순으로 정렬(eventId를 역순으로 정렬)
     * @param shopSeq
     * @return
     */
    public List<Event> findListByShopSeq(Long shopSeq) {
        return em.createQuery("select e from Event e where e.shoppingmall = :shopSeq order by e.eventId desc ",
                        Event.class)
                .setParameter("shopSeq", shopSeq)
                .getResultList();
    }

    /**
     * 게시글 삭제
     * @param eventSeq
     */
    public void deletePost(Long eventSeq) {
        Event findEvent = em.find(Event.class, eventSeq);
        em.remove(findEvent);
    }
}
