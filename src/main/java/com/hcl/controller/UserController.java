package com.hcl.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.entities.BookingRequest;
import com.hcl.entities.LoginRequest;
import com.hcl.entities.QueryRequest;
import com.hcl.entities.QueryResponse;
import com.hcl.entities.Ride;
import com.hcl.entities.UserBookings;
import com.hcl.entities.Users;
import com.hcl.entities.Vehicle;
import com.hcl.service.UsersService;
import com.hcl.util.JwtUtil;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UsersService usersService;
	
	@Autowired
	private JwtUtil jwtUtil;
	int id=0;
	
	@PostMapping("/login")
	public String userLogin(@RequestBody LoginRequest loginRequest)
	{
		List<Users> list = usersService.getAllUsers();
		boolean flag=false;
		if(list==null)
		{
			return "OOPS!!There are no users";
		}
		else
		{
			for(Users u:list)
			{
				if(loginRequest.getUserName().equals(u.getUserName()) && loginRequest.getPassWord().equals(u.getPassWord()))
				{	
					flag=true;
					id=u.getUserId();
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
			String token=jwtUtil.generateToken(loginRequest.getUserName());
			
			return token;
		}
		else
		{
			return "Bad/Invalid Credentials Check And Try To Login Again";
		}	
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> userRegister(@RequestBody Users user) throws Exception
	{
		boolean flag=usersService.getUserById(user.getUserId());
		if(flag==false)
		{
			usersService.userRegister(user);
			return new ResponseEntity<>("Registered Successfully",HttpStatus.CREATED);
		}
		else
		{
			return new ResponseEntity<>("Already Registered",HttpStatus.BAD_REQUEST);
		}	
	} 
	
	@GetMapping("/getAvailableVehicles/{type}")
	public ResponseEntity<?> getAvailableVehicles(@PathVariable String type)
	{
		if(type.equalsIgnoreCase("2-wheeler") || type.equalsIgnoreCase("4-wheeler"))
		{
			List<Vehicle> vehiclesList = usersService.getAvailableVehicles(type);
			List<String> resultList=new ArrayList<>();
			for(Vehicle v:vehiclesList)
			{
				resultList.add("VehicleId:"+v.getVehicleId()+" Model:"+v.getModel()+" Type:"+v.getType());
			}
			
			if(!vehiclesList.isEmpty())
			{
				return new ResponseEntity<>(resultList,HttpStatus.OK);		
	
			}
			else
			{
				return new ResponseEntity<>("No Available Vehicles",HttpStatus.NOT_FOUND);		
			}
		}
		else
		{
			return new ResponseEntity<>("You should enter either 2-wheeler or 4-wheeler",HttpStatus.BAD_REQUEST);		
		}
	}
	
	@GetMapping("/getRidesSortBasedOnPrice")
	public ResponseEntity<?> getRidesSortBasedOnPrice()
	{
		List<Ride> ridesList=usersService.getRidesSortByPrice();
		if(ridesList==null)
		{
			return new ResponseEntity<>("No Rides Available ",HttpStatus.NOT_FOUND);		
		}
		else
		{
			return new ResponseEntity<>(ridesList,HttpStatus.OK);		
		}
	}
	
	@PostMapping("/bookVehicle")
	public ResponseEntity<?> bookVehicle(@RequestBody BookingRequest bookingRequest)
	{
		UserBookings userBookings=new UserBookings();
		userBookings.setUserId(id);
		String res=usersService.checkDateTime(bookingRequest);
		if(res==null)
		{
			boolean flag=usersService.bookVehicle(bookingRequest,id);
			if(flag==true)
			{
				return new ResponseEntity<>("Vehicle Booking is Pending!!",HttpStatus.OK);
			}
			return new ResponseEntity<>("Vehicle Not Found!!",HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<>(res,HttpStatus.BAD_REQUEST);

		}
	}
	
	@DeleteMapping("/cancelVehicle/{booking_id}")
	public ResponseEntity<?> cancelVehicle(@PathVariable int booking_id)
	{
		boolean flag=usersService.cancelVehicle(booking_id,id);
		if(flag==false)
		{
			return new ResponseEntity<>("Booking Not Found for"+booking_id,HttpStatus.NOT_FOUND);
		}
		else
		{
			return new ResponseEntity<>("Booking Cancelled Successfully",HttpStatus.OK);
		}
	}
	
	@GetMapping("/getAllMyBookings")
	public ResponseEntity<?> getAllMyBookings()
	{
		List<UserBookings> list=usersService.getAllMyBookings(id);
		if(!list.isEmpty())
		{
			return new ResponseEntity<>(list,HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>("No Bookings Found",HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/getConfirmedBookings")
	public ResponseEntity<?> getConfirmedBookings()
	{
		UserBookings userBookings = usersService.getConfirmedBookings(id);
		if(userBookings!=null)
		{
			return new ResponseEntity<>(userBookings,HttpStatus.OK);

		}
		else
		{
			return new ResponseEntity<>("Not Yet Confirmed",HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/askQuery/{vehicleId}/{query}")
	public ResponseEntity<?> askQuery(@PathVariable String vehicleId,String query)
	{
		return new ResponseEntity<>(usersService.postQuery(id, vehicleId, query),HttpStatus.OK);
	}
	
	@GetMapping("/getRepliedAnswers")
	public ResponseEntity<?> getRepliedAnswers()
	{
		QueryResponse queryResponse = usersService.getRepliedAnswers(id);
		if(queryResponse==null)
		{
			return new ResponseEntity<>("Not Yet Replied",HttpStatus.NOT_FOUND);
		}
		else
		{
			return  new ResponseEntity<>(queryResponse,HttpStatus.OK);
		}
	}
	
	
}
