# Pdfium4J

Pdfium For Java,通过JavaCPP技术构建的Pdfium类库，请首先构建pdfium，然后才能
构建和使用本项目。

关于PDFium的二进制包，我使用了此项目：
https://github.com/bblanchon/pdfium-binaries

使用本类库前，请务必首调用`Pdfium.doInitialize()`，如果单纯使用本类库，你可以
不这样做，因为大多数对象会自动检查类库有没有初始化，如果打算添加和修改本类库的功能，
请务必进行初始化的检查，以免出现意料之外的崩溃问题。

