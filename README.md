# Free and open source software search engine #
<img
src="https://raw.githubusercontent.com/germanysources/FOSSwareSearch/master/logo.eps" alt="logo">

## Idea ##
This project should search through open source software repositories and
projects. It should use the search api of the hosting provider, if such a api
exists. It should handle different hosting providers.

The search results are put into a inmemory database and the user can filter or
sort them with sql queries.

## Definitions ##
* The service www.github.com is called just github.

## Hosting provider ##
The search api from github is included. The search api from gitlab is planned
to include.

## User interface ##
A user interface for the bash(shell) is included. Before open the program we supplie
the search term (for example we want to know where the source code of a repository called curl is):
```
python runMain.py curl in:name
```
The output will be:
```
Found 5 repositories. Filter or order them with your sql statement.
sql>
```
Now we can type in a sql query (for example order after stars and only with
language c or c++):
```
sel>select * from Repositories where planguage in ('C', 'C++') order by star_count asc view block
```
The view block is a special extension for the output. See chapter sql
syntax. The output looks like this:
```
--------------------------------------------------------------------------------

https://github.com/curl/curl

A command line tool and library for transferring data with URL syntax, supporting HTTP, HTTPS, FTP, FTPS, GOPHER, TFTP, SCP, SFTP, SMB, TELNET, DICT, LDAP, LDAPS, FILE, IMAP, SMTP, POP3, RTSP and RTMP. libcurl offers a myriad of powerful features
[c] [client] [curl] [ftp] [http] [https] [ldap] [libcurl] [library] [transfer-data] [user-agent]
license_key             other
license_description     Other
planguage               C
homepage                https://curl.haxx.se/
star_count              10828
forks_count             2589
last_activity           21.10.2018
created_at              18.03.2010
open_issues             62
score                   158
sql>
```
First the html url is shown, afterwards the description and the topics (if they exists).

We typed ```select * from Repositories```, so all properties are shown for
this repository.

Looking for something different: Just type in a new sql select statement.

## SQL Syntax ##
The complete syntax of sqlite is supported.

For output there is the non-sql extension ```view block```. It displays each
repository as a block. If we omit ```view block```, we can see the
repositories in a tabular view.

A non-sql query is included for fetching more results from github:
```
fetch X rows from github
```
It means, we want to attach X (an integer value) search results to the
database. Not all search results are put immediately into the database. For
example only the first 20 results are put into the database. If we need more
results, we can type this command.

## Database Scheme ##
In the table Repositories the repositories are stored with the following
properties:
* html_url (for example https://github.com/curl/curl)
* description Project description (string)
* license_key (string)
* license_description (string)
* planguage main programming language (string)
* homepage (string)
* star_count (integer)
* forks_count (integer)
* last_activity (datetime format)
* created_at (datetime format)
* open_issues (integer)
* score The score which github give to this repository (integer)

The topics are stored in the table RepositoryTopics. This table has just 2 key
fields:
* html_url (foreign key to Repositories.html_url)
* topic (string)

## Configuration ##
The configuration is saved in the file config.json (as can be seen in the javascript object notation).
Here we can control the following parameters.

For searching with github:
* **maxNoResults**: The numbers of results, that are put into database after
executing the search query. If more results are available, we can fetch them
extra. This is also the number of results, which are fetched from one api
call. If we choose here 500, everytime 500 search results are fetched, if
available. It's recommended to choose a little number like 10 or 20.

* **FavoriteAdditions**: Here we can attach to the search term a suffix (for
 example ```language:C++```, if we only interested in C++
 programms). [At](https://help.github.com/articles/searching-for-repositories/)
 we can take a look at the syntax for this parameter.

* **HostingProvider**: An array, which hosting providers are used. Each hosting
    provider has a unique key (1 stands for github.com, 2 stands for gitlab.com).

## Build ##
### Dependencies ###
Maven is used for building the java part. Except the artifacts:
```xml
    <dependency>
      <groupId>net.sourceforge</groupId>
      <artifactId>docfetcher-util</artifactId>
      <version>1.0.0</version>
    </dependency>
```
and
```xml
    <dependency>
      <groupId>org.kohsuke</groupId>
      <artifactId>github-api</artifactId>
      <version>1.96</version>
    </dependency>
```
all dependencies are available in the central maven repository.

The artifact docfetcher-util is util package from
[https://sourceforge.net/p/docfetcher/code/ci/master/tree/](https://sourceforge.net/p/docfetcher/code/ci/master/tree/). The
jar must created manually and load into maven:
```
mvn install:install-file -D file=<path to your jar> -DgroupId=net.sourceforge -DartifactId=docfetcher-util -Dversion=1.0.0 -Dpackaging=jar
```
It is not the best way and in further releases it will be included in our
installation.

The artifact github-api version 1.96 is compiled from the source code of the
fork
[germanysources/github-api](https://github.com/germanysources/github-api). This
fork contains some necessary extensions for this application. The pull request
[#463](https://github.com/kohsuke/github-api/pull/463) to the parent repository is still open. When it is included in the
parent repository, this dependency will be available in the central maven repository.

## Notes ##
This application uses preview features from the [github api v3](https://developer.github.com/v3/). This preview
features can be changed without any notice. So we don't have a forerun to change the
application, if the preview features are changing.

## Contributing ##
Contributions are welcome. If you know an open source hosting provider, which
can be included in this application, you are welcome the integrate this
provider in the application.
For further information see [CONTRIBUTING.md](https://github.com/germanysources/FOSSwareSearch/blob/master/CONTRIBUTING.md).

## Need help? ##
* Open an issue in the
[tracker](https://github.com/germanysources/FOSSwareSearch/issues)

