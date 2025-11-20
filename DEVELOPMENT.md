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
  - [docker](https://docs.docker.com/engine/install/)
  - [kind](https://kind.sigs.k8s.io/)
  - [kubectl](https://kubernetes.io/docs/reference/kubectl/)
  - [kubectx](https://kubectx.org/)
  - [helm](https://helm.sh/)
- Java:
  - [java](https://sdkman.io/jdks/amzn/)
  - [sdk](https://sdkman.io/)
  - [quarkus](https://sdkman.io/sdks/quarkus/)
- Tools:
  - [pre-commit](https://pre-commit.com/)
  - [lhctl](https://littlehorse.io/docs/server/developer-guide/install)

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

Check our workflow examples in [src/main/java/io/littlehorse/connector/dev/workflow](src/main/java/io/littlehorse/connector/dev/workflow).

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
