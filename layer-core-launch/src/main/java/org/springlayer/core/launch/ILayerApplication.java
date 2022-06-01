package org.springlayer.core.launch;

import org.springlayer.core.launch.service.LauncherService;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目启动器，自定义环境问题
 *
 * @author houzhi
 */
public class ILayerApplication {

    /**
     * Create an application context
     * java -jar app.jar --spring.profiles.active=prod --server.port=2333
     *
     * @param appName application name
     * @param source  The sources
     * @return an application context created from the current state
     */
    public static ConfigurableApplicationContext run(String appName, Class source, String... args) {
        SpringApplicationBuilder builder = createSpringApplicationBuilder(appName, source, args);
        return builder.run(args);
    }

    public static SpringApplicationBuilder createSpringApplicationBuilder(String appName, Class source, String... args) {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(source);

        Assert.hasText(appName, "[appName]服务名不能为空");

        String startJarPath = ILayerApplication.class.getResource("/").getPath().split("!")[0];
        System.out.println(String.format("----启动中，jar地址:[%s]----", startJarPath));

        Properties props = System.getProperties();
        props.setProperty("spring.application.name", appName);
        List<LauncherService> launcherList = new ArrayList<>();
        ServiceLoader.load(LauncherService.class).forEach(launcherList::add);
        launcherList.stream().sorted(Comparator.comparing(LauncherService::getOrder)).collect(Collectors.toList())
                .forEach(launcherService -> launcherService.launcher(builder, appName));

        return builder;
    }
}