# Transaction - 개념

데이터베이스를 사용하는 대표적인 이유는 트랜잭션 개념을 지원하기 때문이다.

### Transaction ACID

### 원자성 [ Automicity ]

트랜잭션 내에서 실행한 작업들은 마치 하나의 작업인 것처럼 모두 성공하거나 모두 실패해야 한다.

### 일관성 [ Consistency ]

모든 트랜잭션은 일관성 있는 데이터베이스 상태를 유지해야 한다.

예를 들어 데이터베이스에서 정한 무결성 제약 조건을 항상 만족해야 한다.

### 격리성 [ Isolation ]

동시에 실행되는 트랜잭션들이 서로에게 영향을 미치지 않도록 격리한다.

격리성은 동시성과 관련된 성능 이슈로 인해 트랜잭션 격리 수준을 선택할 수 있다.

격리성을 완벽히 보장하려면 트랜잭션을 거의 순서대로 실행해야 한다.

이렇게 하면 동시 처리 성능이 매우 나빠진다. 이런 문제로 인해 ANSI 표준은 트랜잭션의 격리 수준을 4단계로 나누어 정의 했다.

- READ UNCOMMITED : 커밋되지 않은 읽기
- READ COMMITTED : 커밋된 읽기
- REPEATABLE READ :  반복 가능한 읽기
- SERIALIZABLE : 직렬화 가능

### 지속성 [ Durability ]

트랜잭션을 성공적으로 끝내면 그 결과가 항상 기록되어야 한다.

중간에 시스템에 문제가 발생해도 데이터베이스 로그 등을 사용해서 성공한 트랜잭션 내용을 복구 해야 한다.

## 데이터베이스 연결 구조와 DB 세션

<img width="645" alt="스크린샷 2023-11-07 13 02 06" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/1736e7cf-7085-418a-93e4-606161161457">


사용자는 WAS 나 DB 접근 TOOL 같은 Client 를 사용해서 데이터베이스 서버에 접근할 수 있다.

데이터베이스 서버에 연결을 요청하고 커넥션을 맺게 된다. 이때 데이터베이스 서버는 내부에 세션이라는 것을 만든다. 앞으로 해당 커넥션을 통한 모든 요청은 이 세션을 통해서 실행하게 된다.

세션은 트랜잭션을 시작하고, 커밋 또는 롤백을 통해 트랜잭션을 종료한다. 이후에 새로운 트랜잭션을 다시 시작할 수 있다.

사용자가 커넥션을 닫거나, 또는 DBA가 세션을 강제로 종료하면 세션은 종료 된다.

<img width="645" alt="스크린샷 2023-11-07 13 02 11" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/c2bc1fa7-a438-4f3f-89c7-3969fe0d9e70">

Connection pool 이 10개 이면 Session 도 10개가 만들어 진다.

## 개념 이해

---

### 트랜잭션 사용법

`commit` : 데이터 변경 쿼리를 실행하고 데이터베이스에 그 결과를 반영

`rollback` : 결과를 되돌림

`commit` 을 호출하기 전까지는 임시로 데이터를 저장

따라서 해당 트랜잭션을 시작한 세션에게만 변경데이터가 보이고 다른 세션에게는 변경데이터가 보이지 않는다.

<img width="634" alt="스크린샷 2023-11-07 13 15 25" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/4f355e73-861d-4cf2-b4c8-bc0549be5a36">


세션1, 세션2 둘다 가운데 있는 기본 테이블을 조회하면 해당 데이터가 그대로 조회된다.

<img width="636" alt="스크린샷 2023-11-07 13 15 30" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/360168ff-edd6-4e4b-9a0f-3503bf5f19e4">

세션1은 트랜잭션을 시작하고 신규 회원1, 신규회원 2를 DB에 추가 했다.

아직 커밋은 하지 않은 상태이다.

새로운 데이터는 임시 상태로 저장된다.

새션1은 `select` 쿼리를 실행해서 본인이 입력한 신규 회원1, 신규 회원 2를 조회할 수 있다.

세션2는 `select` 쿼리를 실행해도 신규 회원들을 조회할 수 없다. 세션1이 아직 커밋하지 않았기 때문이다.

### 커밋하지 않은 데이터를 다른 곳에서 조회할 수 있으면 어떤 문제가 발생할까?

커밋하지 않는 데이터가 보인다면, 세션2는 데이터를 조회했을 때 신규 회원1,2가 보일것이다.

따라서 신규 회원1, 신규회원 2가 있다고 가정하고 어떤 로직을 수행할 수 있다.

그런데 세션1이 롤백을 수행하면 신규 회원1, 신규회원 2의 데이터가 사라지게 된다. 따라서 데이터 정합성에 큰 문제가 발생한다.

세션2 에서 세션1이 아직 커밋하지 않은 변경 데이터가 보인다면 세션1이 롤백했을 때 심각한 문제가 발생할 수 잇다. 따라서 커밋 전의 데이터는 다른 세션에서 보이지 않는다.

