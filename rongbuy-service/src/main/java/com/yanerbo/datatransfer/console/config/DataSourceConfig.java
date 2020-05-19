package com.yanerbo.datatransfer.console.config;

import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.DruidPasswordCallback;

/**
 * 数据源配置（包含源数据库和目标数据库）
 * @author jihaibo
 *
 */
@Configuration
public class DataSourceConfig {	
	
	
	/**
	 * dataSourcePoolProperties配置
	 * @return
	 */
	@Bean(name="dataSourcePoolProperties")
    @ConfigurationProperties(prefix = "datatrans.datasource.target.druid")
    public Properties dataSourcePoolProperties() {
        return new Properties();
    }
	@Bean(name="dataSource")
	@ConfigurationProperties(prefix = "datatrans.datasource.target")
    public DataSource dataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setConnectProperties(addDruidPrefix(dataSourcePoolProperties()));
		druidDataSource.setPasswordCallback(new DbPasswordCallback());
        return druidDataSource;
    }
	
	@Bean
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(dataSource());
	}

	@Bean
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}
	
	@SuppressWarnings("rawtypes")
	private Properties addDruidPrefix(Properties properties) {
		Properties druidProperties = new Properties();
		for(Map.Entry entry: properties.entrySet()) {
			druidProperties.put(entry.getKey().toString(), entry.getValue().toString());
		}
		return druidProperties;
	}
	/**
	 * 加密密码
	 * @author 274818
	 *
	 */
	class DbPasswordCallback extends DruidPasswordCallback{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		@Override
	    public void setProperties(Properties properties) {
			super.setProperties(properties);
	        try {
				String password = (String) properties.get("password");
		        String publickey = (String) properties.get("publickey");
		        //如果没有公钥则不用加密
	        	if(publickey != null) {
	        		String dbpassword = ConfigTools.decrypt(publickey, password);
	 	            setPassword(dbpassword.toCharArray());
	        	}
	        } catch (Exception e) {
	        	throw new RuntimeException("数据源加载失败", e);
	        }
	    }
	}
}
