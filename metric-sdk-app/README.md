# metric-sdk-app

## How to use

* 获取接入token
* 根据token签署signKey，signValue
    ```java
    AppRequestModel requestModel = AppRequestModel.builder()
            .id(1)
            .name("指标系统4Android")
            .params(Arrays.asList(AppRequestModel.ValuePair.builder().name("id").value(1).build()))
            .signMills(System.currentTimeMillis())
            .build();
    requestModel.signValue("your token here");
    String headerValue = requestModel.toHeaderValue();
    System.out.println("Header Value : " + headerValue);
    ```
* 将签署制品headerValue加入header发送

## More

***目前metric-sdk-app仅提供Java语言版本的实现，若有其他语言版本的需求，可根据metric-sdk-app源码进行实现***