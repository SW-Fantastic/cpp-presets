# MariaDB Embedded Driver

本项目基于Mariadb的Embedded Server构建，用于嵌入单个Java应用程序中，
无需单独安装MariaDB数据库。

它的基于通过JavaCPP构建的MariaDB Embedded Server的JNI接口，
并提供了一套JDBC驱动的实现。

本项目要求Java版本大于等于Java 11。

## 构建

在已经正确的构建了mariadb的wrapper后，直接构建本项目即可，这是一个普通的Maven项目，
该项目依赖于我的另一个our-commons项目，它的位于[这里](https://github.com/SW-Fantastic/our-commons)
该项目主要提供了一些反射工具和转换器。

如果不想构建，也可以直接通过本项目的Release页面下载与构建的包或者仓库目录。

## 使用

### 添加依赖

在项目的pom.xml中加入以下依赖：

```xml
<dependency>
    <groupId>com.fantastic</groupId>
    <artifactId>mariadb-embedded-driver</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 使用方法

这个库的使用方法与普通的JDBC驱动类似，你可以通过以下方式来连接MariaDB Embedded Server：

```properties
hibernate.connection.url=jdbc:mysql://databaseName?basedir=./data&datadir=./data&autocreate=true&timeZone=+08:00
```
databaseName指定了数据库名为`databaseName`，这个名字可以根据需要进行修改， 
basedir与datadir指定了Mariadb的数据目录的位置， autocreate表示是否在数据库不存在时自动创建,
timeZone必须指定，必须使用两位数的小时和分钟作为Offset，否则会自动使用本地的TimeZone作为时区。
