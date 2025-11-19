package io.littlehorse.connector.dev.workflow;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.arc.profile.IfBuildProfile;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/*
Run secret example workflow:

lhctl run kubernetes-connector-secret-example name my-secret stringData '
{
    "my-data": "this is my secret data"
}
'

List secrets:

kubectl get secrets -w

Read secret:

kubectl get secret my-secret -o jsonpath='{.data.my-data}' | base64 --decode
*/

@IfBuildProfile("dev")
@LHWorkflow("kubernetes-connector-secret-example")
public class SecretExampleWorkflow implements LHWorkflowDefinition {

    private final String taskSecretName;

    public SecretExampleWorkflow(
            @ConfigProperty(name = ConnectorConfig.TASK_SECRET_NAME) final String taskSecretName) {
        this.taskSecretName = taskSecretName;
    }

    @Override
    public void define(final WorkflowThread wf) {
        final WfRunVariable namespace = wf.declareStr("namespace");
        final WfRunVariable labels = wf.declareJsonObj("labels");
        final WfRunVariable name = wf.declareStr("name").required();
        final WfRunVariable stringData = wf.declareJsonObj("stringData").masked();
        final WfRunVariable data = wf.declareJsonObj("data").masked();

        wf.execute(taskSecretName, namespace, name, labels, stringData, data);
    }
}
