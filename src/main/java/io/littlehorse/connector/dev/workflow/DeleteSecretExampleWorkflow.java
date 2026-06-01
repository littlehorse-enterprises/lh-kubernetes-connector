package io.littlehorse.connector.dev.workflow;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.arc.profile.IfBuildProfile;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/*
Run delete secret example workflow (requires secret to already exist in k8s, see SecretExampleWorkflow.java):

lhctl run kubernetes-connector-secret-delete-example name my-secret

List secrets:

kubectl get secrets -w
*/

@IfBuildProfile("dev")
@LHWorkflow("kubernetes-connector-secret-delete-example")
public class DeleteSecretExampleWorkflow implements LHWorkflowDefinition {

    private final String taskDeleteSecretName;

    public DeleteSecretExampleWorkflow(
            @ConfigProperty(name = ConnectorConfig.TASK_SECRET_DELETE_NAME)
                    final String taskDeleteSecretName) {
        this.taskDeleteSecretName = taskDeleteSecretName;
    }

    @Override
    public void define(final WorkflowThread wf) {
        final WfRunVariable namespace = wf.declareStr("namespace");
        final WfRunVariable name = wf.declareStr("name").required();

        wf.execute(taskDeleteSecretName, namespace, name);
    }
}
