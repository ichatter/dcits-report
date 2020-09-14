# Changelogs 20200914

1. 解决报工失败时没有继续循环尝试报工的问题

# Changelogs 20190826

1. 解决服务端无响应时程序偶尔卡死问题
2. 去除启动类Start.java中的Timer.java定时任务，改用ScheduledExecutorService.java
3. 使用HttpUtil.java统一获取全局的RequestConfig，并默认设置了通讯连接建立超时时间connectTimeout和响应读取超时时间socketTimeout
4. 优化ocr识别时的图片流读取方式，将输入流保存到系统临时目录图片文件的方式，简化为直接使用ImageIO.read(is)
5. 其他优化


# dcits-report使用说明

1. 自动报工需要识别验证码图片，本程序提供**两种**验证码识别方式:
   a. 默认使用google支持的tesseract-ocr(java 语言api为tess4j)进行识别，缺点是工程依赖较大，导致工程发布出来后会大很多；
   b. 其次使用ocrking提供的识别接口，用户需自行前往[ocrking](http://lab.ocrking.com/)网站申请apikey（免费），申请成功后，将该字符串放入dcits/src/main/resources/config.properties的ocrApiKey对应的属性值中。
2. 本程序使用gradle作为项目构建及依赖管理工具，如需使用maven，可自行简单修改即可。
3. 需要部署在能访问外网[报工系统](https://c.dcits.com)的主机/服务器上，建议放在个人的虚拟主机、VPS或app云引擎中
4. 相关配置位于dcits/src/main/resources
5. 默认部署于linux服务器上，需启动start.sh，根据提示输入账户信息才能正常使用

# 关于验证码识别

目前提供两种识别方式：ocrking和tess4j，各有优缺点：

1. ocrking通过远程http接口调用，实现简单，无依赖，但该识别接口易受网络通讯影响，如果使用该方式，应该注释掉build.gradle文件中多余依赖：

   ```groovy
   //compile 'net.sourceforge.tess4j:tess4j:3.4.0'
   ```

2. tess4j采用本地识别方式，无需网络，只需几行代码即可实现验证码的识别，但依赖库较大（size增加40M+），另外在linux环境下，tesseract工具需另行安装。

* ubuntu安装方式：
  ```shell
  sudo apt-get install tesseract-ocr
  ```
* centos安装方式（32位 i686为例）：

  * 安装epel源
    ```shell
    yum install epel-release.noarch
    ```
  * 查询tesseract
    ```shell
    yum search tesseract
    ```
  * 安装tesseract
    ```shell
    yum install tesseract.i686
    ```