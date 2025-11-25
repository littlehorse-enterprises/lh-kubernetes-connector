package io.littlehorse.connector.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import java.util.Map;

@WithKubernetesTestServer
@QuarkusTest
class KubernetesServiceTest {

    final String inputYaml =
            """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              name: nginx-deployment
            spec:
              selector:
                matchLabels:
                  app: nginx
              replicas: 3
              template:
                metadata:
                  labels:
                    app: nginx
                spec:
                  containers:
                  - name: nginx
                    image: nginx:latest
                    ports:
                    - containerPort: 80
            """;

    @Inject
    KubernetesClient client;

    @Inject
    KubernetesService service;

    @Test
    void shouldApplyYaml() {
        service.apply(inputYaml);

        Deployment result =
                client.apps().deployments().withName("nginx-deployment").get();

        assertNotNull(result, "Deployment not found");
        assertEquals(result.getSpec().getReplicas(), 3);
        assertThat(result.getSpec().getSelector().getMatchLabels(), hasEntry("app", "nginx"));
    }

    @Test
    void shouldCreateSecret() {
        String expectedName = "my-secret";
        Secret inputSecret = new SecretBuilder()
                .editMetadata()
                .withName(expectedName)
                .endMetadata()
                .build();

        service.save(inputSecret);

        Secret result = client.secrets().withName(expectedName).get();

        assertNotNull(result, "Secret not found");
    }

    @Test
    void shouldShouldGetStatus() {
        String expectedMessage = "expected message";
        String expectedName = "pod";
        String expectedNamespace = "test";

        Pod pod = new PodBuilder()
                .withNewMetadata()
                .withName(expectedName)
                .withNamespace(expectedNamespace)
                .and()
                .withNewStatus()
                .withMessage(expectedMessage)
                .endStatus()
                .build();
        client.pods().resource(pod).create();

        Object status = service.status("v1", "Pod", expectedNamespace, expectedName);

        assertThat(status, is(Map.of("message", expectedMessage)));
    }
}
