package com.hcl.service;

import java.text.ParseException;
import java.util.List;
import com.hcl.entities.Owner;
import com.hcl.entities.QueryRequest;
import com.hcl.entities.QueryResponse;
import com.hcl.entities.Ride;
import com.hcl.entities.UserBookings;
import com.hcl.entities.Vehicle;

public interface OwnerService {
	
	public List<Owner> getAllOwners();
	public Owner ownerRegister(Owner owner);
	public boolean getOwnerById(int ownerId);
	public Vehicle insertVehicle(Vehicle vehicle);
	public boolean getVehicleById(String vehicleId);
	public List<Vehicle> getAllMyVehicles(int ownerId);
	public boolean deleteVehicle(String vehicleId,int ownerId);
	public boolean updateVehicle(Vehicle vehicle,int ownerId);
	public String insertRide(Ride ride);
	public boolean getRideById(int rideId);
	public boolean deleteRide(int rideId,int ownerId);
	public List<Ride> getAllMyRides(int ownerId);
	public List<Ride> getVehicleRidesByOwnerId(String vehicleId,int ownerId);
	public boolean checkVehicle(UserBookings userBookings);
	public boolean updateRideStatus(String status,int rideId);
	public String  DateConversion(String date) throws ParseException;
	public String timeConversion(String time) throws ParseException;
	public String confirmBooking(int ownerId,int rideId,int userId);
	public List<Ride> sendNotification(int ownerId);
	public QueryRequest getPendingQueries(int ownerId,int userId);
	public boolean replyToQuery(QueryResponse queryResponse);
}
