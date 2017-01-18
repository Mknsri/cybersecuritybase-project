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

This report outlines the found 5 vulnerabilities and how to replicate them, and ultimately how to fix them. For testing reasons the application includes a test user, but you can also register to the application with your own credentials.

Vulnerability nr 1: Forcing user log out using a CSRF -vulnerability
OWASP Top 10 type: A8 - Cross-Site Request Forgery (CSRF)
Description: A logged in user that visits the login page (found at http://<host>/login) logs the user out. This is an intended feature since the logout button on the main page directs to this page. This however opens up a vulnerability where if the user is tricked to request this page when logged in the app, the user is logged out.
How to replicate:
1. Log in to the application either using your own account or the test account:
	Email: test@test.com
	Password: test
2. Using a tool to create http requests or for example embedding this request on another page:
<img src="http://<host address>/login" />
3. Visit the page containing this tag
4. Try to access the main page of the application (http://<host address>/)
5. You are logged out of your current session.

Vulnerability nr 2: Leaking user email addresses due to insecure object references
OWASP Top 10 type: A4 - Insecure Direct Object References
Description: Using the Forgot Password feature, users can request a password reset link sent to their email address. Upon requesting typing in the user's email address and pressing Submit, a messag is display informing that the link has been sent to the user's email address, which is also displayed. However due to insecure object references, an attacker can modify the id parameter of the request to leak email address of the other users of the account.
How to replicate:
1. Go to the password reset page http://<host address>/forgotpassword
2. Type in a legitimate email to request a password reset for, for example test@test.com
3. Press submit, you will see a message about password reset link being sent
4. Change the id parameter of the request url to another number between 2-5
5. The email belonging to the user id's of these accounts will be displayed

Vulnerability nr 3: Stored XSS vulnerability in the posts feature
OWASP Top 10 type: A3 - Cross-Site Scripting (XSS)
Description: The posts feature allows user to post their thoughts for others to see. Due to poor management of the user provided text, an attacker can embed javascript onto the page for all users to see and execute.
How to replicate:
1. Log in to the application, either using your own account or the test account:
	Email: test@test.com
	Password: test
2. Into the post-textfield, type the following text:
<script>document.body.style="background-color: pink;"</script>
3. Press submit
4. The background color will turn pink for your and any other users opening the page

Vulnerability nr 4: Impersonating as other users and submitting posts in their name
OWASP Top 10 type: A2 - Broken Authentication and Session Management
Description: The creators of the application have opted to write their own library for session management. This exposes a few vulnerabilities, one of which allows attackers to submit posts as another users name. This is due to the sessionid-cookie corresponding to the user id of the account creating the post, and this id is not verified to be correct when submitting a new post.
How to replicate:
1. Log in to the application, either using your own account or the test account:
	Email: test@test.com
	Password: test
2. Using your browsers console or some other tool, change the cookie sessionid to point to another users id (for example: 3)
3. Submit a post using the form provided by the website
4. Log back in to the application and you will see your post, but as made by another user

Vulnerability nr 5: Leaking password information through cookies
OWASP Top 10 type: A6 - Sensitive Data Exposure
Description: Cookies are used for session management, and encrypted using the application creators' own crypto library called UltraSecure. This library however uses extremely weak encryption and stores users password in the cookies. If the sites cookies and users email address are leaked through using other vulnerabilities on the site, the plain text password can be easily decrypted from the cookies.
How to replicate:
1. Log in to the application, either using your own account or the test account:
	Email: test@test.com
	Password: test
2. Find and copy the value of the cookie "sessiontoken"
3. Input the value into 

