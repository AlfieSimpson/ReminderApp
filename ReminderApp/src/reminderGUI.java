import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import org.jdesktop.swingx.prompt.PromptSupport;
import org.joda.time.Days;

/**
 * Created by alumniCurie19 on 14/06/2017.
 */
public class reminderGUI extends JFrame {
    private JButton saveReminderButton;
    private JButton startReminderButton;
    private Integer[] lstYear={2017,2018,2019,2020};
    private Integer[] lstDay = new Integer[31];
    private Integer[]lstMonth= new Integer[12];;
    private Integer[]lstHour= new Integer[24];;
    private Integer[]lstMinute= new Integer[60];
    private JSpinner spinned;
    private JSpinner minSpin;
    private JComboBox<Integer> txtYear;
    private JLabel lblYear;
    private JLabel lblDesc;
    private JTextField txtDesc;
    private JComboBox<Integer> txtDay;
    private JLabel lblDay;
    private JComboBox<Integer> txtMonth;
    private JLabel lblMonth;
    private JComboBox<Integer> txtMinute;
    private JLabel lblMinute;
    private JComboBox<Integer> txtHour;
    private JLabel lblHour;
    private JCheckBox a3HoursCheckBox;
    private JCheckBox a1WeekCheckBox;
    private JCheckBox a1DayCheckBox;
    private JLabel lblTest;
    private ArrayList<Reminder>reminders;
    private JTextField descr;
    private ArrayList<Integer> months30;
    private JLabel lbMonth;
    private custRend table;
    private String thisMonth;
    private JCheckBox hr;
    private JCheckBox dy;
    private JCheckBox wk;

