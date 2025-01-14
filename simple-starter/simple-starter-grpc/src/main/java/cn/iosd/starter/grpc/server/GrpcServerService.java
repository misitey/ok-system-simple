package cn.iosd.starter.grpc.server;

import cn.iosd.starter.grpc.server.annotation.GrpcService;
import cn.iosd.starter.grpc.server.properties.GrpcServerProperties;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


/**
 * 启动服务端
 * <p>
 * 服务端口读取配置项：pure.grpc.server.port
 * <p>
 * 扫描使用GrpcService注解的Bean->添加到io.grpc.ServerBuilder
 *
 * @author ok1996
 */
@Slf4j
@Service
public class GrpcServerService implements InitializingBean, ApplicationContextAware {

    @Autowired
    private GrpcServerProperties grpcServerProperties;

    private Server server;

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws IOException {
        GrpcServerService server = new GrpcServerService();
        server.start(grpcServerProperties.getPort());
        log.info("GrpcServer start port:{}", grpcServerProperties.getPort());
    }

    private void start(final int port) throws IOException {
        ServerBuilder serverBuilder = ServerBuilder.forPort(port);
        if (context != null) {
            Map<String, Object> beansWithAnnotationMap = context.getBeansWithAnnotation(GrpcService.class);
            beansWithAnnotationMap.forEach((key, value) -> serverBuilder.addService(
                    (BindableService) value
            ));
        }

        this.server = serverBuilder.build();
        this.server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.server.shutdown();
            log.info("GrpcServer shut down");
        }));
    }

}
