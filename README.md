# adnd-popular-movies
The Popular Movie App reads data from TheMovieDB API. This product uses the TMDb API but is not endorsed or certified by TMDb
To discover API features go to official documentation https://www.themoviedb.org/documentation/api.

To test the project you need a Developer API key from The Movie DB.

To get a TheMovieDB API key got to https://developers.themoviedb.org/3/getting-started/introduction
and follow the steps to register your key.




To get a YOUTUBE V3 API key got to Google Cloud Platform APIs Credentials
https://console.cloud.google.com/apis/credentials. A Google Account is required.

Once you have the keys, create a file `gradle.properties` in the project root directory and paste them into the correspondent fields:

 `API_KEY = "PASTE_YOUR_TMDB_API_KEY_HERE"`

 `YOUTUBE_KEY = "PASTE_YOUR_YOUTUBE_V3_API_KEY_HERE"`


##### WARNINGS:
* You sync or rebuild your project the allows the BuildConfig classes access the keys.
* Make sure your file `gradle.properties` are properly set in gitignore.



