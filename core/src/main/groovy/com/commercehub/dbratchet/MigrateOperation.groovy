package com.commercehub.dbratchet

import com.commercehub.dbratchet.data.DataPackage
import com.commercehub.dbratchet.data.DataPackageConfig
import com.commercehub.dbratchet.filestore.FileStore
import com.commercehub.dbratchet.util.GroovySqlRunner
import groovy.sql.Sql

/**
 * Created with IntelliJ IDEA.
 * User: jgelais
 * Date: 9/25/13
 * Time: 3:52 PM
 */
class MigrateOperation implements Operation {
    final String name = 'Migrate'

    DatabaseConfig dbConfig
    FileStore fileStore

    MigrateOperation(DatabaseConfig dbConfig, FileStore fileStore) {
        this.dbConfig = dbConfig
        this.fileStore = fileStore
    }

    @Override
    boolean run() {
        Date startTime = new Date()
        DataPackageConfig dataPackageConfig = DataPackageConfig.load(fileStore)
        dataPackageConfig.packages.each  { dataPackage->
            migratePackage(dataPackage)
        }
        Date endTime = new Date()
        println "TOTAL Migration time =  ${endTime.time - startTime.time} milliseconds."
        return true
    }

    @Override
    boolean isConfigured() {
        if (!dbConfig.server) {
            return false
        }

        if (!dbConfig.database) {
            return false
        }

        if (dbConfig.user && !dbConfig.password) {
            return false
        }

        return true
    }

    private void migratePackage(DataPackage dataPackage) {
        Date startTime = new Date()
        long mergeTime
        Sql sql = GroovySqlRunner.getSql(dbConfig)
        try {
            Map<String, String> tableToTempTableMap = getTableToTempTableMap(dataPackage)
            generateTempTables(dataPackage, tableToTempTableMap, sql)
            loadTempTables(dataPackage, tableToTempTableMap, sql)
            mergeTime = mergeTables(dataPackage, tableToTempTableMap, sql)
        } finally {
            sql.close()
        }
        Date endTime = new Date()
        println "Migrated package [${dataPackage.name}] in ${endTime.time - startTime.time} milliseconds."
        println "    -> Time in MERGE transaction: ${mergeTime} milliseconds."
    }

    private long mergeTables(DataPackage dataPackage, Map<String, String> tableToTempTableMap, Sql sql) {
        List<String> mergeStatements = getMergeStatementsForDataPackage(dataPackage, tableToTempTableMap, sql)
        Date startTime = new Date()
        sql.withTransaction {
            mergeStatements.each { mergeSql->
                sql.execute(mergeSql)
            }
        }
        Date endTime = new Date()

        return endTime.time - startTime.time
    }

    private List<String> getMergeStatementsForDataPackage(DataPackage dataPackage,
                                                          Map<String, String> tableToTempTableMap, Sql sql) {
        List<String> returnList = [] as Queue<String>
        dataPackage.tables.each { table->
            returnList.add(getMergeSqlForTable(table, tableToTempTableMap, sql))
        }

        return returnList
    }

    private String getMergeSqlForTable(String table, Map<String, String> tableToTempTableMap, Sql sql) {
        List<String> pkColumns = getPrimaryKeyColumnsForTable(sql, table)
        List<String> dataColumns = getDataColumnsForTable(sql, table, pkColumns)
        String sqlStr = """merge ${table} as target
                            using ${tableToTempTableMap.get(table)} as source
                            on ${getMatchSql(pkColumns)}
                            when matched
                                then update set ${printColumnUpdates(dataColumns)}
                            when not matched by target
                                then insert (${printColumnList(pkColumns, dataColumns, '')})
                                     values (${printColumnList(pkColumns, dataColumns, 'source.')})
                            when not matched by source
                                then delete;"""

        return sqlStr
    }

    private String getMatchSql(List<String> pkColumns) {
        boolean isFirst = true
        String returnStr = ''
        pkColumns.each { column->
            if (isFirst) {
                returnStr += "target.${column} = source.${column}"
                isFirst = false
            } else {
                returnStr += " and target.${column} = source.${column}"
            }
        }

        return returnStr
    }

    private String printColumnUpdates(List<String> dataColumns) {
        boolean isFirst = true
        String returnStr = ''
        dataColumns.each  { column->
            if (isFirst) {
                returnStr += "target.${column} = source.${column}"
                isFirst = false
            } else {
                returnStr += ", target.${column} = source.${column}"
            }
        }

        return returnStr
    }

    private String printColumnList(List<String> pkColumns, List<String> dataColumns, String prepend) {
        boolean isFirst = true
        String returnStr = ''
        pkColumns.each { column->
            if (isFirst) {
                returnStr += "${prepend}${column}"
                isFirst = false
            } else {
                returnStr += ", ${prepend}${column}"
            }
        }

        dataColumns.each { column->
            if (isFirst) {
                returnStr += "${prepend}${column}"
                isFirst = false
            } else {
                returnStr += ", ${prepend}${column}"
            }
        }

        return returnStr
    }

