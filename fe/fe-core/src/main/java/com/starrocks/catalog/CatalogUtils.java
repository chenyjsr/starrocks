// This file is licensed under the Elastic License 2.0. Copyright 2021-present, StarRocks Limited.
package com.starrocks.catalog;

import com.google.common.collect.Sets;
import com.starrocks.analysis.SingleRangePartitionDesc;
import com.starrocks.common.DdlException;
import com.starrocks.common.ErrorCode;
import com.starrocks.common.ErrorReport;
import com.starrocks.thrift.TStorageMedium;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

public class CatalogUtils {

    private static final Logger LOG = LogManager.getLogger(CatalogUtils.class);

    // check table exist
    public static void checkTableExist(Database db, String tableName) throws DdlException {
        Table table = db.getTable(tableName);
        if (table == null) {
            ErrorReport.reportDdlException(ErrorCode.ERR_BAD_TABLE_ERROR, tableName);
        }
    }

    // check table type is OLAP
    public static void checkTableTypeOLAP(Database db, Table table) throws DdlException {
        if (table.getType() != Table.TableType.OLAP) {
            throw new DdlException("Table[" + table.getName() + "] is not OLAP table");
        }
    }

    // check table state
    public static void checkTableState(OlapTable olapTable, String tableName) throws DdlException {
        if (olapTable.getState() != OlapTable.OlapTableState.NORMAL) {
            throw new DdlException("Table[" + tableName + "]'s state is not NORMAL");
        }
    }

    // check partition name exist for batch add partition
    public static Set<String> checkPartitionNameExistForAddPartitions(OlapTable olapTable,
                                                                      List<SingleRangePartitionDesc> singleRangePartitionDescs)
            throws DdlException {
        Set<String> existPartitionNameSet = Sets.newHashSet();
        for (SingleRangePartitionDesc singleRangePartitionDesc : singleRangePartitionDescs) {
            String partitionName = singleRangePartitionDesc.getPartitionName();
            if (olapTable.checkPartitionNameExist(partitionName)) {
                if (singleRangePartitionDesc.isSetIfNotExists()) {
                    existPartitionNameSet.add(partitionName);
                } else {
                    ErrorReport.reportDdlException(ErrorCode.ERR_SAME_NAME_PARTITION, partitionName);
                }
            }
        }
        return existPartitionNameSet;
    }

    public static boolean isUseStarOS(TStorageMedium storageMedium) {
        return storageMedium == TStorageMedium.S3;
    }
}
