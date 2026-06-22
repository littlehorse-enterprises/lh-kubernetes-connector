package io.littlehorse.connector.dev.workflow;

import io.littlehorse.connector.config.ConnectorConfig;
import io.littlehorse.quarkus.workflow.LHWorkflow;
import io.littlehorse.quarkus.workflow.LHWorkflowDefinition;
import io.littlehorse.sdk.wfsdk.NodeOutput;
import io.littlehorse.sdk.wfsdk.WfRunVariable;
import io.littlehorse.sdk.wfsdk.WorkflowThread;
import io.quarkus.arc.profile.IfBuildProfile;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/*
Run apply example workflow:

lhctl run kubernetes-connector-apply-example inputYaml "
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
"

List deployments:

kubectl get deployments -w

List pods

kubectl get pods -w
*/

@IfBuildProfile("dev")
@LHWorkflow("kubernetes-connector-apply-example")
public class ApplyExampleWorkflow implements LHWorkflowDefinition {

    private final String taskApplyName;

    public ApplyExampleWorkflow(
            @ConfigProperty(name = ConnectorConfig.TASK_APPLY_NAME) final String taskApplyName) {
        this.taskApplyName = taskApplyName;
    }

    @Override
    public void define(final WorkflowThread wf) {
        final WfRunVariable inputYaml = wf.declareStr("inputYaml");
        final WfRunVariable metadata = wf.declareJsonObj("metadata");

        final NodeOutput output = wf.execute(taskApplyName, inputYaml);
        metadata.assign(output);
    }
}
