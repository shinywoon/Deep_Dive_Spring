# 커넥션 풀 이해
---

<img width="643" alt="스크린샷 2023-11-07 11 13 48" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/22dd47d1-936e-4a3a-b21b-7c5f99dbd459">


### DB Connection 획득하는 과정

1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회한다.
2. DB 드라이버는 DB와 `TCP/IP` Connection 을 연결한다. 물론 이 과정에서 3 way handshake 같은 `TCP/IP` 연결을 위한 네트워크 동작이 발생한다.
3. DB 드라이버는 `TCP/IP` Connection 이 연결 되면 , ID, PW 와 기타 부가 정보를 DB에 전달한다.
4. DB는 ID, PW 를 통해 내부 인증을 완료하고, 내부에 DB 세션을 생성한다.
5. DB는 Connection 생성이 완료되었다는 응답을 보낸다.
6. DB 드라이버는 Connection 객체를 생성해서 클라이언트에 반환한다.

이렇게 커넥션을 새로 만드는 것은 과정도 복잡하고 시간도 많이 소모되는 일이다.

이 문제를 해결하기위한 아이디어가 바로 커넥션을 미리 생성해두고 사용하는 커넥션 풀이라는 방법이다.

<img width="641" alt="스크린샷 2023-11-07 11 13 53" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/485e1e87-42bc-4b15-83f9-69f81277d7ab">


애플리케이션을 시작하는 시점에 커넥션 풀은 필요한 만큼 커넥션을 미리 확보해서 풀에 보관한다.

얼마나 보관할 지는 서비스의 특징과 서버 스펙에 따라 다르지만 기본값은 보통 10개 이다.

<img width="638" alt="스크린샷 2023-11-07 11 13 58" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/348aa56a-825a-4a8c-ab83-c8d7a30f9732">


커넥션 풀에 들어 있는 커넥션은 TPC/IP 로 DB와 커넥션이 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL 을 DB에 전달할 수 있다.

<img width="649" alt="스크린샷 2023-11-07 11 14 04" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/64b11032-6dbd-40cc-bd83-0ac583a19daa">


애플리케이션 로직에서 이제는 DB 드라이버를 통해서 새로운 커넥션을 획득하는 것이 아니다.

이제는 커넥션 풀을 통해 이미 생성되어 있는 커넥션을 객체 참조로 그냥 가져다 쓰기만 하면 된다.

커넥션 풀에 커넥션을 요청하면 커넥션 풀은 자신이 가지고 있는 커넥션 중 하나를 반환한다.

<img width="644" alt="스크린샷 2023-11-07 11 14 11" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/f05be895-0ad4-4cbb-bdf0-439880ee2a26">


커넥션을 모두 사용하고 나면 이제는 커넥션을 종료하는 것이 아니라, 다음에 다시 사용할 수 있도록 해당 커넥션을 그대로 커넥션 풀에 반환하면 된다.

# DataSource 이해

---

<img width="639" alt="스크린샷 2023-11-07 11 14 17" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/7a6bf5b4-1c1b-4d9d-a8f3-39b4b29d107f">


### DriverManager 를 통해 커넥션 획득

<img width="637" alt="스크린샷 2023-11-07 11 14 24" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/c75bf85a-5594-46b1-baaf-68de18e48b55">


`DriverManager` 를 통해서 커넥션을 획득하다가, 커넥션 풀을 사용하는 방법으로 변경하려면?

<img width="635" alt="스크린샷 2023-11-07 11 14 29" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/695e1734-8f17-4738-b304-ecb46da51053">


### 커넥션을 획득하는 방법을 `추상화`

<img width="636" alt="스크린샷 2023-11-07 11 17 11" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/bb936080-f1de-4aeb-afe5-55e869a504d6">


자바에서는 이런 문제를 해결하기 위해 `javax.sql.DataSource` 라는 인터페이스를 제공한다.

`DataDource` 는 커넥션을 획득하는 방법을 추상화 하는 인터페이스이다.

이 인터페이스의 핵심 기능은 커넥션 조회 하나이다. ( 다른 일부 기능도 있지만 크게 중요하지 않음 )

### DataSource 핵심 기능만 축약

```java
public interface DataSource{
	Connection getConnection() throws SQLException;
}
```

대부분의 커넥션 풀은 `DataSource` 인터페이스를 이미 구현해두었다. 따라서 개발자는 `DBCP2 커넥션 풀` , `HikariCP 커넥션 풀` 의 코드를 직접 의존하는 것이 아니라 `DataSource` 인터페이스에만 의존하도록 애플리케이션 로직을 작성하면 된다.

커넥션 풀 구현 기술을 변경하고 싶으면 해당 구현체로 갈아끼우기만 하면 된다.

