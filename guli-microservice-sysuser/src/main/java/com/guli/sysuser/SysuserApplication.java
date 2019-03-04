package com.guli.sysuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author helen
 * @since 2019/3/1
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SysuserApplication {

	public static void main(String[] args) {
		SpringApplication.run(SysuserApplication.class, args);
	}
}
