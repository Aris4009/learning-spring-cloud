# learning-spring-cloud

## 1. 部署Nacos

### 1.1 环境准备

1. 64 bit OS，支持 Linux/Unix/Mac/Windows，推荐选用 Linux/Unix/Mac。
2. 64 bit JDK 1.8+；
3. Maven 3.2.x+；

### 1.2 下载安装包

[Releases · alibaba/nacos · GitHub](https://github.com/alibaba/nacos/releases)

### 1.3 单机环境下，集群安装

由于nacos使用raft协议，所以集群节点数量必须为2n+1。此处以3个节点为例：

1. 解压安装包；

2. 复制三份安装包，分别命名为nacos_1、nacos_2、nacos_3；

3. 修改conf目录下的application.properties配置文件；
   
   > 设置端口号，由于是三个节点，所以服务端口号server.port设置为10000,20000,30000
   > 
   > 绑定IP地址，将设置nacos.inetutils.ip-address=127.0.0.1
   > 
   > 修改数据库地址配置，将数据库连接设置为用户自定义的链接。
   > 
   > 设置属性nacos.naming.data.warmup=true，该含义为是否在Server启动时进行数据预热。
   > 
   > 其他设置暂时按默认设置。

4. 修改cluster.conf文件，内容设置为节点的IP+端口：

> 127.0.0.1:10000
> 127.0.0.1:20000
> 127.0.0.1:30000

### 1.4 将元数据导入mysql

将conf目录下的nacos-mysql.sql文件导入mysql，但是需要首先自行创建mysql数据库

### 1.5 设置nginx

设置nginx的目的是为了将nacos节点统一用nginx反向代理，提供一致的访问url，然后由nginx统一进行转发。

ngnix配置如下：

```
upstream nacos-svr {
    server ip:10000;//此处将ip替换为局域网地址，外部通过test.nacos.com域名的80端口访问nacos
    server ip:20000;
    server ip:30000;
}

server {
         listen  80;
         server_name test.nacos.com;
         location ~*^.+$ {
             proxy_pass http://nacos-svr;
         }
}
```

### 1.6 启动nacos服务

分别在nacos集群的三个节点的bin目录下，指定startup.sh，windows用户执行startup.cmd。此时，分别观察节点目录下，logs子目录中的nacos.log日志有无异常，无异常，说明启动成功。

启动失败的原因可能有端口号设置错误，因为此文档所采用的nacos版本为nacos2.0，它采用了raft协议，在启动nacos节点时，每个节点需要占用4个端口，他们分别为：

```
server.port(默认8848）
raft port: ${server.port} - 1000
grpc port: ${server.port} + 1000
grpc port for server: ${server.port} + 1001
```

所以，单机部署集群时，需额外注意端口是否会有冲突。

### 1.7 启动nginx

启动nginx，访问nacos，需要通过nginx中设置的域名进行访问，所以，客户端需要在/etc/hosts中设置hosts。

```
ip test.nacos.com//将ip替换为nginx中设置的上游ip地址
```

### 1.8 访问控制台

打开浏览器，输入test.nacos.com/nacos后，会跳转至nacos控制台。默认用户名密码为：

```
nacos/nacos
```

至此，nacos伪集群环境搭建完毕。