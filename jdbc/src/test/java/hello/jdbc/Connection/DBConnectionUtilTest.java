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

            Connection con = DBConnectionUtil.getConnection();
            log.info("connection = {} , imple = {}" , con,con.getClass());
            Assertions.assertThat(con).isNotNull();
        }catch (SQLException e){
            log.error("error :",e);
        }


    }

}