# Currency Converter
Android app that converts a value between currencies.

## Overview
The screen displays a list of all available currencies. The top currency is where the user input the value to be converted.
When clicking on another currency, it goes to the top position so a new input value can be entered.

![screenshot 1](https://raw.githubusercontent.com/LBR2048/currency-converter/master/images/screen1.png)
![screenshot 2](https://raw.githubusercontent.com/LBR2048/currency-converter/master/images/screen2.png)

## Use cases

![Use case diagram](https://raw.githubusercontent.com/LBR2048/currency-converter/master/images/use-case.png)

## Architecture
The app was created using the MVVM architecture using Android Architecture Componentes

This allows for a more modular code that is easier to understand, modify and test. The diagram below shows how all the classes are connected. The first draft of the diagram was created before writing any piece of code and was updated to reflect its current state.

![Use case diagram](https://raw.githubusercontent.com/LBR2048/currency-converter/master/images/architecture.png)

## Technologies used
- LiveData
- ViewModel
- Room
- Retrofit
