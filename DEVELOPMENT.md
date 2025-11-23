# Development

## Table of Content

<!-- TOC -->
* [Development](#development)
  * [Table of Content](#table-of-content)
  * [Commands](#commands)
  * [Setup](#setup)
  * [Script](#script)
  * [LittleHorse](#littlehorse)
  * [Helm](#helm)
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
  - [jq](https://jqlang.org/)
  - [yq](https://mikefarah.gitbook.io/yq/)

## Setup

Install pre-commit hooks:

```shell
pre-commit install
```

Run local env with Kind and LittleHorse server:

```shell
./local-dev/setup.sh
```

Port forward:

```shell
kubectl port-forward service/littlehorse 2023:external
```

Run connector in `dev` profile:

```shell
quarkus dev
```

> `quarkus run` for `prod` profile

## Script

Setup kind:

```shell
./local-dev/setup.sh
```

> `./local-dev/setup.sh --clean` to destroy the environment

Install using helm:

```shell
./local-dev/install.sh
```

> `./local-dev/install.sh --build` for building before install

Build docker:

```shell
./local-dev/build.sh
```

> `./local-dev/build.sh --rollout` to load the new docker image into kind

## LittleHorse

Check workflow:

```shell
lhctl search wfSpec
```

Check connector task:

```shell
lhctl search taskDef
```

Check our workflow examples in [src/main/java/io/littlehorse/connector/dev/workflow](src/main/java/io/littlehorse/connector/dev/workflow).

## Helm

Render templates:

```shell
helm template lh-kubernetes-connector \
     --set littlehorse.apiHost="littlehorse" \
     --set littlehorse.apiPort="2024" \
     ./helm
```

Install chart:

```shell
helm upgrade --install lh-kubernetes-connector \
     --set image.repository="littlehorse/lh-kubernetes-connector" \
     --set image.tag="latest" \
     --set littlehorse.apiHost="littlehorse" \
     --set littlehorse.apiPort="2024" \
     ./helm
```

Uninstall chart:

```shell
helm uninstall lh-kubernetes-connector
```

Test chart:

```shell
helm test lh-kubernetes-connector --timeout 10s
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