<img width="645" alt="스크린샷 2023-11-07 13 40 59" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/ea23abf6-06f8-4903-b038-88e7e982cace">


세션1이 신규 데이터를 추가한 후에 `commit` 을 호출했다.

`commit` 으로 새로운 데이터가 실제 데이터베이스에 반영된다. 데이터의 상태도 임시 → 완료로 변경 되었다.

이제 다른 세션에서도 회원 테이블을 조회하면 신규 회원들을 확인할 수 있다.

### 세션1 신규 데이터 추가 후 rollback

<img width="635" alt="스크린샷 2023-11-07 13 42 27" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/581648bc-1b89-4602-b239-2d46fffcbf87">


세션 1이 신규 데이터를 추가한 후에 `commit` 대신에 `rollback` 을 호출 했다.

세션1 이 데이터베이스에 반영한 모든 데이터가 처음 상태로 복구된다.

수정하거나 삭제한 데이터도 `rollback` 을 호출하면 모두 트랜잭션을 시작하기 직전의 상태로 복구된다.

## 자동 커밋, 수동 커밋

예제에서 사용되는 스키마

```sql
drop table member if exists;
create table member(
	member_id varchar(10),
	money integer not null default 0,
	primary key (member_id)
)
```

### 자동 커밋

자동 커밋으로 설정하면 각각의 쿼리 실행 직후에 자동으로 커밋을 호출한다.

다라서 커밋이나 롤백을 직접 호출하지 않아도 되는 편리함이 있다.

하지만 쿼리를 하나하나 실행할 때 마다 자동으로 커밋이 되어버리기 때문에 원하는 트랜잭션 기능을 제대로 사용할 수 없다.

```sql
set autocommit true;
insert into member(member_id,money) values ('data1',10000);
insert into member(member_id,money) values ('data2',20000);
```

따라서 `commit`, `rollback` 을 직접 호출하면서 트랜잭션 기능을 제대로 수행하려면 자동 커밋을 끄고 수동 커밋을 사용해야 한다.

### 수동 커밋 설정

```sql
set autocommit false;
insert into member(member_id,money) values ('data1',10000);
insert into member(member_id,money) values ('data2',20000);
commit; // 수동 커밋
```

보통 자동 커밋 모드가 기본으로 설정된 경우가 많기 때문에, 수동 커밋 모드로 설정하는 것을 트랜잭션 시작 한다고 표현할 수 있다.

## 트랜잭션 실습 - 적용

### MemberServiceV1

```sql
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV1 {

    private final MemberRepositoryV1 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(fromId,fromMember.getMoney()-money);
        validation(toMember);
        memberRepository.update(toId,toMember.getMoney()+money);

    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
```

`fromID` 의 회원을 조회해서 `toId` 의 회원에게 `money` 만큼의 돈을 계좌이체 하는 로직이다.

- `fromId` 회원의 돈을 `money` 만큼 감소한다.
- `toId` 회원의 돈을 `money` 만큼 증가한다.

예외 상황을 테스트해보기 위해 `toId` 가 `"ex"` 인경우 예외 발생

### MemberServiceV1Test

```sql
package hello.jdbc.service;

import hello.jdbc.Connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.Connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberServiceV1Test {

    public static final String MEMBER_A = "memberA";
    public static final String MEMBER_B = "memberB";
    public static final String MEMBER_EX = "ex";

    private MemberRepositoryV1 memberRepository;
    private MemberServiceV1 memberService;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
        memberRepository = new MemberRepositoryV1(dataSource);
        memberService = new MemberServiceV1(memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete(MEMBER_A);
        memberRepository.delete(MEMBER_B);
        memberRepository.delete(MEMBER_EX);
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {

        Member memberA = new Member(MEMBER_A,10000);
        Member memberB = new Member(MEMBER_B,10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {

        Member memberA = new Member(MEMBER_A,10000);
        Member memberEx = new Member(MEMBER_EX,10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        assertThatThrownBy(() -> memberService.accountTransfer(memberA.getMemberId(),memberEx.getMemberId(),2000)).isInstanceOf(IllegalStateException.class);

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);

    }

}
```

## 적용 2 - 문제 해결

Application 에서 Transaction 을 어떤 계층에 걸어야 할까?

<img width="638" alt="스크린샷 2023-11-07 14 23 36" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/e23dca40-a35c-4005-b259-d14c5b486c33">

트랜잭션은 비즈니스 로직이 있는 서비스 계층에서 시작해야 한다.

비즈니스 로직이 잘못되면 해당 비즈니스 로직으로 인해 문제가 되는 부분을 함께 롤백해야 하기 때문이다.

그런데 트랜잭션을 시작하려면 Connection 이 필요하다. 결국 서비스 계층에서 커넥션을 만들고, 트랜잭션 커밋 이후에 커넥션을 종료해야 한다.

Application 에서 DB 트랜잭션을 사용하려면 트랜잭션을 사용하는 동안 같은 Connection 을 유지해야 한다.

