package com.hcl.serviceImpl;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hcl.entities.Owner;
import com.hcl.entities.QueryRequest;
import com.hcl.entities.QueryResponse;
import com.hcl.entities.Ride;
import com.hcl.entities.UserBookings;
import com.hcl.entities.Vehicle;
import com.hcl.exceptions.RideAlreadyExistOrNotException;
import com.hcl.exceptions.UserAlreadyExistOrNotException;
import com.hcl.exceptions.VehicleAlreadyExistOrNotException;
import com.hcl.repository.OwnerRepository;
import com.hcl.repository.QueryRequestRepository;
import com.hcl.repository.QueryResponseRepository;
import com.hcl.repository.RideRepository;
import com.hcl.repository.UserBookingsRepository;
import com.hcl.repository.VehicleRepository;
import com.hcl.service.OwnerService;
@Service
public class OwnerServiceImpl implements OwnerService{
	
	@Autowired
	private OwnerRepository ownerRepository;
	
	@Autowired 
	private VehicleRepository vehicleRepository;
	
	@Autowired
	private RideRepository rideRepository;
	
	@Autowired
	private UserBookingsRepository userBookingsRepository;
	
	@Autowired
	private QueryRequestRepository queryRequestRepository;
	
	@Autowired
	private QueryResponseRepository queryResponseRepository;
	
	
	private static Vehicle vehicle;
	private static Ride ride;
	private static QueryRequest queryRequest;
	private static QueryResponse queryResponse;
	
	
	@Override
	public List<Owner> getAllOwners() {
		//to get all the owners
		return ownerRepository.findAll();
	}
	
	@Override
	public Owner ownerRegister(Owner owner) {
		//to register the owner
		return ownerRepository.save(owner);
		
	}

