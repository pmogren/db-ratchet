/*
 * This configuration allows for filtering out of specific files that you do not want SQL Compare pulling in to your
 * schema file store. This allows for finer grain control over what is captured than the filter file format SQLCompare
 * provides. You can filter based on white lists or blacklists on a per directory basis.
 *
 * This following config example filters out all scheams not in a whitelist and all users whose logins begin with
 * 'test'.
 *
 * filters {
 *     'Security/Schemas' {
 *         filterType = 'whitelist'
 *         patterns = [
 *                 'config.sql',
 *                 'report.sql'
 *         ]
 *     }
 *     'Security/Users' {
 *         filterType = 'blacklist'
 *         patterns = [
 *                 'test*.sql'
 *         ]
 *     }
 * }
 *
 */
filters {
    'Tables' {
        filterType = 'blacklist'
        patterns = [
                'dbo.schema_version.sql'
        ]
    }
}
