# MariaDB Embedded

Mariadb Embedded是基于Mariadb的Embedded版本的C语言接口，通过JavaCPP库进行封装
以便于在java中使用的JNI库。

## 版本

当前版本的Mariadb Embedded使用的Mariadb版本为11.6.0，本库并不会即时更新至Mariadb的最新
版本，而有自己的更新节奏，本作者会根据Mariadb的改动选择合适的时机更新。

## 从源码构建本库

本库提供了包含以下平台的预编译的本地类库：

1. Windows x64
2. Linux x64

如果本类库没有包含需要的平台，可以通过以下步骤从源码构建：

### 必要的准备

首先，本库依赖于our-commons库，需要构建并且安装到本地的maven仓库，
该类库不存在JNI接口，是普通的Java类库，所以本项目的Release的仓库目录中
包含了该类库，可以直接把压缩包解压至项目中，并且添加该目录为本地仓库从而使
用该库。

### 构建Mariadb Embedded

你需要首先获取适当的Mariadb的源代码，并且构建一个Embedded版本的Mariadb库，
Mariadb的仓库地址是这个：https://github.com/MariaDB/server

当然也可以通过git客户端或者Github相关客户端完成下载或者clone。

本项目依赖的其他项目：

- libmariadb
- extra/wolfssl/wolfssl

点进去下载并且解压到它们应该在的位置即可。

构建需要CMake和Visual Studio，请自行下载安装。
构建需要StrawberryPerl和Bison，请自行下载并把它们加入环境变量Path。

开始构建前，请确认源码目录中不含“bld”目录，如果存在请删除，完成后需要手动创建此目录。

构建需要StrawberryPerl和Bison，把它们加入Path路径后，进入bld目录，通过如下指令进行构建：

```bash
cmake .. -DWITH_EMBEDDED_SERVER=1 -DCMAKE_CXX_FLAGS="/utf-8" -DCMAKE_C_FLAGS="/utf-8"
cmake --build . --config RelWithDebInfo
```

构建顺利完成后：

可以在bld的client中找到RelWithDebInfo的目录中找到mysql.exe，mysqldump.exe等命令行客户端。

可以在bld的sql中找到RelWithDebInfo的目录中找到mysqld.exe等服务端可执行程序。

可以在bld的libmysqld中的RelWithDebInfo目录中找到libmysqld.dll，那个是嵌入式mysql类库。

可以在bld的libmariadb的RelWithDebInfo目录中找到libmariadb.dll，那个是可嵌入的MariaDB的客户端。

可以在include目录找到头文件，可以在bld的include目录找到my_config.h和mysql_version.h。

本项目已经提供了Windows x64版本的library和header，其他系统需要把my_config.h和mysql_version.h放入
本项目的ext目录的对应平台的目录下，例如“Windows”，请参考目前这两个header的位置。

如果想在Windows下从其他版本的Mariadb构建本项目，请注意，此时或许需要对一些头文件进行修改，
具体内容请参见conf包里面的各个Configure类的源码，需要修改的位置都已经标注出来了。


### 构建二进制文件

你需要clone整个cpp-preset项目，导入后找到本项目的pom文件，其中的`<properties>`
中包含了`<build.skip>true</build.skip>`，将其值修改为`false`。

此时，在cpp-preset项目的根目录打开终端，对于Windows系统，你需要安装Visual studio，
，对于Linux，你需要c++的构建工具链，请自行通过系统的包管理器安装它们。

当你在Windows系统进行构建的时候，你需要X64-Native-Tools-Command-Prompt，如果你在使用
32位系统，请使用X86-Native-Tools-Command-Prompt。

在本项目的根目录下，执行
``` bash
mvn -pl mariadb package
```

通常你需要执行两次，完成后，你能够在mariadb的src目录中找到动态链接库，
将pom.xml中的properties的build.skip改为false，并把动态链接库复制到mariadb的
src/main/resources/<你的platform>-<CPU架构>/目录中，举个例子：

对于Windows x64，你需要把动态链接库复制到
src/main/resources/windows-x86_64目录中。

此时可以执行install将本类库安装到你的本地仓库，并且用于其他的项目。

### 添加新的Platform

如果需要为新的操作系统添加二进制构建，首先你需要在对应的操作系统编译mariadb的embedded版本，
并且在`org.swdc.mariadb.conf`包中添加对应的配置注解。

以上完成后，请在目标操作系统进行上述的二进制文件构建步骤，请注意，构建新的二进制文件前，你需要
对本项目执行`mvn clean`，否则可能因为缓存的原因导致构建失败。

## 使用

本类库的使用方法与Mariadb的Embedded Server的C语言接口类似，
所以直接参考它的C语言接口文档即可。

需要特别注意的是，JavaCPP的内存申请是通过Pointer.malloc进行的，
申请的内存需要通过Pointer.memset进行初始化，否则可能出现未定义行为,
内存的释放可以通过它的close方法完成。

当然，Pointer.free是可以释放内存的，它会导致内存立即被释放，使用的时候应该
谨慎，更推荐通过close释放内存。

内存操作的原则也和C/C++一致，自己申请的内存需要自己手动释放，否则会造成内存泄露。