package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
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
            Team teamA = new Team();

            Member member = new Member();
            member.setUsername("회원1");

            em.persist(member);

            em.flush();
            em.clear();

            String query = "select m from Member m order by m.age desc";
            List<Member> result = em.createQuery(query, Member.class)
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList();



            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();



    }

}
