package com.about.zhiye.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import static com.about.zhiye.util.DateUtil.SIMPLE_DATE_FORMAT_WITH_TIME;

/**
 * Created by huangyuefeng on 2017/5/6.
 * Contact me : mcxinyu@foxmail.com
 */
public class VersionInfoFir {

    /**
     * 应用名称 name : 知也
     * 版本 version : 1
     * 更新日志 changelog : 终于上线了。
     * 更新时间 updated_at : 1494047446
     * 版本编号(兼容旧版字段) versionShort : 1.0.0
     * 编译号 build : 1
     * 安装地址（兼容旧版字段） installUrl : http://download.fir.im/v2/app/install/590d5a5b959d690e2e000163?download_token=74ce25f9b1e3c6be17c832d800511087&source=update
     * 安装地址(新增字段) install_url : http://download.fir.im/v2/app/install/590d5a5b959d690e2e000163?download_token=74ce25f9b1e3c6be17c832d800511087&source=update
     * direct_install_url : http://download.fir.im/v2/app/install/590d5a5b959d690e2e000163?download_token=74ce25f9b1e3c6be17c832d800511087&source=update
     * 更新地址(新增字段) update_url : http://fir.im/yf7v
     * 更新文件的对象，仅有大小字段fsize binary : {"fsize":4295842}
     */

    @SerializedName("name")
    private String name;
    @SerializedName("version")
    private int version;
    @SerializedName("changelog")
    private String changelog;
    @SerializedName("updated_at")
    private int updatedAt;
    @SerializedName("versionShort")
    private String versionShort;
    @SerializedName("build")
    private int build;
    @SerializedName("installUrl")
    private String installUrl;
    @SerializedName("install_url")
    private String installUrl2;
    @SerializedName("direct_install_url")
    private String directInstallUrl;
    @SerializedName("update_url")
    private String updateUrl;
    @SerializedName("binary")
    private BinaryBean binary;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getUpdatedAt() {
        return SIMPLE_DATE_FORMAT_WITH_TIME.format(new Date(Long.valueOf(updatedAt + "000")));
    }

    public void setUpdatedAt(int updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVersionShort() {
        return versionShort;
    }

    public void setVersionShort(String versionShort) {
        this.versionShort = versionShort;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public String getInstallUrl() {
        return installUrl;
    }

    public void setInstallUrl(String installUrl) {
        this.installUrl = installUrl;
    }

    public String getInstallUrl2() {
        return installUrl2;
    }

    public void setInstallUrl2(String installUrl2) {
        this.installUrl2 = installUrl2;
    }

    public String getDirectInstallUrl() {
        return directInstallUrl;
    }

    public void setDirectInstallUrl(String directInstallUrl) {
        this.directInstallUrl = directInstallUrl;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public BinaryBean getBinary() {
        return binary;
    }

    public void setBinary(BinaryBean binary) {
        this.binary = binary;
    }

    public static class BinaryBean {
        /**
         * fsize : 4295842
         */

        @SerializedName("fsize")
        private int fileSize;

        public int getFileSize() {
            return fileSize / 1024 / 1024;
        }

        public void setFileSize(int fileSize) {
            this.fileSize = fileSize;
        }
    }
}
