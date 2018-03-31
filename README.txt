This app was created using the Google Sheets API's Java Quickstart template.

Follow the instructions here: https://developers.google.com/sheets/api/quickstart/java. Be sure to install Java 1.8 (not 1.7).

Once you've created the gradle project as per the instructions, you can safely delete the sample Quickstart.java, and use the contents of this project's src/main/java instead.

Also, make sure to update the sourceCompatibility and targetCompatibility to 1.8 in build.gradle (or just grab the build.gradle from git).

Once you've completed these steps, run the following from your base working directory to run the app:

gradle -q run

(You can safely ignore the warnings about being unable to change permissions for everybody/owner. Pretty sure it's a bug on the Google side.)