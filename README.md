# moviedb-scroller

This simple Android app uses <a href="https://developer.android.com/topic/libraries/architecture/paging">Paging</a> to handle paginated lists of data coming from <a href="https://www.themoviedb.org/">The Movie DB</a>. 

It uses a <a href="https://developer.android.com/reference/androidx/lifecycle/ViewModel.html">ViewModel</a> to handle states of the view and to observe changes in the content to load into the adapter after receiving updates from a <a href="https://developer.android.com/reference/android/arch/paging/PageKeyedDataSource">data source</a>.

Every page downloaded from the API is also stored in a <a href="https://developer.android.com/topic/libraries/architecture/room">Room</a> database as well as the initial configuration (that is updated after 5 days).


To start the project, you need to sign up for a developer account at <a href="https://developers.themoviedb.org/3/getting-started/introduction">The Movie DB<a/> and get your API key.

Then update `config.gradle` to add your key (there is an empty property `api_key` ready for that).
