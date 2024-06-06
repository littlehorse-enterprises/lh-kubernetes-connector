## Agent Worker
Its main purpose is to process deployment tasks that are requested by the Dispatch Service. ie. Create Cluster.

## Environment Variables

```shell
export AW_DATA_PLANE_ID=${YOUR_DATA_PLANE_ID}
```


## Running locally
1. Run LH from the `lh-control-plane` project, it will be running on port 2025:
    ```shell
      ./localdev/setup.sh littlehorse
    ```
2. Run the Agent Worker
    ```shell
    ./gradlew :agent-worker:run
    ```