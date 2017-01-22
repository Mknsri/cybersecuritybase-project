Vulnerability analysis of Kuontalo-Opus web-application, part of the course project for the Cyber Security Base MOOC by the University of Helsinki

The source code for this application can be found at https://github.com/Mknsri/cybersecuritybase-project
The markdown version with improved readability of this document can be found at https://github.com/Mknsri/cybersecuritybase-project/flaw_report.md

Overview of the application:
=============================
*"Kuontalo-opus is a cutting-edge, fully featured, social media gathering experience where you can post messages and share your personal story with 10's of other completely real users. Our top-notch engineering team have delivered a truly remarkable product that will expand and enrich your social life in new and untold exciting ways.
We are serious about security, that's why we rolled our own UltraSecure© encryptomanagesecretsupertechnical library that keeps your account and posts safe.
And don't worry about giving us your personal information. We'll just keep them here and never do anything with them while you keep using our free and wonderful product.
Enjoy!"*

Installation instructions:
===========================
Installation of this application requires a Java development environment that supports Maven, such as Netbeans or IntelliJ. Once you have the enviroment ready:

1. Clone the repo at https://github.com/Mknsri/cybersecuritybase-project
2. Open the project with your IDE
3. Build and run
4. Access the application from the same machine by typing http://localhost:8080 in your address bar.

Kuontalo-Opus is a simple application, where user can register and log in to the application with their email address, name and password. After logging in they can read posts by other users and leave their own posts. Users who have lost their password can request a reset link to be sent to them by email, but this feature does not seem to be working.

Security analysis of this application revealed at least 5 vulnerabilities with varying severity, ranging from minor vulnerabilities like forced user logout using CSRF or extremely severe leaking user's email or passwords due to poor session management.

This report outlines the found 5 vulnerabilities and how to replicate them, and ultimately how to fix them. For testing purposes the application includes a test user, but you can also register to the application with your own credentials.

Vulnerability #1: Forcing user log out using a CSRF -vulnerability
===========
**OWASP Top 10 type: A8 - Cross-Site Request Forgery (CSRF)**
### Description:
A logged in user that visits the login page (found at `http://<host>/login`) logs the user out. This is an intended feature since the logout button on the main page directs to this page. This however opens up a vulnerability. If the user is tricked to request this page when logged in the app, the user is logged out.
### How to replicate:

1. Log in to the application either using your own account or the test account:
    - Email: test@test.com
    - Password: test
2. Embed this request on another page:
`<img src="http://<host address>/login" />`
3. Visit the page containing this tag
4. Try to access the main page of the application (`http://<host address>/`)
5. You are logged out of your current session.

### How to fix the vulnerability:
CSRF protection is already enabled in the application, but incorrect usage of the request methods allow this exploit to work. To fix this vulnerability, create a POST-action form and request that logs the user out explicitly instead of doing it everytime a user visits the login page

**Fix commit: https://github.com/Mknsri/cybersecuritybase-project/commit/41147ca6faedac03ec1857d076a49077f24ca6a9**

Vulnerability nr 2: Leaking user email addresses due to insecure object references
=============
**OWASP Top 10 type: A4 - Insecure Direct Object References**
### Description:
Using the Forgot Password feature, users can request a password reset link sent to their email address. After typing in the user's email address and pressing Submit, the user's email address is displayed. However due to insecure object references, an attacker can modify the id parameter of the request to leak email addresses of other users of the application.
### How to replicate:

1. Go to the password reset page `http://<host address>/forgotpassword`
2. Type in a legitimate email to request a password reset for, for example test@test.com
3. Press submit, you will see a message about password reset link being sent
4. Change the id parameter of the request url to another number between 2-5
5. The email belonging to the user with that id will be displayed

### How to fix the vulnerability:
The password reset message containing the user's email can be sent in the same response without fetching the user's email in the GET-request. This will fix the vulnerability and as an added measure you can hide part of the users email address to prevent leaking the email address to anyone looking over the user's shoulder.

***Fix commit: https://github.com/Mknsri/cybersecuritybase-project/commit/abf7948745cc75779dd567eadc94ad09764c5c55***

Vulnerability nr 3: Stored XSS vulnerability in the posts feature
======
**OWASP Top 10 type: A3 - Cross-Site Scripting (XSS)**
### Description: 
The posts feature allows user to post their thoughts for others to see. Due to poor management of the user provided text, an attacker can embed javascript onto the page for all users to see and execute.

### How to replicate:
1. Log in to the application, either using your own account or the test account:
    - Email: test@test.com
    - Password: test
2. Into the post-textfield, type the following text:
`<script>document.body.style="background-color: pink;"</script>`
3. Press Submit
4. The background color will turn pink for your and any other users opening the page

### How to fix the vulnerability:
With Thymeleaf templates, fixing this vulnerability is simple. Just change the type of text used for posts in the mainpage-template from "utext" to "text"

**Fix commit: https://github.com/Mknsri/cybersecuritybase-project/commit/c22b8ae796bb3c6630496f2f3a999149f5b3bd86**

Vulnerability nr 4: Impersonating as other users and submitting posts in their name
======
**OWASP Top 10 type: A2 - Broken Authentication and Session Management**
### Description: 
The creators of the application have opted to write their own library for session management. This exposes a few vulnerabilities, one of which allows attackers to submit posts in another user's name. This is due to the sessionid-cookie corresponding to the user id of the account creating the post, and this id is not verified to be correct when submitting a new post.
### How to replicate:

1. Log in to the application, either using your own account or the test account:
    - Email: test@test.com
    - Password: test
2. Using your browsers console or some other tool, change the cookie sessionid to point to another user's id (for example: 3)
3. Submit a post using the form provided by the website
4. Log back in to the application and you will see your post, but as made by another user

### How to fix this vulnerability:
Redoing the session management should be the first priority, however as a quick fix you can add a check to verify that the session id and token match by calling `validUserLoggedIn()` before creating a new post.

**Fix commit: https://github.com/Mknsri/cybersecuritybase-project/commit/ef320061a4778c61c3afb27f6749cea987d79c12**

Vulnerability nr 5: Leaking password information through cookies
===
**OWASP Top 10 type: A6 - Sensitive Data Exposure**
### Description: 
Cookies are used for session management, and encrypted using the application creators' own crypto library called UltraSecure. This library however uses extremely weak encryption and stores user's password in the cookies. If the site's cookies and user's email address are leaked through using other vulnerabilities on the site, the plain text password can be easily decrypted from the cookies.
### How to replicate:

1. Log in to the application using the test account:
    - Email: test@test.com
    - Password: test
2. Find and copy the value of the cookie "sessiontoken"
3. Input the value into an decimal to ASCII converter (for example https://www.branah.com/ascii-converter)
4. Type a space between every 3rd number (so for "test" account the token 116101115116 becomes 116 101 115 116)
5. The password "test" is clearly visible

### How to fix this vulnerability:
Although switching the session management library to a better one should be the first priority, as a quick fix we can encrypt the session token using Bcrypt to properly encrypt the passwords to avoid leaking user data.

**Fix commit: https://github.com/Mknsri/cybersecuritybase-project/commit/823a437c1776cabd3d1a4771e1ded4dce49e2654**