    private void loadTempTables(DataPackage dataPackage, Map<String, String> tableToTempTableMap, Sql sql) {
        def dataset = new XmlSlurper().parse(dataPackage.dataInputStream)
        dataPackage.tables.each { table->
            if (dataset."${table}".size() > 0) {
                def sampleRow = dataset."${table}"[(0)]
                sql.withBatch(1000, getParameterizedInsertToTempTable(tableToTempTableMap, sampleRow)) { stmt->
                    dataset."${table}".each { row->
                        stmt.addBatch(row.attributes())
                    }
                }
            }
        }
    }

    @SuppressWarnings('DuplicateStringLiteral')
    private String getParameterizedInsertToTempTable(Map<String, String> tableToTempTableMap, row) {
        String realTableName = row.name()
        List<String> columns = getColumnListFromRow(row)
        String sqlStr = "insert into ${tableToTempTableMap.get(realTableName)} ("
        boolean isFirstCol = true
        columns.each { col->
            if (!isFirstCol) {
                sqlStr += ', '
            }
            sqlStr += "${col}"
            isFirstCol = false
        }
        sqlStr += ') values ('
        isFirstCol = true
        columns.each { col->
            if (!isFirstCol) {
                sqlStr += ', '
            }
            sqlStr += ":${col}"
            isFirstCol = false
        }
        sqlStr += ')'

        return sqlStr
    }

    private List<String> getColumnListFromRow(row) {
        List<String> list = [] as Queue<String>
        row.attributes().keySet().each { attr->
            list.add(attr)
        }

        return list
    }

    private void generateTempTables(DataPackage dataPackage, Map<String, String> tableToTempTableMap, Sql sql) {
        dataPackage.tables.each { table ->
            sql.execute(getCreateTempTableSql(table, tableToTempTableMap.get(table)))
        }
    }

    private Map<String, String> getTableToTempTableMap(DataPackage dataPackage) {
        Map<String, String> tableToTempTableMap = [:]
        dataPackage.tables.each { table->
            tableToTempTableMap.put(table, getTempTableName(table))
        }

        return tableToTempTableMap
    }

    private String getTempTableName(String realTableName) {
        String escapedTableName = realTableName.replaceAll('\\.', '_')
        return "#temp_${escapedTableName}"
    }

    private String getCreateTempTableSql(String realTableName, String tempTableName) {
        return "select top 0 * into ${tempTableName} from ${realTableName}"
    }

    private List<String> getPrimaryKeyColumnsForTable(Sql sql, String tableName) {
        TableSpec tableSpec = new TableSpec(tableName)
        List<String> pkColumns = [] as Queue<String>

        def pkQuery = '''SELECT c.name
                        FROM   sys.objects AS pk
                               INNER JOIN sys.objects AS tbl
                                       ON tbl.[object_id] = pk.parent_object_id
                               INNER JOIN sys.schemas AS s
                                       ON s.[schema_id] = tbl.[schema_id]
                               INNER JOIN sys.indexes AS i
                                       ON i.[object_id] = tbl.[object_id]
                                          AND i.name = pk.name
                                          AND i.is_primary_key = 1
                               INNER JOIN sys.index_columns AS ic
                                       ON ic.[object_id] = i.[object_id]
                                          AND ic.index_id = i.index_id
                               INNER JOIN sys.[columns] AS c
                                       ON c.column_id = ic.column_id
                                          AND c.[object_id] = tbl.[object_id]
                        WHERE  tbl.name = ? AND s.name = ?'''

        sql.eachRow(pkQuery, [tableSpec.name, tableSpec.schema]) { row ->
            pkColumns.add(row.name)
        }

        return pkColumns
    }

    private List<String> getDataColumnsForTable(Sql sql, String tableName, List<String> pkColumns) {
        List<String> dataColumns = [] as Queue<String>
        sql.eachRow('select name from sys.columns where object_id = OBJECT_ID(?) order by column_id asc',
                [tableName]) { row->
            if (!pkColumns.contains(row.name)) {
                dataColumns.add(row.name)
            }
        }

        return dataColumns
    }

    class TableSpec {
        String schema
        String name

        TableSpec(String tableName) {
            String[] nameParts = tableName.split('\\.')
            if (nameParts.length == 1) {
                schema = 'dbo'
                name = tableName
            } else if (nameParts.length == 2) {
                schema = nameParts[0]
                name = nameParts[1]
            } else {
                throw new IllegalArgumentException("Unable to parse table names [${tableName}]")
            }
        }
    }
}