    private String [] months = {"JANUARY","FEBUARY","MARCH",
            "APRIL","MAY","JUNE","JULY","AUGUST",
            "SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER"};
    private void readInReminders(){
        File file = new File("./reminders.txt");
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(file));
            String [] values;
            String thisLine;
            Reminder temp;
            while(fileReader.ready()){
                 thisLine = fileReader.readLine();
                 values = thisLine.split(",");
                 temp = new Reminder(Boolean.getBoolean(values[0]), //week check
                         Boolean.getBoolean(values[1]), //day check
                         Boolean.getBoolean(values[2]), //hour check
                         Integer.parseInt(values[3]), //day
                         Integer.parseInt(values[4]),//month
                         Integer.parseInt(values[5]),//year
                         Integer.parseInt(values[6]),//hour
                         Integer.parseInt(values[7]),//minute
                         values[8]);//description
                 reminders.add(temp);
            }


        }catch(Exception e){
            e.printStackTrace();
        }

    }
    private void closing(){
        File file = new File("./reminders.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            for (Reminder reminder:reminders) {
                fileWriter.write(reminder.toString());
            }
            fileWriter.close();
            new start().toTray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private int endOf(Calendar calendar){
        int toReturn =0;
        int month = calendar.get(calendar.MONTH);
        if (month==1) {
            toReturn = 28;
        }
        else if (months30.contains(month)){
            toReturn=30;
        }
        else{
            toReturn=31;

        }
        return toReturn;
    }
    private Integer [] [] createDates(Calendar calendar){
        calendar.set(calendar.DAY_OF_MONTH,1);
        int z = (calendar.get(calendar.DAY_OF_WEEK)-1)-calendar.MONDAY;
        int endofMonth=endOf(calendar);
        calendar.set(calendar.MONTH,calendar.get(calendar.MONTH)-1);
        int endPrev=endOf(calendar);
        //To display the full week
        calendar.set(calendar.MONTH,calendar.get(calendar.MONTH)+1);
        int counter = 0;
        Integer [][] toReturn = new Integer[6][7];
        for (int i = z; i >=0;i--){
            toReturn[0][counter]=endPrev-i;
            counter ++;
        }
        //^fills the spillover from the last week of prev month
        int counter2 = 1;
        //Then fill the current month and
        //then reset the counter2 such that it can show the spillover
        //from the next month
        for (int j = 0; j < toReturn.length;j++) {
            for (int i = counter; i < toReturn[0].length; i++) {
                toReturn[j][i]=counter2;
                counter2++;
                if(counter2 == (endofMonth+1)){
                    counter2 =1;
                }
            }
            counter=0;
        }

        return toReturn;
    }
    private void switchToCalendar(){
        String [] colNames = {"MON","TUE","WED","THU","FRI","SAT","SUN"};
        Calendar curr = Calendar.getInstance();
        curr.set(curr.DATE,1);
        thisMonth = months[curr.get(curr.MONTH)];
        lbMonth = new JLabel(months[curr.get(curr.MONTH)]+" "+curr.get(curr.YEAR));
        Integer [][] dates = createDates(curr);
        this.getContentPane().removeAll();
        this.getContentPane().repaint();
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closing();
            }
        });

        setLayout(new BorderLayout());
        this.setSize(500,300);
        this.setVisible(true);
        JCheckBox switchMode = new JCheckBox("Switch to Boxes");
        switchMode.addActionListener(e -> switchToBoxes());
        nonEditModel model = new nonEditModel(dates,colNames);
        table = new custRend(model);
        JPanel sPane = new JPanel();
        sPane.setLayout(new BoxLayout(sPane,BoxLayout.PAGE_AXIS));
        sPane.add(lbMonth);
        sPane.add(Box.createRigidArea(new Dimension(0,5)));
        //Keep the table the same size
        table.setMinimumSize(new Dimension(500,300));
        table.setMaximumSize(new Dimension(500,300));

        JScrollPane contain = new JScrollPane(table);
        contain.setMinimumSize(new Dimension(500,300));
        sPane.add(contain);
        sPane.setSize(new Dimension(500,300));
        this.getContentPane().add(sPane);
        sPane.setPreferredSize(new Dimension(500,150));
        table.setCellSelectionEnabled(true);
        table.setSelectionBackground(new Color(250,255,191));

        table.setComponentPopupMenu(new popUpCust());
        JPanel pane = new JPanel();
        pane.setLayout(new BoxLayout(pane,BoxLayout.X_AXIS));
        pane.add(switchMode);
        pane.add(Box.createRigidArea(new Dimension(this.getWidth()-100,0)));
        JButton leftButton = new JButton("<");
        pane.add(leftButton);
        JButton rightButton = new JButton(">");
        pane.add(rightButton);
        leftButton.addActionListener(e -> {
            //work out the new structure for the previous month
            curr.set(curr.MONTH,curr.get(curr.MONTH)-1);
            curr.set(curr.DAY_OF_MONTH,1);

            Integer [] [] newDates = createDates(curr);
            nonEditModel newModel = new nonEditModel(newDates,colNames);
            table.setModel(newModel);
            lbMonth.setText( months[curr.get(curr.MONTH)] +" "+curr.get(curr.YEAR));
        });
        rightButton.addActionListener(e -> {
            //work out the new structure for the next month
            curr.set(curr.MONTH,curr.get(curr.MONTH) +1);
            curr.set(curr.DAY_OF_MONTH,1);

            Integer [] [] newDates = createDates(curr);
            nonEditModel newModel = new nonEditModel(newDates,colNames);
            table.setModel(newModel);
            lbMonth.setText( months[curr.get(curr.MONTH)]+" "+curr.get(curr.YEAR) );
        });
        Container content = getContentPane();
        content.add(sPane,BorderLayout.CENTER);
        content.add(pane,BorderLayout.PAGE_END);
        pack();
        validate();

    }
    class custRend extends JTable{
        //Need a custom implementation such that i can colour things as needed
        public custRend(TableModel model){
            super(model);
        }
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

            Component comp = super.prepareRenderer(renderer,row,col);
            Integer value = (Integer) getModel().getValueAt(row,col);
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            if (((row == 0) && ((Integer) value > 10)) || ((row >= 4) && ((Integer) value < 16))) {
                //if spillover from other month, colour it grey such that its obvious
                comp.setBackground(new Color(194,194,194));
                comp.setForeground(new Color(125,125,125));
            }
            else if(thisMonth.equals(lbMonth.getText().split(" ")[0])&& value==day){
                //if current day colour it green
                comp.setBackground(new Color(148,255,148));
            }
            else {
                if (this.isCellSelected(row,col)){
                    //if it's selected make the cell a pale yellow
                    comp.setBackground(new Color(250,255,191));
                }
                else {
                    //average cell has white BG black TEXT
                    comp.setBackground(new Color(255, 255, 255));
                    comp.setForeground(new Color(0, 0, 0));
                }
            }
            return comp;
        }
    }

    class nonEditModel extends DefaultTableModel{
        //This is needed such that cells can be selectable and uneditable
        nonEditModel(Object [] [] data, String [] columnNames){
            super(data,columnNames);
        }
        public boolean isCellEditable(int row, int column){
            return false;
        }
    }
    class popUpCust extends JPopupMenu{
        //On click of a cell open this popupmenu to create a new appointment
        //no checkboxes to keep things compact
        //default the checkboxes to true
        public popUpCust(){
            hr = new JCheckBox("HR");
            dy = new JCheckBox("DY");
            wk = new JCheckBox("WK");
            add(hr);
            add(dy);
            add(wk);
            descr = new JTextField();
            SpinnerListModel hrSpinMod = new SpinnerListModel(lstHour);
            SpinnerListModel minSpinMod = new SpinnerListModel(lstMinute);
             spinned = new JSpinner(hrSpinMod);
             minSpin= new JSpinner(minSpinMod);
             add(descr);
            add(spinned);
            add(minSpin);
            JButton Save = new JButton("Save");
            JButton startB = new JButton("Start");
            saveCal saver = new saveCal();
            start starter = new start();
            Save.addActionListener(saver);
            startB.addActionListener(starter);
            add(Save);
            add(startB);
        }
    }
    class calPop extends MouseAdapter{
        // register when a cell has been clicked
        private void doPop(MouseEvent e){

            popUpCust menu = new popUpCust();
            menu.show(e.getComponent(),e.getX(),e.getY());
                    }
        public void mouseClicked(MouseEvent e){
            if(e.isPopupTrigger()){
                doPop(e);
            }
        }

    }
    private void switchToBoxes(){
        this.getContentPane().removeAll();
        this.getContentPane().repaint();
        JCheckBox switchMode = new JCheckBox("Switch to Calendar");
        switchMode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchToCalendar();
            }
        });
        add(switchMode);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closing();
            }
        });
        setLayout(new GridLayout(20,2));

        for (int i = 0; i < 60;i++){
            //fill up the lists for the comboboxes
            lstMinute[i]=i+1;
            if (i<31){
                lstDay[i]=i+1;
            }
            if(i<24){
                lstHour[i]=i+1;
            }
            if(i < 12){
                lstMonth[i]=i+1;
            }
        }
        add(a1DayCheckBox);
        add(a1WeekCheckBox);
        add(a3HoursCheckBox);
        lblDesc = new JLabel("Description");
        add(lblDesc);
        txtDesc = new JTextField();
        add(txtDesc);
        lblDay = new JLabel("Day");
        add(lblDay);
        txtDay = new JComboBox(lstDay);
        add(txtDay);

        saveReminderButton =new JButton("save");
        lblMonth = new JLabel("Month");
        add(lblMonth);
        txtMonth = new JComboBox(lstMonth);
        add(txtMonth);
        lblYear = new JLabel("Year");
        add(lblYear);
        txtYear = new JComboBox(lstYear);
        add(txtYear);
        lblHour = new JLabel("Hour");
        add(lblHour);
        txtHour = new JComboBox(lstHour);
        add(txtHour);

        lblMinute = new JLabel("Minute");
        add(lblMinute);
        txtMinute = new JComboBox(lstMinute);

        add(txtMinute);
        add(saveReminderButton);
        startReminderButton =new JButton("start");
        add(startReminderButton);
        start starter = new start();
        startReminderButton.addActionListener(starter);
        save e = new save();
        saveReminderButton.addActionListener(e);
        validate();
        pack();
    }
    public reminderGUI(){
        months30 = new ArrayList<>();
        //this array is to identify months with 30 days
        months30.addAll(Arrays.asList(new Integer[]{Integer.valueOf(3),
                Integer.valueOf(5),
                Integer.valueOf(8),
                Integer.valueOf(10)}));
        reminders = new ArrayList<>();
        readInReminders();
        this.setVisible(true);
        switchToBoxes();

    }
    public static void main(String [] args){
        reminderGUI reminderGUI = new reminderGUI();
        reminderGUI.setSize(200,500);

    }
    class saveCal implements ActionListener{
        //To save the calendar value as a Reminder
        @Override
        public void actionPerformed(ActionEvent e) {
            int toPut=0;
            for (int i = 0 ; i < months.length;i++){
                if (months[i].equals(lbMonth.getText().split(" ")[0])){
                    toPut=i;
                }
            }
            Reminder reminder = new Reminder(wk.isSelected(),dy.isSelected(),hr.isSelected(),
                    ((Integer) table.getValueAt(table.getSelectedRows()[0],table.getSelectedColumn())).intValue(),
                    toPut,
                    Integer.parseInt(lbMonth.getText().split(" ")[1]),
                    (int) spinned.getValue(),
                    (int) minSpin.getValue(),
                    descr.getText()
                    );
            reminders.add(reminder);
        }
    }
    class save implements ActionListener {
        public void actionPerformed(ActionEvent e){

            Reminder reminder = new Reminder(a1WeekCheckBox.isSelected(),
                    a1DayCheckBox.isSelected(),
                    a3HoursCheckBox.isSelected(),
                    (int)txtDay.getSelectedItem(),
                    (int)txtMonth.getSelectedItem(),
                    (int)txtYear.getSelectedItem(),
                    (int)txtHour.getSelectedItem(),
                    (int) txtMinute.getSelectedItem(),

                    txtDesc.getText());
            reminders.add(reminder);

        }

    }

    class start implements ActionListener {
        TrayIcon icon;
        public long checkDays(Reminder reminder){
            Calendar date = new GregorianCalendar(reminder.year
                    ,reminder.month
                    ,reminder.day,
                    reminder.hour,
                    reminder.minute);

            Calendar now = Calendar.getInstance();
            long diff =0;
            diff =TimeUnit.DAYS.convert(date.getTime().getTime()-now.getTime().getTime(),TimeUnit.MILLISECONDS);

            return diff;
        }
        public long checkHours(Reminder reminder){
            Calendar date = new GregorianCalendar(reminder.year
                    ,reminder.month
                    ,reminder.day,
                    reminder.hour,
                    reminder.minute);

            Calendar now = Calendar.getInstance();
            long diff =0;
            diff =TimeUnit.MINUTES.convert(date.getTime().getTime()-now.getTime().getTime(),TimeUnit.MILLISECONDS);


            return diff;
        }
        public void toTray() throws IOException {
            //To move the program into the System Tray (Assumes Windows)
            reminderGUI.this.setVisible(false);
            SystemTray tray = SystemTray.getSystemTray();
            final PopupMenu pop = new PopupMenu();
            MenuItem newR = new MenuItem("New Reminder");
            File image = new File("./clock-icon.png");
            BufferedImage trayIconImage = ImageIO.read(image);
            int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
            icon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
            MenuItem closeR = new MenuItem("Close");
            Timer timer = new Timer(60000, e -> {
                for (Reminder reminder:reminders) {
                    //IF / ELSE IF to work out which message it should display during this
                    //Runthrough
                    Long toPrint = checkDays(reminder);
                    if (toPrint ==0){
                        toPrint=checkHours(reminder);
                        if ((toPrint <= 60)&&reminder.hourC){
                            try {
                                icon.displayMessage("REMINDER",
                                        "Your appointment with \"" + reminder.desc + "\" is within 3 Hours", TrayIcon.MessageType.WARNING);

                                Thread.sleep(1000);
                                Toolkit.getDefaultToolkit().beep();
                                Thread.sleep(1000);

                                Toolkit.getDefaultToolkit().beep();
                                Thread.sleep(1000);
                                Toolkit.getDefaultToolkit().beep();
                            }
                            catch (InterruptedException e1) {
                                                                e1.printStackTrace();
                                                            }
                        }
                        else if ((toPrint <= 7)){
                            try {
                                icon.displayMessage("REMINDER",
                                        "Your appointment with \"" + reminder.desc + "\" is imminent", TrayIcon.MessageType.WARNING);
                                Thread.sleep(1000);
                                Toolkit.getDefaultToolkit().beep();
                                Thread.sleep(1000);
                                Toolkit.getDefaultToolkit().beep();
                                Thread.sleep(1000);
                                Toolkit.getDefaultToolkit().beep();
                                Thread.sleep(1000);
                                Toolkit.getDefaultToolkit().beep();
                                reminders.remove(reminders.indexOf(reminder));
                            }catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                    else if ((toPrint <=1)&&reminder.dayC){
                        icon.displayMessage("REMINDER",
                                "Your appointment with \""+reminder.desc+"\" is within a Day", TrayIcon.MessageType.WARNING);
                        Toolkit.getDefaultToolkit().beep();
                        Toolkit.getDefaultToolkit().beep();
                        reminder.dayC=false;
                    }else if ((toPrint <= 7)&&reminder.weekC){
                        icon.displayMessage("REMINDER",
                                "Your appointment with \""+reminder.desc+"\" is within a Week", TrayIcon.MessageType.WARNING);
                        Toolkit.getDefaultToolkit().beep();
                        reminder.weekC=false;
                    }

                }
            });
            timer.start();
            pop.add(newR);
            pop.add(closeR);
            icon.setPopupMenu(pop);
            try {
                tray.add(icon);
            }catch(AWTException g){
                System.out.println("Could not be added");
            }
            newR.addActionListener(e -> {
                reminderGUI.this.setVisible(true);
                tray.remove(icon);
            });
            closeR.addActionListener(e -> closing());
        }
        public void actionPerformed(ActionEvent e) {
            try {
                toTray();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
