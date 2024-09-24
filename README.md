# JPA

## JPA 설정
    - /META-INF/persistence.xml 위치에 설정해야한다.

## JPA 구동 방식
    1. Persistence 클래스에서 
    2. /META-INF/persistence.xml 에 있는 설정 정보를 조회
    3. EntityManagerFactory를 생성한다.
    4. 필요할때마다 EntityManager을 생성한다.

## @Entity
    -  JPA가 관리할 객체

## @Id
    - 데이터베이스와 PK 매핑

## JPA의 모든 데이터 변경은 트랜잭션 안에서 실행
```    
스프링 없을때
  EntityTransaction tx = em.getTransaction();
        tx.begin();
        //code
        tx.commit();
        
         EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();

            member.setId(2L);
            member.setName("HelloB");
            em.persist(member);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        
        }
            emf.close();
```

## <property name="hibernate.hbm2ddl.auto" value="create" />
    - Hibernate의 DDL 동작을 제어하는 설정이다.
    - 데이터베이스 테이블의 생성 및 스키마 관리를 자동화 할때 사용된다.
    - create : 기존 테이블을 모두 삭제하고 새로운 테이블을 생성한다.

## 단순 조회할때 em.find()
```
Member findMember = em.find(Member.class, 1L);
            System.out.println("findMember.getId() = " + findMember.getId());
            System.out.println("findMember.getname() = " + findMember.getName());

```

## 삭제 em.remove()

## 수정 
    - em.find()로 값을 찾아올때 JPA가 관리하기 때문에 
    - 변경된 사항이 있으면 update쿼리를 날려서 값이 변경되어 저장된다.
```
Member findMember = em.find(Member.class, 1L);
이렇게 값을 찾았을때 

findMember.setName("hlloJpa");
코드를 추가하여 실행하면 
jpa가 update 쿼리를 날려 이름이 helloJPA로 변경된다.
```

## 주의
 1. 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체에서 공유
 2. 엔티티 매니저는 쓰레드간에 공유X (사용하고 버려야 한다).
 3. JPA의 모든 데이터 변경은 트랜잭션 안에서 실행


## 복잡한 조회 방법은 JPQL을 사용한다.
    - ex) 18살만 조회하고싶다

## em.createQuery
```
List<Member> result = em.createQuery("select m from Member as m", Member.class)
                .getResultList();
```
- member 객체를 대상으로 쿼리를 한다.
- 테이블이 대상이 아니고 객체가 대상이다.

## JPQL
 1. JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
 2. JPQL은 엔티티 객체를 대상으로 쿼리
 3. SQL은 데이터베이스 테이블을 대상으로 쿼리
 4. PQL을 한마디로 정의하면 객체 지향 SQL