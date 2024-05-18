package com.hcl.serviceImpl;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.hcl.entities.BookingRequest;
import com.hcl.entities.QueryRequest;
import com.hcl.entities.QueryResponse;
import com.hcl.entities.Ride;
import com.hcl.entities.UserBookings;
import com.hcl.entities.Users;
import com.hcl.entities.Vehicle;
import com.hcl.exceptions.UserAlreadyExistOrNotException;
import com.hcl.repository.QueryRequestRepository;
import com.hcl.repository.QueryResponseRepository;
import com.hcl.repository.RideRepository;
import com.hcl.repository.UserBookingsRepository;
import com.hcl.repository.UsersRepository;
import com.hcl.repository.VehicleRepository;
import com.hcl.service.OwnerService;
import com.hcl.service.UsersService;
@Service
public class UsersServiceImpl implements UsersService{

	@Autowired
	private UsersRepository usersRepository;
	
	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Autowired
	private RideRepository rideRepository;
	
	@Autowired
	private UserBookingsRepository userBookingsRepository;
	
	@Autowired
	private OwnerService ownerService;
	
	@Autowired
	private QueryRequestRepository queryRequestRepository;
	
	@Autowired
	private QueryResponseRepository queryResponseRepository;
	
	private static QueryRequest queryRequest;
	
	@Override
	public List<Users> getAllUsers() {
		// TO get all the users
		return usersRepository.findAll();
	}
	@Override
	public Users userRegister(Users user) {
		// TO insert user 
		return usersRepository.save(user);
	}
	@Override
	public boolean getUserById(int userId) {
		// TO check and get the user exist or not by id
		boolean flag=true;
		try
		{
			Users user=usersRepository.findById(userId).orElse(null);
			if(user!=null)
			{	
				throw new UserAlreadyExistOrNotException("Already Exist So U can't Register Again");
			}
			else
			{
				flag= false;
			}
			}
			catch(UserAlreadyExistOrNotException e)
			{
				System.out.println(e.getMessage());
			}
			return flag;
	}
	@Override
	public List<Vehicle> getAvailableVehicles(String type) {
		// TO get available vehicles based on type
		return vehicleRepository.findVehiclesByType("available", type);
	}
	@Override
	public List<Ride> getRidesSortByPrice() {
		// TO get the rides by sorting the price
		List<Ride> list=rideRepository.findAll(Sort.by(Sort.Direction.
				fromString("asc"), "price"));
		return list;
	}
	@Override
	public boolean bookVehicle(BookingRequest bookingRequest,int userId) {
		// TO book the vehicle
		UserBookings userBookings=new UserBookings();
		userBookings.setUserId(userId);
		userBookings.setBookingDate(bookingRequest.getBookingDate());
		userBookings.setBookingTime(bookingRequest.getBookingTime());
		userBookings.setDropLocation(bookingRequest.getDropLocation());
		userBookings.setPickupLocation(bookingRequest.getPickupLocation());
		boolean flag=ownerService.checkVehicle(userBookings);
		return flag;
	}
	@Override
	public boolean cancelVehicle(int booking_id,int id) {
		// TO cancel the booked vehicle
		boolean result=false;
		UserBookings userBookings= userBookingsRepository.findBookingByUserId(id, booking_id);
		if(userBookings==null)
		{
			result= false;
		}
		else
		{
			int rideId=userBookings.getRideId();
			boolean flag=ownerService.updateRideStatus("available",rideId);
			if(flag==true)
			{
				result= true;
			}
			else
			{
				result= false;
			}
			userBookingsRepository.deleteById(booking_id);
			result= true;
		}
		return result;
	}
	@Override
	public List<UserBookings> getAllMyBookings(int user_id) {
		// TO get all my bookings
		return userBookingsRepository.findAllBookings(user_id);	
	}
	@Override
	public String  DateConversion(String date) 
	{
		String  msg=null;
		//defining the format of string representation
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		try
		{
			
			//parsing the string to date
			java.util.Date utilDate=dateFormat.parse(date);
		}
		catch(ParseException e)
		{
			msg="Date format is not correct check format should be=yyyy-MM-dd";
			return msg;
		}
		return msg;
	}
	
	@Override
	public String timeConversion(String time) 
	{
		String  msg=null;
		//defining the format of string representation
		SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss");
		try
		{
			//parse the string to java.util.Date
			java.util.Date utilTime=timeFormat.parse(time);
		}
		catch(ParseException e)
		{
			msg="Time format is not correct check format should be=HH:mm:ss";
			return msg;
		}
		return msg;
	}
	@Override
	public String checkDateTime(BookingRequest bookingRequest)
	{
		String strDate=DateConversion(bookingRequest.getBookingDate());
		String strtime=timeConversion(bookingRequest.getBookingTime());
		
		if(strDate!=null)
		{
			return strDate;
		}
		else if(strtime!=null)
		{
			return strtime;
		}
		else
		{
			//2024-12-31  24:60:60
			int year=Integer.parseInt(bookingRequest.getBookingDate().substring(0, 4));
			int month=Integer.parseInt(bookingRequest.getBookingDate().substring(5,7));
			int date=Integer.parseInt(bookingRequest.getBookingDate().substring(8,10));
			int hour=Integer.parseInt(bookingRequest.getBookingTime().substring(0, 2));
			int min=Integer.parseInt(bookingRequest.getBookingTime().substring(3,5));
			int sec=Integer.parseInt(bookingRequest.getBookingTime().substring(6,8));
			if((year>=2024 && year<=3000) && (month>=1 && month<=12) && (date>=1 && date<=31))
			{
				if((hour>=1 && hour<=24) && (min>=0 && min<=59) && (sec>=0 && sec<=59))
				{
					return null;
				}
					
				else
				{
					return "Enter correct Time!!!";	
				}
				
			}
			else
			{
				return "Enter Correct Date!!!";
			}
		}
	}
	@Override
	public UserBookings getConfirmedBookings(int userId) {
		// TO get confirmed bookings from the database
		return userBookingsRepository.findConfirmedBookings("successfull", userId);
		
	}
	@Override
	public QueryRequest postQuery(int userId, String vehicleId, String query) {
		// TO post the query asked by user
		queryRequest=new QueryRequest(query,vehicleId,userId,"pending");
		queryRequestRepository.save(queryRequest);
		return queryRequest;
	}
	@Override
	public QueryResponse getRepliedAnswers(int userId) {
		// TO get the answers for queried
		queryRequest=queryRequestRepository.findQueries("replied", userId);
		QueryResponse queryResponse=queryResponseRepository.findById(queryRequest.getQueryId()).orElse(null);
		return queryResponse;
	}
	

}
