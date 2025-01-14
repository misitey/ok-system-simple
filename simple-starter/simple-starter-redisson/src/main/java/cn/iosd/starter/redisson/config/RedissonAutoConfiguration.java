package cn.iosd.starter.redisson.config;

import cn.iosd.starter.redisson.properties.RedissonProperties;
import cn.iosd.starter.redisson.service.RedissonManager;
import cn.iosd.starter.redisson.service.RedissonService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Redisson自动化配置
 *
 * @author ok1996
 */
@Slf4j
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnProperty(prefix = "simple.redisson", name = "enabled", havingValue = "true")
public class RedissonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @Order(value = 2)
    public RedissonService redissonLock(RedissonManager redissonManager) {
        RedissonService redissonLock = new RedissonService(redissonManager);
        return redissonLock;
    }

    @Bean
    @ConditionalOnMissingBean
    @Order(value = 1)
    public RedissonManager redissonManager(RedissonProperties redissonProperties) {
        RedissonManager redissonManager =
                new RedissonManager(redissonProperties);
        log.info("[RedissonManager]组装完毕,当前连接方式:" + redissonProperties.getType() +
                ",连接地址:" + redissonProperties.getAddress());
        return redissonManager;
    }
}

