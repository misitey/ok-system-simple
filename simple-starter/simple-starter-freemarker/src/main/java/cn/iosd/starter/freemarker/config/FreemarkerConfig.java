package cn.iosd.starter.freemarker.config;

import freemarker.template.Configuration;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


/**
 * @author ok1996
 */
@Component
public class FreemarkerConfig {

    @Resource
    private Configuration configuration;

    @PostConstruct
    public void setSharedVariable() {
        configuration.setNumberFormat("#");
    }

}