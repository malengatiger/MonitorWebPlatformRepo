/*
 * To change this license header, choose License Headers in ProjectDTO Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.monitor.dto;

import com.boha.monitor.data.Project;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author aubreyM
 */
public class ProjectDTO implements Serializable {
    private List<PhotoUploadDTO> photoUploadList;
    private static final long serialVersionUID = 1L;
    private Integer projectID;
    private Integer programmeID, statusCount;
    private String projectName;
    private Double latitude;
    private Double longitude;
    private Float accuracy;
    private Boolean activeFlag;
    private Boolean locationConfirmed;
    private String address;
    private String description, programmeName;
    private List<ProjectTaskDTO> projectTaskList;
    private Integer companyID;
    private List<ProjectStatusDTO> projectStatusList;
    private List<GcmDeviceDTO> gcmDeviceList;
    private List<ChatDTO> chatList;
    private List<MonitorProjectDTO> monitorProjectList;
    private List<StaffProjectDTO> staffProjectList;
    private ProjectTaskStatusDTO lastStatus;

    public ProjectDTO() {
    }

    
    public ProjectDTO(Project a) {
        this.projectID = a.getProjectID();
        this.projectName = a.getProjectName();
        latitude = a.getLatitude();
        longitude = a.getLongitude();
        accuracy = a.getAccuracy();
        activeFlag = a.getActiveFlag();
        locationConfirmed = a.getLocationConfirmed();
        address = a.getAddress();
        description = a.getDescription();
        companyID = a.getCompany().getCompanyID();
        if (a.getProgramme() != null) {
            programmeName = a.getProgramme().getProgrammeName();
            programmeID = a.getProgramme().getProgrammeID();
        }
        
    }

    public String getProgrammeName() {
        return programmeName;
    }

    public void setProgrammeName(String programmeName) {
        this.programmeName = programmeName;
    }

    public ProjectTaskStatusDTO getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(ProjectTaskStatusDTO lastStatus) {
        this.lastStatus = lastStatus;
    }

    
    public Integer getStatusCount() {
        return statusCount;
    }

    public void setStatusCount(Integer statusCount) {
        this.statusCount = statusCount;
    }

    
    public List<PhotoUploadDTO> getPhotoUploadList() {
        return photoUploadList;
    }

    public void setPhotoUploadList(List<PhotoUploadDTO> photoUploadList) {
        this.photoUploadList = photoUploadList;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getProgrammeID() {
        return programmeID;
    }

    public void setProgrammeID(Integer programmeID) {
        this.programmeID = programmeID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Boolean getLocationConfirmed() {
        return locationConfirmed;
    }

    public void setLocationConfirmed(Boolean locationConfirmed) {
        this.locationConfirmed = locationConfirmed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ProjectTaskDTO> getProjectTaskList() {
        return projectTaskList;
    }

    public void setProjectTaskList(List<ProjectTaskDTO> projectTaskList) {
        this.projectTaskList = projectTaskList;
    }


    public List<ProjectStatusDTO> getProjectStatusList() {
        return projectStatusList;
    }

    public void setProjectStatusList(List<ProjectStatusDTO> projectStatusList) {
        this.projectStatusList = projectStatusList;
    }

    public List<GcmDeviceDTO> getGcmDeviceList() {
        return gcmDeviceList;
    }

    public void setGcmDeviceList(List<GcmDeviceDTO> gcmDeviceList) {
        this.gcmDeviceList = gcmDeviceList;
    }

    public List<ChatDTO> getChatList() {
        return chatList;
    }

    public void setChatList(List<ChatDTO> chatList) {
        this.chatList = chatList;
    }

    public List<MonitorProjectDTO> getMonitorProjectList() {
        return monitorProjectList;
    }

    public void setMonitorProjectList(List<MonitorProjectDTO> monitorProjectList) {
        this.monitorProjectList = monitorProjectList;
    }

    public List<StaffProjectDTO> getStaffProjectList() {
        return staffProjectList;
    }

    public void setStaffProjectList(List<StaffProjectDTO> staffProjectList) {
        this.staffProjectList = staffProjectList;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectID != null ? projectID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectDTO)) {
            return false;
        }
        ProjectDTO other = (ProjectDTO) object;
        if ((this.projectID == null && other.projectID != null) || (this.projectID != null && !this.projectID.equals(other.projectID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.monitor.data.Project[ projectID=" + projectID + " ]";
    }

   
    
}