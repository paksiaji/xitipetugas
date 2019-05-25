package com.example.xitipetugas;

public class UploadResultModel {
    private String status;
    private String file_name;

    public UploadResultModel(String status, String file_name) {
        this.status = status;
        this.file_name = file_name;
    }

    public String getStatus() {
        return status;
    }

    public String getFile_name() {
        return file_name;
    }
}
