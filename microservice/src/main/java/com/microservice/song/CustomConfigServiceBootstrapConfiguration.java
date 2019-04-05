//package com.microservice.song;
//
//import io.fabric8.kubernetes.client.KubernetesClient;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.config.client.ConfigClientProperties;
//import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.env.Environment;
//
//import java.util.Base64;
//import java.util.Optional;
//
//@Slf4j
//@Configuration
//@Profile("kubernetes")
//public class CustomConfigServiceBootstrapConfiguration {
//
//    @Autowired
//    private KubernetesClient client;
//
//    private static final String
//            K8S_NAMESPACE = "k8s-namespace",
//            K8S_SECRET = "k8s-secret";
//
//    @Bean
//    public ConfigServicePropertySourceLocator configServicePropertySourceLocator(
//            ConfigClientProperties clientProperties,
//            Environment env) {
//
//        Base64.Decoder decoder = Base64.getDecoder();
//
//        Optional.ofNullable(client.secrets())
//                .map(secrets -> secrets
//                        .inNamespace(env.getProperty(K8S_NAMESPACE))
//                        .withName(env.getProperty(K8S_SECRET))
//                        .get())
//                .map(secret -> secret.getData().get("config-server"))
//                .map(decoder::decode)
//                .map(String::new)
//                .ifPresent(configServerUri -> {
//                    log.info("@lookup: {}", configServerUri);
//                    clientProperties.setUri(new String[]{configServerUri});
//                });
//        return new ConfigServicePropertySourceLocator(clientProperties);
//
//    }
//
//}
