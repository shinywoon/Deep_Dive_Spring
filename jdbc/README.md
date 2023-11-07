# JDBC  이해

## Project Metadata

Group : hello

Artifact : jdbc

Name : jdbc

Package name : hello.jdbc

Packaging : Jar

Java : 11

Dependencies : JDBC API, H2 Database, Lombok

### 테스트에서도 lombok 을 사용하기 위해 코드 추가

`build.gradle`

```groovy
dependencies{

	...
	
	//테스트에서 lombok 사용
	testCompileOnly 'org.projectlombok:lombok'
	testAnnotationProcessor 'org.projectlombok:lombok'

}
```

이 설정을 사용하면 테스트코드에서 `@Slfj4` 같은 롬복 애노테이션을 사용할 수 있다.

## 테이블 생성하기

```sql
drop table member if exists cascade;
create table member(
	member_id varchar(10),
	money integer not null default 0,
  primary key(member_id)
);

insert into member(member_id, money) values ('hi1',10000);
insert into member(member_id, money) values ('hi2',20000);
```

# JDBC 이해

---

## 등장 이유

애플리케이션을 개발할 때 중요한 데이터는 대부분 데이터베이스에 보관한다.

<img width="851" alt="스크린샷 2023-11-07 09 28 46" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/5e3efad9-4028-4456-afea-563d93284c67">


클라이언트가 애플리케이션 서버를 통해 데이터를 저장하거나 조회하면, 애플리케이션 서버는 다음과정을 통해 DB를 사용한다.

<img width="851" alt="스크린샷 2023-11-07 09 28 50" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/528a4db1-79ab-4f10-8b12-9042367ae6d0">


1. 커넥션 연결 : 주로 TCP / IP 를 사용해 커넥션 연결
2. SQL 전달 : 애플리케이션 서버는 DB가 이해할 수 있는 SQL을 연결된 커넥션을 통해 DB에 전달한다.
3. 결과 응답 : DB는 전달된 SQL을 수행하고 그 결과를 응답한다. 애플리케이션 서버는 응답 결과를 활용한다.

<img width="847" alt="스크린샷 2023-11-07 09 28 56" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/681b7e64-fb1e-4e23-baef-9f0cc2ed3950">


### 문제 ]

각각의 데이터 베이스마다 커넥션을 연결하는 방법, SQL을 전달하는 방법, 그리고 결과를 응답 받는 방법은 모두 다름

1. DB를 다른 종류의 DB로 변경하면 애플리케이션 서버에 개발된 데이터베이스 사용 코드도 함께 변경해야 한다.
2. 개발자가 각각의 데이터베이스마다 커넷션 연결, SQL 전달, 그리고 그 결과를 응답 받는 방법을 새로 학습해야 한다.

위 문제를 해결하기 위해 JDBC 라는 자바 표준이 등장한다.

## JDBC 표준 인터페이스

Java Database Connectivity 는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API 다.

JDBC는 DB 에서 자료를 쿼리하거나 업데이트 하는 방법을 제공한다.

<img width="850" alt="스크린샷 2023-11-07 09 30 28" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/f0b0f256-7e86-4377-bda2-e726d1e47ccf">

대표적으로 다음 3가지 기능을 표준 인터페이스로 정의해서 제공한다.

- `java.sql.Connection` - 연결
- `java.sql.Statement` - SQL을 담은 내용
- `java.sql.ResultSet` - SQL 요청 응답

자바는 표준 인터페이스를 정의해두었다.

하지만 인터페이스만 있다고 해서 기능이 동작하지 않는다.

이 JDBC 인터페이스를 각각의 DB 벤더 (회사) 에서 자신의 DB에 맞도록 구현해서 라이브러리로 제공하는데,

이것을 JDBC 드라이버라 한다.

<img width="858" alt="스크린샷 2023-11-07 09 31 12" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/f91faaa2-af58-4012-8570-5c9fe9b74a4a">

<img width="849" alt="스크린샷 2023-11-07 09 31 26" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/bd77fe3a-06d2-412c-847c-31c161e2a5b6">


## JDBC 와 최신 데이터 접근 기술

JDBC는 1997년에 출시될 정도로 오래된 기술이고, 사용하는 방법도 복잡하다.

그래서 최근엔 JDBC 를 직접하용하기 보다는 JDBC 를 편리하게 사용하는 다양한 기술이 존재한다.

