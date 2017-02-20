# MapBoxExperiment

This project is an experiment around [Mapbox](https://www.mapbox.com) API. I built an Android app that uses Mapbox SDK and implement a simple interface to add pins on a map.

### Concept

The app does the following:

- Displays a map using Mapbox, centered on user location
- Provides with a search box to search addresses and places with autocomplete
- Adds a pin on the map when a searched place is clicked
- Provides with a FAB to manually add a pin by moving the map
- Saves the last 15 added pins
- Provides with an history screen that displays all added pins


### Technical details

The app is made using MVP pattern and provides with an abstraction layer around everything map related, so that you can easily replace Mapbox with Gmaps or something else.

It's based a few dependencies:

- [Mapbox Android SDK](https://www.mapbox.com/android-sdk/)
- [RxJava & RxAndroid](https://github.com/ReactiveX/RxJava)
- [Dagger 2](https://github.com/google/dagger)
- [Leak Canary](https://github.com/square/leakcanary)
- [Mockito](https://github.com/mockito/mockito)

To build it, all you have to do is provide with a valid Mapbox API key, to do so: add the following file: `app/mapbox.gradle` containing your API key. A sample is provided: `app/mapbox.gradle.sample`.

### License

Sources are availables under the Apache 2 licence (See LICENSE for details).

