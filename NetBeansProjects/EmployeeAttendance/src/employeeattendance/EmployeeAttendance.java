package employeeattendance;

import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class EmployeeAttendance {

    public static void main(String[] args) {
        String userId="admin",password="admin";
       
       JFrame f = new JFrame();
        try {
            f.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("loginImage.jpg")))));
        } catch (IOException ex) {
            Logger.getLogger(EmployeeAttendance.class.getName()).log(Level.SEVERE, null, ex);
        }
       JTextField adminIdTextField;
       JPasswordField passwordField;
       JLabel adminIdLabel,passwordLabel,titleLabel;
       JButton login,employee;
       
       titleLabel = new JLabel("Admin Login");
       titleLabel.setBounds(200,100,150,20);
       adminIdLabel = new JLabel("Admin Id");
       adminIdLabel.setBounds(100, 150, 100, 20);
       passwordLabel = new JLabel("Password");
       passwordLabel.setBounds( 100, 170, 100, 20);
       adminIdTextField = new JTextField();
       adminIdTextField.setBounds(200,150,100,20);
       passwordField = new JPasswordField();
       passwordField.setBounds(200,170,100,20);
       login = new JButton("Log in");
       login.setBounds(140,200,100,25);
       
       JLabel errorMessage = new JLabel();
       errorMessage.setForeground(Color.RED);
       errorMessage.setBounds(50,250,200,20);
       
       employee = new JButton("Employee");
       employee.setBounds(270,240,120,25);
       
       f.add(employee);
       f.add(errorMessage);
       f.add(login);
       f.add(adminIdTextField);
       f.add(passwordField);
       f.add(titleLabel);
       f.add(adminIdLabel);
       f.add(passwordLabel);
       
       f.setSize(440,322);
       f.setLayout(null);
       f.setVisible(true);
       
       employee.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                f.setVisible(false);
                JFrame eFrame = new JFrame();
                
                JLabel title = new JLabel("Employee Attendence");
                title.setBounds(100,100,200,25);
                eFrame.add(title);
                
                JTextField text = new JTextField();
                text.setBounds(100,190,200,25);
                eFrame.add(text);
                
                JButton select = new JButton("Select");
                select.setBounds(350,190,90,25);
                eFrame.add(select);
                
                JButton submit = new JButton("Submit");
                submit.setBounds(120,230,90,25);
                eFrame.add(submit);
                
                JButton logout = new JButton("Logout");
                logout.setBounds(230,230,90,25);
                eFrame.add(logout);
                
                eFrame.setLayout(null);
                eFrame.setSize(500,400);
                eFrame.setVisible(true);
                
                logout.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        eFrame.setVisible(false);
                        f.setVisible(true);
                    }
                    
                });
                
                select.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        int result = fileChooser.showOpenDialog(null);
                        if(result==JFileChooser.APPROVE_OPTION)
                        {
                            File selectedFile = fileChooser.getSelectedFile();
                            text.setText(selectedFile.getAbsolutePath());
                        }
                    }
                    
                });
                
                submit.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                        String filePath = text.getText();
                        String charset = "UTF-8";
                        Map hintMap = new HashMap();
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                        try 
                        {
                            String empId = QRCode.readQRCode(filePath, charset, hintMap);
                            FileInputStream fin = new FileInputStream("EmployeeAttendence.xlsx");                            XSSFWorkbook wb = new XSSFWorkbook(fin);
                            XSSFSheet sheet = wb.getSheetAt(0);
                            int rowcount,colcount,i;
                            Row row;
                            Cell cell;
                            String eid;
                            rowcount = sheet.getLastRowNum();
                            
                            JFrame aFrame = new JFrame();
                                    
                            JLabel label = new JLabel();
                            label.setBounds(30,60,150,25);
                            aFrame.add(label);
                                    
                            JButton ok = new JButton("OK");
                            ok.setBounds(120,100,60,25);
                            aFrame.add(ok);
                            ok.addActionListener(new ActionListener()
                            {
                                @Override
                                 public void actionPerformed(ActionEvent e) {
                                    aFrame.setVisible(false);
                                 }
                                        
                             });
                            
                            for(i=0;i<=rowcount;i++)
                            {
                                row = sheet.getRow(i);
                                cell = row.getCell(0);
                                eid = cell.getStringCellValue();
                                if(eid.equals(empId))
                                {
                                    cell = row.getCell(6);
                                    String cellInput = cell.getStringCellValue();
                                    if(cellInput.equals("Absent"))
                                    {
                                       label.setText("Success") ;
                                       colcount = 5;
                                       DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                                       String myDate = df.format(new Date());
                                       cell = row.getCell(++colcount);
                                       cell.setCellValue((String)myDate);
                                       Date date = new Date();
                                       SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                                       String time = sdf.format(date);
                                       cell = row.getCell(++colcount);
                                       cell.setCellValue((String)time);
                                       FileOutputStream fout = new FileOutputStream("EmployeeAttendence.xlsx");
                                       wb.write(fout);
                                       
                                    }
                                    else
                                    {
                                        label.setBackground(Color.RED);
                                        label.setText("Already Inserted");
                                        
                                    }
                                    
                                    break;
                                }
                            }
                            aFrame.setLayout(null);
                            aFrame.setSize(200,150);
                            aFrame.setVisible(true);
                        }
                        catch (IOException | NotFoundException ex) 
                        {
                            Logger.getLogger(EmployeeAttendance.class.getName()).log(Level.SEVERE, null, ex);
                        }
                       
                    }
                    
                });
            }
           
       });
       
       login.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               String inputUserId,inputPassword;
               inputUserId = adminIdTextField.getText();
               inputPassword = passwordField.getText();
               if(userId.equals(inputUserId)&&password.equals(inputPassword))
               {
                   f.setVisible(false);
                   
                   JFrame adminFrame = new JFrame();
                   adminFrame.setSize(1200,650);
                   adminFrame.setLayout(null);
                   adminFrame.setVisible(true);
                   
                   JPanel p1 = new JPanel();
                   p1.setBounds(5,5,400,640);
                   p1.setBackground(Color.GRAY);
                   p1.setLayout(null);
                   JLabel p1Heading = new JLabel("NEW EMPLOYEE REGISTRATION");
                   p1Heading.setBounds(80,100,250,20);
                   p1.add(p1Heading);
                   
                   JLabel empId = new JLabel("EMP ID");
                   empId.setBounds(55,160,100,20);
                   p1.add(empId);
                   JTextField empIdTextField = new JTextField();
                   empIdTextField.setBounds(230,160,100,20);
                   p1.add(empIdTextField);
                   
                   JLabel firstName = new JLabel("FIRST NAME");
                   firstName.setBounds(55,220,100,20);
                   p1.add(firstName);
                   JTextField firstNameTextField = new JTextField();
                   firstNameTextField.setBounds(230,220,100,20);
                   p1.add(firstNameTextField);
                   
                   JLabel lastName = new JLabel("LAST NAME");
                   lastName.setBounds(55,280,100,20);
                   p1.add(lastName);
                   JTextField lastNameTextField = new JTextField();
                   lastNameTextField.setBounds(230,280,100,20);
                   p1.add(lastNameTextField);
                   
                   JLabel email = new JLabel("EMAIL");
                   email.setBounds(55,340,100,20);
                   p1.add(email);
                   JTextField emailTextField = new JTextField();
                   emailTextField.setBounds(230,340,100,20);
                   p1.add(emailTextField);
                   
                   JLabel phone = new JLabel("PHONE");
                   phone.setBounds(55,400,100,20);
                   p1.add(phone);
                   JTextField phoneTextField = new JTextField();
                   phoneTextField.setBounds(230,400,100,20);
                   p1.add(phoneTextField);
                   
                   JLabel designation = new JLabel("DESIGNATION");
                   designation.setBounds(55,460,100,20);
                   p1.add(designation);
                   JTextField designationTextField = new JTextField();
                   designationTextField.setBounds(230,460,100,20);
                   p1.add(designationTextField);
                   
                   JButton submit = new JButton("SUBMIT");
                   submit.setBounds(55,520,100,30);
                   p1.add(submit);
                   
                   JButton clear = new JButton("CLEAR");
                   clear.setBounds(230,520,100,30);
                   p1.add(clear);
                   
                   JLabel text = new JLabel();
                   text.setBounds(100,580,200,20);
                   p1.add(text);
                   
                   submit.addActionListener(new ActionListener()
                   {
                       @Override
                       public void actionPerformed(ActionEvent e) {
                           Employee emp = new Employee();
                           emp.empId = empIdTextField.getText();
                           emp.firstName = firstNameTextField.getText();
                           emp.lastName = lastNameTextField.getText();
                           emp.email = emailTextField.getText();
                           emp.phone = phoneTextField.getText();
                           emp.designation = designationTextField.getText();
                           
                           String QRCodeData = emp.empId;
                           String filePath = emp.firstName + ".png" ;
                           String charset = "UTF-8";
                           Map hintMap = new HashMap();
                           hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                           try {
                               QRCode.createQRCode(QRCodeData, filePath, charset, hintMap, 200, 200);
                           } catch (WriterException | IOException ex) {
                               Logger.getLogger(EmployeeAttendance.class.getName()).log(Level.SEVERE, null, ex);
                           }
                           
                           
                           int rowcount,colcount;
                           File file = new File("EmployeeAttendence.xlsx");
                           if(file.exists())
                           {
                              try
                              {
                                  FileInputStream inp = new FileInputStream(file);
                                  XSSFWorkbook wb = new XSSFWorkbook(inp);
                                  XSSFSheet sheet = wb.getSheetAt(0);
                                  rowcount = sheet.getLastRowNum();
                                  Row row = sheet.createRow(++rowcount);
                                  colcount=-1;
                                  Cell cell = row.createCell(++colcount);
                                  cell.setCellValue((String) emp.empId );
                                  cell = row.createCell(++colcount);
                                  cell.setCellValue((String) emp.firstName );
                                  cell = row.createCell(++colcount);
                                  cell.setCellValue((String) emp.lastName );
                                  cell = row.createCell(++colcount);
                                  cell.setCellValue((String) emp.email );
                                  cell = row.createCell(++colcount);
                                  cell.setCellValue((String) emp.phone );
                                  cell = row.createCell(++colcount);
                                  cell.setCellValue((String) emp.designation );
                                  cell = row.createCell(++colcount);
                                  cell.setCellValue((String) "Absent" );
                                  cell = row.createCell(++colcount);
                                  cell.setCellValue((String) "Absent" );
                                  
                                  FileOutputStream fout = new FileOutputStream("EmployeeAttendence.xlsx");
                                  wb.write(fout);
                              }
                              catch(IOException er)
                              {
                                  System.out.println("Error occured");
                              }
                           
                               
                           }
                           else
                           {
                               try
                               {
                                   XSSFWorkbook wb = new XSSFWorkbook();
                                   XSSFSheet sheet = wb.createSheet("EmployeeAttendence");
                                   rowcount=-1;
                                   colcount=-1;
                                   Row row = sheet.createRow(++rowcount);
                                   Cell cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "EMPLOYEE ID" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "FIRST NAME" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "LAST NAME" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "EMAIL" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "PHONE" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "DESIGNATION" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "DATE" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "TIME" );
                                   
                                   row = sheet.createRow(++rowcount);
                                   colcount=-1;
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) emp.empId );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) emp.firstName );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) emp.lastName );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) emp.email );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) emp.phone );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) emp.designation );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "Absent" );
                                   cell = row.createCell(++colcount);
                                   cell.setCellValue((String) "Absent" );
                                   
                                   FileOutputStream fout = new FileOutputStream("EmployeeAttendence.xlsx");
                                   wb.write(fout);
                               }
                               catch(IOException er)
                               {
                                     System.out.println("Error occured");
                               }
                           }
                           text.setForeground(Color.GREEN);
                           text.setText("Registered Successfully");
                           empIdTextField.setText(null);
                           firstNameTextField.setText(null);
                           lastNameTextField.setText(null);
                           emailTextField.setText(null);
                           phoneTextField.setText(null);
                           designationTextField.setText(null);
                       }
                       
                   });
                   
                   clear.addActionListener(new ActionListener(){
                       @Override
                       public void actionPerformed(ActionEvent e) {
                           empIdTextField.setText(null);
                           firstNameTextField.setText(null);
                           lastNameTextField.setText(null);
                           emailTextField.setText(null);
                           phoneTextField.setText(null);
                           designationTextField.setText(null);
                       }
                   });
                   
               
                   
                   
                   JPanel p2 = new JPanel();
                   p2.setBounds(410,5,785,640);
                   p2.setBackground(Color.red);
                   p2.setLayout(null);
                   
                   JButton logout = new JButton("Logout");
                   logout.setBounds(600,100,100,25);
                   p2.add(logout);
                   logout.addActionListener(new ActionListener()
                   {
                       @Override
                       public void actionPerformed(ActionEvent e) {
                           adminFrame.setVisible(false);
                           f.setVisible(true);
                           adminIdTextField.setText(null);
                           passwordField.setText(null);
                       }
                       
                   });
                   
                   adminFrame.getContentPane().setBackground( Color.gray );
                   
                   adminFrame.add(p1);
                   adminFrame.add(p2);
                   
                   
               }
               else
               {
                   errorMessage.setText("Sorry try again");
               }
           }
       });
    }
    
}
//test