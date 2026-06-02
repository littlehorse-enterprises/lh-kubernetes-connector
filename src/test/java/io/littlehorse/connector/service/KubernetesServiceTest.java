package io.littlehorse.connector.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.*;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kubernetes.client.WithKubernetesTestServer;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import java.util.Optional;
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

    private Secret buildSecret(String namespace, String name) {

        SecretBuilder secretBuilder =
                new SecretBuilder().editMetadata().withName(name).endMetadata();
        if (namespace != null) {
            secretBuilder.editMetadata().withNamespace(namespace).endMetadata();
        }
        return secretBuilder.build();
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
    void shouldDeleteSecretInDefaultNamespace() {
        String expectedName = UUID.randomUUID().toString();
        Secret inputSecret = new SecretBuilder()
                .editMetadata()
                .withName(expectedName)
                .endMetadata()
                .build();

        service.apply(inputSecret);
        assertNotNull(client.secrets().withName(expectedName).get(), "Secret not found");

        service.delete("v1", "Secret", null, expectedName);

        Secret result = client.secrets().withName(expectedName).get();
        assertNull(result, "Secret should be deleted");
    }

    @Test
    void shouldDeleteSecretInNamespace() {
        String expectedName = UUID.randomUUID().toString();
        String expectedNamespace = UUID.randomUUID().toString();
        Secret inputSecret = new SecretBuilder()
                .editMetadata()
                .withName(expectedName)
                .withNamespace(expectedNamespace)
                .endMetadata()
                .build();

        service.apply(inputSecret);
        assertNotNull(
                client.secrets()
                        .inNamespace(expectedNamespace)
                        .withName(expectedName)
                        .get(),
                "Secret not found");

        service.delete("v1", "Secret", expectedNamespace, expectedName);

        Secret result = client.secrets()
                .inNamespace(expectedNamespace)
                .withName(expectedName)
                .get();
        assertNull(result, "Secret should be deleted");
    }

    @Test
    void shouldDeleteDeploymentInDefaultNamespace() {
        String expectedName = UUID.randomUUID().toString();
        service.apply(buildYaml(null, expectedName));
        assertNotNull(
                client.apps().deployments().withName(expectedName).get(), "Deployment not found");

        service.delete("apps/v1", "Deployment", null, expectedName);

        Deployment result = client.apps().deployments().withName(expectedName).get();
        assertNull(result, "Deployment should be deleted");
    }

    @Test
    void shouldGetResourceInDefaultNamespace() {
        String expectedName = UUID.randomUUID().toString();
        service.apply(buildYaml(null, expectedName));

        Optional<GenericKubernetesResource> result =
                service.get("apps/v1", "Deployment", null, expectedName);

        assertTrue(result.isPresent());
        assertEquals(expectedName, result.get().getMetadata().getName());
    }

    @Test
    void shouldGetResourceInNamespace() {
        String expectedNamespace = UUID.randomUUID().toString();
        String expectedName = UUID.randomUUID().toString();
        service.apply(buildYaml(expectedNamespace, expectedName));

        Optional<GenericKubernetesResource> result =
                service.get("apps/v1", "Deployment", expectedNamespace, expectedName);

        assertTrue(result.isPresent());
        assertEquals(expectedName, result.get().getMetadata().getName());
        assertEquals(expectedNamespace, result.get().getMetadata().getNamespace());
    }

    @Test
    void shouldReturnEmptyWhenResourceDoesNotExist() {
        Optional<GenericKubernetesResource> result =
                service.get("apps/v1", "Deployment", null, "non-existent-resource");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldSkipSecondDeleteCallOnSecret() {
        final String name = UUID.randomUUID().toString();
        service.apply(buildSecret(null, name));

        service.delete("v1", "Secret", null, name);
        assertNull(client.secrets().withName(name).get(), "Secret should be deleted");

        assertDoesNotThrow(() -> service.delete("v1", "Secret", null, name));
    }
}
