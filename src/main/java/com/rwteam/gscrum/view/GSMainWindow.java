package com.rwteam.gscrum.view;

import com.google.api.services.calendar.model.CalendarListEntry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.rwteam.gscrum.controller.GSMainWindowController;
import com.rwteam.gscrum.controller.googleapi.GoogleCalendarConnector;
import com.rwteam.gscrum.model.Task;
import com.rwteam.gscrum.model.UserStory;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.List;

/**
 * Created by wrabel on 11/30/2014.
 */
public class GSMainWindow implements ToolWindowFactory {
    private JPanel contentPanel;
    private JButton btnLogin;
    private JButton btnAddNewTask;
    private JButton btnSaveTask;
    private JLabel lblLoginStatus;
    private JButton btnLoadCalendarInfo;
    private DefaultComboBoxModel<String> cbxChooseCalendarModel;
    private JComboBox<String> cbxChooseCalendar;

    private JPanel statusPanel;
    private JLabel statusLabel;

    private DefaultListModel listUserStoriesModel;
    private JList<UserStory> listUserStories;
    private JScrollPane scrollPaneListUserStories;
    private DefaultListModel listTasksModel;
    private JList<Task> listTasks;
    private JScrollPane scrollPaneListTasks;

    private TaskEditPanel taskEditPanel;

    private JTextArea txtTaskDetails;
    private GSMainWindowController controller = new GSMainWindowController(this);
    private Container container;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        container = toolWindow.getComponent().getParent();
        container.setLayout(new BorderLayout());

        contentPanel = new JPanel();
        btnLogin = new JButton("Login");
        btnAddNewTask = new JButton("New task");
        btnSaveTask = new JButton("Save task");
        lblLoginStatus = new JLabel("You are not logged");
        btnLoadCalendarInfo = new JButton("Refresh calendar info");
        cbxChooseCalendarModel = new DefaultComboBoxModel<String>();
        cbxChooseCalendar = new JComboBox<String>(cbxChooseCalendarModel);
        listUserStoriesModel = new DefaultListModel();
        listUserStories = new JList<UserStory>(listUserStoriesModel);
        scrollPaneListUserStories = new JScrollPane(listUserStories);
        listTasksModel = new DefaultListModel();
        listTasks = new JList<Task>(listTasksModel);
        scrollPaneListTasks = new JScrollPane(listTasks);
        txtTaskDetails = new JTextArea();
        taskEditPanel = new TaskEditPanel(this);

        statusPanel = new JPanel();
        statusLabel = new JLabel("status");

        btnLogin.setBounds(10, 10, 150, 30);
        lblLoginStatus.setBounds(180, 10, 200, 30);
        cbxChooseCalendar.setBounds(10, 50, 250, 30);
        btnLoadCalendarInfo.setBounds(270, 50, 150, 30);
        scrollPaneListUserStories.setBounds(10, 90, 150, 200);
        scrollPaneListTasks.setBounds(170, 90, 150, 200);
        txtTaskDetails.setBounds(330, 90, 500, 200);
        btnAddNewTask.setBounds(10, 295, 100, 30);
        btnSaveTask.setBounds(120, 295, 100, 30);
        taskEditPanel.setBounds(10, 330, 400, 200);

        contentPanel.setLayout(null);
        contentPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

        txtTaskDetails.setLineWrap(true);
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);


        container.add(contentPanel, BorderLayout.CENTER);
        contentPanel.add(btnLogin);
        contentPanel.add(lblLoginStatus);
        contentPanel.add(btnLoadCalendarInfo);
        contentPanel.add(cbxChooseCalendar);
        contentPanel.add(scrollPaneListUserStories);
        contentPanel.add(scrollPaneListTasks);
        contentPanel.add(txtTaskDetails);
        contentPanel.add(btnAddNewTask);
        contentPanel.add(btnSaveTask);
        contentPanel.add(taskEditPanel);

        container.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.add(statusLabel);


        cbxChooseCalendar.setEnabled(false);
        btnLoadCalendarInfo.setEnabled(false);


        initListeners();
    }

    private void initListeners() {
        btnLogin.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginAction();
            }
        });

        btnLoadCalendarInfo.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCalendarAction();
            }
        });

        btnAddNewTask.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewTaskAction();
            }
        });

        btnSaveTask.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTaskAction();
            }
        });

        listUserStories.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                userStorySelectionChangedAction();
            }
        });

        listTasks.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                taskSelectionChangedAction();
            }
        });

        listTasks.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    taskDoubleClickedAction();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    private void taskDoubleClickedAction() {
        Task selectedTask = getSelectedTask();
        if (selectedTask != null) {
            JOptionPane.showMessageDialog(container, selectedTask.getAllInfo(), selectedTask.getId(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(container, "Incorrect task!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Task getSelectedTask() {
        Task task = listTasks.getSelectedValue();
        return task;
    }

    private void taskSelectionChangedAction() {
        txtTaskDetails.setText("");
        Task task = listTasks.getSelectedValue();
        if (task != null) {
            taskEditPanel.populateWithTask(task, listUserStories.getModel());
            txtTaskDetails.setText(task.getAllInfo());
        }
    }

    private void userStorySelectionChangedAction() {
        listTasksModel.clear();
        txtTaskDetails.setText("");

        System.out.println("Events list selection changed");
        UserStory userStory = listUserStories.getSelectedValue();
        if (userStory != null) {
            for (Task task : userStory.getTaskCollection()) {
                listTasksModel.addElement(task);
            }
        }
    }

    private void saveTaskAction() {
        System.out.println("---- SAVING TASK");
        Task task = taskEditPanel.retrieveTaskObject();
        System.out.println(task.getAllInfo());

        GoogleCalendarConnector.getInstance().saveTask(task.convertToGoogleTask());
    }

    private void addNewTaskAction() {
        Task task = new Task();
        task.setDescription("Enter description here....");
        task.setId("Set id...");
        taskEditPanel.populateWithTask(task, listUserStories.getModel());
    }

    private void loadCalendarAction() {
        listUserStoriesModel.clear();
        String currentCalendarId = (String) cbxChooseCalendarModel.getSelectedItem();
        DefaultListModel<UserStory> defaultListModel = controller.loadCalendarsInfo(currentCalendarId);
        listUserStories.setModel(defaultListModel);
        setStatus("Refreshed calendar info at " + new Date());
    }

    private void loginAction() {
        controller.loginOrLogout();
    }

    public void populateCalendarComboBox(List<CalendarListEntry> calendars) {
        System.out.println("Populating calendar comboBox");
        cbxChooseCalendarModel.removeAllElements();
        for (CalendarListEntry entry : calendars) {
            System.out.println("Adding calendar to comboBox: " + entry.getId());
            cbxChooseCalendarModel.addElement(entry.getId());
            cbxChooseCalendar.setEnabled(true);
            btnLoadCalendarInfo.setEnabled(true);
        }
    }

    public void setLogged(boolean isUserLogged) {
        btnLogin.setText(isUserLogged ? "Logout" : "Login");
        cbxChooseCalendar.setEnabled(isUserLogged);
        btnLoadCalendarInfo.setEnabled(isUserLogged);

        if (!isUserLogged) {
            clearAll();
        }
    }

    private void clearAll() {
        System.out.println("Clearing all");
        listUserStoriesModel.clear();
        listTasksModel.clear();
        txtTaskDetails.setText("");
        cbxChooseCalendarModel.removeAllElements();
        taskEditPanel.clearData();
    }


    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}
