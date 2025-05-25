package HospitalManagementSystem;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class HospitalManagementSystem  {
	
	private static final String url = "jdbc:mysql://localhost:3306/hospital";

	private static final String username="root";
	private static final String password="Healer529";


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		Scanner scanner=new Scanner(System.in);
		try {
			Connection connection=DriverManager.getConnection(url,username,password);
			Patients patients=new Patients(connection,scanner);
			Doctors doctors=new Doctors(connection);
			while(true) {
				System.out.println("HOSPITAL MANAGEMENT SYSTEM");
				System.out.println("1. Add Patient");
				System.out.println("2. View Patients");
				System.out.println("3. View Doctors");
				System.out.println("4. Book Appointment");
				System.out.println("5. Remove Patient");
				System.out.println("6. Exit");
				System.out.print("Enter Your Choice:");
				int choice=scanner.nextInt();
				switch(choice) {
				case 1:
					patients.addPatient();
					break;
				case 2:
					patients.viewPatients();
					break;
				case 3:
					doctors.viewDoctors();
					break;
				case 4:
					bookAppointment(patients,doctors,connection,scanner);
					break;
				case 5:
					patients.removePatient();
					break;
				case 6:
					return;
					default:
						System.out.println("Enter a valid choice");
						break;
				}


			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void bookAppointment(Patients patient, Doctors doctor,Connection connection, Scanner scanner) {
		System.out.print("Enter Patient ID:");
		int patientId=scanner.nextInt();
		System.out.print("Enter Doctor ID:");
		int doctorId=scanner.nextInt();
		System.out.println("Enter Appointment Date(YYYY-MM-DD):");
		String appointmentDate=scanner.next();
		if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
			if(checkDoctorAvailability(doctorId,appointmentDate,connection)) {
				String query="insert into appointments(patient_id,doctor_id,appointment_date)values(?,?,?)";
				try {
					PreparedStatement preparedStatement=connection.prepareStatement(query);
					preparedStatement.setInt(1, patientId);
					preparedStatement.setInt(2, doctorId);
					preparedStatement.setString(3, appointmentDate);
					int affectedRows=preparedStatement.executeUpdate();
					if(affectedRows>0) {
						System.out.println("Appointment booked");
					}else {
						System.out.println("Failed to book appointment");
					}
				}catch(SQLException e) {
					e.printStackTrace();
				}
			}else {
				System.out.println("Doctor not available");
			}
		}else {
			System.out.println("Either doctor or patient doesnt exist");
		}
	}
	public static boolean checkDoctorAvailability(int doctorId,String appointmentDate,Connection connection) {
		String query="select count(*) from appointments where doctor_id=? and appointment_date=?";
		try {
			PreparedStatement preparedStatement=connection.prepareStatement(query);
			preparedStatement.setInt(1, doctorId);
			preparedStatement.setString(2, appointmentDate);
			ResultSet resultSet=preparedStatement.executeQuery();
			if(resultSet.next()) {
				int count=resultSet.getInt(1);
				if(count==0) {
					return true;
				}else {
					return false;
				}
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
