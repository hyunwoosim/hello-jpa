# 객체 지향 쿼리 언어 기본 문법


# JPQL
1. JPA를 사용하면 엔티티 객체를 중심으로 개발
2. 검색 할 때도  테이블이 아닌 엔티티 객체를 대상으로 검색
3. JPQL: 엔티티 객체를 대상으로 쿼리
4. SQL: 데이터 베이스 테이블 대상으로 쿼리 

# Criteria
1. 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
2. JPA 공식 기능
3. !!!! 단점: 너무 복잡하고 실용성이 없다. !!!
4. Criteria 대신에 QueryDSL 사용 권장

# QueryDSL
1. 문자가 아닌 자바코드로 JPQL을 작성할 수 있음
2. 컴파일 시점에 문법 오류를 찾을 수 있음
3. 동적쿼리 작성 편리함
4. 실무 사용 권장


# JPQL 기본 문법 기능

## TypeQuery, Query

1. TypeQuery: 반환 타입이 명확할 때 사용
```
TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);

```
2. Query: 반환 타입이 명확하지 않을 때 사용
```
Query query = em.createQuery("SELECT m.username, m.age from Member m");
```

## 결과 조회 API
1. query.getResultList(): 결과가 하나 이상일 때, 리스트 반환
   - 결과과 없으면 빈 리스트 반환

2. query.getSingleResult(): 결과가 정확히 하나, 단일 객체 반환
   - 결과가 안나오면 Exceiption 터트린다.
   ```
   1. 결과가 없으면: javax.persistence.NoResultException
   2.둘 이상이면: javax.persistence.NonUniqueResultException
   ```
   
# 프로젝션
- Select절에 조회할 대상을 지정하는 것
- 프로젝션 대상 : 엔티티, 임베디드 타입, 스칼라 타입(숫자, 문자등 기본 데이터 타입)

## new 명령어로 조회
1. 단순 값을 DTO로 바로 조회
```
SELECT new jpabook.jpql.UserDTO(m.username, m.age) FROM Member m
```
2. 패키지 명을 포함한 전체 클래스 명 입력
3. 순서와 타입이 일치하는 생성자 필요

# 페이징 API
1. setFirstResult(int startPosition) : 조회 시작 위치 (0부터 시작)
2. setMaxResults(int maxResult) : 조회할 데이터 수

# 조인
- 엔티티 중심의 조인이다.

# 서브 쿼리

## 지원 함수
1. [NOT] EXISTS (subquery): 서브쿼리에 결과가 존재하면 참
   1. {ALL | ANY | SOME} (subquery)
   2. ALL 모두 만족하면 참
   3. ANY, SOME: 같은 의미, 조건을 하나라도 만족하면 참
2. [NOT] IN (subquery): 서브쿼리의 결과 중 하나라도 같은 것이 있으면 참

## JPA 서브 쿼리 한계
1. JPA는 WHERE, HAVING 절에서만 서브 쿼리 사용 가능
2. SELECT 절도 가능(하이버네이트에서 지원)


# JPQL 타입 표현
1. ENUM: jpabook.MemberType.Admin (패키지명 포함)
2. 엔티티 타입: TYPE(m) = Member (상속 관계에서 사용)

# 조건식 - case 식
## 1. 기본 CASE 식
```
select
      case when m.age <= 10 then '학생요금'
      when m.age >= 60 then '경로요금'
      else '일반요금'
   end
from Member m
```
## 2. 단순 CASE 식
```
select
      case t.name
      when '팀A' then '인센티브110%'
      when '팀B' then '인센티브120%'
      else '인센티브105%'
   end
from Team t
```

## 3. 조건식- CASE
1. COALESCE: 하나씩 조회해서 null이 아니면 반환
2. NULLIF: 두 값이 같으면 null 반환, 다르면 첫번째 값 반환

# JPQL 기본 함수

# 경로 표현식
- .(점)을 찍어 객체 그래프를 탐색하는 것
```
select m.username -> 상태 필드
   from Member m
   join m.team t -> 단일 값 연관 필드
   join m.orders o -> 컬렉션 값 연관 필드
where t.name = '팀A'
```

1. 상태 필드
   - 상태 필드(state field): 단순히 값을 저장하기 위한 필드 (ex: m.username)
2. 연관 필드
-  연관 필드(association field): 연관관계를 위한 필드
    - 단일 값 연관 필드: @ManyToOne, @OneToOne, 대상이 엔티티(ex: m.team)
    - 컬렉션 값 연관 필드: @OneToMany, @ManyToMany, 대상이 컬렉션(ex: m.orders)

## 경로 표현식 특징
1. 상태 필드(state field): 경로 탐색의 끝, 탐색X
2. 단일 값 연관 경로: 묵시적 내부 조인(inner join) 발생, 탐색O
3. 컬렉션 값 연관 경로: 묵시적 내부 조인 발생, 탐색X
   - FROM 절에서 명시적 조인을 통해 별칭을 얻으면 별칭을 통해서 탐색 가능
### 실무에서는 명시적 조인을 사용하자 


# fetch join (매우 중요!!!!!!!!!)
- JPQL에서 성능 최적화를 위해 제공하는 기능
- 연관된 엔티티나 컬렉션을 SQL 한 번에 함께 조회 기능

## 엔티티 fetch join
```
[JPQL]
select m from Member m join fetch m.team

[SQL]
SELECT M.*, T.* FROM MEMBER M INNER JOIN TEAM T ON M.TEAM_ID=T.ID
```

## 컬렉션 fetch join
- 일대다 관계, 컬렉션 페치 조인
```
[JPQL]
select t from Team t join fetch t.members where t.name = ‘팀A'

[SQL]
SELECT T.*, M.* FROM TEAM T INNER JOIN MEMBER M ON T.ID=M.TEAM_ID WHERE T.NAME = '팀A'
```

## fetch 조인과 DISTINCT
- SQL의 DISTINCT는 중복된 결과를 제거하는 명령
- JPQL의 DISTINCT 2가지 기능 제공
  1. SQL에 DISTINCT를 추가
  2. 애플리케이션에서 엔티티 중복 제거

### 하이버네이트6 부터는 DISTINCT 명령어를 사용하지 않아도 애플리케이션에서 중복 제거가 자동으로 적용된다.

## fetch 조인의 한계
1. 페치 조인 대상에는 별칭을 줄 수 없다
2. 둘 이상의 컬렉션은 페치 조인 할 수 없다.
3. 컬렉션을 페치 조인하면 페이징 API(setFirstResult, setMaxResults)를 사용할 수 없다.
