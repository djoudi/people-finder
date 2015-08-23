# **Build Cycle Two Steps:** #

Installing the APK:  use adb to isntall the provided apk to your emulator or put the apk on your phone's sd card and use an app like Astro File Manager to install it.

Loading Activity: The person is clickable.  Tapping on him will take you to our login screen.

Login Activity:  Before you authenticate with facebook add "Kyle Shea Dausin" on facebook.  If you don't have any friends that have accepted the app's permissions, then one of the GUI elements will not show up.

Once you've added Kyle, authenticate by pressing the "Login through Facebook" button.  Once you've accepted the permissions, you'll be taken to the friends activity.

Friends Activity:  The initial first time set up is a little longer than the subsequent setups.  A progress dialog will open and eventually close.  A number of toasts will show up, these are used for debugging purposes and can be ignored.

After the progress dialog goes away, your basic information(name / photo) will show up on the top of the screen.  Any friends you have (Like Kyle) that have accepted the apps permissions will show up with some of the same information.  They are short and long clickable, with each opening up a special dialog.

Lastly, pressing "Add a friend" will bring up a list of all of your friends.  Press it, scroll down to "Kyle Shea Dausin", and select him.  If you go to his page on facebook, you will see a wall post with some dummy information about the app.



# **Build Cycle Three Notes:** #

- This build cycle requires that you and another team member are both facebook friends and you have both installed the People Finder app and accepted the app's permissions when logging in through facebook.

- when your friends list is populated click a person (This should be the teammate who is also running the app) with a short press, this will bring up a dialog to ask if you want to request a map with that person on it.

- if you send the request the app will try to find that friends phone number, if it doesn't have it stored, it will prompt you for that facebook friend's number.  We have not yet added code for error-checking what you input, so be careful. :P

- upon sending the request the sending app will go to the map dialog and display you on the map

- the receiving app, after receiving the request, will get a toast saying that in the next build cycle the app receiver will be able to accept or deny the request -- if accepted the receiving app will go to the map dialog and place both people on it, and if denied nothing will happen


# **Build Cycle Four Notes:** #

- known bug to fix this week: whenever the phone receives any text it will toast -- this week it should only respond to a text sent by the app to that phone
- Two ways to access compass Activity:
> - Long press on facebook picture
> - Click on facebook picture and select "compass" option
- When you hit "accept" when you get a request both phones will go to the map activity. If you hit "ignore" both phones will go back to the main activity

# **Build Cycle FIve Notes:** #

- Did not finish augmented reality userstory
> - I never got to be able to overlay an image on the camera view, but A camera view was implemented
> - To launch the camera view, long press on any f your facebook friend and click the camera view button