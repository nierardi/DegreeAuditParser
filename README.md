# DegreeAuditParser

A program that parses your degree audit and outputs what it finds.

The goal of this is to eventually become a "course wizard" - basically, a self-advising tool that looks at your degree audit, the semesters that you have left, and helps you plan your schedule going forward. This parser will be a key part of that project, once I start working on it.

### How it works

The program uses a JavaFX WebView to allow the user to login to their degree audit. The WebView supports modern HTML5/JS/CSS3, so the user can successfully login and authenticate via Duo. Once the degree audit is fully rendered, the HTML can be captured directly and parsed.

The parse results are currently output to a file, along with some debug information. An HTML copy of the degree audit inner frame is also created.

### Want to help?

To help understand the format a little better, I need some samples from other people's degree audits. I have a few edge cases I need to test:

- Student is in/approaching their last semester, and all courses are complete (green) or partial (blue), none are incomplete (red)
- Rules that require x credits, student has completed/partialed y credits (e.g. major requires 9 elective credits, and student has only completed 3)
- More electives taken than required for a rule
- ...

Download the JAR on the root level of the repo. This is the latest build of the project. If you have Java installed, you probably can just double-click it to run. (If that doesn't work, you can always `java -jar blah.jar`.) Click Go to load the login page, login, and then wait for the audit to appear. Once it does, click the button at the bottom. If all goes well, you'll get two files in the same directory the JAR is in - `output.txt` and `degAudit.html`. Send those to me at nierardi@radford.edu. 

The parser is open-source, and anyone is welcome to use/improve it, granted credit is given.