<img width="637" alt="스크린샷 2023-11-07 14 23 41" src="https://github.com/shinywoon/Deep_Dive_Spring/assets/100909578/2b89f2c5-44fb-4d0a-99ae-e86c6687c793">

### 1. 커넥션을 파라미터로 전달 - 가장 단순한 방법

MemberRepositoryV2

```sql
package hello.jdbc.repository;

import hello.jdbc.Connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
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

    public Member findById(Connection con,String memberId) throws SQLException {

        String sql = "select * from member where member_id=?";

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
            //close(con,pstmt,rs);
            JdbcUtils.closeStatement(pstmt);
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

    public void update(Connection con,String memberId, int money) throws SQLException {

        String sql = "update member set money=? where member_id=?";

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
            //close(con,pstmt,null);
            JdbcUtils.closeStatement(pstmt);
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

`MemberRepositoryV2` 는 기존 코드와 같고 커넥션 유지가 필요한 다음 두 메서드가 추가 되었다.

`findById(Connection con, String memberId)`

`update(Connection con, String memberId, int money)`

두 메서드 주의점

con = getConnection() 코드가 있으면 안된다 : 전달 된 Connection 사용

connection 을 이어서 사용해야 하므로 서비스 로직 종료전 Connection 을 닫으면 안된다.

### MemberServiceV2

```sql
package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV1;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();

        try{

            con.setAutoCommit(false);

            bizLogic(con,fromId, toId, money);

            con.commit();

        }catch (Exception e){
            con.rollback();
        }finally {
            release(con);
        }

    }

    private void bizLogic(Connection con,String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(fromId);
        Member toMember = memberRepository.findById(toId);

        memberRepository.update(con,fromId,fromMember.getMoney()- money);
        validation(toMember);
        memberRepository.update(con,toId,toMember.getMoney()+ money);
    }

    private void release(Connection con) {
        if (con != null){
            try {
                con.setAutoCommit(true);
                con.close();
            }catch (Exception e){
                log.info("error",e);
            }
        }
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")){
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }

}
```

`Connection con = dataSource.getConnection();` : 트랜잭션을 시작하려면 커넥션이 필요하다.

`con.setAutoCommit(false);` : 트랜잭션 시작 = 수동 모드

`bizLogic(con,fromId,toId,money)` 

- 트랜잭션이 시작된 커넥션을 전달하면서 비즈니스 로직 수행

`con.commit()` : 비즈니스 로직이 정상 수행되면 커밋

`con.rollback()` : `catch(Ex){...}` 를 사용해서 비즈니스 로직 수행 도중에 예외가 발생하면 트랜잭션을 롤백

`release(con);` 

- `finally {..}` 를 사용해서 커넥션을 모두 사용하고 나면 안전하게 종료한다. 현재  수동 커밋 모드로 동작하기 때문에 풀에 돌려주기 전에 기본 값인 자동 커밋 모드로 변경하는 것이 안전하다.

### MemberServiceV2Test

```sql
package hello.jdbc.service;

import hello.jdbc.Connection.ConnectionConst;
import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static hello.jdbc.Connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;

class MemberServiceV2Test {

    private MemberRepositoryV2 memberRepository;
    private MemberServiceV2 memberService;

    @BeforeEach
    void before(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
        memberRepository = new MemberRepositoryV2(dataSource);
        memberService = new MemberServiceV2(dataSource,memberRepository);
    }

    @AfterEach
    void after() throws SQLException {
        memberRepository.delete("memberA");
        memberRepository.delete("memberB");
        memberRepository.delete("ex");
    }

    @Test
    @DisplayName("정상 이체")
    void accountTransfer() throws SQLException {

        Member memberA = new Member("memberA",10000);
        Member memberB = new Member("memberB",10000);
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        memberService.accountTransfer(memberA.getMemberId(),memberB.getMemberId(),2000);

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberB = memberRepository.findById(memberB.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(8000);
        assertThat(findMemberB.getMoney()).isEqualTo(12000);

    }

    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {

        Member memberA = new Member("memberA",10000);
        Member memberEx = new Member("ex",10000);
        memberRepository.save(memberA);
        memberRepository.save(memberEx);

        assertThatThrownBy(()->memberService.accountTransfer(memberA.getMemberId(),memberEx.getMemberId(),2000)).isInstanceOf(IllegalStateException.class);

        Member findMemberA = memberRepository.findById(memberA.getMemberId());
        Member findMemberEx = memberRepository.findById(memberEx.getMemberId());

        assertThat(findMemberA.getMoney()).isEqualTo(10000);
        assertThat(findMemberEx.getMoney()).isEqualTo(10000);

    }

}
```

## 남은 문제

Application 에서 DB 트랜잭션을 적용하려면 서비스 계층이 매우 지저분해지고, 생각보다 매우 복잡한 코드를 요구한다.

추가로 커넥션을 유지하도록 코드를 변경하는 것도 쉬운일은 아니다.

하나씩 해결해 나가자
