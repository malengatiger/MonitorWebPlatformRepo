/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.monitor.dto;

import com.boha.monitor.data.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author aubreyM
 */
public class StaffProjectDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer staffProjectID;
    private Date dateAssigned;
    private boolean activeFlag;
    private Integer projectID;
    private Integer companyStaffID;

    public StaffProjectDTO() {
    }

    public StaffProjectDTO(Integer staffProjectID) {
        this.staffProjectID = staffProjectID;
    }

    public StaffProjectDTO(StaffProject a) {
        this.staffProjectID = a.getStaffProjectID();
        this.dateAssigned = a.getDateAssigned();
        this.activeFlag = a.getActiveFlag();
        this.projectID = a.getProject().getProjectID();
        this.companyStaffID = a.getCompanyStaff().getCompanyStaffID();
    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID(Integer projectID) {
        this.projectID = projectID;
    }

    public Integer getCompanyStaffID() {
        return companyStaffID;
    }

    public void setCompanyStaffID(Integer companyStaffID) {
        this.companyStaffID = companyStaffID;
    }
    

    public Integer getStaffProjectID() {
        return staffProjectID;
    }

    public void setStaffProjectID(Integer staffProjectID) {
        this.staffProjectID = staffProjectID;
    }

    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (staffProjectID != null ? staffProjectID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof StaffProjectDTO)) {
            return false;
        }
        StaffProjectDTO other = (StaffProjectDTO) object;
        if ((this.staffProjectID == null && other.staffProjectID != null) || (this.staffProjectID != null && !this.staffProjectID.equals(other.staffProjectID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.monitor.data.StaffProject[ staffProjectID=" + staffProjectID + " ]";
    }
    
}