`DriverManager` 는 `DataSource` 인터페이스를 사용하지 않는다. 따라서 `DriverManager` 는 직접 사용해야 한다. 따라서 `DriverManager` 를 사용하다가 `DataSource` 기반의 커넥션 풀을 사용하도록 변경하면 관련 코드를 다 고쳐야 한다. 이런 문제를 해결하기 위해 스프링은 `DriverManager` 도 `DataSource` 를 통해서 사용할 수 있도록 `DriverManageDateSource` 라는 `DataSource` 를 구현한 클래스를 제공한다.

자바는 `DataSource` 를 통해 커넥션을 획득하는 방법을 추상화 했다.

이제 애플리케이션 로직은 `DataSource` 인터페이스에만 의존하면 된다. 덕분에 `DriverManageDataSource` 를 통해서 `DriverManager` 를 사용하다가 커넥션 풀을 사용하도록 코드를 변경해도 애플리케이션 로직은 변경하지 않아도 된다.

## DataSource 예제 1 - DriverManager

### ConnectionTest - 드라이버 매니저

```java
package hello.jdbc.Connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
class DBConnectionUtilTest {

    @Test
    @DisplayName("커넥션 테스트")
    void getConnectTest(){

        try {

            Connection con1 = DBConnectionUtil.getConnection();
            log.info("connection = {} , imple = {}" , con1, con1.getClass());

            Connection con2 = DBConnectionUtil.getConnection();
            log.info("connection = {} , imple = {}" , con1, con1.getClass());

            Assertions.assertThat(con1).isNotNull();
            Assertions.assertThat(con2).isNotNull();
        }catch (SQLException e){
            log.error("error :",e);
        }

    }

}
```

<img width="945" alt="스크린샷 2023-11-07 10 11 45" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/4075dec5-b920-4e3a-8a74-5467208fa3a2">


### `DataSource` 가 적용된 `DriverManager` → `DriverManagerDataSource`

### ConnectionTest - 데이터 소스 드라이버 매니저 추가

```java
package hello.jdbc.Connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static hello.jdbc.Connection.ConnectionConst.*;

@Slf4j
class DBConnectionUtilTest {

    @Test
    @DisplayName("커넥션 테스트")
    void getConnectTest() throws SQLException {

            Connection con1 = DBConnectionUtil.getConnection();
            log.info("connection = {} , imple = {}" , con1, con1.getClass());

            Connection con2 = DBConnectionUtil.getConnection();
            log.info("connection = {} , imple = {}" , con1, con1.getClass());

            Assertions.assertThat(con1).isNotNull();
            Assertions.assertThat(con2).isNotNull();

            DriverManagerDataSource ds = new DriverManagerDataSource(URL,USERNAME,PASSWORD);

            userDataSource(ds);
    }

    private void userDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection = {} , imple = {}" , con1, con1.getClass());
        log.info("connection = {} , imple = {}" , con2, con2.getClass());
    }

}
```

<img width="501" alt="스크린샷 2023-11-07 10 37 40" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/1f453946-31ab-432b-8d0b-c3e731fac6cd">


`DriverManagerDataSource` 는 스프링이 제공하는 코드이다.

### 차이점

`DriverManager`

```java
DriverManager.getConnection(URL,USERNAME,PASSWORD);
DriverManager.getConnection(URL,USERNAME,PASSWORD);
```

`DataSource`

```java
void dataSourceDriverManager() throws SQLException {
      DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,
  USERNAME, PASSWORD);
      useDataSource(dataSource);
}
  private void useDataSource(DataSource dataSource) throws SQLException {
      Connection con1 = dataSource.getConnection();
      Connection con2 = dataSource.getConnection();
      log.info("connection={}, class={}", con1, con1.getClass());
      log.info("connection={}, class={}", con2, con2.getClass());
}
```

`DriverManager` 는 커넥션을 획득할 때마다, `URL`, `USERNAME`, `PASSWORD` 같은 파라미터를 계속 전달해야 한다.

반면 `DataSource` 를 사용하는 방식은 처음 객체를 생성할 때만 필요한 파라미터를 넘겨두고, 커넥션을 획득할 때는 단순히 `dataSource.getConnection()` 만 호출하면 된다. ( 설정과 사용의 분리 )

## DataSource 예제 2 - 커넥션 풀

### ConnectionTest - 데이터소스 커넥션 풀 추가

```java
package hello.jdbc.Connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.net.UnknownServiceException;
import java.sql.Connection;
import java.sql.SQLException;

import static hello.jdbc.Connection.ConnectionConst.*;

@Slf4j
class DBConnectionUtilTest1 {

    @Test
    @DisplayName("커넥션 테스트")
    void getConnectTest() throws SQLException, InterruptedException {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);

        Thread.sleep(1000);

    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection = {} , imple = {}" , con1, con1.getClass());
        log.info("connection = {} , imple = {}" , con2, con2.getClass());
    }

}
```

HikariCP Connection Pool 사용

`HikariDataSource` 는 `DataSource` interface 를 구현

 `dataSource.setMaximumPoolSize(10);` : Connection Pool 최대 Size 지정

