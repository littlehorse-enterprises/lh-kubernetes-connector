package io.littlehorse.connector.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.littlehorse.connector.exception.NotFoundException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

@WithKubernetesTestServer
@QuarkusTest
class KubernetesServiceTest {

    final String inputYaml = """
                    apiVersion: apps/v1
                    kind: Deployment
                    metadata:
                      name: %s
                      %s
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

    private String buildYaml(String namespace, String name) {
        return inputYaml.formatted(name, namespace == null ? "" : "namespace: " + namespace);
    }

    @Test
    void shouldApplyYamlInDefaultNamespace() {
        String expectedName = UUID.randomUUID().toString();
        service.apply(buildYaml(null, expectedName));

        Deployment result = client.apps().deployments().withName(expectedName).get();

        assertNotNull(result, "Deployment not found");
        assertEquals(result.getSpec().getReplicas(), 3);
        assertEquals(result.getMetadata().getNamespace(), "default");
        assertThat(result.getSpec().getSelector().getMatchLabels(), hasEntry("app", "nginx"));
    }

    @Test
    void shouldApplyYamlInNamespace() {
        String expectedNamespace = UUID.randomUUID().toString();
        String expectedName = UUID.randomUUID().toString();
        service.apply(buildYaml(expectedNamespace, expectedName));

        Deployment result = client.apps()
                .deployments()
                .inNamespace(expectedNamespace)
                .withName(expectedName)
                .get();

        assertNotNull(result, "Deployment not found");
        assertEquals(result.getSpec().getReplicas(), 3);
        assertEquals(result.getMetadata().getNamespace(), expectedNamespace);
        assertThat(result.getSpec().getSelector().getMatchLabels(), hasEntry("app", "nginx"));
    }

    @Test
    void shouldCreateSecretInDefaultNamespace() {
        String expectedName = UUID.randomUUID().toString();
        Secret inputSecret = new SecretBuilder()
                .editMetadata()
                .withName(expectedName)
                .endMetadata()
                .build();

        service.apply(inputSecret);

        Secret result = client.secrets().withName(expectedName).get();

        assertNotNull(result, "Secret not found");
        assertEquals("default", result.getMetadata().getNamespace());
        assertEquals(expectedName, result.getMetadata().getName());
    }

    @Test
    void shouldCreateSecretInNamespace() {
        String expectedName = UUID.randomUUID().toString();
        String expectedNamespace = UUID.randomUUID().toString();
        Secret inputSecret = new SecretBuilder()
                .editMetadata()
                .withName(expectedName)
                .withNamespace(expectedNamespace)
                .endMetadata()
                .build();

        service.apply(inputSecret);

        Secret result = client.secrets()
                .inNamespace(expectedNamespace)
                .withName(expectedName)
                .get();

        assertNotNull(result, "Secret not found");
        assertEquals(expectedNamespace, result.getMetadata().getNamespace());
        assertEquals(expectedName, result.getMetadata().getName());
    }

    @Test
    void shouldGetStatusInDefaultNamespace() {
        String expectedMessage = UUID.randomUUID().toString();
        String expectedName = UUID.randomUUID().toString();

        Pod pod = new PodBuilder()
                .withNewMetadata()
                .withName(expectedName)
                .and()
                .withNewStatus()
                .withMessage(expectedMessage)
                .endStatus()
                .build();
        client.pods().resource(pod).create();

        Object status = service.status("v1", "Pod", null, expectedName);

        assertThat(status, is(Map.of("message", expectedMessage)));
    }

    @Test
    void shouldGetStatusInNamespace() {
        String expectedMessage = UUID.randomUUID().toString();
        String expectedName = UUID.randomUUID().toString();
        String expectedNamespace = UUID.randomUUID().toString();

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

    @Test
    void shouldThrowNotFoundException() {
        assertThrows(
                NotFoundException.class,
                () -> service.status("v1", "Pod", null, UUID.randomUUID().toString()));
    }
}
