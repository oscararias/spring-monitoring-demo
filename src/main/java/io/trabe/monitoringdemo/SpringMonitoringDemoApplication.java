package io.trabe.monitoringdemo;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringMonitoringDemoApplication {

	@Bean
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(@Value("${server.region}")String region) {
		return (registry) -> registry.config()
				.commonTags("region", region)
				.meterFilter(MeterFilter.ignoreTags("too.much.information"))
				.meterFilter(MeterFilter.denyNameStartsWith("jvm"));
	}

	@Bean
	public MeterBinder queueSize(MyQueue queue) {
		return (registry) -> Gauge.builder("myqueue.size", queue::size).register(registry);
	}

	@Bean
	public TimedAspect timedAspect(MeterRegistry registry) {
		return new TimedAspect(registry);
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringMonitoringDemoApplication.class, args);
	}

}
