/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boha.monitor.util;

import com.boha.monitor.data.Chat;
import com.boha.monitor.data.ChatMember;
import com.boha.monitor.data.Company;
import com.boha.monitor.data.ErrorStore;
import com.boha.monitor.data.ErrorStoreAndroid;
import com.boha.monitor.data.GcmDevice;
import com.boha.monitor.data.LocationTracker;
import com.boha.monitor.data.Monitor;
import com.boha.monitor.data.PhotoUpload;
import com.boha.monitor.data.Programme;
import com.boha.monitor.data.Project;
import com.boha.monitor.data.ProjectStatusType;
import com.boha.monitor.data.ProjectTask;
import com.boha.monitor.data.ProjectTaskStatus;
import com.boha.monitor.data.Staff;
import com.boha.monitor.data.StaffProject;
import com.boha.monitor.data.Task;
import com.boha.monitor.data.TaskStatusType;
import com.boha.monitor.dto.ChatDTO;
import com.boha.monitor.dto.ChatMemberDTO;
import com.boha.monitor.dto.CompanyDTO;
import com.boha.monitor.dto.ErrorStoreDTO;
import com.boha.monitor.dto.GcmDeviceDTO;
import com.boha.monitor.dto.LocationTrackerDTO;
import com.boha.monitor.dto.MonitorDTO;
import com.boha.monitor.dto.PhotoUploadDTO;
import com.boha.monitor.dto.ProjectDTO;
import com.boha.monitor.dto.ProjectStatusTypeDTO;
import com.boha.monitor.dto.ProjectTaskDTO;
import com.boha.monitor.dto.ProjectTaskStatusDTO;
import com.boha.monitor.dto.StaffDTO;
import com.boha.monitor.dto.StaffProjectDTO;
import com.boha.monitor.dto.TaskDTO;
import com.boha.monitor.dto.TaskStatusTypeDTO;
import com.boha.monitor.dto.transfer.ResponseDTO;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.joda.time.DateTime;