	@Override
	public boolean getOwnerById(int ownerId) {
		// TO check and get the owner exist or not by id
		boolean flag=true;
		try
		{
			Owner owner=ownerRepository.findById(ownerId).orElse(null);
			if(owner!=null)
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
	public Vehicle insertVehicle(Vehicle vehicle) {
		// TO insert the vehicle into database
		return  vehicleRepository.save(vehicle);
		
	}

	@Override
	public boolean getVehicleById(String vehicleId) {
		// TO get or check vehicle exist or not
		boolean flag=true;
		try
		{
		Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
		if(vehicle==null)
		{
			flag= false;		
		}
		else
		{
			throw new VehicleAlreadyExistOrNotException("Vehicle number with "+vehicleId+" Already Exist");
		}
		}
		catch(VehicleAlreadyExistOrNotException e)
		{
			System.out.println(e.getMessage());
		}
		return flag;
		
	}

	@Override
	public List<Vehicle> getAllMyVehicles(int ownerId) {
		// TO get all the vehicles
		return vehicleRepository.findVehiclesByOwnerId(ownerId);
	}

	@Override
	public boolean deleteVehicle(String vehicleId,int ownerId) {
		// To delete the his vehicle by id 
		 vehicle = vehicleRepository.findById(vehicleId).orElse(null);
		 Owner owner = vehicle.getOwner();
		 if(owner!=null && ownerId==owner.getOwnerId())
		 {
			 vehicleRepository.deleteById(vehicleId);
			 return true;
		 }
		return false;	
	}

	@Override
	public boolean updateVehicle(Vehicle vehicle,int ownerId) {
		// TO update the vehicle by id
		Vehicle vehicle2=vehicleRepository.findOwnerVehicles(vehicle.getVehicleId(),ownerId);
		vehicle.setVehicleId(vehicle2.getVehicleId());
		if(vehicle2!=null)
		{
			vehicleRepository.save(vehicle);
			return true;
		}
		return false;	
	}
	
	@Override
	public String insertRide(Ride ride){
		boolean res = getVehicleById(ride.getVehicleId());
		Vehicle vehicle = vehicleRepository.findOwnerVehicles(ride.getVehicleId(), ride.getOwnerId());
		try
		{
			if(res==true )
			{
				//to check whether vehicle is owned by logged in owner or not
				if(vehicle!=null && vehicle.getVehicleId().equals(ride.getVehicleId()) && vehicle.getOwner().getOwnerId()==ride.getOwnerId())
				{
					//if vehicle is owner's
					String strDate=DateConversion(ride.getRideDate());
					String strtime=timeConversion(ride.getRideTime());
					
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
						int year=Integer.parseInt(ride.getRideDate().substring(0, 4));
						int month=Integer.parseInt(ride.getRideDate().substring(5,7));
						int date=Integer.parseInt(ride.getRideDate().substring(8,10));
						int hour=Integer.parseInt(ride.getRideTime().substring(0, 2));
						int min=Integer.parseInt(ride.getRideTime().substring(3,5));
						int sec=Integer.parseInt(ride.getRideTime().substring(6,8));
						//to check date is correct or not
						if((year>=2024 && year<=3000) && (month>=1 && month<=12) && (date>=1 && date<=31))
						{
							//to check time is correct or not
							if((hour>=1 && hour<=24) && (min>=0 && min<=59) && (sec>=0 && sec<=59))
							{
								// if all details are correct ride will be added 	
								rideRepository.save(ride);
								return null;
							}
								
							else
							{
								//if time is not correct
								return "Enter correct Time!!!";	
							}
							
						}
						else
						{
							//if date is not correct
							return "Enter Correct Date!!!";
						}
					}
				}
				else
				{
					//if vehicle is not owned by logged in owner
					return "vehicle id " +ride.getVehicleId()+" is not owned by you so u can't add ride for it!!";
				}
			}
			else
			{
				//if vehicle is not exist ride can't be added
				throw new VehicleAlreadyExistOrNotException("Vehicle Id "+ride.getVehicleId()+" not exist so can't add ride for that vehicle!!");
			}
			
		}
		catch(VehicleAlreadyExistOrNotException e)
		{
			return e.getMessage();
		}
		
	}
	

	@Override
	public boolean getRideById(int rideId) {
		// TO get or check ride exist or not
		boolean flag=true;
		Optional<Ride> ride=rideRepository.findById(rideId);
		try
		{
			//if ride not exist
			if(ride.isEmpty())
			{
				flag=false;
			}
			else
			{
				//if ride already exist
				throw new RideAlreadyExistOrNotException("Ride with "+rideId+"already exist");

			}
		}
		catch(RideAlreadyExistOrNotException e)
		{
			System.out.println(e.getMessage());
		}
		return flag;
	}

	@Override
	public boolean deleteRide(int rideId,int ownerId) {
		// TO delete ride from owner
		ride=rideRepository.findById(rideId).orElse(null);
		//to check whether ride is created by logged in owner or not
		if(ride.getOwnerId()==ownerId)
		{
			//if ride is created by already logged in owner
			rideRepository.deleteById(rideId);
			return true;
		}
		//if ride is not created by already logged in owner
		return false;
		
	}

	@Override
	public List<Ride> getAllMyRides(int ownerId) {
		// TO get all the rides
		return rideRepository.findRidesByOwnerId(ownerId);
	}
	
	
	@Override
	public boolean checkVehicle(UserBookings userBookings) {
		// TO check the vehicle available or not for booking
		int rideId=rideRepository.checkVehicle(userBookings.getBookingDate(),userBookings.getBookingTime()
				,userBookings.getPickupLocation(),
				userBookings.getDropLocation(), "available");
		String vehicleId=rideRepository.checkVehicleId(userBookings.getBookingDate(),userBookings.getBookingTime()
				,userBookings.getPickupLocation(),
				userBookings.getDropLocation(), "available");
		if(rideId!=0 && vehicleId!=null)
		{
			int booking_id=userBookings.getBookingId();
			userBookings.setRideId(rideId);
			userBookings.setVehicleId(vehicleId);
			userBookings.setStatus("pending");
			userBookingsRepository.save(userBookings);
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean updateRideStatus(String status, int rideId) {
		// TO update the status as booked
		
		ride=rideRepository.findById(rideId).orElse(null);
		if(ride!=null)
		{
			ride.setRideId(rideId);
			ride.setStatus(status);
			rideRepository.save(ride);
			return true;
		}
		return false;
		
		
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
	public List<Ride> getVehicleRidesByOwnerId(String vehicleId, int ownerId) {
		// TO get vehicle rides bases on ownerid and vehidid
		return rideRepository.findVehicleRidesByOwnerId(vehicleId, ownerId);
		 
	}

	@Override
	public String confirmBooking(int ownerId,int rideId,int userId) {
		// To confirm the user booking
		UserBookings userBookings = userBookingsRepository.findPendingBookings("pending", rideId,userId);
		Ride ride = rideRepository.getById(rideId);
		if(ride.getOwnerId()==ownerId)
		{
			userBookings.setStatus("successfull");
			userBookingsRepository.save(userBookings);
			ride.setStatus("booked");
			ride.setRideId(rideId);
			rideRepository.save(ride);
			return "Booking Confirmed for ride Id: "+rideId;
		}
		else
		{
			return "You can't confirm the booking because this ride is not created By You !!!";
		}
	}

	@Override
	public List<Ride> sendNotification(int ownerId) {
		// TO send the notification of his vehicles once it is booked
		List<Ride> ridesList = rideRepository.findOwnerBookedRides(ownerId, "booked");
		return ridesList;
	}

	@Override
	public QueryRequest getPendingQueries(int ownerId,int userId) {
		// TO get the queries what are pending
		queryRequest = queryRequestRepository.findQueries("pending", userId);
		if(queryRequest!=null)
		{
			 vehicle = vehicleRepository.findById(queryRequest.getVehicleId()).orElse(null);
			 if(vehicle!=null && vehicle.getOwner().getOwnerId()==ownerId)
			 {
				 return queryRequest;
			 }
			 else
			 {
				 return null;
			 }
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean replyToQuery(QueryResponse queryResponse) {
		//to save the query answer
		queryRequest=queryRequestRepository.findById(queryResponse.getQueryId()).orElse(null);
		if(queryRequest!=null && queryResponse.getQueryId()==queryRequest.getQueryId())
		{
			//if owner enters correct query details
			queryRequest.setStatus("replied");
			queryRequestRepository.save(queryRequest);
			queryResponseRepository.save(queryResponse);
			return true;
		}
		else
		{
			//if owner enters Incorrect query details
			return false;
		}
		
	}

}

