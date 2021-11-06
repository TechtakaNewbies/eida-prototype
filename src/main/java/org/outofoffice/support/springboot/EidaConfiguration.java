package org.outofoffice.support.springboot;

import org.outofoffice.eidaprototype.lib.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan
public class EidaConfiguration {

    @Bean
    protected EidaDdlGenerator ddlGenerator() {
        return new EidaDdlGenerator();
    }

    @Bean
    protected EidaDllGenerator dllGenerator() {
        return new EidaDllGenerator();
    }

    @Bean
    protected EidaDmlGenerator dmlGenerator() {
        return new EidaDmlGenerator();
    }

    @Bean
    protected EidaManagerClient managerClient() {
        return new EidaManagerClientImpl(dllGenerator(), "localhost:1234");
    }

    @Bean
    protected EidaShardClient shardClient() {
        return new EidaShardClientImpl(dmlGenerator());
    }

    @Bean
    protected EidaSerializer eidaserializer() {
        return new EidaSerializerImpl();
    }

}
