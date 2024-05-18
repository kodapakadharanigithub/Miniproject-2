package com.hcl.service;

import java.text.ParseException;
import java.util.List;

import com.hcl.entities.BookingRequest;
import com.hcl.entities.QueryRequest;
import com.hcl.entities.QueryResponse;
import com.hcl.entities.Ride;
import com.hcl.entities.UserBookings;
import com.hcl.entities.Users;
import com.hcl.entities.Vehicle;


public interface UsersService {
	
	public List<Users> getAllUsers();
	public Users userRegister(Users user);
	public boolean getUserById(int userId);
	public List<Vehicle> getAvailableVehicles(String type);
	public List<Ride> getRidesSortByPrice();
	public boolean bookVehicle(BookingRequest bookingRequest,int userId);
	public boolean cancelVehicle(int booking_id,int id);
	public List<UserBookings> getAllMyBookings(int user_id);
	public String  DateConversion(String date) throws ParseException;
	public String timeConversion(String time) throws ParseException;
	public String checkDateTime(BookingRequest bookingRequest);
	public UserBookings getConfirmedBookings(int userId);
	public QueryRequest postQuery(int userId,String vehicleId,String query);
	public QueryResponse getRepliedAnswers(int userId);

}