대표적으로 SQL Mapper 와 ORM 기술로 나눌수 있다.

<img width="851" alt="스크린샷 2023-11-07 09 32 17" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/49bd1111-d925-47f2-befb-12926a6dc3f4">

SQL Mapper 

- [ 장점 ] : JDBC를 편리하게 사용하도록 도와준다.
    - SQL 응답 결과를 객체로 편리하게 변환해준다.
    - JDBC의 반복 코드를 제거해준다.
- [ 단점 ] : 개발자가 SQL을 직접 작성해야 한다.
- 대표기술 : 스프링 jdbcTemplate, MyBatis
  
<img width="848" alt="스크린샷 2023-11-07 09 33 18" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/fb08e403-77a2-4835-85e2-f243ee97320f">

ORM 기술

- OMR은 객체를 관계현 데이터베이스 테이블과 매핑해주는 기술이다.
- 개발자는 반복적인 SQL을 직접 작성하지 않고, ORM 기술이 개발자 대신에 SQL을 동적으로 만들어 실행해 준다.
- 추가로 각각의 데이터베이스마다 다른 SQL을 사용하는 문제도 중간에서 해결해준다.
- JPA, 하이버네이트, 이클립스링크
- JPA는 자바 진영의 ORM 표준 인터페이스이고, 이것을 구현하는 것으로 하이버네이트와 이클립스 링크등의 구현 기술이 있다.

## DB 연결

### ConnectionConst

```java
package hello.jdbc.connection;

public abstract class ConnectionConst {

    public static final String URL = "jdbc:mysql://localhost/testDB?serverTimezone=UTC";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "1234";

}
```

### DBConnectionUtil

```java
package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConnectionUtil {

    public static Connection getConnection(){
        
        try{
            Connection connection = DriverManager.getConnection(URL,USERNAME,PASSWORD);
            log.info("get Connection={}, class= {}", connection, connection.getClass());
            return connection;    
        }catch (SQLException e){
            throw new IllegalStateException(e);
        }
        
    }

}
```

데이터베이스에 연결하려면 JDBC 가 제공하는 `DriverManager.getConnection(..)` 를 사용하면 된다.

이렇게 하면 라이브러리에 있는 데이터베이스 드라이버를 찾아서 해당 드라이버가 제공하는 커넥션을 반환해준다.

### DB ConnectionUtilTest

```java
package hello.jdbc.connection;

import jakarta.xml.bind.annotation.XmlSeeAlso;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DBConnectionUtilTest {
    
    @Test
    void connection(){
        Connection connection = DBConnectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }
    

}
```

## JDBC DriverManager 연결 이해

<img width="839" alt="스크린샷 2023-11-07 09 33 50" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/3b0b0bd1-725e-4c95-85a1-154aac700b1e">

JDBC 는 `java.sql.Connection` 표준 커넥션인 인터페이스를 정의한다.

### DriverManager 커넥션 요청 흐름

<img width="849" alt="스크린샷 2023-11-07 09 33 56" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/147ab2d7-933f-4455-a2b4-36e357210ea9">

JDBC가 제공하는 `DriverManager` 는 라이브러리에 등록된 DB 드라이버들을 관리하고, 커넥션을 획득하는 기능을 제공한다.

1. 애플리케이션 로직에서 커넥션이 필요하면 `DriverManager.getConnection()` 을 호출한다.
2. `DriverManager` 는 라이브러리에 등록된 드라이버 목록을 자동으로 인식한다. 이 드라이버들에게 순서대로 다음 정보를 넘겨서 커넥션을 획득할 수 있는지 확인한다.
    1. URL : ex ) `jdbc:h2:tcp://localhost/~/test`
    2. 이름, 비밀번호 등 접속에 필요한 추가 정보
    3. 각각의 드라이버는 URL 정보를 체크해서 본인이 처리할 수 있는 요청인지 확인 한다.
        
        URL 이 `jdbc:mysql` 로 시작하면 이것은 mysql 데이터베이스에 접근하기 위한 규칙이다.
        
        따라서 mysql 드라이버는 본인이 처리할 수 있으므로 실제 데이터페이스에 연결해서 커넥션을 획득하고 이 커넥션을 클라이언트에 반환한다.
        
        반면 `jdbc:mysql` 로 시작했는데 h2 드라이버가 먼저 실행되면 본인이 처리 할 수 없다는 결과를 반환하고 다음 드라이버에게 순서가 넘어간다.
        
