package com.annakhuseinova.apachekafka.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Profile("local")
public class AutoCreateConfiguration {

    /*
    * But this type of configuration is not preferable in production
    * */
    @Bean
    public NewTopic libraryEvents(){
        return TopicBuilder.name("libraryEvents").partitions(3).replicas(3).build();
    }


}
