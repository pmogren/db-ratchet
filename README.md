[![Build Status](https://travis-ci.org/commercehub-oss/db-ratchet.png?branch=master)](https://travis-ci.org/commercehub-oss/db-ratchet)

#What is db-ratchet?
db-ratchet is a tool to manage database compositions through the use of small incremental versions (individual clicks of a ratchet). This 
tool handles both the schema that defines a databases structure and behaviour as well as *control data* that the system requires
to be present in the database for application to function properly.

#Usage
db-ratchet manages a *repository-like* set of directories and files that make up the sources necessary to manage 
a database. db-ratchet uses a command pattern that users of Mercurial or Git will be familiar with. All db-ratchet
commands expect to be run from the root of a *repository* directory. Running `ratchet help` will print a list of 
currently commands supported by db-ratchet.

##Getting db-ratchet
Releases of db-ratchet can be found on [Bintray](https://bintray.com/commercehub-oss/apps/db-ratchet)

To install, simply unpack the zip file containing the release you want and make sure **&lt;install-root&gt;/db-ratchet/bin** 
is added to your path.

#Additional Documentation
- [User's Guide](docs/UsersGuide.md)
- [Developer's Guide](docs/DevelopersGuide.md)
- [Gradle Plugin](gradle-plugin/README.md)
- [Configuring Redgate SQL Compare](docs/ConfiguringRedgateSqlCompare.md)

---
*Interested in working with Markdown? Get started with this Chrome extension for viewing .md files: [Markdown Preview](https://chrome.google.com/webstore/detail/markdown-preview/jmchmkecamhbiokiopfpnfgbidieafmd)*
