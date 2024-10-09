package lesson_plannerv2;



import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.Properties;

public class LessonPlanner extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JFormattedTextField serialNoField, lecturesField;
    private JTextField periodsField, subjectField, departmentField, topicField, hodSignField;
    private JButton addButton, exportButton;
    private JDatePickerImpl datePicker;
    private JTextField dayField;

    public LessonPlanner() {
        setTitle("Teacher's Lesson Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Lesson Planner by SVInfotech for Lecturers", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set font and size
        add(titleLabel, BorderLayout.NORTH);

        // Create table with columns
        String[] columns = {"Serial No", "Date", "Day", "Periods", "Total No. of Lectures", "Subject", "Department", "Topic Covered", "HOD Sign"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        // Scroll pane for table
        JScrollPane tablePane = new JScrollPane(table);
        add(tablePane, BorderLayout.CENTER);

        // Panel for input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(11, 2)); // Add one more row for the export button

        // Input fields
        inputPanel.add(new JLabel("Serial No (Integer Only):"));
        serialNoField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        inputPanel.add(serialNoField);

        inputPanel.add(new JLabel("Date:"));
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        inputPanel.add(datePicker);

        inputPanel.add(new JLabel("Day:"));
        dayField = new JTextField();
        dayField.setEditable(false); // Make it non-editable since it's auto-filled
        inputPanel.add(dayField);

        inputPanel.add(new JLabel("Periods:"));
        periodsField = new JTextField();
        inputPanel.add(periodsField);

        inputPanel.add(new JLabel("Total No. of Lectures (Integer Only):"));
        lecturesField = new JFormattedTextField(NumberFormat.getIntegerInstance());
        inputPanel.add(lecturesField);

        inputPanel.add(new JLabel("Subject (Theory/Practical):"));
        subjectField = new JTextField();
        inputPanel.add(subjectField);

        inputPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        inputPanel.add(departmentField);

        inputPanel.add(new JLabel("Topic Covered:"));
        topicField = new JTextField();
        inputPanel.add(topicField);

        inputPanel.add(new JLabel("HOD Sign:"));
        hodSignField = new JTextField();
        inputPanel.add(hodSignField);

        // Add entry button
        addButton = new JButton("Add Entry");
        inputPanel.add(addButton);

        // Export to Excel button
        exportButton = new JButton("Export to Excel");
        inputPanel.add(exportButton);

        add(inputPanel, BorderLayout.SOUTH);

        // Add action listener to date picker
        datePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDayField();
            }
        });

        // Add button action listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRow();
            }
        });

        // Export button action listener
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel();
            }
        });

        pack();
        setVisible(true);
    }

    // Method to update the day field based on the selected date
    private void updateDayField() {
        String selectedDate = datePicker.getJFormattedTextField().getText();
        if (!selectedDate.isEmpty()) {
            // Parse the selected date into LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(selectedDate, formatter);

            // Get the day of the week in a localized format (e.g., Monday, Tuesday)
            String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            // Update the day field
            dayField.setText(dayOfWeek);
        }
    }

    // Method to add a new row to the table
    private void addRow() {
        try {
            String serialNo = serialNoField.getText();
            String date = datePicker.getJFormattedTextField().getText();  // Get the date from the date picker
            String day = dayField.getText();
            String periods = periodsField.getText();
            String lectures = lecturesField.getText();
            String subject = subjectField.getText();
            String department = departmentField.getText();
            String topic = topicField.getText();
            String hodSign = hodSignField.getText();

            // Check if serial number and total lectures are valid integers
            if (serialNo.isEmpty() || lectures.isEmpty()) {
                throw new NumberFormatException("Serial No and Total No of Lectures must be integers.");
            }

            // Add new row to the table
            model.addRow(new Object[]{serialNo, date, day, periods, lectures, subject, department, topic, hodSign});

            // Clear input fields after adding
            serialNoField.setText("");
            periodsField.setText("");
            lecturesField.setText("");
            subjectField.setText("");
            departmentField.setText("");
            topicField.setText("");
            hodSignField.setText("");
            dayField.setText("");
            datePicker.getJFormattedTextField().setText(""); // Clear the date field
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integer values for Serial No and Total No of Lectures.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to export table data to Excel
    private void exportToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lesson Planner");

        // Create header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < model.getColumnCount(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(model.getColumnName(i));
        }

        // Populate data rows
        for (int i = 0; i < model.getRowCount(); i++) {
            Row row = sheet.createRow(i + 1); // Start from row 1 (second row)
            for (int j = 0; j < model.getColumnCount(); j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(model.getValueAt(i, j).toString());
            }
        }

        // Autosize columns
        for (int i = 0; i < model.getColumnCount(); i++) {
            sheet.autoSizeColumn(i);
        }

        // Save to file
        try (FileOutputStream fileOut = new FileOutputStream("LessonPlanner.xlsx")) {
            workbook.write(fileOut);
            workbook.close();
            JOptionPane.showMessageDialog(this, "Data exported to Excel successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting data to Excel.", "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LessonPlanner());
    }
}

