Vulnerability analysis of Kuontalo-Opus web-application, part of the course project for the Cyber Security Base MOOC by the University of Helsinki
The source code for this application can be found at https://github.com/Mknsri/cybersecuritybase-project
The markdown version with improved readability of this document can be found at https://github.com/Mknsri/cybersecuritybase-project/flaw_report.md

Overview of the application:
"Kuontalo-opus is a cutting-edge, fully featured, social media gathering experience where you can post messages and share your personal story with 10's of others completely real users. Our top-notch engineering team have delivered a truly remarkable product that will expand and enrich your social life in new and untold exciting ways.
We are serious about security, that's why we rolled our own UltraSecure© encryptomanagesecretsupertechnical library that keeps your account and posts safe.
And don't worry about giving us your personal information. We'll just keep them here and never do anything with them while you keep using our free and wonderful product.
Enjoy!"

Installation instructions:
Installation of this application requires a Java development environment that supports Maven, such as Netbeans or IntelliJ. After you have the enviroment ready.
1. Clone the repo at https://github.com/Mknsri/cybersecuritybase-project
2. Open the project with your IDE
3. Build and run
4. Access the application from the same machine by typing http://localhost:8080 in your address bar.

Kuontalo-Opus is fairly simple application, where user can register and log in to the application with their email address, name and password. After logging in they can read posts by other users and leave their own posts. Users who have lost their password can request a reset link to be sent to them by email, but this feature does not seem to be working.

Security analysis of this application revealed at least 5 vulnerabilities with varying severity, ranging from minor vulnerabilities like forced user logout using CSRF or extremely severe leaking user's email or passwords due to poor session management.

This report outlines the found 5 vulnerabilities and how to replicate them, and ultimately how to fix them.

Vulnerability nr 1: Forcing user log out using a CSRF -vulnerability
Description: A logged in user that visits the login page (found at http://<host>/login) logs the user out. This is an intended feature since the logout button on the main page directs to this page. This however opens up a vulnerability where if the user is tricked to request this page when logged in the app, the user is logged out.
How to replicate:
1. 	