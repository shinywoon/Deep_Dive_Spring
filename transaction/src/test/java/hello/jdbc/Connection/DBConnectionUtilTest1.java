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