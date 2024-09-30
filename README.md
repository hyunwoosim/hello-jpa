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

# 플러시

## 플러시란?
    - 영속성 컨텍스트의 변경내용을 데이터베이스에 반영하는 것이다.

## 플러시 발생
    - 변경 감지
    - 수정된 엔티티 쓰기 지연 SQL 저장소에 등록
    - 쓰기 지연 SQL 저장소 등록된 SQL 쿼리를 데이터베이스에 전송

## 영속성 컨텍스트를 플러시하는 방법
1.  em.flush() - 직접 호출
2. 트랜잭션 커밋 - 플러시 자동 호출
3. JPQL 쿼리 실행 - 플러시 자동 호출

## 플러시 정리
 - 영속성 컨텍스트를 비우지 않음
 - 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화
 - 트랜잭션이라는 작업 단위가 중요 -> 커밋 직전에만 동기화 하면 됨

# 준영속

## 준영속 상태
- 영속 상태 -> 준영속 상태
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached) 
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

## 준영속 상태로 만드는 방법
- em.detach(entity) 특정 엔티티만 준영속 상태로 만듬
- em.clear(entity) 영속성 컨텍스트를 완전 초기화
- em.close(entity) 영속성 컨텍스트를 종료

# 엔티티 매핑

## @Entity
    - @Entity가 붙은 클래스는 JPA가 관리하는 엔티티라 한다.
    - 기본 생성자 필수(파라미어가 없는 public 또는 protected생성자)
    - default 값은 사용하는 클래스의 이름이다.

## @ Table
    - @Entity와 매핑할 테이블 지정(데이터베이스의 테이블)
```
@Entity
@Table(name = "MBR")
public class Member {
```
 - Member클래스의 Entity가 데이터베이스의 MBR 테이블과 매핑된다
```
Hibernate: 
    /* insert for
        hellojpa.Member */insert 
    into
        MBR (name, id)   <<=== Member 이름이 MBR로 바뀌었다.
    values
        (?, ?)
```

## 데이터베이스 스키마 자동 생성
    - 개발 할 때 사용하면 유용하다.
    - DDL을 애플리케이션 실행 시점에 자동 생성한다.
```
<property name="hibernate.hbm2ddl.auto" value="create" />
```
- persistence.xml 옵션에 사용한다.

### 옵션
- Create : 기존테이블 삭제 후 다시 생성 (DROP + CREATE)
- create-drop : create와 같으나 종료시점에 테이블 DROP
- update : 변경분만 반영(운영DB에는 사용하면 안됨)
- validate : 엔티티와 테이블이 정상 매핑되었는지만 확인
  - 새로운 컬럼을 추가했을때 테이블에 없으면 에러가 뜬다.

### 주의점
    - 운영 장비에는 절대 create, create-drop, update 사용하면 안된다.
    - 운영할 때는 다 사용하지 말자
    - 로컬 pc에서만 사용하는게 좋다.

## DDL 생성기능
    - DDL 생성 기능은 DDL을 자동 생성할 때만 사용되고 JPA의 실행 로직에는 영향을 주지 않는다.
```
@Column(nullable = false, length = 10)
```

# 필드와 컬럼 매핑

## @Column
1. name : 필드와 매핑할 테이블의 컬럼 이름
2. insertable : 등록 가능 여부 default는 true
3. updatable : 변경 가능 여부 default는 true
4. nullable(DDL) : null 값의 허용 여부를 설정한다. false로 설정하면 DDL 생성 시에 not null 제약조건이 붙는다
5. unique(DDL) : @Table의 uniqueConstraints와 같지만 한 컬럼에 간단히 유니크 제 약조건을 걸 때 사용한다.
   - 잘 사용하지 않는다.(이름이 이상한 이름으로 나온다.)
6. length(DDL) : 문자 길이 제약조건, String 타입에만 사용한다. defalut 값은 255
7. columnDefinition (DDL) : 데이터베이스 컬럼 정보를 직접 줄 수 있다.   ex) varchar(100) default ‘EMPTY'
8. precision, scale(DDL) : 아주 큰 숫자나 소수점을 사용할 때 사용한다.

## @Enumerated
1. EnumType.ORDINAL: enum 순서를 데이터베이스에 저장(사용하지 말자)
   - 만약 새로운 요청사항을 추가해서 적용하면 순서가 꼬여버리는 상황이 나온다.

```
enum 클래스 롤타입
USER, ADMIN
DB에는 0 ,1 순서로 적용되는데

GEST가 추가되면
enum 클래스 롤타입
GUSET, USER ,ADMIN // 이런식으로 추가되게 되면
DB에 게스트가 0 번이 된다. 0, 1, 2 순서

이렇게 되면 이미 저장되있는 USER도 0번이고  GUEST도 0번인  상황이 발생된다

```

2. EnumType.STRING: enum 이름을 데이터베이스에 저장
   - enum클래스의 저장된 이름으로 DB에 저장된다.

## @Temporal
- 과거 버전 사용할떄 사용한다.
  - 요새는 localDate 타입 쓰면된다.

