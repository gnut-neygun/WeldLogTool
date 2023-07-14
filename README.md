# How to build

- Have a Java Development Kit installed (11 or higher)
- https://www.oracle.com/de/java/technologies/downloads/
- Cross compiling is not supported, so to build for Windows, you need to build on Windows, same for Linux / MacOS
- Cd to the project and run:
- `./gradlew createDistributable`
- First time build maybe slower because gradle wrapper is trying to download gradle and all of the dependencies
- It would be built in `build/compose/binaries` folder
- There are other options too (create an installer / uberjar), check:
- `https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/Native_distributions_and_local_execution/README.md`
- For development, run:
- `./gradlew run`

# How to use

- test.csv is a sample CSV to test the App. Can be safely deleted.
- The file WpsList.csv is expected in the app folder, with its name unchanged for the app to run. The file can also be
  modified to add/edit
  additional WPS number, respecting the CSV format (semi-colon seperated list of value)
- settings.txt is used to store the output folder path, can be safely deleted.
- logs folder is generated for diagnostics purpose in case of error. Can be safely deleted.

Tips: When choosing the CSV file in the file picker dialog, the recent items menu on the left can be helpful.
