# weka-android

A minimal Android port of machine learning library [WEKA](http://www.cs.waikato.ac.nz/~ml/weka/) 3.8.

## Project scope


## Provided Annotations
```java

```

## Download

```gradle
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" }
    }
}

dependencies {
    compile 'com.github.hkjinlee.weka-android:annotation:0.0.1'
    apt 'com.github.hkjinlee.weka-android:compiler:0.0.1'
}
```