#Using the db-ratchet gradle plugin
To use the db-ratchet gradle plugin place a `build.gradle` in the root of your db-ratchet database directory with the
following content:

    buildscript {
         repositories {
              jcenter()
         }
         dependencies {
            classpath "com.commercehub:db-ratchet-gradle-plugin:0.2.1"
        }
    }
    apply plugin: 'db-ratchet'
    
    repositories {
          jcenter()
    }

The plugin adds the following tasks to the build:

- **package**: This tasks bundles the db-ratchet-core library and it's dependencies into a *Fat-Jar* along with your
databases schema version scripts, static scripts and data packages.
- **build**: This task does nothing and depends on package. It's here for convenience when configuring a database build
as a sub-project in a multi-project gradle build. This way when you run a *build* task to compile your application this
jar will be produced as well.

The *Fat-Jar* produced by this plugin can be used as follows:

    usage: java -jar <deployment-jar>.jar [options]                            
     -d,--database <database>   Name of database to create. REQUIRED.          
        --data-only             Only build out control data.                   
     -h,--help                  Displays this usage message.                   
     -p,--password <password>   Database server password to use. Leave blank   
                                to use Active Directory authentication.        
     -s,--server <server>       Database server to create database on.         
                                REQUIRED if alias not present.                 
        --schema-only           Only build out database schema.                
     -u,--user <user>           Database server login to use. Leave blank to   
                                use Active Directory authentication.           
     -v,--version <version>     Schema version to use in creating this         
                                database.


## Additional Configuration
The db-ratchet gradle plaugin supports setting the version for the db-ratchet-core jar to bundle in the database 
*Fat-Jar* by specifying the following property as part of the **dbRatchet** extension.

    dbRatchet {
        dbRatchetVersion = '0.2.1'
    }

---
*Interested in working with Markdown? Get started with this Chrome extension for viewing .md files: [Markdown Preview](https://chrome.google.com/webstore/detail/markdown-preview/jmchmkecamhbiokiopfpnfgbidieafmd)*
