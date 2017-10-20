# Android-Prompter
Android library that facilitates input validation

Simple wrap your view with 

```kotlin
Prompter.with(yourView)
```
and enjoy nice UX flow

<img src="https://media.giphy.com/media/3ohhwl3ae9q7wyjvWM/giphy.gif"/>

All properties like inputType, hint/text, message or title will be taken from `yourView` or you can specify them manualy with
```kotlin
Prompter.with(etPage)
        .title("Jump to page")
        .message("Enter page you would like to jump to")
        .inputType(InputType.TYPE_CLASS_NUMBER)
        .currentValue(book.currentPosition)
        .hintMode() //Current value will be displayed as hint
        .validate("Please enter page in range of [1, ${book.size}]"){ it.toInt() in 1..book.size }
```


By default empty values won't pass validation process but you can change this with `.allowEmpty()`
                
You can even customize whole callback method (callbacks are called only when validation pass or is not specified)
```kotlin
Prompter.with(etPage)
        .setOnValueChangeListener {
            if (it.toInt() in 0..100)
                etPage.setText(newInt.toString())
        }
```

It's worth to mention that setOnValueChangeListener overrides all listeners whereas addOnValueChangeListener appends to current list of listeners.
So:
```kotlin
Prompter.with(etDouble)
        .addOnValueChangeListener {
            Log.d(TAG, "A")
        }
        .addOnValueChangeListener {
            Log.d(TAG, "B")
        }
        .setOnValueChangeListener {
            Log.d(TAG, "C")
        }
```
will print 
`D/Prompter: C`


Usage:
Step 1. Add the JitPack repository to your root build.gradle
```gradle
allprojects {
        repositories {
                ...
                maven { url 'https://jitpack.io' }
        }
}
```
Step 2. Add the dependency
```gradle
dependencies {
        implementation 'com.github.stasbar:Android-Prompter:1.0.0'
}
```
