# Cpp Presets

my java-cpp preset projects，will not publish on maven center，
Please clone and build it yourself if you want to have a try。

基于JavaCPP技术的Presets，各类本地类库通过JavaCPP使得我们可以在Java语言中使用
它们，本项目不定期维护和更新，不会上传到公共仓库，想要使用的话请自行clone或者到Release下载发行版。

## 关于发布

由于文件大小超过Github的限制，现提供Release版本的本地仓库，下载并且解压Release中的发布版本，
你可以在maven中创建一个repository标签，其URL指定为你解压的目录，此时你就能正常的通过maven来使用这些类库了，
例如，你把下载的Release解压到你的项目的根目录下的“swdc-cpp-preset”，此时你可以像这样配置它：

Because size of binaries is too large，so I decide to provide some release version，
you can create a folder and extract files what download from release，that you can use it
as same as normal maven dependency，for example，files extract into a folder named “swdc-cpp-presets“ under your
maven project root folder, you can add a repository like following:

```xml
<repositories>
    <repository>
        <id>swdc-local</id>
        <name>SW-Fantastic-Local</name>
        <url>file:///${project.basedir}/swdc-cpp-presets</url>
    </repository>
</repositories>
```

然后你就能够像普通的maven仓库一样使用这里面的库，例如在里面加入这样的内容来使用Live2d：

and then you can use them as normal maven dependency，for example，add the following that you can 
use Live2d。
```xml
<dependency>
    <groupId>org.swdc</groupId>
    <artifactId>live2d-framework</artifactId>
    <version>1.0</version>
</dependency>
```

我也会尽可能的在项目里面添加全部的必要资源，如果你在Platforms里面无法找到必要的二进制库，那么通常是我把它压缩了，
解压其中的zip或者7z包或许就能找到。

## 子项目列表（sub-project list）

1. Live2D Core & Live2D Java SDK（YES，**JAVA SDK，NOT ANDROID**）
   - Build with Live2D Native SDK version 1.5-r1
   - Has pre-built binary with (windows-x64, linux-x64, macos-x64)
     本库提供了预构建的Windows，linux，macos的64位二进制文件。
2. Pdfium Core & Pdfium4J（new library）
   - Build with pdfium version 122.0.6248
   - Has pre-built binary with (window-x64, linux-x64, macos-x64)
     本库提供了预构建的Windows，linux和macos的64位二进制文件。
3. MariaDB Embedded & MariaDB Embedded JDBC（Embedded Mariadb java version）
   - Build with Mariadb version 11.6
   - Has pre-built binary with (windows-x64, linux-x64)
     本库提供了预构建的Windows，linux的64位二进制文件。
   - this library has dependency with repository `our-commons`，Please build and install
     it before you do build of this one，本库依赖了另一个`our-commons`库。
   - `our-commons` [Click here for this repository](https://github.com/SW-Fantastic/our-commons)
4. DearImGui（Library only，no frameworks at this time）
   - Build with DearImGUI Docking branch version 1.91.1
   - Has pre-built binary with (windows-x64)，目前只有Windows，macos没有更新。
5. LLama.cpp (Library only, no frameworks at this time / not release yet)
   - Build with llama.cpp version b4730.
   - Has pre-built binary with (windows-x64 cpu only)