package com.yanerbo.datatransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import com.alibaba.druid.support.http.StatViewServlet;
@SpringBootApplication
public class DataTransferApplication {
	
	
	/**
	 * 
	 * http://localhost:15725/druid/index.html
	 * 数据源监控
	 * @return
	 */
	@Bean
	public ServletRegistrationBean<StatViewServlet> druidStatViewServlet() {
		ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(),  "/druid/*");
		//registrationBean.addInitParameter("allow", "127.0.0.1");// IP白名单 (没有配置或者为空，则允许所有访问)
		//registrationBean.addInitParameter("deny", "");// IP黑名单 (存在共同时，deny优先于allow)
		registrationBean.addInitParameter("loginUsername", "root");
		registrationBean.addInitParameter("loginPassword", "root");
		registrationBean.addInitParameter("resetEnable", "false");
		return registrationBean;
	}
	
	

	public static void main(String[] args) {
		SpringApplication.run(DataTransferApplication.class, args);
	}

}
