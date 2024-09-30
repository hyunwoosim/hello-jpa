package hellojpa;

import hellojpa.items.Book;
import hellojpa.items.Movie;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // persistence.xml에 설정한 UnitName을 적으면 된다.

        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Book book = new Book();
            book.setName("JPA");
            book.setAuthor("일이삼");
            em.persist(book);
            
            em.flush();
            em.clear();



            tx.commit();

        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();



    }
}
