package hellojpa;

import hellojpa.items.Book;
import hellojpa.items.Movie;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;

public class JpaMain {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        // persistence.xml에 설정한 UnitName을 적으면 된다.

        EntityManager em = emf.createEntityManager();
        //code

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setName("member1");
            member.setHomeAddress(new Address("homeCity", "street", "100"));

            member.getFavoriteFoods().add("치킨");
            member.getFavoriteFoods().add("피자");
            member.getFavoriteFoods().add("햄버거");

            member.getAddressesHistory().add(new Address("old1", "street", "100"));
            member.getAddressesHistory().add(new Address("old2", "street", "100"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("================Start================");
            Member findMember = em.find(Member.class, member.getId());

            // homeCity -> new City
//            findMember.getHomeAddress().setCity("newCity"); XXXXX
//            Address a = findMember.getHomeAddress();
//            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));
//
//            //치킨-> 한식
//            findMember.getFavoriteFoods().remove("치킨");
//            findMember.getFavoriteFoods().add("한식");

            findMember.getAddressesHistory().remove(new Address("old1", "street", "100"));
            findMember.getAddressesHistory().add(new Address("newCity1", "street", "100"));


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
