package com.hcl.controller;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.entities.LoginRequest;
import com.hcl.entities.Owner;
import com.hcl.entities.QueryRequest;
import com.hcl.entities.QueryResponse;
import com.hcl.entities.Ride;
import com.hcl.entities.Vehicle;
import com.hcl.service.OwnerService;
import com.hcl.util.JwtUtil;


@RestController
@RequestMapping("/owner")
public class OwnerController {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private OwnerService ownerService;
	
	private static int id=0;
	private static String username,password=null;
	private static Owner owner=null;
	
	
	@PostMapping("/login")
	public String ownerLogin(@RequestBody LoginRequest loginRequest )
	{
		//to get the all the owners
		List<Owner> list = ownerService.getAllOwners();
		boolean flag=false;
		if(list==null)
		{
			return "OOPS!!There are no owners";
		}
		else
		{
			for(Owner o:list)
			{
				//to check whether owner is valid or not 
				if(loginRequest.getUserName().equals(o.getOwnerName()) && loginRequest.getPassWord().equals(o.getPassWord()))
				{	
					flag=true;
					id=o.getOwnerId();
					username=o.getOwnerName();
					password=o.getPassWord();
					break;
				}
				else
				{
					flag=false;	
				}
			}
		}
		if(flag==true)
		{
			//if owner credentials are valid token generated for the owner on their username
			String token=jwtUtil.generateToken(loginRequest.getUserName());
			
			return token;
		}
		else
		{
			//if owner credentials are Invalid
			return "Bad/Invalid Credentials Check And Try To Login Again";
		}	
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> ownerRegister(@RequestBody Owner owner)
	{
		//it will check whether owner exist or not
		boolean flag=ownerService.getOwnerById(owner.getOwnerId());
		if(flag==false)
		{
			//if owner not exist owner will be registered
			ownerService.ownerRegister(owner);
			return new ResponseEntity<>("Registered Successfully",HttpStatus.CREATED);
		}
		else
		{
			//if owner already exist
			return new ResponseEntity<>("Already Registered",HttpStatus.BAD_REQUEST);
		}	
	}
	@PostMapping("/addVehicle")
	public ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle)		
	{
		//to check whether vehicle type is correct or not
		if(vehicle.getType().equalsIgnoreCase("2-wheeler") || vehicle.getType().equalsIgnoreCase("4-wheeler"))
		{
			//it will check vehicle exist or not
			boolean flag=ownerService.getVehicleById(vehicle.getVehicleId());
			if(flag==false)
			{
					//if vehicle not exist then vehicle will be added
					owner=new Owner();
					owner.setOwnerId(id);
					owner.setOwnerName(username);
					owner.setPassWord(password);
					vehicle.setOwner(owner);
					ownerService.insertVehicle(vehicle);
					return new ResponseEntity<>(vehicle,HttpStatus.CREATED);
			}
			
			else
			{
				//if vehicle existed it will not add
				return new ResponseEntity<>(vehicle.getVehicleId()+" Already Existed",HttpStatus.BAD_REQUEST);
			}
		}
		else
		{
			//if owner enters invalid vehicle type
			return new ResponseEntity<>("Vehicle type should be 2-wheeler/4-wheeler",HttpStatus.BAD_REQUEST);
		}
		
	}
	@GetMapping("/getAllMyVehicles")
	public ResponseEntity<?> getAllMyVehicles()
	{
		//to get the list owner vehicle based on id
		List<Vehicle> vehiclesList = ownerService.getAllMyVehicles(id);
		List<String> resultList=new ArrayList<>();
		for(Vehicle v:vehiclesList)
		{
			resultList.add("VehicleId:"+v.getVehicleId()+" Model:"+v.getModel()+" Type:"+v.getType());
		}
		
		if(!vehiclesList.isEmpty())
		{
			//if there are any vehicles it will display
			return new ResponseEntity<>(resultList,HttpStatus.OK);
		}
		else
		{
			//if there are no vehicles
			return new ResponseEntity<>("No vehicles on owner id: "+id,HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping("/deleteVehicle/{vehicleId}")
	public ResponseEntity<?> deleteVehicle(@PathVariable String vehicleId)
	{
		//to check whether vehicle id exist or not
		boolean flag=ownerService.getVehicleById(vehicleId);
		if(flag==false)
		{
			//if vehicle not exist
			return new ResponseEntity<>(vehicleId+" not Exist So can't Delete",HttpStatus.NOT_FOUND);
		}
		else
		{
			//if vehicle exist
			boolean res=ownerService.deleteVehicle(vehicleId,id);
			if(res==true)
			{
				List<Ride> ridesList=ownerService.getVehicleRidesByOwnerId(vehicleId, id);
				for(Ride r:ridesList)
				{
					ownerService.deleteRide(r.getRideId(), id);
				}
				return new ResponseEntity<>("Vehicle Deleted Successfully",HttpStatus.OK);
			}
			else
				return new ResponseEntity<>("You can't delete this Vehicle Because it is not created By You!!",HttpStatus.BAD_REQUEST);

		}	
	}
	
	@PutMapping("/updateVehicle")
	public ResponseEntity<?> updateVehicle(@RequestBody Vehicle vehicle)
	{
		//to check owner enter correct vehicle type or not
		if(vehicle.getType().equalsIgnoreCase("2-wheeler") || vehicle.getType().equalsIgnoreCase("4-wheeler"))
		{
			//to check whether vehicle exist or not
			boolean flag=ownerService.getVehicleById(vehicle.getVehicleId());
			if(flag==false)
			{
				//if vehicle not exist
				return new ResponseEntity<>(vehicle.getVehicleId()+" not Exist So can't Update",HttpStatus.NOT_FOUND);
			}
			else
			{
				//if vehicle exist update that vehicle details only
				owner=new Owner();
				owner.setOwnerId(id);
				owner.setOwnerName(username);
				owner.setPassWord(password);
				vehicle.setOwner(owner);
				boolean res=ownerService.updateVehicle(vehicle,id);
				if(res==true)
					return new ResponseEntity<>("Vehicle Updated Successfully",HttpStatus.OK);
				else
					return new ResponseEntity<>("You can't Update this Vehicle Because it is created By You!!",HttpStatus.BAD_REQUEST);
	
			}
		}
		else
		{
			//if owner enters invalid vehicle type
			return new ResponseEntity<>("You should enter either 2-wheeler or 4-wheeler",HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/getAllMyRides")
	public ResponseEntity<?> getAllMyRides()
	{
		//to get ride list based on owner id
		List<Ride> ridesList = ownerService.getAllMyRides(id);
		if(!ridesList.isEmpty())
		{
			//if there are rides
			return new ResponseEntity<>(ridesList,HttpStatus.OK);
		}
		else
		{
			//if there are no rides
			return new ResponseEntity<>("No Rides on ownerId: "+id,HttpStatus.NOT_FOUND);
		}
	}
	@PostMapping("/addRide")
	public ResponseEntity<?> addRide(@RequestBody Ride ride) throws ParseException
	{
		//to check ride exist or not
		boolean flag = ownerService.getRideById(ride.getRideId());
		
		if(flag==false)
		{
			//if ride is not exist then ride will be inserted
			ride.setOwnerId(id);
			ride.setStatus("available");
			String res=ownerService.insertRide(ride);
			if(res==null)
				return new ResponseEntity<>(ride,HttpStatus.CREATED);
			else
				return new ResponseEntity<>(res,HttpStatus.CREATED);

		}
		else
		{
			//if ride is already existed
			return new ResponseEntity<>("Ride Already Exist u can't add again",HttpStatus.BAD_REQUEST);
		}
			
	}
	
	@DeleteMapping("/cancelRide/{rideId}")
	public ResponseEntity<?> cancelRide(@PathVariable int rideId)
	{
		//to check whether ride exist or not
		boolean flag=ownerService.getRideById(rideId);
		if(flag==false)
		{
			//id ride not exist then owner can't cancel
			return new ResponseEntity<>(rideId+" not Exist So can't Cancel",HttpStatus.NOT_FOUND);
		}
		else
		{
			//if ride exist  then owner can cancel
			boolean res=ownerService.deleteRide(rideId,id);
			if(res==true)
				return new ResponseEntity<>("Ride Cancelled Successfully",HttpStatus.OK);
			else
				return new ResponseEntity<>("You can't cancel this Ride  Because it is not created By You!!",HttpStatus.BAD_REQUEST);		
		}	
	}
	
	@PostMapping("/confirmBooking/{rideId}/{userId}")
	public ResponseEntity<?> confirmBooking(@PathVariable int rideId,int userId)
	{
		//to confirm booking of user based on userId and rideId
		return new ResponseEntity<>(ownerService.confirmBooking(id,rideId,userId),HttpStatus.OK);
		
	}
	
	@GetMapping("/getNotificationBooking")
	public ResponseEntity<?> getNotificationBooking()
	{
		//to get the notification once it is booked
		List<Ride> ridesList = ownerService.sendNotification(id);
		if(ridesList.isEmpty())
		{
			//if there no vehicles booked
			return new ResponseEntity<>("No vehicle Is Booked!!",HttpStatus.NOT_FOUND);
		}
		else
		{
			List<String> result=new ArrayList<>();
			for(Ride r:ridesList)
			{
				
				result.add("Ride is Booked for vehicleId "+r.getVehicleId() + "  and  for rideId  "+r.getRideId());
			}
			//it will display  vehicles list which are booked
			return new ResponseEntity<>(result,HttpStatus.OK);
		}
	}
	
	@GetMapping("/getPendingQueries/{userId}")
	public ResponseEntity<?> getPendingQueries(@PathVariable int userId)
	{
		//to get pending queries based on userid
		QueryRequest queryRequest = ownerService.getPendingQueries(id,userId);
		if(queryRequest==null)
		{
			//if there are no queries pending
			return new ResponseEntity<>("There are no pending queries from userId: "+userId +" for OwnerId: "+id,HttpStatus.NOT_FOUND);

		}
		else
		{
			//if their is any query pending it will display
			return new ResponseEntity<>(queryRequest,HttpStatus.OK);
		}
	}
	
	@PostMapping("/replyToQuery")
	public ResponseEntity<?> replyToQuery(@RequestBody QueryResponse queryResponse)
	{
		//to reply the query asked by user
		boolean flag=ownerService.replyToQuery(queryResponse);
		if(flag==true)
		{
			return new ResponseEntity<>("Replied Successfully",HttpStatus.OK) ;
		}
		else
		{
			return new ResponseEntity<>("You entered incorrect query details",HttpStatus.BAD_REQUEST) ;
		}
	}

	
	
}