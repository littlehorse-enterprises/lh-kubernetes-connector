package io.littlehorse.connector.dev.workflow;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.arc.profile.IfBuildProfile;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@IfBuildProfile("dev")
@LHWorkflow("kubernetes-connector-example")
public class ExampleWorkflow implements LHWorkflowDefinition {

    private final String connectorTaskName;

    public ExampleWorkflow(
            @ConfigProperty(name = ConnectorConfig.TASK_NAME) final String connectorTaskName) {
        this.connectorTaskName = connectorTaskName;
    }

    @Override
    public void define(final WorkflowThread wf) {
        wf.execute(connectorTaskName, wf.declareStr("inputYaml"));
    }
}
