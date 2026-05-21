package io.littlehorse.connector.dev.workflow;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.arc.profile.IfBuildProfile;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/*
Run status example workflow:

lhctl run kubernetes-connector-status-example apiVersion "apps/v1" kind "Deployment" name "nginx-deployment"

Describe deployment:

kubectl describe deployment/nginx-deployment
*/

@IfBuildProfile("dev")
@LHWorkflow("kubernetes-connector-status-example")
public class StatusExampleWorkflow implements LHWorkflowDefinition {

    private final String taskStatusName;

    public StatusExampleWorkflow(
            @ConfigProperty(name = ConnectorConfig.TASK_STATUS_NAME) final String taskStatusName) {
        this.taskStatusName = taskStatusName;
    }

    @Override
    public void define(final WorkflowThread wf) {
        final WfRunVariable apiVersion = wf.declareStr("apiVersion");
        final WfRunVariable kind = wf.declareStr("kind");
        final WfRunVariable namespace = wf.declareStr("namespace");
        final WfRunVariable name = wf.declareStr("name");

        wf.execute(taskStatusName, apiVersion, kind, namespace, name);
    }
}
