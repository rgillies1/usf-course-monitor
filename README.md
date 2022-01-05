# usf-course-monitor
A Java program that monitors a set of courses at USF for open seats. Has options to inform user via Direct Message from (their own) Discord bot and attempt to automatically register for course via Selenium.

This application assumes the user has at least some of the following:
- The authentication token to a Discord bot that will send notifications (i.e. a bot that *you* have created). Creating a Discord bot requires the creation of a Discord app, the details of which can be found at the [Discord Developer Documentation](https://discord.com/developers/docs/intro)
- The User ID of the Discord user to be notified of an opening. This is NOT the same as your User Tag (which is of the form Username#####). Information on how to get your Discord User ID can be found [here](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-).
- Google Chrome, as well as a matching version of Chromium. The correct chromedriver version should be downloaded from [the Selenium website](https://www.selenium.dev/documentation/webdriver/getting_started/install_drivers/).
- A direct link to OASIS. This can be any direct link, as long as it leads to the USF login page and goes directly to OASIS right after. An example of such a link is https://bannersso.usf.edu/ssomanager/c/SSB

As one can imagine, it also requires the Course Registration Numbers (CRNs) of each course that should be monitored. It also requires at least Java version 11 to run.

Click [here](https://www.dropbox.com/s/6jzz0cmssqffy9t/Monitor.jar?dl=1) for a direct download of the executable .jar file.

Some notes:
- The path to the Chromium installation should lead directly to the *file*. not the folder it is in.
- Using Chromium will keep you constantly logged in to OASIS. Logging in to OASIS elsewhere will likely log Chromium out, meaning you have to restart the program.
- The bot will notify you every 10 seconds when it finds an open seat. It will continue until the seat is taken, by you or anyone else. This can get annoying. Limiting message rates is on the TODO list.
- The bot will also notify you if it successfully registers. It will then no longer attempt to register for that course (unless the program is restarted)

TODO:
- Limit the rate at which the bot notifies the user of open seats.
- Proper error reporting when a course is unabled to be registered for.
- A permanent UI that allows the user to modify the program's settings as it executes.