# 기본키 매핑

## @Id
- 직접 할당할때 사용

## @GeneratedValue
- 자동 생성

### IDENTITY 전략
```
@GeneratedValue(strategy = GenerationType.IDENTITY)
```
- 기본 키 생성을 데이터베이스에 위임
- 예: MySQL의 AUTO_ INCREMENT

### SEQUENCE 전략
```
@GeneratedValue(strategy = GenerationType.SEQUENCE)
```
- 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
1. initialValue : DDL 생성 시에만 사용됨, 시퀀스 DDL을 생성할 때 처음 1 시작하는 수를 지정한다.
   - default : 1
2. allocationSize : 시퀀스 한 번 호출에 증가하는 수(성능 최적화에 사용됨 데이터베이스 시퀀스 값이 
    하나씩 증가하도록 설정되어 있으면 이 값 을 반드시 1로 설정해야 한다
    - default : 50

# 연관 관계 매핑

## 객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력 관계를 만들 수 없다.
- 테이블은 왜래 키로 조인을 사용해서 연관된 테이블을 찾는다.
- 객체는 참조를 사용해서 연관된 객체를 찾는다.
- 테이블과 객체 사이에는 이런 큰 간격이 있다.

## 단방향 연관 관계
- @ManyToOne
- @JoinColumn

## 양방향 연관과계와 연관관계의 주인
### !!!!!외래키가 있는 곳을 주인으로 정해라!!!!
- N쪽이 연관관계의 주인이 된다.(N:1)

## mappedBy
- mappedBy가 적힌 곳은 읽기,조회만 가능하다.
     
## 양방향 매핑시 가장 많이 하는 실수
- 연관관계의 주인에 값을 입력하지 않고
- 주인이 아닌 방향에 입력한다(주인이 아닌곳은 읽기만 가능하기 때문에)
###  순수 객체 상태를 고려
- 연관 관계 편의 메서드
- setter에 넣자(이름을 바꿔서)
```
 public void setTeam(Team team) {
        this.team = team;
 }       
        
public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }

```

## 매핑을 할 떄 단방향 매핑으로 설계 하자
- 양뱡향 매핑은 필요할때 설정

# 연관 관계 매핑

## 다대일[N:1] @ManyToOne
- 양방향일땐 외래키가 있는 쪽이 연관 관계 주인

## 일대다[1:N] @OneToMany
- 일대다 단방향,양방향 매핑보다는 다대일 양방향 매핑을 사용하자

## 일대일[1:1] @OneToOne

## 다대다[N:M] @ManyToMany
- 테이블은 다대다 관계 보단 일대다, 다대일로 풀어내서 사용하자 
- 연결테이블을 추가해서 사용한다.
- @JoinTable 사용
- 편리해 보이지만 실무에서 사용 X


# 고급 매핑

## 상속 관계 매핑
- Album, Book, Movie가 Item을 상속 받았을 때 
```
create table Item (
        price integer not null,
        stockQuantity integer not null,
        ITEM_ID bigint not null,
        DTYPE varchar(31) not null,
        actor varchar(255),
        artist varchar(255),
        author varchar(255),
        director varchar(255),
        isbn varchar(255),
        name varchar(255),
        primary key (ITEM_ID)
    )
```
- 이런 식으로 매핑된다.

## 주요 어노테이션 @Inheritance(strategy = )

### 1. InheritanceType.JOINED 조인 전략
```
create table Item (
        price integer not null,
        ITEM_ID bigint not null,
        DTYPE varchar(31) not null,
        name varchar(255),
        primary key (ITEM_ID)
    )

create table Album (
        ITEM_ID bigint not null,
        artist varchar(255),
        primary key (ITEM_ID)
    )
    
    create table Book (
        ITEM_ID bigint not null,
        author varchar(255),
        isbn varchar(255),
        primary key (ITEM_ID)
    )
    
    create table Movie (
        ITEM_ID bigint not null,
        actor varchar(255),
        director varchar(255),
        primary key (ITEM_ID)
    )
    
```

### 2. InheritanceType.SINGLE_TABLE 단일 테이블 전략
- 한 테이블에 다 생성된다.
- 성능이 잘 나온다는 장점
```
create table Item (
        price integer not null,
        ITEM_ID bigint not null,
        DTYPE varchar(31) not null,
        actor varchar(255),
        artist varchar(255),
        author varchar(255),
        director varchar(255),
        isbn varchar(255),
        name varchar(255),
        primary key (ITEM_ID)
```
- 단일 테이블 전략은 @DiscriminatorColumn 없어도 자동으로 생성된다.

### 3. InheritanceType.TABLE_PER_CLASS 구현 클래스마다 테이블 전략 
- 부모 테이블이 만들어지지 않는다.
- 부모 테이블의 컬럼들을 공통으로 가지고 있는다.
```
create table Album (
        price integer not null,
        ITEM_ID bigint not null,
        artist varchar(255),
        name varchar(255),
        primary key (ITEM_ID)
    )
    
    create table Book (
        price integer not null,
        ITEM_ID bigint not null,
        author varchar(255),
        isbn varchar(255),
        name varchar(255),
        primary key (ITEM_ID)
    )
    
    create table Movie (
        price integer not null,
        ITEM_ID bigint not null,
        actor varchar(255),
        director varchar(255),
        name varchar(255),
        primary key (ITEM_ID)
    )
```
- 조회 할 떄 모든 테이블을 조회해야 데이터를 찾을 수 있다.

### @DiscriminatorColumn
- 컬럼에 엔티티 명이 나온다.
- 부모 클래스에서 설정한다.
```
DTYPE - Movie
DTYPE - Album
```
- 데이터 베이스에서 확인하면 바로 알 수 있다.

### @DiscriminatorValue
- 자식 클래스의 이름 설정하고 싶을 떄 사용
- 자식 클래스에 설정한다.
```
@Entity
@DiscriminatorValue("M")
public class Movie extends Item


!! DB !!
DTYPE - M
```

## MappedSuperclass
- 엔티티가 아니다. 
- 테이블과 매핑하지 않는다.
- 부모 클래스가 상속 받는 자식클래스에 매핑 정보만 제공한다.
- 직접 생성해서 사용할 일이 없으므로 추상 클래스 권장
- 테이블과 관계 없고, 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할
- 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용

# 프록시와 연관 관계

## 프록시
- em.getReference: 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회

### 특징
-  실제 클래스를 상속 받아서 만들어진다.
- 실제 클래스와 겉 모양이 같다.
- 사용하는 입장에서는 진짜 객체인지 프록시 객체인지 구분하지 않고 사용한다.(이론상)

## 특징(중요!!!!)
- 프록시 객체는 처음 사용할 때 한 번만 초기화(여러번 초기화 안됨)
- 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는 것은 아님, 초기화 되면 프록시 객체를 통해서 실제 엔티티에 접근 가능
    ```
         Member findMember = em.getReference(Member.class, member.getId());
                System.out.println("before findMember = " + findMember.getClass());
                System.out.println("findMember.id = " + findMember.getId());
                System.out.println("after findMember = " + findMember.getClass());
                
    =========================결과=============================================            
        before findMember = class hellojpa.Member$HibernateProxy$w9BoyeM4
        findMember.id = 1
        after findMember = class hellojpa.Member$HibernateProxy$w9BoyeM4
    ```

- !!!!프록시 객체는 원본 엔티티를 상속 받는다. 따라서 타입 체크시 주의해야한다.(== 비교는 실패!  instance of 사용해야한다.!!!)
  1. == 비교
      ```
      둘다 .find 일 때
    
      Member m1 = em.find(Member.class, member1.getId());
      Member m2 = em.find(Member.class, member2.getId());
    
      System.out.println("m1 == m2: " + (m1.getClass() == m2.getClass()));
      =====================결과===============================
    
      m1 == m2: true 
      타입 비교기 때문에 true
      =========================================================
    
      find, getReference 일 때
    
      Member m1 = em.find(Member.class, member1.getId());
      Member m2 = em.getReference(Member.class, member2.getId());
    
      System.out.println("m1 == m2: " + (m1.getClass() == m2.getClass()));
      =====================결과===============================
    
      m1 == m2: false
      ```
    
  2. instanceof
     ```
     System.out.println("m1 == m2: " + (m1 instanceof Member));
     System.out.println("m1 == m2: " + (m2 instanceof Member));
     =====================결과===============================
    
     m1 == m2: true
     m1 == m2: true
     ```

- !!!! 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 getReference()를 호출해도 실제 엔티티를 반환한다.
    ```
    Member m1 = em.find(Member.class, member1.getId());
    System.out.println("m1 = " + m1.getClass());
    
    Member reference = em.getReference(Member.class, member1.getId());
    System.out.println("reference.getClass() = " + reference.getClass());
    =====================결과===============================
    
    m1 = class hellojpa.Member
    reference.getClass() = class hellojpa.Member
    ```

- !!!!!!영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제 발생
  - em.detach(), em.clear(), em.close()ß
    ```
      Member refMember = em.getReference(Member.class, member1.getId());
      System.out.println("refMember = " + refMember.getClass()); // Proxy
        
      em.detach(refMember);
        
      refMember.getName();
        
      =====================결과===============================
      refMember = class hellojpa.Member$HibernateProxy$jRFrGpp6
      org.hibernate.LazyInitializationException: could not initialize proxy [hellojpa.Member#1] - no Session
    ```

### 프록시 확인
1. 프록시 인스턴스의 초기화 여부 확인
    - .getPersistenceUnitUtil().isLoaded()
        ```
        Member refMember = em.getReference(Member.class, member1.getId());
        System.out.println("isLoaded = " + emf.getPersistenceUnitUtil().isLoaded(refMember));
        =====================결과===============================
        
        refMember = class hellojpa.Member$HibernateProxy$AZ95Tr81
        isLoaded = false
        ```
      
2. 프록시 강제 초기화
    - Hibernate.initialize(refMember);
