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