# Configuring maven projects with GIT hooks and CheckStyles

If you are reading this, probably you know the importance of GIT hooks.

For someone who is listening to GIT hooks for the first time:

> Git hooks are scripts that are triggered on a specific action inside a repository. They let you enable guard rails,
> automations, more to maintain a quality software.

You might already know, GIT stores the complete status of working tree in .git directory under root. Likewise the hooks
are stored under .git/hooks directory

Let’s see what are all available?

The simplest way to know, is to list all the files inside your .git/hooks folder of a repository like below:

These hooks can be configured in either of following ways:

Collaboration: A engineer/ lead can manually register/ share the hooks with contributors to configure at their local
workspace. This work is tedious and with huge team ensuring every contributor status is painful.
Dynamic SDLC: In this approach we (lead/ DevOps) configure a utility that automatically update local workspace of
contributors during the first command execution. Command can be installation of some software/ running application
locally.
Throughout this article we will be discussing about 2nd approach.

> NOTE: This article is actually inspired
> from [here](https://dwmkerr.com/conventional-commits-and-semantic-versioning-for-java/). Highly recommend to go
> through
> the original blog.

### Table of Contents:

1. Project Setup
2. Configure Hooks
3. Configure CheckStyles
4. Testing
5. Conclusion

> NOTE: For the purpose of this article IntelliJ IDE is used. The process will work in any other IDE/ editor you may
> use.

### Project Setup:

We begin by creating a new project:

The only options required to be provided are JDK, GroupId and ArtifactId, you can leave the rest as default.

### Configuring Hooks:

Well, this is were the actual task begin. To enable hooks remember the utility we discussed earlier? Yes, utility is
nothing but a helper function that does the action behind the scenes.

One such open source utility is [Git Build Plugin](https://github.com/rudikershaw/git-build-hook)

All we have to do is simply push the dependency plugin to pom.xml like following

```xml

<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>id.my.hendisantika</groupId>
    <artifactId>pre-commit-sample9</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>pre-commit-sample9</name>
    <url>http://maven.apache.org</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.11.4</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>com.rudikershaw.gitbuildhook</groupId>
                <artifactId>git-build-hook-maven-plugin</artifactId>
                <version>3.5.0</version>
                <configuration>
                    <installHooks>
                        <pre-commit>hooks/pre-commit</pre-commit>
                        <commit-msg>hooks/commit-msg</commit-msg>
                    </installHooks>
                    <gitConfig>
                        <!-- The location of the directory you are using to store the Git hooks in your project. -->
                        <core.hooksPath>githooks</core.hooksPath>
                        <!-- Some other project specific git config that you want to set. -->
                        <custom.configuration>true</custom.configuration>
                    </gitConfig>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <!-- Sets git config specified under configuration > gitConfig. -->
                            <goal>configure</goal>
                            <goal>install</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>

```

### Now a few of the important configurations which make the setup complete:

1. Hooks file path
2. Phase of execution
   in [maven lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html). Here in this
   we used install

### We configure two hooks primarily:

1. commit-msg: A hook to validate/ verify the message
2. pre-commit: Any tasks before committing, like enforcing formatting & standards, linters, static analysis & unit tests

The above mentioned two are the most heavily used hook types across projects.

Please note this blog assumes you already
follow [conventional commit message](https://www.conventionalcommits.org/en/v1.0.0/) structure, only then you will be
able to understand the below script.

```shell
#!/usr/bin/env bash

# Create a regex for a conventional commit.
convetional_commit_regex="^(build|chore|ci|docs|feat|fix|perf|refactor|revert|style|test)(\([a-z \-]+\))?!?: .+$"

# Get the commit message (the parameter we're given is just the path to the
# temporary file which holds the message).
commit_message=$(cat "$1")

# Check the message, if we match, all good baby.
if [[ "$commit_message" =~ $convetional_commit_regex ]]; then
   echo -e "Commit message meets Conventional Commit standards..."
   exit 0
fi

# Uh-oh, this is not a conventional commit, show an example and link to the spec.
echo -e "The commit message does not meet the Conventional Commit standard"
echo "An example of a valid message is: "
echo "feat(login): add the 'remember me' button"
echo "More details at: https://www.conventionalcommits.org/en/v1.0.0/#summary"
exit 1

```

#### Testing:

```shell
mvn clean git-build-hook:configure
mvn clean git-build-hook:initialize
mvn clean git-build-hook:install
```

#### The output of bad commit:

```shell
(base) hendisantika@Hendis-MacBook-Pro pre-commit-sample9 % git commit -m "Test Commit 1"
[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------< id.my.hendisantika:pre-commit-sample9 >----------------
[INFO] Building pre-commit-sample9 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- clean:3.2.0:clean (default-clean) @ pre-commit-sample9 ---
[INFO] Deleting /Users/hendisantika/IdeaProjects/pre-commit-sample9/target
[INFO] 
[INFO] --- git-build-hook:3.5.0:configure (default) @ pre-commit-sample9 ---
[INFO] Git config 'core.hooksPath' set to - githooks/
[INFO] Git config 'custom.configuration' set to - true
[INFO] 
[INFO] --- git-build-hook:3.5.0:install (default) @ pre-commit-sample9 ---
[WARNING] Could not find file on filesystem or classpath
[WARNING] Could not find file on filesystem or classpath
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ pre-commit-sample9 ---
[INFO] skip non existing resourceDirectory /Users/hendisantika/IdeaProjects/pre-commit-sample9/src/main/resources
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ pre-commit-sample9 ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 1 source file with javac [debug target 1.8] to target/classes
[WARNING] bootstrap class path is not set in conjunction with -source 8
  not setting the bootstrap class path may lead to class files that cannot run on JDK 8
    --release 8 is recommended instead of -source 8 -target 1.8 because it sets the bootstrap class path automatically
[WARNING] source value 8 is obsolete and will be removed in a future release
[WARNING] target value 8 is obsolete and will be removed in a future release
[WARNING] To suppress warnings about obsolete options, use -Xlint:-options.
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ pre-commit-sample9 ---
[INFO] skip non existing resourceDirectory /Users/hendisantika/IdeaProjects/pre-commit-sample9/src/test/resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ pre-commit-sample9 ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 1 source file with javac [debug target 1.8] to target/test-classes
[WARNING] bootstrap class path is not set in conjunction with -source 8
  not setting the bootstrap class path may lead to class files that cannot run on JDK 8
    --release 8 is recommended instead of -source 8 -target 1.8 because it sets the bootstrap class path automatically
[WARNING] source value 8 is obsolete and will be removed in a future release
[WARNING] target value 8 is obsolete and will be removed in a future release
[WARNING] To suppress warnings about obsolete options, use -Xlint:-options.
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ pre-commit-sample9 ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running id.my.hendisantika.AppTest
@BeforeAll - executes once before all test methods in this class
@BeforeEach - executes before each test method in this class
@BeforeEach - executes before each test method in this class
@BeforeEach - executes before each test method in this class
@BeforeEach - executes before each test method in this class
Success
[WARNING] Tests run: 5, Failures: 0, Errors: 0, Skipped: 1, Time elapsed: 0.021 s -- in id.my.hendisantika.AppTest
[INFO] 
[INFO] Results:
[INFO] 
[WARNING] Tests run: 5, Failures: 0, Errors: 0, Skipped: 1
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.663 s
[INFO] Finished at: 2025-02-04T07:21:41+07:00
[INFO] ------------------------------------------------------------------------
\e[31mThe commit message does not meet the Conventional Commit standard\e[0m
An example of a valid message is: 
feat(login): add the 'remember me' button
More details at: https://www.conventionalcommits.org/en/v1.0.0/#summary
```

Notice in the output you can see a commit fail status with proper indication of what is expected?

Let’s See a commit with right message adhering to conventional standards.

```shell
[INFO] Scanning for projects...
[INFO] 
[INFO] ---------------< id.my.hendisantika:pre-commit-sample9 >----------------
[INFO] Building pre-commit-sample9 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- clean:3.2.0:clean (default-clean) @ pre-commit-sample9 ---
[INFO] Deleting /Users/hendisantika/IdeaProjects/pre-commit-sample9/target
[INFO] 
[INFO] --- git-build-hook:3.5.0:configure (default) @ pre-commit-sample9 ---
[INFO] Git config 'core.hooksPath' set to - githooks/
[INFO] Git config 'custom.configuration' set to - true
[INFO] 
[INFO] --- git-build-hook:3.5.0:install (default) @ pre-commit-sample9 ---
[WARNING] Could not find file on filesystem or classpath
[WARNING] Could not find file on filesystem or classpath
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ pre-commit-sample9 ---
[INFO] skip non existing resourceDirectory /Users/hendisantika/IdeaProjects/pre-commit-sample9/src/main/resources
[INFO] 
[INFO] --- compiler:3.13.0:compile (default-compile) @ pre-commit-sample9 ---
[INFO] Recompiling the module because of changed source code.
[INFO] Compiling 1 source file with javac [debug target 1.8] to target/classes
[WARNING] bootstrap class path is not set in conjunction with -source 8
  not setting the bootstrap class path may lead to class files that cannot run on JDK 8
    --release 8 is recommended instead of -source 8 -target 1.8 because it sets the bootstrap class path automatically
[WARNING] source value 8 is obsolete and will be removed in a future release
[WARNING] target value 8 is obsolete and will be removed in a future release
[WARNING] To suppress warnings about obsolete options, use -Xlint:-options.
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ pre-commit-sample9 ---
[INFO] skip non existing resourceDirectory /Users/hendisantika/IdeaProjects/pre-commit-sample9/src/test/resources
[INFO] 
[INFO] --- compiler:3.13.0:testCompile (default-testCompile) @ pre-commit-sample9 ---
[INFO] Recompiling the module because of changed dependency.
[INFO] Compiling 1 source file with javac [debug target 1.8] to target/test-classes
[WARNING] bootstrap class path is not set in conjunction with -source 8
  not setting the bootstrap class path may lead to class files that cannot run on JDK 8
    --release 8 is recommended instead of -source 8 -target 1.8 because it sets the bootstrap class path automatically
[WARNING] source value 8 is obsolete and will be removed in a future release
[WARNING] target value 8 is obsolete and will be removed in a future release
[WARNING] To suppress warnings about obsolete options, use -Xlint:-options.
[INFO] 
[INFO] --- surefire:3.2.5:test (default-test) @ pre-commit-sample9 ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO] 
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running id.my.hendisantika.AppTest
@BeforeAll - executes once before all test methods in this class
@BeforeEach - executes before each test method in this class
@BeforeEach - executes before each test method in this class
@BeforeEach - executes before each test method in this class
@BeforeEach - executes before each test method in this class
Success
[WARNING] Tests run: 5, Failures: 0, Errors: 0, Skipped: 1, Time elapsed: 0.021 s -- in id.my.hendisantika.AppTest
[INFO] 
[INFO] Results:
[INFO] 
[WARNING] Tests run: 5, Failures: 0, Errors: 0, Skipped: 1
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  1.470 s
[INFO] Finished at: 2025-02-04T07:22:56+07:00
[INFO] ------------------------------------------------------------------------

\e[32mCommit message meets Conventional Commit standards...\e[0m
[main 2bfa974] docs: Add more details in README.md file 🫸🌀✏️📗🐧🐳
 1 file changed, 0 insertions(+), 0 deletions(-)
 create mode 100644 README.md

```

Voilà 🎉 this works! you can change the script as you like according to requirements.

Similarly we can configure a pre-commit hook. For the purpose of demonstration, we will be using a checkstyle and
configure hook to prevent commit if any checkstyle fails

CheckStyle: These are guidelines for a codebase to follow a format ensuring smooth collaboration and improved
productivity.

We will use [Maven CheckStyle plugin](https://github.com/apache/maven-checkstyle-plugin) for this purpose

We will be using [Google style guide](https://google.github.io/styleguide/javaguide.html)

Similar, to how we configured hooks path, We configure style-guides path

The pre-commit script may look like:

```shell
#!/bin/sh
# Name: pre-commit.sh
# Date: Thursday, January 30 2025 09.00 WIB
# Usage: Pre-Commit Check Style Shell Script
# Author: Hendi Santika {https:/s.id/hendisantika} under GPL v2.x+


# Run Checkstyle using Maven
mvn clean spotless:check
mvn clean spotless:apply

# Check if Checkstyle found any violations
if [ $? -ne 0 ]; then
  mvn clean site
  echo "Checkstyle violations found. Fix them before committing."
  echo "Check the Checkstyle report in the 'target/site' directory."
  echo "Hendi Santika: $(date)"
  exit 1
fi

# Run Maven tests
mvn clean test

# Check the result of the Maven test command
if [ $? -ne 0 ]; then
    echo "Maven tests failed. Commit aborted."
    exit 1
fi
```

### Testing:

```shell
mvn clean git-build-hook:configure
mvn clean git-build-hook:initialize
mvn clean git-build-hook:install
```

An execution of bad formatting will lead commit to fail like following:

```shell
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Apache Maven' to 'Apache_Maven'
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Maven Coordinates' to 'Maven_Coordinates'
[INFO] Generating "About" report         --- maven-project-info-reports-plugin:3.8.0:index
[warn] [XHTML5 Sink] Modified invalid anchor name: 'About pre-commit-sample9' to 'About_pre-commit-sample9'
[INFO] Generating "Plugin Management" report --- maven-project-info-reports-plugin:3.8.0:plugin-management
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Project Plugin Management' to 'Project_Plugin_Management'
[INFO] Generating "Plugins" report       --- maven-project-info-reports-plugin:3.8.0:plugins
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Project Build Plugins' to 'Project_Build_Plugins'
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Project Report Plugins' to 'Project_Report_Plugins'
[INFO] Generating "Summary" report       --- maven-project-info-reports-plugin:3.8.0:summary
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Build Information' to 'Build_Information'
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Project Information' to 'Project_Information'
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Project Organization' to 'Project_Organization'
[warn] [XHTML5 Sink] Modified invalid anchor name: 'Project Summary' to 'Project_Summary'
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.853 s
[INFO] Finished at: 2025-02-04T07:44:50+07:00
[INFO] ------------------------------------------------------------------------
Checkstyle violations found. Fix them before committing.
Check the Checkstyle report in the 'target/site' directory.
Hendi Santika: Tue Feb  4 07:44:50 WIB 2025
```

This way you can configure your maven projects to have strict guidelines to allow for smooth collaboration and maintain
high coherence.

Likewise semantic versioning and automatic change-logs generation can be
configured. [Check here for details](https://dwmkerr.com/conventional-commits-and-semantic-versioning-for-java/)

Thanks for reading. Please share your feedback if you find this information helpful.
