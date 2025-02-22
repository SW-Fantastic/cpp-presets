# LLama.cpp

基于JavaCPP对LLama.cpp的封装，用于在Java中运行多种大语言模型，
例如：Llama 2, Mistral 7B, Nous Hermes, RWKV等。

当前LLAMA.cpp的版本：

[LLAMA b4730](https://github.com/ggml-org/llama.cpp/releases/tag/b4730)

LLAMA.cpp的更新非常频繁，所以本项目不会频繁更新，而是会在适当的时候追上最新版本。

运行LLAMA需要相当大的内存，同时，本封装采用的是LLAMA.cpp的CPU模式运行，因此推荐
使用具有AVX或者AVX2指令集的CPU，并且运行量化版本的模型，以尽可能达到更好的效果。

本项目目前没有Framework，是一个裸的Cpp封装，使用的时候请注意及时回收系统资源，
避免内存溢出等常见于C++项目的问题。

请注意! 经过LLama量化后的文件，在新版本的兼容性可能较差，请尽可能使用接近此LLama的版本的
量化文件，防止出现系统错误。

## 系统支持：

目前只有windows。