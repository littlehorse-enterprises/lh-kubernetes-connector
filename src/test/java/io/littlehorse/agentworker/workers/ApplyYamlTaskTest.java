package io.littlehorse.agentworker.workers;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.ListMeta;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.impl.NamespaceableResourceAdapter;
import io.littlehorse.sdk.common.exception.LHTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class ApplyYamlTaskTest {
    @Mock
    private KubernetesClient kubernetesClient;

    private ApplyYamlTask subject;

    @BeforeEach
    void init() {
        subject  = new ApplyYamlTask(kubernetesClient);
    }

    @Test
    void createOrUpdateResource_shouldCreateResourceSuccessfullyWhenNoK8SExceptionIsThrown() {
        HasMetadata somePod = new Pod();
        somePod.setMetadata(new ObjectMeta());
        NamespaceableResourceAdapter resourceAdapter = mock(NamespaceableResourceAdapter.class);
        String resourceYaml = """
                apiVersion: v1
                kind: Pod
                metadata:
                  name: ubuntu
                spec:
                  containers:
                    - image: ubuntu:24.04
                      name: ubuntu
                      command: ["/bin/sh", "-c"]
                      args: ["tail -f /dev/null"]
                """;

        when(kubernetesClient.resource(resourceYaml)).thenReturn(resourceAdapter);
        when(resourceAdapter.create()).thenReturn(somePod);

        assertDoesNotThrow(() -> subject.createOrUpdateResource(resourceYaml));

        verify(kubernetesClient).resource(resourceYaml);
        verify(resourceAdapter).create();
        verify(resourceAdapter, never()).update();
    }

    @Test
    void createOrUpdateResource_shouldUpdateResourceSuccessfullyWhenAlreadyExistsK8SExceptionIsThrownByK8sClient() {
        HasMetadata someDeployment = new Deployment();
        someDeployment.setMetadata(new ObjectMeta());
        NamespaceableResourceAdapter resourceAdapter = mock(NamespaceableResourceAdapter.class);
        String resourceYaml = """
                apiVersion: apps/v1
                kind: Deployment
                metadata:
                  name: someName
                  namespace: someNamespace
                spec:
                  replicas: 1
                  selector:
                    matchLabels:
                      app: someName
                  template:
                    metadata:
                      labels:
                        app: someName
                    spec:
                      serviceAccountName: someName
                      containers:
                        - name: someName
                          image: someImage:someVersion
                          imagePullPolicy: IfNotPresent
                          livenessProbe:
                            httpGet:
                              path: /health
                              port: 8091
                """;
        Status exceptionStatus = new Status("v1", 409, new StatusDetails(), "Pod",
                "some-error-message", new ListMeta(), "AlreadyExists", "409");

        when(kubernetesClient.resource(resourceYaml)).thenReturn(resourceAdapter);
        when(resourceAdapter.create()).thenThrow(new KubernetesClientException(exceptionStatus));
        when(resourceAdapter.update()).thenReturn(someDeployment);

        assertDoesNotThrow(() -> subject.createOrUpdateResource(resourceYaml));

        verify(kubernetesClient, times(2)).resource(resourceYaml);
        verify(resourceAdapter).create();
        verify(resourceAdapter).update();
    }

    @Test
    void createOrUpdateResource_shouldThrowLHTaskExceptionWhenK8SExceptionIsThrownByK8sClient() {
        HasMetadata somePod = new Pod();
        somePod.setMetadata(new ObjectMeta());
        NamespaceableResourceAdapter resourceAdapter = mock(NamespaceableResourceAdapter.class);
        String resourceYaml = """
                apiVersion: v1
                kind: Pod
                metadata:
                  name: ubuntu
                spec:
                  containers:
                    - image: ubuntu:24.04
                      name: ubuntu
                      command: ["/bin/sh", "-c"]
                      args: ["tail -f /dev/null"]
                """;
        Status exceptionStatus = new Status("v1", 409, new StatusDetails(), "Pod",
                "some-error-message", new ListMeta(), "UnknownField", "409");


        when(kubernetesClient.resource(resourceYaml)).thenReturn(resourceAdapter);
        when(resourceAdapter.create()).thenThrow(new KubernetesClientException(exceptionStatus));

        LHTaskException thrownException = assertThrows(LHTaskException.class, () -> subject.createOrUpdateResource(resourceYaml));

        String expectedK8sExceptionName = "K8s Exception";

        assertEquals(expectedK8sExceptionName, thrownException.getName());

        verify(kubernetesClient).resource(resourceYaml);
        verify(resourceAdapter).create();
        verify(resourceAdapter, never()).update();
    }

    @Test
    void createOrUpdateResource_shouldThrowLHTaskExceptionWhenUnhandledExceptionIsThrownByK8sClient() {
        HasMetadata somePod = new Pod();
        somePod.setMetadata(new ObjectMeta());
        NamespaceableResourceAdapter resourceAdapter = mock(NamespaceableResourceAdapter.class);
        String resourceYaml = """
                apiVersion: v1
                kind: Pod
                metadata:
                  name: ubuntu
                spec:
                  containers:
                    - image: ubuntu:24.04
                      name: ubuntu
                      command: ["/bin/sh", "-c"]
                      args: ["tail -f /dev/null"]
                """;

        when(kubernetesClient.resource(resourceYaml)).thenReturn(resourceAdapter);
        when(resourceAdapter.create()).thenThrow(new RuntimeException());

        LHTaskException thrownException = assertThrows(LHTaskException.class, () -> subject.createOrUpdateResource(resourceYaml));

        String expectedK8sExceptionName = "Unknown Exception in Agent";

        assertEquals(expectedK8sExceptionName, thrownException.getName());

        verify(kubernetesClient).resource(resourceYaml);
        verify(resourceAdapter).create();
        verify(resourceAdapter, never()).update();
    }

}