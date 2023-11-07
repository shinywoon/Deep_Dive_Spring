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