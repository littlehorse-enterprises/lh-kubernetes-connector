# Configurations

## Table of Content

<!-- TOC -->
* [Configurations](#configurations)
  * [Table of Content](#table-of-content)
  * [LittleHorse Client Configurations](#littlehorse-client-configurations)
    * [Client](#client)
    * [Task Worker](#task-worker)
  * [LittleHorse Kubernetes Connector](#littlehorse-kubernetes-connector)
  * [Quarkus Kubernetes Client](#quarkus-kubernetes-client)
<!-- TOC -->

## LittleHorse Client Configurations

More about LH Configurations at: [Workers/Clients Configurations](https://littlehorse.io/docs/server/operations/client-configuration)
and [Configuring the Clients](https://littlehorse.io/docs/server/developer-guide/client-configuration).

### Client

``lhc.api.host``
The bootstrap host for the LittleHorse Server.

* Type: string
* Importance: high

``lhc.api.port``
The bootstrap port for the LittleHorse Server.

* Type: int
* Importance: high

``lhc.api.protocol``
The bootstrap protocol for the LittleHorse Server.

* Type: string
* Default: PLAINTEXT
* Valid Values: [PLAINTEXT, TLS]
* Importance: high

``lhc.tenant.id``
Tenant ID which represents a logically isolated environment within LittleHorse.

* Type: string
* Default: default
* Importance: medium

``lhc.ca.cert``
Optional location of CA Cert file that issued the server side certificates. For TLS and mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.client.cert``
Optional location of Client Cert file for mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.client.key``
Optional location of Client Private Key file for mTLS connection.

* Type: string
* Default: null
* Importance: low

``lhc.grpc.keepalive.time.ms``
Time in milliseconds to configure keepalive pings on the grpc client.

* Type: long
* Default: 45000 (45 seconds)
* Importance: low

``lhc.grpc.keepalive.timeout.ms``
Time in milliseconds to configure the timeout for the keepalive pings on the grpc client.

* Type: long
* Default: 5000 (5 seconds)
* Importance: low

``lhc.oauth.access.token.url``
Optional Access Token URL provided by the OAuth Authorization Server. Used by the Worker to obtain a token using client credentials flow.

* Type: string
* Default: null
* Importance: low

``lhc.oauth.client.id``
Optional OAuth2 Client Id. Used by the Worker to identify itself at an Authorization Server. Client Credentials Flow.

* Type: string
* Default: null
* Importance: low

``lhc.oauth.client.secret``
Optional OAuth2 Client Secret. Used by the Worker to identify itself at an Authorization Server. Client Credentials Flow.

* Type: password
* Default: null
* Importance: low

### Task Worker

``lhw.num.worker.threads``
The number of worker threads to run. It allows you to improve the task execution's performance parallelizing the tasks assigned to this worker.

* Type: int
* Default: 8
* Importance: medium

``lhw.task.worker.id``
Unique identifier for the Task Worker. It is used by the LittleHorse cluster to load balance the worker requests across all servers. Additionally, it is journalled on every `TaskAttempt` run by the Task Worker, so that you can more easily debug where a request was executed from. It is recommended to set this value for production environments.

* Type: string
* Default: a random value
* Importance: medium

``lhw.task.worker.version``
Optional version identifier. Intended to be informative. Useful when you're running different versions of a worker. Along with the `lhw.task.worker.id`, this is journalled on every `TaskAttempt`.

* Type: string
* Default: ""
* Importance: medium

## LittleHorse Kubernetes Connector

``littlehorse.connector.kubernetes.default.namespace``
Default namespace. Used when a namespace is not passed to a connector task.

* Type: string
* Default: default
* Importance: high

``littlehorse.connector.kubernetes.task.apply.name``
Defines the name of the task apply in LittleHorse.

* Type: string
* Default: lh-kubernetes-connector-apply
* Importance: high

``littlehorse.connector.kubernetes.task.apply.enabled``
Enables task apply.

* Type: boolean
* Default: true
* Importance: medium

## Quarkus Kubernetes Client

This connector uses [Quarkus](https://quarkus.io/) as a framework.

More about the kubernetes client configurations at: [Quarkus Configuration Reference](https://quarkus.io/guides/kubernetes-client#configuration-reference).
