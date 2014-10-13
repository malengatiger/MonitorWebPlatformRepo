/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.monitor.dto;

import com.boha.monitor.data.Project;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aubreyM
 */
public class ProjectDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer projectID;
    private String projectName;
    private String description;
    private long dateRegistered;
    private Integer completeFlag;
    private Integer companyID, clientID;
    private List<ProjectSiteDTO> projectSiteList = new ArrayList<>();

    public ProjectDTO() {
    }

    public ProjectDTO(Integer projectID) {
        this.projectID = projectID;
    }

    public ProjectDTO(Project a) {
        this.projectID = a.getProjectID();
        this.projectName = a.getProjectName();
        this.dateRegistered = a.getDateRegistered().getTime();
        this.description = a.getDescription();
        this.completeFlag = a.getCompleteFlag();
        this.companyID = a.getCompany().getCompanyID();
        if (a.getClient() != null) {
            this.clientID = a.getClient().getClientID();
        }
    }

    public Integer getClientID() {
        return clientID;
    }

    public void setClientID(Integer clientID) {
        this.clientID = clientID;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(long dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public Integer getCompleteFlag() {
        return completeFlag;
    }

    public void setCompleteFlag(Integer completeFlag) {
        this.completeFlag = completeFlag;
    }

    public List<ProjectSiteDTO> getProjectSiteList() {
        return projectSiteList;
    }

    public void setProjectSiteList(List<ProjectSiteDTO> projectSiteList) {
        this.projectSiteList = projectSiteList;
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
