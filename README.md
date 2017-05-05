# dcits使用说明
1. 自动报工需要识别验证码图片，本程序默认使用ocrking提供的识别接口，用户需自行前往[ocrking](http://lab.ocrking.com/)网站申请apikey（免费），申请成功后，将该字符串放入dcits/src/main/resources/config.properties的ocrApiKey对应的属性值中。
         也可根据自身需要使用人工打码平台或其他方式实现验证码识别。
2. 本程序使用gradle作为项目构建及依赖管理工具，如需使用maven，可自行简单修改即可。
3. 需要部署在能访问外网[报工系统](https://c.dcits.com)的主机/服务器上，建议放在个人的虚拟主机、VPS或app云引擎中
4. 相关配置位于dcits/src/main/resources
5. 默认部署于linux服务器上，需启动start.sh，根据提示输入账户信息才能正常使用