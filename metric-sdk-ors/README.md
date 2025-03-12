# ors (open register service)

## What is ors

***ors是基于jwt构建的开放注册服务，用于在可信系统之间开放用户注册数据交换。***

ors的工作原理在于，客户系统将本系统的用户信息签发为jwt，并提交至服务系统中。服务系统验证解析jwt，并按照约定的规则处理客户系统提交的用户信息（自动登陆/注册登陆）。

### ors典型的应用场景如下：

- 用户登陆互通
- 影子账户登陆

## How to use

***目前ors仅提供Java语言版本的实现，若有其他语言版本的需求，可根据ors源码进行实现***

* 获取服务系统的接入入口及sign密钥组
* 根据sign密钥组构建签署密钥交换器
    ```java
        //key组由验证方服务器提供
        String[] avatarKeys = {...};
        AvatarSignKeyExchanger avatarSignKeyExchanger = new AvatarSignKeyExchanger(avatarKeys);
    ```
* 按步骤签署jwt
    ```java
            //构建jwt对象
        Jwt.Header header = ...
        ResignUserModel payload =...
        String jwtStr = Jwt.buildToken(header, payload, avatarSignKeyExchanger);
    ```
* 将jwt提交到验证方的接口