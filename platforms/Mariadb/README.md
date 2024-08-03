# MariaDB Embedded

这是Mariadb的Embedded模式下的JNIWrapper，想要构建本模块，你需要首先构建
一个Mariadb，这是因为Mariadb的Embedded库，在默认的情况下不会被构建，所以我们
需要手动完成Mariadb的构建来得到这个类库。

本项目采用的Mariadb的版本是11.6.

## 如何构建Mariadb

Mariadb的仓库地址是这个：https://github.com/MariaDB/server
可以使用Download Zip的方式下载该仓库，但是下载后请注意，该仓库依赖了其他的仓库，
这些其他的仓库在Download Zip的方式下载的时候，是不会被包含在本仓库之中的，所以
需要手动下载它们并且放入合适的位置。

当然也可以通过git客户端或者Github相关客户端完成下载或者clone。

本项目依赖的其他项目：
libmariadb
extra/wolfssl/wolfssl
点进去下载并且解压到它们应该在的位置即可。

构建需要CMake和Visual Studio，请自行下载安装。
构建需要StrawberryPerl和Bison，请自行下载并把它们加入环境变量Path。

开始构建前，请确认源码目录中不含“bld”目录，如果存在请删除，完成后需要手动创建此目录。

构建需要StrawberryPerl和Bison，把它们加入Path路径后，进入bld目录，通过如下指令进行构建：

```bash
cmake .. -DWITH_EMBEDDED_SERVER=1 -DCMAKE_CXX_FLAGS="/utf-8" -DCMAKE_C_FLAGS="/utf-8"
cmake --build . --config RelWithDebInfo
```

构建顺利完成后，
可以在bld的client中找到RelWithDebInfo的目录中找到mysql.exe，mysqldump.exe等命令行客户端。
可以在bld的sql中找到RelWithDebInfo的目录中找到mysqld.exe等服务端可执行程序。
可以在bld的libmysqld中的RelWithDebInfo目录中找到libmysqld.dll，那个是嵌入式mysql类库。
可以在bld的libmariadb的RelWithDebInfo目录中找到libmariadb.dll，那个是可嵌入的MariaDB的客户端。
可以在include目录找到头文件，可以在bld的include目录找到my_config.h和mysql_version.h。

## 构建本项目

本项目已经提供了Windows x64版本的library和header，其他系统需要把my_config.h和mysql_version.h放入
本项目的ext目录的对应平台的目录下，例如“Windows”，请参考目前这两个header的位置。

如果想在Windows下从其他版本的Mariadb构建本项目，请注意，此时或许需要对一些头文件进行修改，
具体内容请参见conf包里面的各个Configure类的源码，需要修改的位置都已经标注出来了。

