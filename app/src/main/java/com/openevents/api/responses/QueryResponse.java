package com.openevents.api.responses;

import java.io.Serializable;

public class QueryResponse implements Serializable {
    private int fieldCount;
    private int affectedRows;
    private int insertID;
    private String info;
    private int serverStatus;
    private int warningStatus;
    private int changedRows;

    public QueryResponse(int fieldCount, int affectedRows, int insertID, String info,
                         int serverStatus, int warningStatus) {
        this.fieldCount = fieldCount;
        this.affectedRows = affectedRows;
        this.insertID = insertID;
        this.info = info;
        this.serverStatus = serverStatus;
        this.warningStatus = warningStatus;
    }

    public QueryResponse(int fieldCount, int affectedRows, int insertID, String info,
                         int serverStatus, int warningStatus, int changedRows) {
        this.fieldCount = fieldCount;
        this.affectedRows = affectedRows;
        this.insertID = insertID;
        this.info = info;
        this.serverStatus = serverStatus;
        this.warningStatus = warningStatus;
        this.changedRows = changedRows;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public int getAffectedRows() {
        return affectedRows;
    }

    public int getInsertID() {
        return insertID;
    }

    public String getInfo() {
        return info;
    }

    public int getServerStatus() {
        return serverStatus;
    }

    public int getWarningStatus() {
        return warningStatus;
    }

    public int getChangedRows() {
        return changedRows;
    }
}
