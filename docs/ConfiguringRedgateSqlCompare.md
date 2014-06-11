#Configuring Redgate SQL Compare
This document covers what additional configurations db-ratchet supports when using the Redgate SQL Compare schema 
difference engine.

## Redgate SQL Compare Schema Store
The Redgate SQL Compare based Schema Difference Engine store schema and configuration in the following two folders off
the root of the database folder:

1. **/redgate-schema**
    
    The contents of this folder are managed by SQLCompare when this folder is used as the value for on of 
    the `/Scripts` switches. A typical layout will look something like this, with a .sql file for each schema element
    stored in this repository. Changes to the SQLCompare filters from the default may result in a different structure.
        
        /redgate-schema
            |-/Functions
            |-/Security
            |   |-/Schemas
            |-/Stored Procedures
            |-/Tables
            |-RedGateDatabaseInfo.xml

2. **/redgate-config**
    
    This folder contains Redgate SQLCompare specific configurations. Reasonable defaults are provided when running 
    the `init` operation. Further information provided below on how to change these files to implement custom schema 
    filtering.

## Ensuring db-ratchet finds SQLCompare.exe on your system
db-ratchet will check for SQLCompare.exe in the following locations (in this order):

1. Available on the current user's path.
2. At the path specified by the `SQLCOMPARE_HOME` environment variable if present.
3. At the default installation location (for version 10): `C:\Program Files (x86)\Red Gate\SQL Compare 10\`

## Filtering schema comparisons
The Redgate SQL Compare schema difference engine supports two methods for filtering what schema elements are considered
during comparison.

1. **/redgate-config/filter.scpf**

    This is redgate's XML based filter format. A sample starting point version of this file will be added to your schema
    repository by the  `init` operation. This file can be editted from within the SQL Compare GUI.

2. **/redgate-config/file-filter-config.groovy**
    
    Due to some limitations of SQL Compare's filtering capabilities as it relates to the `pull` operation, an
    additional mechanism is available to create whitelists and blacklists controling what files should be present after
    a `pull` operation. This file is a groovy properties file. The initial version fo this file created by the `init`
    operation will filter out Flyway's schema_version table. Whitelists and blacklists can be configured in the 
    following manor:
    
        filters {
            // Will blacklist files name 'dbo.schema_version.sql' in the Tables folder.
            'Tables' {
                filterType = 'blacklist'
                patterns = [
                        'dbo.schema_version.sql'
                ]
            }
            // Will whitelist only approved database schemas in the Schema folder.
            'Security/Schemas' {
                filterType = 'whitelist'
                patterns = [
                    'config.sql',
                    'report.sql'
                ]
            }
        }


---
*Interested in working with Markdown? Get started with this Chrome extension for viewing .md files: [Markdown Preview](https://chrome.google.com/webstore/detail/markdown-preview/jmchmkecamhbiokiopfpnfgbidieafmd)*