`dataSource.setPoolName("MyPool");` : Pool Name 설정

### 실행 결과

(로그 순서는 이해하기 쉽게 약간 수정)

```java
#커넥션 풀 초기화 정보 출력
HikariConfig - MyPool - configuration:
HikariConfig - maximumPoolSize................................10 HikariConfig - poolName................................"MyPool"
#커넥션 풀 전용 쓰레드가 커넥션 풀에 커넥션을 10개 채움
[MyPool connection adder] MyPool - Added connection conn0: url=jdbc:h2:.. user=SA
[MyPool connection adder] MyPool - Added connection conn1: url=jdbc:h2:.. user=SA
[MyPool connection adder] MyPool - Added connection conn2: url=jdbc:h2:.. user=SA
[MyPool connection adder] MyPool - Added connection conn3: url=jdbc:h2:.. user=SA
[MyPool connection adder] MyPool - Added connection conn4: url=jdbc:h2:.. user=SA
 ...
[MyPool connection adder] MyPool - Added connection conn9: url=jdbc:h2:.. user=SA

#커넥션 풀에서 커넥션 획득1
ConnectionTest - connection=HikariProxyConnection@446445803 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection

#커넥션 풀에서 커넥션 획득2
ConnectionTest - connection=HikariProxyConnection@832292933 wrapping conn1: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
MyPool - After adding stats (total=10, active=2, idle=8, waiting=0)
```

### MyPool Connection adder

별도의 쓰레드를 사용해 커넥션을 채운다.

왜 별도의 쓰레드를 사용해 커넥션을 채울까?

커넥션 풀에 커넥션을 채우는 것은 상대적으로 오래 걸리는 일

다 채울때 까지 기다리면 Application 실행시간이 늦어진다.

## DataSource 적용

### MemberRepositoryV1

```java
package hello.jdbc.repository;

import hello.jdbc.Connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Member save(Member member) throws SQLException {

        String sql = "insert into member(member_id,money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {

            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();

            return member;

        }catch (SQLException e){
            log.error("err : ", e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }

    }

    public Member findById(String memberId) throws SQLException {

        String sql = "select * from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            rs = pstmt.executeQuery();

            if (rs.next()){
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            }else {
                throw new NoSuchElementException("member not found member_id = " + memberId);
            }

        }catch (SQLException e){
            log.error("err : ",e);
            throw e;
        }finally {
            close(con,pstmt,rs);
        }

    }

    public void update(String memberId, int money) throws SQLException {

        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {

            con =getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2,memberId);
            pstmt.executeUpdate();

        }catch (SQLException e){
            log.error("err : " ,e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }

    }

    public void delete(String memberId) throws SQLException {

        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {

            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            pstmt.executeUpdate();

        }catch (SQLException e){
            log.error("err :", e);
            throw e;
        }finally {
            close(con,pstmt,null);
        }

    }

    private void close(Connection con, Statement stmt, ResultSet rs) throws SQLException {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection ={} , class={}", con, con.getClass());
        return con;
    }

}
```

`DataSource` 의존 관계 주입

- 외부에서 `DataSource` 를 주입 받아서 사용
- `DataSource` 는 표준 인터페이스 이기 때문에 `DriverManagerDataSource` 에서 `HikariDataSource` 로 변경 되어도 해당 코드를 변경하지 않아도 된다.

`JdbcUtils` 편의 메서드

- 스프링은 JDBC 를 편리하게 다룰 수 있는 `JdbcUtils` 를 제공

### MemberRepositoryV1Test

```java
package hello.jdbc.repository;

import com.zaxxer.hikari.HikariDataSource;
import hello.jdbc.Connection.ConnectionConst;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static hello.jdbc.Connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach
    void beforeEach(){

        //DriverManagerDataSource ds = new DriverManagerDataSource(URL,USERNAME,PASSWORD);

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(URL);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);

        repository = new MemberRepositoryV1(ds);

    }

    @Test
    @DisplayName("crud")
    void crud() throws SQLException {
        //save
        Member member = new Member("shin",10000);
        Member saveMember = repository.save(member);

        //findById
        Member byId = repository.findById(member.getMemberId());
        assertThat(byId).isEqualTo(member);

        //update
        repository.update(member.getMemberId(), 20000);
        Member byId1 = repository.findById(member.getMemberId());
        assertThat(byId1.getMoney()).isEqualTo(20000);

        //delect
        repository.delete(member.getMemberId());
        assertThatThrownBy(()-> repository.findById(member.getMemberId())).isInstanceOf(NoSuchElementException.class);
    }
}
```

`DataSource` 의존 관계 주입이 필요하다.

DriverManagerDataSource HikariDataSource 로 변경해도 MemberRepositoryV1 의 코드는 전혀 변경하지 않아도 된다. MemberRepositoryV1 는 DataSource 인터페이스에만 의존하기 때문이다. 

이것이 DataSource 를 사용하는 장점이다.(DI + OCP)