3. 이 과정을 통해 찾은 커넥션 구현체가 클라이언트에 반환된다.

## JDBC 개발 - 등록

### table 만들기

```sql
drop table member if exists cascade;
    create table member (
        member_id varchar(10),
        money integer not null default 0,
        primary key (member_id)
);
```

### Member [ VO ]

```java
package hello.jdbc.domain;

import lombok.Data;

@Data
public class Member {

    private String memberId;
    private int money;

    public Member() {
    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
```

### MemberRepositoryV0 - 회원 등록

```java
package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import javax.xml.transform.Result;
import java.sql.*;

@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {

        String sql = "insert into member(member_id,money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{

            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate();
            return member;

        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) throws SQLException {
        if (rs != null){
            try {
                rs.close();
            }catch (SQLException e){
                log.info("error",e);
            }
        }

        if (stmt != null){
          try {
              stmt.close();
          }catch (SQLException e){
              log.info("error",e);
          }
        }

        if (con != null){
            try {
                con.close();
            }catch (SQLException e){
                log.info("error",e);
            }
        }

    }

}
```

### 커넥션 획득

`getConnection()` : `DBConnectionUtil` 를 통해서 데이터베이스 커넥션을 획득한다.

### MemberRepositoryV0Test - 회원 등록

```java
package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member member = new Member("memberV0",10000);
        repository.save(member);
    }

}
```

`select * from member` 쿼리를 실행하면 데이터가 저장된 것을 확인 할 수 있다.

## JDBC 개발 - 조회

### MemberRepositoryV0 - 회원 조회 추가

```java
public Member findById(String memberId) throws SQLException {

        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            rs = pstmt.executeQuery();

            if (rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found memberId ="+memberId);
            }

        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }

    }
```

### findById() - 쿼리 실행

`sql`: 데이터 조회를 위한 select SQL을 준비한다.

데이터를 조회할때는 `executeQuery()` 를 사용한다. 결과를 `ResultSet` 에 담아서 반환한다.

### MemeberRepositoryV0Test - 회원 조회 추가

```java
package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
    
        // save
        Member member = new Member("memberV0",10000);
        repository.save(member);
    
        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        assertThat(findMember).isEqualTo(member);
        
    }
}
```

실행 시 정상 저장된 결과를 잘 조회한다.

> 참고 : 실행결과에 `member` 객체의 참조 값이 아니라 실제 데이터가 보이는 이유는 롬복의 `@Data` 가 `toString()` 을 적절히 오버라이딩 해서 보여주기 때문이다.
`isEqualTo()`: `findMember.equals(member)` 를 비교한다. 결과가 참인 이유는 롬복의 `@Data` 는 해당 객체의 모든 필드를 사용하도록 `equals()` 를 오버라이딩 하기 때문이다.
> 

## JDBC 개발 - 수정 , 삭제

### MemberRepositoryV0 - 회원 수정 추가

```java
public void update(String memberId, int money) throws SQLException {
        
        String sql = "update member set money=? where member_id=?";
        
        Connection con = null;
        PreparedStatement pstmt = null;
        
        try {
            
            con = pstmt.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}",resultSize);
            
        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }
        
    }
```

### MemberRepositoryV0Test - 회원 수정 추가

```java
@Test
    void crud() throws SQLException {

        // save
        Member member = new Member("memberV0",10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        assertThat(findMember).isEqualTo(member);

        //update : money 10000 -> 20000
        repository.update(member.getMemberId(),20000);
        Member updateMember = repository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);
        
    }
```

### MemberRepositoryV0 - 회원 삭제 추가

```java
public void delete(String memberId) throws SQLException {

        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try{

            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);

            pstmt.executeUpdate();

        }catch (SQLException e){
            log.error("db error",e);
            throw e;
        }finally{
            close(con,pstmt,null);
        }

    }
```

### MemberRepositoryV0Test - 회원 삭제 추가

```java
package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {

        // save
        Member member = new Member("memberV0",10000);
        repository.save(member);

        //findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember={}",findMember);
        assertThat(findMember).isEqualTo(member);

        //update : money 10000 -> 20000
        repository.update(member.getMemberId(),20000);
        Member updateMember = repository.findById(member.getMemberId());
        assertThat(updateMember.getMoney()).isEqualTo(20000);

        //delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(()-> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);

    }
}
```
