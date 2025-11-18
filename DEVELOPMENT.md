# Development

## Table of Content

<!-- TOC -->
* [Development](#development)
  * [Table of Content](#table-of-content)
  * [Commands](#commands)
  * [Setup](#setup)
  * [Run Connector](#run-connector)
  * [Unit Tests](#unit-tests)
  * [Apply Code Style](#apply-code-style)
  * [Interesting Links](#interesting-links)
<!-- TOC -->

## Commands

- Kubernetes:
  - [kind](https://kind.sigs.k8s.io/)
  - [kubectl](https://kubernetes.io/docs/reference/kubectl/)
  - [kubectx](https://kubectx.org/)
- Java:
  - [java](https://sdkman.io/jdks/amzn/)
  - [sdk](https://sdkman.io/)
  - [quarkus](https://sdkman.io/sdks/quarkus/)

## Setup

Install pre-commit hooks:

```shell
pre-commit install
```

Run local env with Kind and LittleHorse server:

```shell
./local-dev/setup.sh
```

> Clean `./local-dev/setup.sh --clean`

## Run Connector

Run connector in `dev` profile:

```shell
quarkus dev
```

> `quarkus run` for `prod` profile

Check workflow:

```shell
lhctl search wfSpec
```

Check connector task:

```shell
lhctl search taskDef
```

Run example workflow:

```shell
lhctl run kubernetes-connector-example inputYaml "
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
```

List deployments:

```shell
kubectl get deployments -w
```

List pods

```shell
kubectl get pods -w
```

## Unit Tests

```shell
./gradlew test
```

## Apply Code Style

```shell
./gradlew spotlessApply
```

## Interesting Links

- [Kubernetes Quarkus Extension](https://quarkus.io/guides/kubernetes-client)
