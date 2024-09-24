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

# 영속성 컨텍스트 1

## JPA 가장 주용한 2가지
 1. 객체와 관계형 데이터베이스 매핑하기
 2. 영속성 컨텍스트

## 영속성 컨텍스트
    - JPA를 이해하는데 가장 중요한 용어
    - 엔티티를 영구저장하는 환경 이라는 뜻
    - EntityManager.persist(entity);

## J2SE 환경
    - 엔티티 매니저와 영속성 컨텍스가 1:1
    - EntityManager가 생성되면 PersistenceContext도 함께 생성된다. 

## 엔티티의 생명주기 
 1. 비영속(new/transient)
    - 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
 2. 영속(managed)
    - 영속성 컨텍스트에 관리되는 상태
 3. 준영속(detached)
    - 영속성 컨텍스트에 저장되었다가 분리된 상태
 4. 삭제(removed)
    - 삭제된 상태

## 비영속
    - 객체를 생성한 상태(비영속)
```
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");
```

## 영속
```
Member member = new Member();
member.setId("member1");
member.setUsername(“회원1”);

EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

//객체를 저장한 상태(영속)
em.persist(member);
```

# 영속성 컨텍스트 2

## 1차 캐시 조회
- 1차 캐시안에 저장된다
```
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");

//1차 캐시에 저장됨
em.persist(member);

//1차 캐시에서 조회
Member findMember = em.find(Member.class, "member1");
```

## 영속성 엔티티의 동일성 보장
- 자바 컬렉션에서 똑같은 래퍼런스가 같은거 처럼 동일성을 보장한다.
```
Member a = em.find(Member.class, "member1");
Member b = em.find(Member.class, "member1");
System.out.println(a == b); //동일성 비교 true

```

## 엔티티 등록: 트랜잭션을 지원하는 쓰기 지연
```
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
//엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.   
transaction.begin(); // [트랜잭션] 시작

em.persist(memberA);
em.persist(memberB);

//여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.
//커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.

transaction.commit(); // [트랜잭션] 커밋
```

## 엔티티 수정: 변경 감지
- 데이터를 찾아 온 다음에 값만 변경하면 값이 변경된다.
- update같은건 필요 없다.
- 1차 캐시에서 저장되어있는 데이터와 변경된 데이터가 있으면 update 쿼리를 생성하고 커밋하여 데이터베이스에 저장한다.
- em.persist를 호출하지 않아도 된다.
- 트랜잭션의 커밋되는 시점에 적용된다.
```
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();

transaction.begin(); // [트랜잭션] 시작

// 영속 엔티티 조회
Member memberA = em.find(Member.class, "memberA");

// 영속 엔티티 데이터 수정
memberA.setUsername("hi");
memberA.setAge(10);

transaction.commit(); // [트랜잭션] 커밋
```


