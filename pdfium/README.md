# PdfiumCore

Pdfium的核心功能，基于JavaCPP提供的JNIWrapper，构建方法：
使用的PDFium版本目前为138.0.7202.0

Windows：

通过`Visual Studio`的`X64 Native Tools Command Prompt`，启动Maven命令
构建本项目。

如果想要重头构建，请删除源码中除了Configure结尾的其他所有类，并且清空edit和view
两个Package（不要删除package）。

接下来，在Native Tools的命令行中连续执行两次`mvn package`，在此后，请找到java目录
的所有dll文件，移入resource目录的x86_64目录里面，此时，需要执行`mvn install`将本项目
安装到本地的Maven仓库。

如果想要使用Pdfium4j，需要首先构建并安装本项目。

MacOS & Linux:

本模块提供了预构建的Linux x64模块，但是 由于作者没有合适的MacOS系统，
所以暂时不提供预构建的MacOS模块，需要的请自行构建。