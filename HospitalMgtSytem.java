package HospitalMgtSystem;

import com.mysql.cj.jdbc.Driver;

import java.sql.*;
import java.util.Scanner;

public class HospitalMgtSytem {
    private  static  final String url = "jdbc:mysql://localhost:3306/hospital ";
    private  static  final String username ="root";
    private  static  final  String password ="1560";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Scanner scanner =new Scanner(System.in);

        try {
            Connection connection= DriverManager.getConnection(url,username,password);
            patient patient =new patient(connection,scanner);
            doctors doctors =new doctors(connection);
            while(true){
                System.out.println("HOSPITAL MANGAMENT SYSTEM");
                System.out.println("1 : ADD PATIENTS");
                System.out.println("2 : VIEW PATIENTS");
                System.out.println("3 : VIEW DOCOTRS");
                System.out.println("4 : BOOK APPOINTMENTS");
                System.out.println("5 : EXIT");
                System.out.println("Enter your choices: ");

                int choice =scanner.nextInt();

                switch(choice){
                    case 1:
                        //add patient
                        patient.addpatient();
                        System.out.println();
                        break;
                    case 2:
                        //view patients
                        patient.viewpatient();
                        System.out.println();
                        break;
                    case 3:
                        //view doctors
                        doctors.viewdoctors();
                        System.out.println();
                        break;
                    case 4:
                        //Book Appoinments
                        bookAppointment(patient,doctors,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        System.out.println("thanky for using Hospital mgt system");
                        return;
                    default:
                        System.out.println("Enter the valid choice");

                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
  public static void bookAppointment( patient patient, doctors doctors ,Connection connection,Scanner scanner){
      System.out.println("Enter Patient Id: ");
      int patientId = scanner.nextInt();
      System.out.println("Enter doctors Id: ");
      int doctorsId = scanner.nextInt();
      System.out.println("Enter Appointment Date (YYY-MM-DD)");
      String appointmentDate=scanner.next();
      if(patient.getpatientById(patientId)&&doctors.getDoctorsById(doctorsId)){
          if(CheckDoctorAvailability(doctorsId,appointmentDate,connection)){
              String appointmentQuery = "INSERT INTO appointments(p_id,d_id appointment_date)VALUES(?,?,?)";
              try {
                  PreparedStatement preparedStatement= connection.prepareStatement(appointmentQuery);
                  preparedStatement.setInt(1,patientId);
                  preparedStatement.setInt(2,doctorsId);
                  preparedStatement.setString(3,appointmentDate);
                  int rowAffected =preparedStatement.executeUpdate();
                  if(rowAffected>0){
                      System.out.println("Appointment Booked");
                  }else {
                      System.out.println("fail appointment");
                  }
              }catch (SQLException e){
                  e.printStackTrace();
              }

          }else {
              System.out.println("Doctor not Available on this date");
          }

      }else {
          System.out.println("Either doctors or patients doesn't exits");
      }
  }

  public static boolean CheckDoctorAvailability(int doctorsId, String appointmentDate,Connection connection) {
      String query = "SELECT COUNT(*) FROM appointments WHERE d_id = ? AND appointment_date = ?";
      try {
          PreparedStatement preparedStatement = connection.prepareStatement(query);
          preparedStatement.setInt(1, doctorsId);
          preparedStatement.setString(2, appointmentDate);
          ResultSet resultSet = preparedStatement.executeQuery();
          if (resultSet.next()) {
              int count = resultSet.getInt(1);
              if (count == 0) {
                  return true;
              } else {
                  return false;
              }
          }
      } catch (SQLException e) {
          e.printStackTrace();
      }
      return false;
  }
}
