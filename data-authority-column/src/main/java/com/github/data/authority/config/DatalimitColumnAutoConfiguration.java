package com.github.data.authority.config;

import com.github.data.authority.IDatalimitColumn;
import com.github.data.authority.aspectj.DatalimitColResourceAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenzhh
 */
@Configuration
public class DatalimitColumnAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DatalimitColResourceAspect datalimitColResourceAspect(IDatalimitColumn datalimitColumn) {
        return new DatalimitColResourceAspect(datalimitColumn);
    }

}
