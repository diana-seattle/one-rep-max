# one-rep-max
Example app that displays one-repetition max (1 RM) info based on workout data.

## Features
1. List of exercises, navigation to charted data for each 
2. Dark mode 
    * Android 10+: set according to system settings
    * Android 9 and below: set via menu item within app
3. Portrait and landscape modes
4. Animated fragment transitions

## Libraries
1. opencsv: http://opencsv.sourceforge.net
2. MPAndroidChart: https://github.com/PhilJay/MPAndroidChart

## Technologies
* MVVM with Architecture Components (ViewModel, LiveData, Repository pattern)
* Hilt for dependency injection
* Jetpack Navigation
* Kotlin coroutines
* Room
* Jetpack Compose
* Unit tests with MockK
* Themes/styles
* Coordinator and Constraint layouts
* View binding (which replaces findViewById)
* Custom views
* Shared preferences (for dark mode on Android 9 and below)
