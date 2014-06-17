# Changelog

*   0.2.2
    *   Added `jarName` property to `dbRatchet` extension of gradle plugin. This will allow users to specify a different 
        name for the database one-jar produced by the build.
*   0.2.1
    *   Support for post create script to run on `build` operation.
*   0.2.0
    *   gradle plugin for packing database schema and data into a on-jar deployment.
    *   whitelist/blacklist post `pull` operation filtering of schema files for red-gate SQLCompare schemas.
*   0.1.0
    *   Initial version
    *   Support for using redgate SQL COmpare for schema difference engine
    *   Support for DbUnit style data migrations
