package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

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