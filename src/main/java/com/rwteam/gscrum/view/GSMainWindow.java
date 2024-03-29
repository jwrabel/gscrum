package com.rwteam.gscrum.view;

import com.google.api.services.calendar.model.CalendarListEntry;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.rwteam.gscrum.controller.GSMainWindowController;
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
    private JLabel lblChooseProfile;
    private JLabel lblChooseCalendar;
    private JComboBox<String> cbxChooseProfile;
    private JPanel contentPanel;
    private JButton btnLogin;
    private JButton btnAddNewTask;
    private JButton btnSaveTask;
    private JButton btnEditTask;
    private JButton btnAddUserStory;
    private JButton btnEditUserStory;
    private JButton btnAddNewProfile;
    private JButton btnDeleteProfile;
    private JButton btnLoadCalendarInfo;
    private DefaultComboBoxModel<String> cbxChooseCalendarModel;
    private JComboBox<String> cbxChooseCalendar;
    private TimelinePanel timelinePanel;

    private JPanel statusPanel;
    private JLabel statusLabel;

    private DefaultListModel listUserStoriesModel;
    private JList<UserStory> listUserStories;
    private JScrollPane scrollPaneListUserStories;
    private DefaultListModel listTasksModel;
    private JList<Task> listTasks;
    private JScrollPane scrollPaneListTasks;

    private TaskEditPanel taskEditPanel;

    private GSMainWindowController controller = new GSMainWindowController(this);
    private Container container;
    private boolean isInAddNewTaskMode = true;

    @Override
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        container = toolWindow.getComponent().getParent();
        container.setLayout(new BorderLayout());

        lblChooseProfile = new JLabel("Choose profile");
        cbxChooseProfile = new JComboBox<String>();
        contentPanel = new JPanel();
        btnLogin = new JButton("Login");
        btnAddNewTask = new JButton("New task");
        btnSaveTask = new JButton("Save task");
        btnEditTask = new JButton("Edit task");
        btnAddUserStory = new JButton("New US");
        btnEditUserStory = new JButton("Edit US");
        btnAddNewProfile = new JButton("Add new");
        btnDeleteProfile = new JButton("Delete");
        btnLoadCalendarInfo = new JButton("Load/Refresh");
        lblChooseCalendar = new JLabel("Calendar");
        cbxChooseCalendarModel = new DefaultComboBoxModel<String>();
        cbxChooseCalendar = new JComboBox<String>(cbxChooseCalendarModel);
        listUserStoriesModel = new DefaultListModel();
        listUserStories = new JList<UserStory>(listUserStoriesModel);
        scrollPaneListUserStories = new JScrollPane(listUserStories);
        listTasksModel = new DefaultListModel();
        listTasks = new JList<Task>(listTasksModel);
        scrollPaneListTasks = new JScrollPane(listTasks);
        taskEditPanel = new TaskEditPanel(this);
        timelinePanel = new TimelinePanel();

        statusPanel = new JPanel();
        statusLabel = new JLabel("status");

        lblChooseProfile.setBounds(10, 5, 100, 25);
        cbxChooseProfile.setBounds(100, 5, 150, 25);
        btnLogin.setBounds(255, 5, 70, 25);
        btnAddNewProfile.setBounds(330, 5, 80, 25);
        btnDeleteProfile.setBounds(415, 5, 80, 25);
        lblChooseCalendar.setBounds(10, 40, 100, 25);
        cbxChooseCalendar.setBounds(100, 40, 200, 25);
        btnLoadCalendarInfo.setBounds(300, 40, 100, 25);
        scrollPaneListUserStories.setBounds(10, 80, 150, 150);
        scrollPaneListTasks.setBounds(170, 80, 150, 150);
        btnAddUserStory.setBounds(10, 260, 100, 30);
        btnEditUserStory.setBounds(220, 260, 100, 30);
        btnAddNewTask.setBounds(10, 295, 100, 30);
        btnSaveTask.setBounds(120, 295, 100, 30);
        btnEditTask.setBounds(220, 295, 100, 30);
        taskEditPanel.setBounds(10, 330, 600, 500);
        timelinePanel.setBounds(10, 400, 500, 100);


        contentPanel.setLayout(null);
        contentPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);


        container.add(contentPanel, BorderLayout.CENTER);
        contentPanel.add(lblChooseProfile);
        contentPanel.add(cbxChooseProfile);
        contentPanel.add(btnLogin);
        contentPanel.add(btnAddNewProfile);
        contentPanel.add(btnDeleteProfile);
        contentPanel.add(btnLoadCalendarInfo);
        contentPanel.add(lblChooseCalendar);
        contentPanel.add(cbxChooseCalendar);
        contentPanel.add(scrollPaneListUserStories);
        contentPanel.add(scrollPaneListTasks);
        contentPanel.add(btnAddUserStory);
        contentPanel.add(btnEditUserStory);
        contentPanel.add(btnAddNewTask);
        contentPanel.add(btnSaveTask);
        contentPanel.add(btnEditTask);
        contentPanel.add(taskEditPanel);
        contentPanel.add(timelinePanel);

        container.add(statusPanel, BorderLayout.SOUTH);
        statusPanel.add(statusLabel);


        cbxChooseCalendar.setEnabled(false);
        btnLoadCalendarInfo.setEnabled(false);


        initListeners();
        setLogged(false);
        controller.init();
    }

    private void initListeners() {
        btnAddNewProfile.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewProfileAction();
            }
        });

        btnLogin.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread queryThread = new Thread() {
                    public void run() {
                        loginAction((String) cbxChooseProfile.getSelectedItem());
                    }
                };
                queryThread.start();
            }
        });

        btnDeleteProfile.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteProfileAction((String) cbxChooseProfile.getSelectedItem());
            }
        });
        btnLoadCalendarInfo.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadCalendarAction();
            }
        });

        btnAddUserStory.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUserStoryAction();
            }
        });

        btnEditUserStory.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUserStoryAction();
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

        btnEditTask.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editTaskAction();
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

        listUserStories.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    userStoryDoubleClickedAction();
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

    private void editUserStoryAction() {
        if(getSelectedUserStory() != null){
            new UserStoryEditWindow(this, getSelectedUserStory());
        }
    }

    private void addUserStoryAction() {
        new UserStoryEditWindow(this, null);
    }

    private void userStoryDoubleClickedAction() {
        UserStory userStory = getSelectedUserStory();
        if (getSelectedUserStory() != null) {
            JOptionPane.showMessageDialog(container, userStory.getAllInfo(), userStory.getId(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            displayErrorDialog("Incorrect user strory!");
        }
    }


    private void editTaskAction() {
        taskEditPanel.setEditable(true, isInAddNewTaskMode);
//        btnSaveTask.setEnabled(false);
        isInAddNewTaskMode = false;
    }

    private void addNewProfileAction() {
        String profileName = JOptionPane.showInputDialog(container, "Enter profile name?", "Create new GScrum profile", JOptionPane.QUESTION_MESSAGE);
        controller.addNewProfile(profileName);
    }

    private void deleteProfileAction(String profileName) {
        if (profileName == null || profileName.isEmpty()) {
            displayErrorDialog("No profile has been selected");
            return;
        }

        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you really want to remove profile " + profileName + "?", "Delete profile?", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            controller.deleteProfile(profileName);
            controller.refreshProfilesComboBox();
        }
    }

    private void taskDoubleClickedAction() {
        Task selectedTask = getSelectedTask();
        if (selectedTask != null) {
            JOptionPane.showMessageDialog(container, selectedTask.getAllInfo(), selectedTask.getId(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            displayErrorDialog("Incorrect task!");
        }
    }

    private Task getSelectedTask() {
        Task task = listTasks.getSelectedValue();
        return task;
    }

    private void taskSelectionChangedAction() {
        Task task = listTasks.getSelectedValue();
        if (task != null) {
            taskEditPanel.populateWithTask(task, listUserStories.getModel(), true);
            isInAddNewTaskMode = false;
            taskEditPanel.setEditable(false);
        }
    }

    private void userStorySelectionChangedAction() {
        listTasksModel.clear();

        System.out.println("Events list selection changed");
        UserStory userStory = listUserStories.getSelectedValue();
        if (userStory != null) {
            for (Task task : userStory.getTaskCollection()) {
                listTasksModel.addElement(task);
            }
        }
    }

    private void saveTaskAction() {
        Task task = taskEditPanel.retrieveTaskObject();
//        System.out.println(task.getAllInfo());

        if (isInAddNewTaskMode) {
            controller.saveNewTask(task);
        } else {
            controller.updateTask(task);
        }
    }

    private void addNewTaskAction() {
        Task task = new Task();
        task.setDescription("Enter description here....");
        task.setId("Set id...");
        taskEditPanel.populateWithTask(task, listUserStories.getModel(), false);
        isInAddNewTaskMode = true;
        taskEditPanel.setEditable(true);
    }

    private void loadCalendarAction() {
        listUserStoriesModel.clear();
        String currentCalendarId = (String) cbxChooseCalendarModel.getSelectedItem();
        DefaultListModel<UserStory> defaultListModel = controller.loadCalendarsInfo(currentCalendarId);
        listUserStories.setModel(defaultListModel);
        setStatus("Refreshed calendar info at " + new Date());
    }

    private void loginAction(String profileName) {
        controller.loginOrLogout(profileName);
    }

    public void populateCalendarComboBox(final List<CalendarListEntry> calendars) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.out.println("Populating calendar comboBox");
                cbxChooseCalendarModel.removeAllElements();
                for (CalendarListEntry entry : calendars) {
                    System.out.println("Adding calendar to comboBox: " + entry.getId());
                    cbxChooseCalendarModel.addElement(entry.getId());
                    cbxChooseCalendar.setEnabled(true);
                    btnLoadCalendarInfo.setEnabled(true);
                }
            }
        });

    }

    public void setLogged(final boolean isUserLogged) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                btnLogin.setText(isUserLogged ? "Logout" : "Login");
                cbxChooseCalendar.setEnabled(isUserLogged);
                btnLoadCalendarInfo.setEnabled(isUserLogged);
                cbxChooseProfile.setEnabled(!isUserLogged);
                btnAddNewProfile.setVisible(!isUserLogged);
                btnDeleteProfile.setVisible(!isUserLogged);

                lblChooseCalendar.setVisible(isUserLogged);
                cbxChooseCalendar.setVisible(isUserLogged);
                btnLoadCalendarInfo.setVisible(isUserLogged);
                scrollPaneListTasks.setVisible(isUserLogged);
                scrollPaneListUserStories.setVisible(isUserLogged);
                taskEditPanel.setVisible(isUserLogged);
                btnAddUserStory.setVisible(isUserLogged);
                btnEditUserStory.setVisible(isUserLogged);
                btnAddNewTask.setVisible(isUserLogged);
                btnSaveTask.setVisible(isUserLogged);
                btnEditTask.setVisible(isUserLogged);

                if (!isUserLogged) {
                    clearAll();
                }
            }
        });

    }

    private void clearAll() {
        System.out.println("Clearing all");
        listUserStoriesModel.clear();
        listTasksModel.clear();
        cbxChooseCalendarModel.removeAllElements();
        taskEditPanel.clearData();
    }


    public void setStatus(final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                statusLabel.setText(status);
            }
        });
    }

    public void populateProfilesComboBox(String[] profiles) {
        cbxChooseProfile.setModel(new DefaultComboBoxModel<>(profiles));
    }

    public void displayErrorDialog(String errorText) {
        JOptionPane.showMessageDialog(container, errorText, "Error!", JOptionPane.ERROR_MESSAGE);
    }

    public void displayInfoDialog(String message) {
        JOptionPane.showMessageDialog(container, message, "Info!", JOptionPane.INFORMATION_MESSAGE);
    }

    public boolean displayYesNoDialog(String question, String title) {
        int dialogResult = JOptionPane.showConfirmDialog(null, question, title, JOptionPane.YES_NO_OPTION);
        return dialogResult == JOptionPane.YES_OPTION;
    }

    public UserStory getSelectedUserStory() {
        return listUserStories.getSelectedValue();
    }

    public void saveNewUS(UserStory userStory) {
        controller.saveNewUserStory(userStory);
    }

    public void updateUS(UserStory userStory) {
        controller.updateUserStory(userStory);
    }
}
