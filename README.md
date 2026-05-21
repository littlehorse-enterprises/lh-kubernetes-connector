# LittleHorse Kubernetes Connector

<a href="https://github.com/littlehorse-enterprises/lh-kubernetes-connector"><img alt="github" src="https://img.shields.io/badge/GitHub-7200d6?logo=github&logoColor=white"></a>
<a href="https://kubernetes.io/"><img alt="kubernetes" src="https://img.shields.io/badge/Kubernetes-326CE5?logo=kubernetes&logoColor=white"/></a>
<a href="https://littlehorse.io/"><img alt="littlehorse" src="https://raw.githubusercontent.com/littlehorse-enterprises/littlehorse/refs/heads/master/img/badges/gray.svg"/></a>

LittleHorse Kubernetes Connector is an [LH Worker](https://littlehorse.io/docs/server/developer-guide/task-worker-development) that allows you to interact with kubernetes.

## Table of Content

<!-- TOC -->
* [LittleHorse Kubernetes Connector](#littlehorse-kubernetes-connector)
  * [Table of Content](#table-of-content)
  * [Tasks](#tasks)
    * [Task Apply](#task-apply)
    * [Task Secret](#task-secret)
    * [Task Status](#task-status)
  * [Installation](#installation)
  * [Versioning](#versioning)
  * [Examples](#examples)
  * [Development Instructions](#development-instructions)
  * [Configurations List](#configurations-list)
  * [License](#license)
<!-- TOC -->

## Tasks

### Task Apply

This task allows you to apply any manifest in Kubernetes.
Default name `lh-kubernetes-connector-apply`.

```java
public void define(WorkflowThread wf) {
    WfRunVariable inputYaml = wf.declareStr("inputYaml");

    wf.execute("lh-kubernetes-connector-apply", inputYaml);
}
```

| Parameter  | Position | Type   | Required | Masked | Description       |
|------------|----------|--------|----------|--------|-------------------|
| Input yaml | 1        | String | True     | False  | Resource manifest |

### Task Secret

This task allows you to create secret in Kubernetes.
Default name `lh-kubernetes-connector-secret`. More at [Secrets](https://kubernetes.io/docs/concepts/configuration/secret/).

```java
public void define(WorkflowThread wf) {
    WfRunVariable namespace = wf.declareStr("namespace");
    WfRunVariable labels = wf.declareJsonObj("labels");
    WfRunVariable name = wf.declareStr("name").required();
    WfRunVariable stringData = wf.declareJsonObj("stringData").masked();
    WfRunVariable data = wf.declareJsonObj("data").masked();

    wf.execute("lh-kubernetes-connector-secret", namespace, name, labels, stringData, data);
}
```

| Parameter   | Position | Type   | Required | Masked | Description     |
|-------------|----------|--------|----------|--------|-----------------|
| Namespace   | 1        | String | False    | False  | Namespace       |
| Name        | 2        | String | True     | False  | Resource name   |
| Labels      | 3        | Json   | False    | False  | Resource labels |
| String data | 4        | Json   | False    | True   | Plain text data |
| Data        | 5        | Json   | False    | True   | Base64 data     |

### Task Status

This task allows you to get a resource's status from Kubernetes.
Default name `lh-kubernetes-connector-status`.

```java
public void define(WorkflowThread wf) {
    WfRunVariable apiVersion = wf.declareStr("apiVersion");
    WfRunVariable kind = wf.declareStr("kind");
    WfRunVariable namespace = wf.declareStr("namespace");
    WfRunVariable name = wf.declareStr("name");

    wf.execute("lh-kubernetes-connector-status", apiVersion, kind, namespace, name);
}
```

| Parameter   | Position | Type   | Required | Masked | Description         |
|-------------|----------|--------|----------|--------|---------------------|
| Api Version | 1        | String | True     | False  | Resource apiVersion |
| King        | 2        | String | True     | False  | Resource king       |
| Namespace   | 3        | String | False    | False  | Namespace           |
| Name        | 4        | String | True     | False  | Resource name       |

## Installation

<a href="https://github.com/littlehorse-enterprises/lh-kubernetes-connector/releases"><img alt="GitHub Release" src="https://img.shields.io/github/v/release/littlehorse-enterprises/lh-kubernetes-connector?label=latest"></a>

Add repository

```shell
helm repo add littlehorse https://littlehorse-enterprises.github.io/lh-helm-charts
```

Install chart

```shell
helm install lh-kubernetes-connector littlehorse/lh-kubernetes-connector --values values.yaml
```

Go to [LH Helm Charts Repository](https://github.com/littlehorse-enterprises/lh-helm-charts).

## Versioning

These connector keeps the same versioning as LittleHorse.

## Examples

Go to [src/main/java/io/littlehorse/connector/dev/workflow](src/main/java/io/littlehorse/connector/dev/workflow).

## Development Instructions

Go to [DEVELOPMENT.md](DEVELOPMENT.md).

## Configurations List

Go to [CONFIGURATIONS.md](CONFIGURATIONS.md).

## License

<a href="https://github.com/littlehorse-enterprises/lh-kubernetes-connector/blob/main/LICENSE.md"><img alt="Apache-2.0" src="https://img.shields.io/github/license/littlehorse-enterprises/lh-kubernetes-connector?label=covered%20by"></a>

All code in this repository is licensed by the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) and is copyright of LittleHorse Enterprises LLC.
