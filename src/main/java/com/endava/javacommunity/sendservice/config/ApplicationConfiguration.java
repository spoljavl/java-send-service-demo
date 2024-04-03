package com.endava.javacommunity.sendservice.config;

import com.mongodb.reactivestreams.client.MongoClient;
import java.time.Duration;
import org.redisson.Redisson;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@EnableScheduling
public class ApplicationConfiguration {

  @Bean
  public RedissonReactiveClient redissonReactiveClient(@Value("${spring.data.redis.database}") int database,
      @Value("${spring.data.redis.host}") String host) {
    final Config redissonConfig = new Config();
    redissonConfig.useSingleServer()
        .setDatabase(database)
        .setAddress(host);

    return Redisson.create(redissonConfig).reactive();
  }

  @Bean
  public ReactiveMongoTemplate mongoTemplate(MongoClient mongoClient, @Value("${spring.data.mongodb.database}") String database) {
    return new ReactiveMongoTemplate(mongoClient, database);
  }

  @Bean
  public WebClient getWebClient() {
    final ConnectionProvider provider = ConnectionProvider.builder("fixed")
        .maxConnections(500)
        .maxIdleTime(Duration.ofSeconds(20))
        .maxLifeTime(Duration.ofSeconds(60))
        .pendingAcquireTimeout(Duration.ofSeconds(60))
        .build();

    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider)))
        .build();
  }

}
