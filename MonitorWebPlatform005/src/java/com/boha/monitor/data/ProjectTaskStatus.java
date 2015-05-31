/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.monitor.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author aubreyM
 */
@Entity
@Table(name = "projectTaskStatus")
@NamedQueries({
    @NamedQuery(name = "ProjectTaskStatus.findByProject",
            query = "SELECT p FROM ProjectTaskStatus p WHERE p.projectTask.project.projectID = :projectID ORDER BY p.dateUpdated desc")
})
public class ProjectTaskStatus implements Serializable {

    @JoinColumn(name = "projectTaskID", referencedColumnName = "projectTaskID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private ProjectTask projectTask;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "projectTaskStatusID")
    private Integer projectTaskStatusID;
    @Column(name = "statusDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "dateUpdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateUpdated;

    @JoinColumn(name = "taskStatusTypeID", referencedColumnName = "taskStatusTypeID")
    @ManyToOne(optional = false)
    private TaskStatusType taskStatusType;
    @JoinColumn(name = "staffID", referencedColumnName = "staffID")
    @ManyToOne
    private Staff staff;
    @JoinColumn(name = "monitorID", referencedColumnName = "monitorID")
    @ManyToOne
    private Monitor monitor;

    public ProjectTaskStatus() {
    }

    public ProjectTaskStatus(Integer projectTaskStatusID) {
        this.projectTaskStatusID = projectTaskStatusID;
    }

    public ProjectTaskStatus(Integer projectTaskStatusID, Date dateUpdated) {
        this.projectTaskStatusID = projectTaskStatusID;
        this.dateUpdated = dateUpdated;
    }

    public Integer getProjectTaskStatusID() {
        return projectTaskStatusID;
    }

    public void setProjectTaskStatusID(Integer projectTaskStatusID) {
        this.projectTaskStatusID = projectTaskStatusID;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public TaskStatusType getTaskStatusType() {
        return taskStatusType;
    }

    public void setTaskStatusType(TaskStatusType taskStatusType) {
        this.taskStatusType = taskStatusType;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (projectTaskStatusID != null ? projectTaskStatusID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProjectTaskStatus)) {
            return false;
        }
        ProjectTaskStatus other = (ProjectTaskStatus) object;
        if ((this.projectTaskStatusID == null && other.projectTaskStatusID != null) || (this.projectTaskStatusID != null && !this.projectTaskStatusID.equals(other.projectTaskStatusID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.monitor.data.ProjectTaskStatus[ projectTaskStatusID=" + projectTaskStatusID + " ]";
    }

    public ProjectTask getProjectTask() {
        return projectTask;
    }

    public void setProjectTask(ProjectTask projectTask) {
        this.projectTask = projectTask;
    }

}
