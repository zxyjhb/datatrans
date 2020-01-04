package com.yanerbo.datatransfer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import com.alibaba.druid.support.http.StatViewServlet;

/**
 * 
 * 这里需要剔除DataSourceAutoConfiguration类，因为存在多个数据源
 * springboot 2.0版本以上就没有这个了
 * spring 启动类
 * @author 274818
 *
 */
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class Application {
	
	/***
	 * springboot的全局配置文件路径变量名
	 */
	protected static String SPRING_CONFIG_PATH = "spring.config.location";
	/**
	 * springboot的全局配置文件路径变量名
	 */
	protected static String GLOBAL_CONFIG_PATH = "global.config.path";
	/***
	 * springboot配置文件名，不需要路径 ，但要包括扩展名
	 */
	protected static String SPRING_CONFIG_FILES = "system-config.properties";
	/**
	 * 
	 * http://localhost:port/druid/index.html
	 * 数据源监控
	 * @return
	 */
	@Bean
	public ServletRegistrationBean druidStatViewServlet() {
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new StatViewServlet(),  "/druid/*");
		//registrationBean.addInitParameter("allow", "127.0.0.1");// IP白名单 (没有配置或者为空，则允许所有访问)
		//registrationBean.addInitParameter("deny", "");// IP黑名单 (存在共同时，deny优先于allow)
		registrationBean.addInitParameter("loginUsername", "root");
		registrationBean.addInitParameter("loginPassword", "root");
		registrationBean.addInitParameter("resetEnable", "false");
		return registrationBean;
	}

	public static void main(String[] args) {
		
		String configPath = System.getProperty(GLOBAL_CONFIG_PATH);
		if (StringUtils.isEmpty(configPath)) {
			throw new RuntimeException("you must set environment " + GLOBAL_CONFIG_PATH + " before run!");
		}
		new SpringApplicationBuilder(Application.class)
				.properties(SPRING_CONFIG_PATH + "=" + configPath + SPRING_CONFIG_FILES).run(args);
	}

}
