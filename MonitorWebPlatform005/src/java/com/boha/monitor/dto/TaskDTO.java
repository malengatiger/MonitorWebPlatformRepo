/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.boha.monitor.dto;

import com.boha.monitor.data.Task;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author aubreyM
 */
public class TaskDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer taskID;
    private String taskName;
    private Integer taskNumber;
    private String description;
    private List<ProjectTaskDTO> projectTaskList;
    private List<SubTaskDTO> subTaskList;
    private Integer taskTypeID;

    public TaskDTO() {
    }

    public TaskDTO(Task a) {
        this.taskID = a.getTaskID();
        this.taskName = a.getTaskName();
        description = a.getDescription();
        taskNumber = a.getTaskNumber();
        taskTypeID = a.getTaskType().getTaskTypeID();
    }

    public List<SubTaskDTO> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(List<SubTaskDTO> subTaskList) {
        this.subTaskList = subTaskList;
    }

    public Integer getTaskTypeID() {
        return taskTypeID;
    }

    public void setTaskTypeID(Integer taskTypeID) {
        this.taskTypeID = taskTypeID;
    }

  

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Integer getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(Integer taskNumber) {
        this.taskNumber = taskNumber;
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



    @Override
    public int hashCode() {
        int hash = 0;
        hash += (taskID != null ? taskID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TaskDTO)) {
            return false;
        }
        TaskDTO other = (TaskDTO) object;
        if ((this.taskID == null && other.taskID != null) || (this.taskID != null && !this.taskID.equals(other.taskID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.boha.monitor.data.Task[ taskID=" + taskID + " ]";
    }
    
}
