package com.yanerbo.datatransfer.config;

import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 数据源配置（包含源数据库和目标数据库）
 * @author jihaibo
 *
 */
@Configuration
public class DataSourceConfig {
	
	
	/**
	 * druid配置
	 * @return
	 */
	@Bean(name="sourceDataSourcePoolProperties")
    @ConfigurationProperties(prefix = "datatrans.datasource.source.druid")
    public Properties sourceDataSourcePoolProperties() {
        return new Properties();
    }
	/**
	 * druid配置
	 * @return
	 */
	@Bean(name="targetDataSourcePoolProperties")
    @ConfigurationProperties(prefix = "datatrans.datasource.target.druid")
    public Properties targetDataSourcePoolProperties() {
        return new Properties();
    }
	
	@Bean(name="sourceDataSource")
	@ConfigurationProperties(prefix = "datatrans.datasource.source")
    public DataSource sourceDataSource() {
		
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setConnectProperties(addDruidPrefix(sourceDataSourcePoolProperties()));
        return druidDataSource;
    }
	
	@Bean(name="targetDataSource")
	@ConfigurationProperties(prefix = "datatrans.datasource.target")
    public DataSource targetDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setConnectProperties(addDruidPrefix(targetDataSourcePoolProperties()));
        return druidDataSource;
    }
	
	
	@Bean
	public JdbcTemplate sourceJdbcTemplate() {
		return new JdbcTemplate(sourceDataSource());
	}
	
	@Bean
	public JdbcTemplate targetJdbcTemplate() {
		return new JdbcTemplate(targetDataSource());
	}
	
	@Bean
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(targetDataSource());
	}
	
	
	@SuppressWarnings("rawtypes")
	private Properties addDruidPrefix(Properties properties) {
	
		Properties druidProperties = new Properties();
		for(Map.Entry entry: properties.entrySet()) {
			druidProperties.put(entry.getKey().toString(), entry.getValue().toString());
		}
		return druidProperties;
	}
}
