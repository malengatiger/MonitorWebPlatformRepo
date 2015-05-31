/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.monitor.util;

import com.boha.monitor.data.*;
import com.boha.monitor.dto.*;
import com.boha.monitor.dto.transfer.*;
import static com.boha.monitor.util.DataUtil.log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.joda.time.DateTime;

/**
 *
 * @author aubreyM
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ListUtil {

    @PersistenceContext
    EntityManager em;

    public ResponseDTO loginStaff(GcmDeviceDTO device, String email,
            String pin, ListUtil listUtil) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        resp.setStaffList(new ArrayList<>());
        Query q = null;
        try {
            q = em.createNamedQuery("Staff.login", Staff.class);
            q.setParameter("email", email);
            q.setParameter("pin", pin);
            q.setMaxResults(1);
            Staff cs = (Staff) q.getSingleResult();
            Company company = cs.getCompany();
            resp.getStaffList().add(new StaffDTO(cs));
            resp.setCompany(new CompanyDTO(company));

            device.setCompanyID(company.getCompanyID());
            device.setStaffID(cs.getStaffID());
            addDevice(device);


            q = em.createNamedQuery("StaffProject.findByStaff", StaffProject.class);
            q.setParameter("staffID", cs.getStaffID());
            List<StaffProject> sList = q.getResultList();
            resp.setProjectList(new ArrayList<>());
            for (StaffProject x : sList) {
                resp.getProjectList().add(new ProjectDTO(x.getProject()));
            }

        } catch (NoResultException e) {
            log.log(Level.WARNING, "Invalid login attempt: " + email + " pin: " + pin, e);
            resp.setStatusCode(ServerStatus.ERROR_LOGGING_IN);
            resp.setMessage(ServerStatus.getMessage(resp.getStatusCode()));
        }
        return resp;
    }

    public ResponseDTO loginMonitor(GcmDeviceDTO device, String email,
            String pin, ListUtil listUtil) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        resp.setMonitorList(new ArrayList<>());
        Query q = null;
        try {
            q = em.createNamedQuery("Monitor.login", Monitor.class);
            q.setParameter("email", email);
            q.setParameter("pin", pin);
            q.setMaxResults(1);
            Monitor cs = (Monitor) q.getSingleResult();
            Company company = cs.getCompany();
            resp.getMonitorList().add(new MonitorDTO(cs));
            resp.setCompany(new CompanyDTO(company));

            device.setCompanyID(company.getCompanyID());
            device.setMonitorID(cs.getMonitorID());
            addDevice(device);

            setProjectDetailsForMonitor(resp, cs.getMonitorID());
            resp.setTaskStatusTypeList(getTaskStatusTypeList(company.getCompanyID()).getTaskStatusTypeList());

        } catch (NoResultException e) {
            log.log(Level.WARNING, "Invalid monitor login attempt: " + email + " pin: " + pin, e);
            resp.setStatusCode(ServerStatus.ERROR_LOGGING_IN);
            resp.setMessage(ServerStatus.getMessage(resp.getStatusCode()));
        }
        return resp;
    }

    public ResponseDTO getMonitorProjects(Integer monitorID) throws DataException{
        ResponseDTO resp = new ResponseDTO();
        try {
            setProjectDetailsForMonitor(resp, monitorID);
        } catch (Exception e) {
            
            throw new DataException("Falied to get monitor projects");
        }
        
        return resp;
    }
    private void setProjectDetailsForMonitor(ResponseDTO resp, Integer monitorID) {
        Query q = em.createNamedQuery("MonitorProject.findByMonitor", Project.class);
        q.setParameter("monitorID", monitorID);
        List<Project> sList = q.getResultList();
        resp.setProjectList(new ArrayList<>());
        for (Project x : sList) {
            ProjectDTO dto = new ProjectDTO(x);
            dto.setProjectTaskList(new ArrayList<>());

            for (ProjectTask pts : x.getProjectTaskList()) {
                ProjectTaskDTO pt = new ProjectTaskDTO(pts);

                pt.setProjectTaskStatusList(new ArrayList<>());
                for (ProjectTaskStatus xx : pts.getProjectTaskStatusList()) {
                    pt.getProjectTaskStatusList().add(new ProjectTaskStatusDTO(xx));
                }
                dto.getProjectTaskList().add(pt);

            }

            resp.getProjectList().add(dto);
        }
    }

    public ResponseDTO getCompanyData(Integer companyID) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("Project.findByCompany", Project.class);
        q.setParameter("companyID", companyID);
        List<Project> list = q.getResultList();
        resp.setProjectList(new ArrayList<>());
        for (Project cm : list) {
            resp.getProjectList().add(new ProjectDTO(cm));
        }

        resp.setStaffList(getCompanyStaffList(companyID).getStaffList());
        resp.setTaskStatusTypeList(getTaskStatusTypeList(companyID).getTaskStatusTypeList());
        resp.setProjectStatusTypeList(getProjectStatusTypeList(companyID).getProjectStatusTypeList());
        resp.setTaskTypeList(getTaskTypeList(companyID).getTaskTypeList());
        resp.setMonitorList(getMonitorList(companyID).getMonitorList());

        return resp;
    }

    public void addDevice(GcmDeviceDTO d) throws DataException {
        try {
            GcmDevice g = new GcmDevice();
            g.setCompany(em.find(Company.class, d.getCompanyID()));
            if (d.getStaffID() != null) {
                g.setStaff(em.find(Staff.class, d.getStaffID()));
            }
            if (d.getMonitorID() != null) {
                g.setMonitor(em.find(Monitor.class, d.getMonitorID()));
            }

            g.setDateRegistered(new Date());
            g.setManufacturer(d.getManufacturer());
            g.setMessageCount(0);
            g.setModel(d.getModel());
            g.setRegistrationID(d.getRegistrationID());
            g.setSerialNumber(d.getSerialNumber());
            g.setProduct(d.getProduct());
            g.setAndroidVersion(d.getAndroidVersion());

            em.persist(g);
            log.log(Level.WARNING, "New device loaded");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to add device\n" + getErrorString(e));

        }
    }

    public ResponseDTO getMonitorList(Integer companyID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("Monitor.findByCompany", Monitor.class);
        q.setParameter("companyID", companyID);
        List<Monitor> tList = q.getResultList();
        resp.setMonitorList(new ArrayList<>());
        for (Monitor mon : tList) {
            resp.getMonitorList().add(new MonitorDTO(mon));
        }
        return resp;
    }

    public ResponseDTO getTaskTypeList(Integer companyID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("TaskType.findByCompany", TaskType.class);
        q.setParameter("companyID", companyID);
        List<TaskType> tList = q.getResultList();
        resp.setTaskTypeList(new ArrayList<>());
        for (TaskType taskType : tList) {
            resp.getTaskTypeList().add(new TaskTypeDTO(taskType));
        }
        return resp;
    }

    public ResponseDTO getMessagesByProjectAndStaff(Integer projectID, Integer companyStaffID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("ChatMessage.findByProjectAndStaff", ChatMessage.class);
        q.setParameter("projectID", projectID);
        q.setParameter("companyStaffID", companyStaffID);
        List<ChatMessage> list = q.getResultList();
        resp.setChatMessageList(new ArrayList<>());
        for (ChatMessage cm : list) {
            resp.getChatMessageList().add(new ChatMessageDTO(cm));
        }
        return resp;
    }

    public ResponseDTO getMessagesByProject(Integer projectID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("ChatMessage.findByProject", ChatMessage.class);
        q.setParameter("projectID", projectID);
        List<ChatMessage> list = q.getResultList();
        resp.setChatMessageList(new ArrayList<>());
        for (ChatMessage cm : list) {
            resp.getChatMessageList().add(new ChatMessageDTO(cm));
        }

        return resp;
    }

    public ResponseDTO getChatsByProject(Integer projectID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("Chat.findByProject", Chat.class);
        q.setParameter("projectID", projectID);
        List<Chat> list = q.getResultList();
        q = em.createNamedQuery("ChatMessage.findByProject", ChatMessage.class);
        q.setParameter("projectID", projectID);
        List<ChatMessage> cmList = q.getResultList();
        q = em.createNamedQuery("ChatMember.findByProject", ChatMember.class);
        q.setParameter("projectID", projectID);
        List<ChatMember> mmList = q.getResultList();
        resp.setChatList(new ArrayList<>());
        for (Chat t : list) {
            ChatDTO xx = new ChatDTO(t);
            xx.setChatMessageList(new ArrayList<>());
            xx.setChatMemberList(new ArrayList<>());
            for (ChatMessage cm : cmList) {
                if (Objects.equals(cm.getChat().getChatID(), t.getChatID())) {
                    xx.getChatMessageList().add(new ChatMessageDTO(cm));
                }
            }
            for (ChatMember mm : mmList) {
                if (Objects.equals(mm.getChat().getChatID(), t.getChatID())) {
                    xx.getChatMemberList().add(new ChatMemberDTO(mm));
                }
            }
            resp.getChatList().add(xx);
        }

        return resp;
    }

    public ResponseDTO getLocationTracksByStaff(Integer companyStaffID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("LocationTracker.findByStaff", LocationTracker.class);
        q.setParameter("companyStaffID", companyStaffID);
        List<LocationTracker> list = q.getResultList();
        resp.setLocationTrackerList(new ArrayList<>());
        for (LocationTracker t : list) {
            resp.getLocationTrackerList().add(new LocationTrackerDTO(t));
        }

        return resp;
    }

    public ResponseDTO getLocationTracksByStaffInPeriod(Integer companyStaffID,
            Long df, Long dx) {
        Date dateFrom, dateTo;
        if (df == null) {
            DateTime dt = new DateTime();
            DateTime xx = dt.minusDays(7);
            dateFrom = xx.toDate();
            dateTo = dt.toDate();
            log.log(Level.INFO, "Get Location tracks from {0} to {1}",
                    new Object[]{dateFrom.toString(), dateTo.toString()});
        } else {
            dateFrom = new Date(df);
            dateTo = new Date(dx);
        }
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("LocationTracker.findByStaffInPeriod", LocationTracker.class);
        q.setParameter("companyStaffID", companyStaffID);
        q.setParameter("dateFrom", dateFrom);
        q.setParameter("dateTo", dateTo);
        List<LocationTracker> list = q.getResultList();
        resp.setLocationTrackerList(new ArrayList<LocationTrackerDTO>());
        for (LocationTracker t : list) {
            resp.getLocationTrackerList().add(new LocationTrackerDTO(t));
        }
        return resp;
    }

    public ResponseDTO getLocationTracksByCompanyLastMonth(Integer companyID,
            Long df, Long dx) {

        Date dateFrom, dateTo;
        if (df == null) {
            DateTime dt = new DateTime();
            DateTime xx = dt.minusDays(7);
            dateFrom = xx.toDate();
            dateTo = dt.toDate();
            log.log(Level.INFO, "Get Location tracks from {0} to {1}",
                    new Object[]{dateFrom.toString(), dateTo.toString()});
        } else {
            dateFrom = new Date(df);
            dateTo = new Date(dx);
        }
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("LocationTracker.findByCompanyInPeriod", LocationTracker.class);
        q.setParameter("company", em.find(Company.class, companyID));
        q.setParameter("dateFrom", dateFrom.getTime());
        q.setParameter("dateTo", dateTo.getTime());

        List<LocationTracker> list = q.getResultList();
        resp.setLocationTrackerList(new ArrayList<>());
        for (LocationTracker t : list) {
            resp.getLocationTrackerList().add(new LocationTrackerDTO(t));
        }
        log.log(Level.INFO, "LocationTrackers found, db: {0} out: {1}",
                new Object[]{list.size(), resp.getLocationTrackerList().size()});
        return resp;
    }

    public ResponseDTO getPhotosByProject(Integer projectID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("PhotoUpload.findProjectPhotos", PhotoUpload.class);
        q.setParameter("projectID", projectID);
        List<PhotoUpload> list = q.getResultList();
        resp.setPhotoUploadList(new ArrayList<PhotoUploadDTO>());
        for (PhotoUpload cp : list) {
            resp.getPhotoUploadList().add(new PhotoUploadDTO(cp));
        }

        return resp;
    }

    public ResponseDTO getAllPhotosByProject(Integer projectID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("PhotoUpload.findAllProjectPhotos", PhotoUpload.class);
        q.setParameter("projectID", projectID);
        List<PhotoUpload> list = q.getResultList();
        resp.setPhotoUploadList(new ArrayList<PhotoUploadDTO>());
        for (PhotoUpload cp : list) {
            resp.getPhotoUploadList().add(new PhotoUploadDTO(cp));
        }
        System.out.println("**** found project photos: "
                + resp.getPhotoUploadList().size());

        return resp;
    }

    public ResponseDTO getProjectStatus(Integer projectID) {
        ResponseDTO resp = new ResponseDTO();
        Project s = em.find(Project.class, projectID);
        //ProjectDTO project = new ProjectDTO(s);

        Query q = em.createNamedQuery("ProjectTask.findByProject", ProjectTask.class);
        q.setParameter("projectID", projectID);
        List<ProjectTask> taskList = q.getResultList();
        resp.setProjectTaskList(new ArrayList<>());
        for (ProjectTask projectTask : taskList) {
            ProjectTaskDTO dto = new ProjectTaskDTO(projectTask);
            dto.setProjectTaskStatusList(new ArrayList<>());
            resp.getProjectTaskList().add(dto);
        }

        q = em.createNamedQuery("ProjectTaskStatus.findByProject", ProjectTaskStatus.class);
        q.setParameter("projectID", projectID);
        List<ProjectTaskStatus> taskStatusList = q.getResultList();
        System.out.println("ProjectTaskStatus found: " + taskList.size());

        for (ProjectTaskStatus projectTaskStatus : taskStatusList) {
            ProjectTaskStatusDTO dto = new ProjectTaskStatusDTO(projectTaskStatus);
            for (ProjectTaskDTO projectTask : resp.getProjectTaskList()) {
                if (Objects.equals(projectTask.getProjectTaskID(), dto.getProjectTask().getProjectTaskID())) {
                    projectTask.getProjectTaskStatusList().add(dto);
                }
            }
        }

        q = em.createNamedQuery("PhotoUpload.findByProject", PhotoUpload.class);
        q.setParameter("projectID", projectID);
        List<PhotoUpload> pList = q.getResultList();
        System.out.println("photos found: " + pList.size());
        resp.setPhotoUploadList(new ArrayList<>());
        for (PhotoUpload photoUpload : pList) {
            resp.getPhotoUploadList().add(new PhotoUploadDTO(photoUpload));
        }

        resp.setStatusCount(taskStatusList.size());
        if (!taskStatusList.isEmpty()) {
            ProjectTaskStatusDTO dto = new ProjectTaskStatusDTO(taskStatusList.get(0));
            resp.setLastStatus(dto);
        }

        System.out.println("################# Hooray, project status done!");
        return resp;
    }

    private String getRandomPin() {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random(System.currentTimeMillis());
        int x = rand.nextInt(9);
        if (x == 0) {
            x = 3;
        }
        sb.append(x);
        sb.append(rand.nextInt(9));
        sb.append(rand.nextInt(9));
        sb.append(rand.nextInt(9));
        sb.append(rand.nextInt(9));
        sb.append(rand.nextInt(9));
        return sb.toString();
    }

    public ResponseDTO getPhotosByTask(Integer projectTaskID) {
        ResponseDTO resp = new ResponseDTO();
        Query q = em.createNamedQuery("PhotoUpload.findByTask", PhotoUpload.class);
        q.setParameter("projectTaskID", projectTaskID);
        List<PhotoUpload> list = q.getResultList();
        resp.setPhotoUploadList(new ArrayList<>());
        for (PhotoUpload cp : list) {
            resp.getPhotoUploadList().add(new PhotoUploadDTO(cp));
        }

        return resp;
    }

    public ResponseDTO getCompanyStaffList(Integer companyID) throws DataException {
        ResponseDTO resp = new ResponseDTO();

        try {
            Query q = em.createNamedQuery("Staff.findByCompany", Staff.class);
            q.setParameter("companyID", companyID);
            List<Staff> sList = q.getResultList();
            resp.setStaffList(new ArrayList<>());
            for (Staff cs : sList) {
                resp.getStaffList().add(new StaffDTO(cs));
            }
            //log.log(Level.OFF, "company staff found: {0}", sList.size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to get project data\n" + getErrorString(e));
        }

        return resp;
    }

    public ResponseDTO getTaskStatusTypeList(Integer companyID) throws DataException {
        ResponseDTO resp = new ResponseDTO();

        try {
            Query q = em.createNamedQuery("TaskStatusType.findByCompany", TaskStatusType.class);
            q.setParameter("companyID", companyID);
            List<TaskStatusType> sList = q.getResultList();
            resp.setTaskStatusTypeList(new ArrayList<>());
            for (TaskStatusType cs : sList) {
                resp.getTaskStatusTypeList().add(new TaskStatusTypeDTO(cs));
            }
            //log.log(Level.OFF, "task status types found: {0}", sList.size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to get project data\n" + getErrorString(e));
        }

        return resp;
    }

    public ResponseDTO getProjectStatusTypeList(Integer companyID) throws DataException {
        ResponseDTO resp = new ResponseDTO();

        try {
            Query q = em.createNamedQuery("ProjectStatusType.findByCompany", ProjectStatusType.class);
            q.setParameter("companyID", companyID);
            List<ProjectStatusType> sList = q.getResultList();
            resp.setProjectStatusTypeList(new ArrayList<>());
            for (ProjectStatusType cs : sList) {
                resp.getProjectStatusTypeList().add(new ProjectStatusTypeDTO(cs));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to get project status list\n" + getErrorString(e));
        }

        return resp;
    }

    public ResponseDTO getProjectTasks(Integer projectID) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Query q = em.createNamedQuery("ProjectTask.findByProject", ProjectTask.class);
            q.setParameter("projectID", projectID);
            List<ProjectTask> pstList = q.getResultList();
            log.log(Level.INFO, "tasks found: {0}", pstList.size());
            resp.setProjectTaskList(new ArrayList<>());
            for (ProjectTask projectTask : pstList) {
                resp.getProjectTaskList().add(new ProjectTaskDTO(projectTask));
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to get tasks\n" + getErrorString(e));
        }

        return resp;
    }

    public ResponseDTO getProjectData(Integer projectID) throws DataException {
        long s = System.currentTimeMillis();
        ResponseDTO resp = new ResponseDTO();
        try {
            Project p = em.find(Project.class, projectID);
            ProjectDTO project = new ProjectDTO(p);
            project.setProjectTaskList(getProjectStatus(projectID).getProjectTaskList());
            project.setPhotoUploadList(getPhotosByProject(projectID).getPhotoUploadList());

            DateTime now = new DateTime();
            DateTime then = now.minusDays(7);
            then = then.withHourOfDay(0);
            then = then.withMinuteOfHour(0);
            then = then.withSecondOfMinute(0);

            long e = System.currentTimeMillis();
            log.log(Level.INFO,
                    "############---------- project data retrieved: {0} seconds", Elapsed.getElapsed(s, e));
        } catch (OutOfMemoryError e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to get project data: OUT OF MEMORY!\n");
        }

        return resp;
    }

    public String getErrorString(Exception e) {
        StringBuilder sb = new StringBuilder();
        if (e.getMessage() != null) {
            sb.append(e.getMessage()).append("\n\n");
        }
        if (e.toString() != null) {
            sb.append(e.toString()).append("\n\n");
        }
        StackTraceElement[] s = e.getStackTrace();
        if (s.length > 0) {
            StackTraceElement ss = s[0];
            String method = ss.getMethodName();
            String cls = ss.getClassName();
            int line = ss.getLineNumber();
            sb.append("Class: ").append(cls).append("\n");
            sb.append("Method: ").append(method).append("\n");
            sb.append("Line Number: ").append(line).append("\n");
        }

        return sb.toString();
    }

    public void addErrorStore(int statusCode, String message, String origin) {
        log.log(Level.OFF, "------ adding errorStore, message: {0} origin: {1}", new Object[]{message, origin});
        try {
            ErrorStore t = new ErrorStore();
            t.setDateOccured(new Date());
            t.setMessage(message);
            t.setStatusCode(statusCode);
            t.setOrigin(origin);
            em.persist(t);
            log.log(Level.INFO, "####### ErrorStore row added, origin {0} \nmessage: {1}",
                    new Object[]{origin, message});
        } catch (Exception e) {
            log.log(Level.SEVERE, "####### Failed to add errorStore from " + origin + "\n" + message, e);
        }
    }

    public ResponseDTO getServerEvents(
            Long dt, Long dx) throws DataException {
        ResponseDTO r = new ResponseDTO();
        Date startDate, endDate;
        if (dt == null) {
            DateTime ed = new DateTime();
            DateTime sd = ed.minusMonths(3);
            startDate = sd.toDate();
            endDate = ed.toDate();
        } else {
            startDate = new Date(dt);
            endDate = new Date(dx);
        }
        try {
            Query q = em.createNamedQuery("ErrorStoreAndroid.findByPeriod", ErrorStoreAndroid.class);
            q.setParameter("from", startDate);
            q.setParameter("to", endDate);
            List<ErrorStoreAndroid> list = q.getResultList();
            List<ErrorStoreAndroidDTO> dList = new ArrayList();
            for (ErrorStoreAndroid e : list) {
                dList.add(new ErrorStoreAndroidDTO(e));
            }
            r.setErrorStoreAndroidList(dList);
            r.setErrorStoreList(getServerErrors(startDate.getTime(), endDate.getTime()).getErrorStoreList());

            String logx = LogfileUtil.getFileString();
            r.setLog(logx);
            log.log(Level.OFF, "Android Errors found {0}", r.getErrorStoreAndroidList().size());
        } catch (DataException | IOException e) {
            log.log(Level.SEVERE, "Failed to findClubsWithinRadius");
            throw new DataException("Failed to findClubsWithinRadius\n"
                    + getErrorString(e));
        }
        return r;
    }

    public ResponseDTO getServerErrors(
            long startDate, long endDate) throws DataException {
        ResponseDTO r = new ResponseDTO();
        if (startDate == 0) {
            DateTime ed = new DateTime();
            DateTime sd = ed.minusMonths(3);
            startDate = sd.getMillis();
            endDate = ed.getMillis();
        }
        try {
            Query q = em.createNamedQuery("ErrorStore.findByPeriod", ErrorStore.class);
            q.setParameter("startDate", new Date(startDate));
            q.setParameter("endDate", new Date(endDate));
            List<ErrorStore> list = q.getResultList();
            List<ErrorStoreDTO> dList = new ArrayList();
            for (ErrorStore e : list) {
                dList.add(new ErrorStoreDTO(e));
            }
            r.setErrorStoreList(dList);
            log.log(Level.OFF, "Errors found {0}", r.getErrorStoreList().size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to getServerErrors");
            throw new DataException("Failed to getServerErrors\n"
                    + getErrorString(e));
        }
        return r;
    }
    static final Logger log = Logger.getLogger(ListUtil.class
            .getSimpleName());
}