/**
 *
 * @author aubreyM
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DataUtil {

    @PersistenceContext
    EntityManager em;
    @Inject
    CloudMsgUtil cloudMsgUtil;

    static final int OPERATIONS_MANAGER = 1,
            SITE_SUPERVISOR = 2,
            EXECUTIVE_STAFF = 3,
            PROJECT_MANAGER = 4;

    public EntityManager getEm() {
        return em;
    }

   
    public ResponseDTO setStaffProjects(List<StaffProjectDTO> list) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        Staff cs = em.find(Staff.class, list.get(0).getStaffID());
        try {
            for (StaffProjectDTO sp : list) {
                StaffProject d = new StaffProject();
                d.setStaff(cs);
                d.setProject(em.find(Project.class, sp.getProjectID()));
                d.setActiveFlag(true);
                d.setDateAssigned(new Date());
                em.persist(d);
            }
            em.flush();

            Query q = em.createNamedQuery("StaffProject.findByStaff", StaffProject.class);
            q.setParameter("staffID", list.get(0).getStaffID());
            List<StaffProject> sList = q.getResultList();
            resp.setStaffProjectList(new ArrayList<StaffProjectDTO>());
            for (StaffProject x : sList) {
                resp.getStaffProjectList().add(new StaffProjectDTO(x));
            }
            log.log(Level.INFO, "Staff projects added: {0}", resp.getStaffProjectList().size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to add Staff projects ", e);
            throw new DataException("Failed to add Staff projects \n"
                    + getErrorString(e));
        }
        return resp;
    }

    public ResponseDTO addChatMembers(List<ChatMemberDTO> cmList) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            for (ChatMemberDTO cm : cmList) {
                ChatMember chatMember = new ChatMember();
                chatMember.setChat(em.find(Chat.class, cm.getChatID()));
                if (cm.getStaff() != null) {
                    chatMember.setStaff(em.find(Staff.class, cm.getStaff().getStaffID()));
                }
                if (cm.getMonitor() != null) {
                    chatMember.setMonitor(em.find(Monitor.class, cm.getMonitor().getMonitorID()));
                }
                chatMember.setDateJoined(new Date());
                em.persist(chatMember);
            }

            resp.setStatusCode(0);
            resp.setMessage("ChatMembers added to chat: " + cmList.size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to add ChatMember", e);
            throw new DataException("Failed to add ChatMember\n"
                    + getErrorString(e));
        }
        return resp;
    }

    public ResponseDTO addChat(ChatDTO chat) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        Staff cs = null;
        Monitor mon = null;
        try {
            if (chat.getStaff() != null) {
                cs = em.find(Staff.class, chat.getStaff().getStaffID());
            }
            if (chat.getMonitor() != null) {
                mon = em.find(Monitor.class, chat.getMonitor().getMonitorID());
            }

            Chat c = new Chat();
            c.setStaff(cs);
            c.setMonitor(mon);
            c.setDateStarted(new Date());
            c.setChatName(chat.getChatName());
            c.setAvatarNumber(chat.getAvatarNumber());
            if (chat.getProjectID() != null) {
                c.setProject(em.find(Project.class, chat.getProjectID()));
            }
            if (chat.getProjectID() != null) {
                c.setProject(em.find(Project.class, chat.getProjectID()));
            }

            em.persist(c);
            em.flush();
            resp.setChat(new ChatDTO(c));

            log.log(Level.INFO, "Chat added, chatID: {0}", c.getChatID());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to add Chat", e);
            throw new DataException("Failed to add Chat\n"
                    + getErrorString(e));
        }
        resp.setStatusCode(ServerStatus.OK);
        return resp;
    }

    public void addAndroidError(ErrorStoreAndroid err) throws DataException {
        try {
            em.persist(err);
            log.log(Level.INFO, "Android error added");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to add Android Error", e);
            throw new DataException("Failed to add Android Error\n"
                    + getErrorString(e));
        }
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

    public Company getCompanyByID(Integer id) {
        return em.find(Company.class, id);
    }

    public Staff getStaffByID(Integer id) {
        return em.find(Staff.class, id);
    }

    public void addPhotoUpload(PhotoUploadDTO pu) {
        log.log(Level.OFF, "adding photo to database");
        try {
            PhotoUpload u = new PhotoUpload();
            u.setCompany(em.find(Company.class, pu.getCompanyID()));
            if (pu.getProjectID() != null) {
                u.setProject(em.find(Project.class, pu.getProjectID()));
            }

            if (pu.getProjectTaskID() != null) {
                u.setProjectTask(em.find(ProjectTask.class, pu.getProjectTaskID()));
            }
            if (pu.getStaffID() != null) {
                u.setStaff(em.find(Staff.class, pu.getStaffID()));
            }
            u.setPictureType(pu.getPictureType());
            u.setLatitude(pu.getLatitude());
            u.setLongitude(pu.getLongitude());
            u.setUri(pu.getUri());
            u.setDateTaken(new Date(pu.getDateTaken()));
            u.setDateUploaded(new Date(pu.getDateUploaded()));
            u.setThumbFlag(pu.getThumbFlag());
            u.setThumbFilePath(pu.getThumbFilePath());
            u.setAccuracy(pu.getAccuracy());
            if (pu.isIsStaffPicture()) {
                u.setStaffPicture(1);
            }
            em.persist(u);
            em.flush();

            log.log(Level.OFF, "PhotoUpload added to table, date taken: {0}", pu.getDateTaken().toString());
        } catch (Exception e) {
            log.log(Level.SEVERE, "PhotoUpload failed", e);
            addErrorStore(9,
                    "PhotoUpload database add failed\n"
                    + getErrorString(e), "DataUtil");

        }

    }

    public ResponseDTO deleteProjectPhotos(List<PhotoUploadDTO> list) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        int count = 0;
        try {
            for (PhotoUploadDTO p : list) {
                PhotoUpload u = em.find(PhotoUpload.class, p.getPhotoUploadID());
                em.remove(u);
                FileUtility.deleteProjectImageFile(p.getCompanyID(),
                        p.getProjectID(), p.getUri());
                count++;
            }
            em.flush();
            resp.setStatusCode(ServerStatus.OK);
            resp.setMessage("" + count + " project photos deleted");
            log.log(Level.WARNING, "photos deleted: {0}", count);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed delete photo\n" + getErrorString(e));

        }
        return resp;
    }

    public void addLocationTrackers(List<LocationTrackerDTO> list) throws DataException {
        try {
            for (LocationTrackerDTO dto : list) {
                LocationTracker t = new LocationTracker();
                t.setStaff(em.find(Staff.class, dto.getStaffID()));
                t.setDateTracked(new Date(dto.getDateTracked()));
                t.setLatitude(dto.getLatitude());
                t.setLongitude(dto.getLongitude());
                t.setAccuracy(dto.getAccuracy());
                t.setDateAdded(new Date());
                t.setDateTrackedLong(BigInteger.valueOf(new Date().getTime()));
                t.setGeocodedAddress(dto.getGeocodedAddress());
                em.persist(t);
            }

            log.log(Level.WARNING, "LocationTrackers added: {0}", list.size());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to add device\n" + getErrorString(e));

        }
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

    public ResponseDTO addProjectTaskStatus(
            ProjectTaskStatusDTO status) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            ProjectTask c = em.find(ProjectTask.class,
                    status.getProjectTask().getProjectTaskID());
            ProjectTaskStatus t = new ProjectTaskStatus();
            t.setDateUpdated(new Date());
            t.setStatusDate(new Date());
            t.setProjectTask(c);
            if (status.getStaffID() != null) {
                t.setStaff(em.find(Staff.class, status.getStaffID()));
            }
            t.setTaskStatusType(em.find(TaskStatusType.class, status.getTaskStatusType().getTaskStatusTypeID()));

            em.persist(t);
            em.flush();
            resp.setProjectTaskStatusList(new ArrayList<ProjectTaskStatusDTO>());
            resp.getProjectTaskStatusList().add(
                    new ProjectTaskStatusDTO(t));
            log.log(Level.OFF, "ProjectTaskStatus added");

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }

        return resp;

    }

    public ResponseDTO addTaskStatusType(
            TaskStatusTypeDTO status) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Company c = em.find(Company.class,
                    status.getCompanyID());
            TaskStatusType t = new TaskStatusType();
            t.setCompany(c);
            t.setStatusColor(status.getStatusColor());
            t.setTaskStatusTypeName(status.getTaskStatusTypeName());
            em.persist(t);
            em.flush();
            resp.setTaskStatusTypeList(new ArrayList<>());
            resp.getTaskStatusTypeList().add(
                    new TaskStatusTypeDTO(t));
            log.log(Level.OFF, "TaskStatusType added");

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }

        return resp;

    }

    public ResponseDTO addCompanyProjectStatus(ProjectStatusTypeDTO b) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Company c = em.find(Company.class, b.getCompanyID());
            ProjectStatusType cli = new ProjectStatusType();
            cli.setProjectStatusName(b.getProjectStatusName());
            cli.setStatusColor(b.getStatusColor());
            cli.setCompany(c);

            em.persist(cli);
            em.flush();
            resp.setProjectStatusTypeList(new ArrayList<ProjectStatusTypeDTO>());
            resp.getProjectStatusTypeList().add(new ProjectStatusTypeDTO(cli));
            log.log(Level.OFF, "######## ProjectStatusType added: {0}", b.getProjectStatusName());

        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Failed", e);
            resp.setStatusCode(ServerStatus.ERROR_DUPLICATE_DATA);
            resp.setMessage(ServerStatus.getMessage(resp.getStatusCode()));

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }
        return resp;
    }

    public ResponseDTO addCompanyTaskStatusType(TaskStatusTypeDTO b) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Company c = em.find(Company.class, b.getCompanyID());
            TaskStatusType cli = new TaskStatusType();
            cli.setTaskStatusTypeName(b.getTaskStatusTypeName());
            cli.setStatusColor(b.getStatusColor());
            cli.setCompany(c);

            em.persist(cli);
            em.flush();
            resp.setTaskStatusTypeList(new ArrayList<TaskStatusTypeDTO>());
            resp.getTaskStatusTypeList().add(new TaskStatusTypeDTO(cli));
            log.log(Level.OFF, "######## TaskStatusType added: {0}", b.getTaskStatusTypeName());

        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Failed", e);
            resp.setStatusCode(ServerStatus.ERROR_DUPLICATE_DATA);
            resp.setMessage(ServerStatus.getMessage(resp.getStatusCode()));

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }
        return resp;
    }

    public ResponseDTO addCompanyTask(TaskDTO b) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Company c = em.find(Company.class, b.getCompanyID());
            Task cli = new Task();
            cli.setTaskName(b.getTaskName());
            cli.setDescription(b.getDescription());
            cli.setTaskNumber(b.getTaskNumber());
            cli.setCompany(c);

            em.persist(cli);
            em.flush();
            b = new TaskDTO(cli);

            resp.setTaskList(new ArrayList<TaskDTO>());
            resp.getTaskList().add(b);
            log.log(Level.OFF, "######## Task added: {0}", b.getTaskName());

        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Failed", e);
            resp.setStatusCode(ServerStatus.ERROR_DUPLICATE_DATA);
            resp.setMessage(ServerStatus.getMessage(resp.getStatusCode()));

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }
        return resp;
    }

    public ResponseDTO addProjectTask(
            ProjectTaskDTO siteTask) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Project site = em.find(Project.class, siteTask.getProjectID());
            Task task = em.find(Task.class, siteTask.getTaskID());
            ProjectTask t = new ProjectTask();
            t.setDateRegistered(new Date());
            t.setProject(site);
            t.setTask(task);

            em.persist(t);
            em.flush();
            resp.setProjectTaskList(new ArrayList<ProjectTaskDTO>());
            resp.getProjectTaskList().add(new ProjectTaskDTO(t));
            resp.setStatusCode(0);
            resp.setMessage("ProjectTask added successfully");
            log.log(Level.OFF, "Project task registered for: {0} ",
                    new Object[]{site.getProjectName()});

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }

        return resp;

    }

    public ResponseDTO registerProject(
            ProjectDTO dto) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        Company c = null;
        Programme p = null;
        try {
            if (dto.getCompanyID() != null) {
                c = em.find(Company.class, dto.getCompanyID());
            }
            if (dto.getProgrammeID() != null) {
                p = em.find(Programme.class, dto.getProgrammeID());
            }
            Project ps = new Project();
            ps.setCompany(c);
            ps.setProgramme(p);
            ps.setDescription(dto.getDescription());
            ps.setAccuracy(dto.getAccuracy());
            ps.setAddress(dto.getAddress());
            ps.setActiveFlag(Boolean.TRUE);
            ps.setLatitude(dto.getLatitude());
            ps.setLongitude(dto.getLongitude());
            ps.setProjectName(dto.getProjectName());

            em.persist(ps);
            em.flush();

            addInitialProjectChats(ps);
            //get company tasks and create projectsitetask
            Query q = em.createNamedQuery("Task.findByCompany", Task.class);
            q.setParameter("companyID", c.getCompanyID());
            List<Task> taskList = q.getResultList();
            resp.setTaskList(new ArrayList<>());
            for (Task task : taskList) {
                resp.getTaskList().add(new TaskDTO(task));
            }

            log.log(Level.OFF, "Project  registered for: {0} - {1} ",
                    new Object[]{c.getCompanyName(), dto.getProjectName()});

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }

        return resp;

    }

    private void addInitialProjectChats(Project p) throws DataException {
        try {
            Chat c1 = new Chat();
            c1.setAvatarNumber(1);
            c1.setChatName("#General");
            c1.setDateStarted(new Date());
            c1.setProject(p);
            em.persist(c1);
            Chat c2 = new Chat();
            c2.setAvatarNumber(2);
            c2.setChatName("#Emergency");
            c2.setDateStarted(new Date());
            c2.setProject(p);
            em.persist(c2);
            Chat c3 = new Chat();
            c3.setAvatarNumber(3);
            c3.setChatName("#Work&Business");
            c3.setDateStarted(new Date());
            c3.setProject(p);
            em.persist(c3);

            log.log(Level.OFF, "Project chats registered for: {0} ",
                    new Object[]{p.getProjectName()});

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed\n" + getErrorString(e));
        }
    }

    public ResponseDTO registerCompanyStaff(
            StaffDTO staff) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Company c = em.find(Company.class, staff.getCompanyID());
            Staff cs = new Staff();
            cs.setCompany(c);
            cs.setFirstName(staff.getFirstName());
            cs.setCellphone(staff.getCellphone());
            cs.setEmail(staff.getEmail());
            cs.setLastName(staff.getLastName());
            cs.setPin(getRandomPin());
            cs.setActiveFlag(staff.getActiveFlag());
            em.persist(cs);
            em.flush();
            //ch
            c = em.find(Company.class, staff.getCompanyID());
            log.log(Level.OFF, "checking staff: {0}", c.getStaffList().size());

            resp.setCompany(new CompanyDTO(c));
            resp.setStaffList(new ArrayList<>());
            resp.getStaffList().add(new StaffDTO(cs));
            try {
                if (staff.getGcmDevice() != null) {
                    addDevice(staff.getGcmDevice());
                }

            } catch (DataException e) {
                log.log(Level.WARNING, "Unable to add device to GCMDevice table", e);
            }
            if (staff.getStaffProjectList() != null && !staff.getStaffProjectList().isEmpty()) {
                resp.setStaffProjectList(
                        setStaffProjects(staff.getStaffProjectList()).getStaffProjectList());
            }

            log.log(Level.OFF, "Company staff registered for: {0} - {1} {2}",
                    new Object[]{c.getCompanyName(), staff.getFirstName(), staff.getLastName()});
        } catch (PersistenceException e) {
            resp.setStatusCode(ServerStatus.ERROR_DUPLICATE_DATA);
            resp.setMessage(ServerStatus.getMessage(resp.getStatusCode()));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to register staff\n" + getErrorString(e));
        }

        return resp;

    }

    public ResponseDTO registerCompany(CompanyDTO company,
            StaffDTO staff,
            ListUtil listUtil) throws DataException {
        log.log(Level.OFF, "####### * attempt to register company");
        ResponseDTO resp = new ResponseDTO();
        try {
            Company c = new Company();
            c.setCompanyName(company.getCompanyName());
            c.setAddress(company.getAddress());
            c.setEmail(company.getEmail());
            c.setCellphone(company.getCellphone());

            em.persist(c);
            em.flush();

            //add operations staff - employee #1
            Staff cs = new Staff();
            cs.setCompany(c);
            cs.setFirstName(staff.getFirstName());
            cs.setCellphone(staff.getCellphone());
            cs.setEmail(staff.getEmail());
            cs.setLastName(staff.getLastName());
            cs.setPin(staff.getPin());
            em.persist(cs);
            em.flush();

            //add sample data - app not empty at startup
            addInitialTaskStatus(c);
            addinitialProjectStatusType(c);
            addInitialTasks(c);
            addInitialProject(c);

            resp = listUtil.getCompanyData(c.getCompanyID());
            resp.setStaffList(new ArrayList<>());
            resp.getStaffList().add(new StaffDTO(cs));

            log.log(Level.OFF, "######## Company registered: {0}", c.getCompanyName());

        } catch (PersistenceException e) {
            log.log(Level.SEVERE, "Failed", e);
            resp.setStatusCode(ServerStatus.ERROR_DUPLICATE_DATA);
            resp.setMessage(ServerStatus.getMessage(resp.getStatusCode()));

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException("Failed to register company\n" + getErrorString(e));
        }

        return resp;

    }

    private void addInitialProject(Company c) {

        Project project = new Project();
        project.setCompany(c);
        project.setDescription("This is a sample project meant to help you practice the features of the Monitor app. "
                + "This project can be removed when you are done");
        project.setProjectName("Sample Construction Project");
        project.setLatitude(Double.NaN);
        project.setLongitude(Double.NaN);
        project.setAddress(null);

        em.persist(project);
        em.flush();

        log.log(Level.INFO, "#### Initial Project and Sites added");
    }

    private void addInitialTasks(Company c) {
        Task t1 = new Task();
        t1.setTaskName("Clear Building Site");
        t1.setTaskNumber(1);
        t1.setDescription("Preparation of site prior to commencement of construction");
        t1.setCompany(c);
        em.persist(t1);
        Task t2 = new Task();
        t2.setTaskName("Foundation");
        t2.setDescription("Construction of building foundation and supports");
        t2.setTaskNumber(2);
        t2.setCompany(c);
        em.persist(t2);
        Task t3 = new Task();
        t3.setTaskName("Wallplate");
        t3.setDescription("Construct walls, windows, doors, entrance etc.");
        t3.setTaskNumber(3);
        t3.setCompany(c);
        em.persist(t3);
        Task t4 = new Task();
        t4.setTaskName("Completion Tasks");
        t4.setDescription("Complete roofing and sundry fittings");
        t4.setTaskNumber(4);
        t4.setCompany(c);
        em.persist(t4);
        Task t5 = new Task();
        t5.setTaskName("Site Cleanup");
        t5.setDescription("Remove construction rubble and associated debris");
        t5.setTaskNumber(5);
        t5.setCompany(c);
        em.persist(t5);
        Task t6 = new Task();
        t6.setTaskName("Snag List Preparation");
        t6.setDescription("Prepare and document Snag List");
        t6.setTaskNumber(6);
        t6.setCompany(c);
        em.persist(t6);
        em.flush();
        log.log(Level.INFO, "Initial Tasks added");
    }

    private void addinitialProjectStatusType(Company c) {
        ProjectStatusType p1 = new ProjectStatusType();
        p1.setCompany(c);
        p1.setProjectStatusName("Project is ahead of schedule");
        p1.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_GREEN);
        em.persist(p1);
        ProjectStatusType p2 = new ProjectStatusType();
        p2.setCompany(c);
        p2.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_GREEN);
        p2.setProjectStatusName("Project is on schedule");
        em.persist(p2);
        ProjectStatusType p3 = new ProjectStatusType();
        p3.setCompany(c);
        p3.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_GREEN);
        p3.setProjectStatusName("Project is complete");
        em.persist(p3);
        ProjectStatusType p4 = new ProjectStatusType();
        p4.setCompany(c);
        p4.setProjectStatusName("Project is behind schedule");
        p4.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_RED);
        em.persist(p4);
        ProjectStatusType p5 = new ProjectStatusType();
        p1.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_GREEN);
        p5.setCompany(c);
        p5.setProjectStatusName("Project is on budget");
        em.persist(p5);
        ProjectStatusType p6 = new ProjectStatusType();
        p6.setCompany(c);
        p1.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_YELLOW);
        p6.setProjectStatusName("Project is over budget");
        em.persist(p6);

        log.log(Level.INFO, "*** Initial ProjectStatusTypes added");
    }

    private void addInitialTaskStatus(Company c) {
        TaskStatusType ts1 = new TaskStatusType();
        ts1.setTaskStatusTypeName("Completed");
        ts1.setCompany(c);
        ts1.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_GREEN);
        em.persist(ts1);
        TaskStatusType ts2 = new TaskStatusType();
        ts2.setTaskStatusTypeName("Delayed - Weather");
        ts2.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_RED);
        ts2.setCompany(c);
        em.persist(ts2);
        TaskStatusType ts3 = new TaskStatusType();
        ts3.setTaskStatusTypeName("Delayed - Staff");
        ts3.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_RED);
        ts3.setCompany(c);
        em.persist(ts3);
        TaskStatusType ts4 = new TaskStatusType();
        ts4.setTaskStatusTypeName("Delayed - Materials");
        ts4.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_RED);
        ts4.setCompany(c);
        em.persist(ts4);
        TaskStatusType ts5 = new TaskStatusType();
        ts5.setTaskStatusTypeName("Not started yet");
        ts5.setStatusColor((short) TaskStatusTypeDTO.STATUS_COLOR_YELLOW);
        ts5.setCompany(c);
        em.persist(ts5);

        log.log(Level.INFO, "Initial TaskStatusTypes added");
    }

    public String getErrorString(Exception e) {

        StringBuilder sb = new StringBuilder();
        try {
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
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Failed, ignored " + ex.getMessage());
        }

        return sb.toString();
    }

    public String getRandomPin() {
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
    static final Logger log = Logger.getLogger(DataUtil.class.getSimpleName());

    public void updateProjectStatusType(ProjectStatusTypeDTO dto) throws DataException {
        try {
            ProjectStatusType ps = em.find(ProjectStatusType.class, dto.getProjectStatusTypeID());
            if (ps != null) {
                if (dto.getProjectStatusName() != null) {
                    ps.setProjectStatusName(dto.getProjectStatusName());
                }
                if (dto.getStatusColor() != null) {
                    ps.setStatusColor(dto.getStatusColor());
                }
                em.merge(ps);
                log.log(Level.INFO, "Project Status Type updated");
            }
        } catch (Exception e) {
            log.log(Level.OFF, null, e);
            throw new DataException("Failed to update project status type\n" + getErrorString(e));
        }

    }

    public void updateTaskStatus(TaskStatusTypeDTO dto) throws DataException {
        try {
            TaskStatusType ps = em.find(TaskStatusType.class, dto.getTaskStatusTypeID());
            if (ps != null) {
                if (dto.getTaskStatusTypeName() != null) {
                    ps.setTaskStatusTypeName(dto.getTaskStatusTypeName());
                }
                if (dto.getStatusColor() != null) {
                    ps.setStatusColor(dto.getStatusColor());
                }
                em.merge(ps);
                log.log(Level.INFO, "Task Status Type updated");
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            log.log(Level.OFF, null, e);
            throw new DataException("Failed to update statusType\n" + getErrorString(e));
        }

    }

    public void updateTask(TaskDTO dto) throws DataException {
        try {
            Task ps = em.find(Task.class, dto.getTaskID());
            if (ps != null) {
                if (dto.getTaskName() != null) {
                    ps.setTaskName(dto.getTaskName());
                }
                if (dto.getDescription() != null) {
                    ps.setDescription(dto.getDescription());
                }
                if (dto.getTaskNumber() != null) {
                    ps.setTaskNumber(dto.getTaskNumber());
                }
                em.merge(ps);

                log.log(Level.INFO, "Task updated");
            }
        } catch (Exception e) {
            log.log(Level.OFF, null, e);
            throw new DataException("Failed to update task\n" + getErrorString(e));
        }

    }

    public ResponseDTO setNewPin(Integer staffID) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Staff cs = em.find(Staff.class, staffID);
            cs.setPin(getRandomPin());
            em.merge(cs);
            em.flush();
            resp.setStaffList(new ArrayList<>());
            resp.getStaffList().add(new StaffDTO(cs));

        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed", e);
            throw new DataException(("Failed to set PIN\n" + getErrorString(e)));
        }
        return resp;
    }

    public ResponseDTO updateStaff(StaffDTO dto) throws DataException {
        ResponseDTO resp = new ResponseDTO();
        try {
            Staff ps = em.find(Staff.class, dto.getStaffID());
            if (ps != null) {
                if (dto.getFirstName() != null) {
                    ps.setFirstName(dto.getFirstName());
                }
                if (dto.getLastName() != null) {
                    ps.setLastName(dto.getLastName());
                }
                if (dto.getActiveFlag() != null) {
                    ps.setActiveFlag(dto.getActiveFlag());
                }
                if (dto.getEmail() != null) {
                    ps.setEmail(dto.getEmail());
                }
                if (dto.getCellphone() != null) {
                    ps.setCellphone(dto.getCellphone());
                }
                if (dto.getAppInvitationDate() != null) {
                    ps.setAppInvitationDate(new Date());
                }

                em.merge(ps);
                log.log(Level.INFO, "Staff updated");
            }
        } catch (Exception e) {
            log.log(Level.OFF, null, e);
            throw new DataException("Failed to update staff\n" + getErrorString(e));
        }

        return resp;
    }

    public void updateProject(ProjectDTO dto) throws DataException {
        try {
            Project ps = em.find(Project.class, dto.getProjectID());
            if (ps != null) {
                if (dto.getProjectName() != null) {
                    ps.setProjectName(dto.getProjectName());
                }
                if (dto.getDescription() != null) {
                    ps.setDescription(dto.getDescription());
                }
                if (dto.getLatitude() != null) {
                    ps.setLatitude(dto.getLatitude());
                }
                if (dto.getLongitude() != null) {
                    ps.setLongitude(dto.getLongitude());
                }
                em.merge(ps);
                log.log(Level.INFO, "Project updated");
            }
        } catch (Exception e) {
            log.log(Level.OFF, null, e);
            throw new DataException("Failed to update project\n" + getErrorString(e));
        }

    }

    public void updateStaffProjects(List<StaffProjectDTO> dtoList) throws DataException {
        try {
            for (StaffProjectDTO dto : dtoList) {
                StaffProject ps = em.find(StaffProject.class, dto.getStaffProjectID());
                ps.setActiveFlag(dto.getActiveFlag());
                em.merge(ps);
                log.log(Level.INFO, "StaffProject  updated, activeFlag: {0}", dto.getActiveFlag());
            }

        } catch (Exception e) {
            log.log(Level.OFF, null, e);
            throw new DataException("Failed to update staffProject\n" + getErrorString(e));
        }
    }

    public void confirmLocation(Integer projectID, double latitude, double longitude, Float accuracy) throws DataException {
        try {
            Project ps = em.find(Project.class, projectID);
            if (ps != null) {
                ps.setLocationConfirmed(Boolean.TRUE);
                ps.setLatitude(latitude);
                ps.setLongitude(longitude);
                ps.setAccuracy(accuracy);
                em.merge(ps);
                log.log(Level.INFO, "Project Site location confirmed");
            }
        } catch (Exception e) {
            log.log(Level.OFF, null, e);
            throw new DataException("Failed to confirm location\n" + getErrorString(e));
        }
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
}