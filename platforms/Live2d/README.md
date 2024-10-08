this is Live2D native library README file
We will not add dll and lib folder in this repository，
Please download them from Live2D Cubism official site。

---

这是Live2DCubism NativeSDK的readme文件，本项目不会包含
Live2D的动态库和静态库，请自行在官网下载。

---

# Live2D Cubism Core

This folder contains header and platform-specific library files for developing native applications.

What's the Core anyway? It's a small library that allows you to load Cubism models into your application.


## Getting Started

You probably want to play back animations and want to display your models on screen, too.

[Go here](https://github.com/Live2D/CubismNativeSamples) for further information.


## File Structure

```
Core
├─ dll          # Shared (Dynamic) Library files
├─ include      # Header file
└─ lib          # Static Library files
```


## Library List

| Platform | Architecture | dll | lib | Path | Note |
| --- | --- | --- | --- | --- | --- |
| Android | ARM64 | ✓ | ✓ | android/arm64-v8a |   |
| Android | ARMv7 | ✓ | ✓ | android/armeabi-v7a | This library is currently deprecated and will be removed in the near future. |
| Android | x86 | ✓ | ✓ | android/x86 |   |
| Android | x86_64 | ✓ | ✓ | android/x86_64 |   |
| iOS | ARM64 |   | ✓ | ios/xxx-iphoneos | iOS Devices |
| iOS | x86_64 |   | ✓ | ios/xxx-iphonesimulator | iOS Simulator |
| Linux | x86_64 | ✓ | ✓ | linux/x86_64 |   |
| macOS | ARM64 | ✓ | ✓ | macos/arm64 |   |
| macOS | x86_64 | ✓ | ✓ | macos/x86_64 |   |
| Mac Catalyst | ARM64 |   | ✓ | experimental/catalyst | Universal Binary |
| Mac Catalyst | x86_64 |   | ✓ | experimental/catalyst | Universal Binary |
| Raspberry Pi | ARM | ✓ | ✓ | experimental/rpi |   |
| UWP | ARM | ✓ |   | experimental/uwp/arm |   |
| UWP | ARM64 | ✓ |   | experimental/uwp/arm64 |   |
| UWP | x64 | ✓ |   | experimental/uwp/x64 |   |
| UWP | x86 | ✓ |   | experimental/uwp/x86 |   |
| Windows | x86 | ✓ | ✓ | windows/x86 |   |
| Windows | x86_64 | ✓ | ✓ | windows/x86_64 |   |

### Experimental Library

`Raspberry Pi`, `UWP` and `catalyst` are experimental libraries.

### Windows Static Library

Libraries are located under the VC++ version name directory.

Below is the Visual Studio version for the VC++ version.

| VC++ version | Visual Studio version |
| ---: | --- |
| 120 | Visual Studio 2013 |
| 140 | Visual Studio 2015 |
| 141 | Visual Studio 2017 |
| 142 | Visual Studio 2019 |
| 143 | Visual Studio 2022 |

Also, the suffix library (`_MD`, `_MDd`, `_MT`, `_MTd`) refers to the [Use Run-Time Library] option used to compile the library.

### Calling convention

When using the dynamic library of *Windows/x86*, explicitly use `__stdcall` as the calling convention.
