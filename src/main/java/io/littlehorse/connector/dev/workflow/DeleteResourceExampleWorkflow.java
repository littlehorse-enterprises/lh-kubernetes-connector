package io.littlehorse.connector.dev.workflow;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.arc.profile.IfBuildProfile;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/*
Run delete resource example workflow (requires resource to already exist in k8s):

lhctl run kubernetes-connector-delete-resource-example apiVersion v1 kind Secret name my-secret

List resources:

kubectl get secrets -w
*/

@IfBuildProfile("dev")
@LHWorkflow("kubernetes-connector-delete-resource-example")
public class DeleteResourceExampleWorkflow implements LHWorkflowDefinition {

    private final String taskDeleteName;

    public DeleteResourceExampleWorkflow(
            @ConfigProperty(name = ConnectorConfig.TASK_DELETE_NAME) final String taskDeleteName) {
        this.taskDeleteName = taskDeleteName;
    }

    @Override
    public void define(final WorkflowThread wf) {
        final WfRunVariable apiVersion = wf.declareStr("apiVersion").required();
        final WfRunVariable kind = wf.declareStr("kind").required();
        final WfRunVariable namespace = wf.declareStr("namespace");
        final WfRunVariable name = wf.declareStr("name").required();

        wf.execute(taskDeleteName, apiVersion, kind, namespace, name);
    }
}
