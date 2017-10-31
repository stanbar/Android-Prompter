# Android-Prompter
Android library that facilitates input validation

Simple wrap your view with 

```kotlin
Prompter.showWithClick(yourView)
```
and enjoy nice UX flow

<img src="https://media.giphy.com/media/3ohhwl3ae9q7wyjvWM/giphy.gif"/>

All properties like inputType, hint/text, message or title will be taken from `yourView` or you can specify them manualy with
```kotlin
Prompter.showWithClick(etPage)
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
Prompter.showWithClick(etPage)
        .setOnValueChangeListener {
            if (it.toInt() in 0..100)
                etPage.setText(newInt.toString())
        }
```

It's worth to mention that setOnValueChangeListener overrides all listeners whereas addOnValueChangeListener appends to current list of listeners.
So:
```kotlin
Prompter.showWithClick(etDouble)
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
will print only
`D/Prompter: C`

Sometimes you may want specify different View that prompt the dialog. In this case use `Prompter.on()` and trigger manually `.show()`
```kotlin
val prompter = Prompter.on(tvPage)
        .title("Jump to page")
        .validate("Please enter page in range of [1, ${book.size}]") { it.toInt() in 1..book.size }
        
button1.setOnClickListener { prompter.show() }
button2.setOnClickListener { prompter.show() }
button3.setOnClickListener { prompter.show() }
container.setOnClickListener { prompter.show() }

```

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
        implementation 'com.github.stasbar:Android-Prompter:2.0.0'
}
```
