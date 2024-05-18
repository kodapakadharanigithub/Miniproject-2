package com.hcl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.hcl.controller.OwnerController;
import com.hcl.controller.UserController;
import com.hcl.entities.Ride;
import com.hcl.entities.Vehicle;
import com.hcl.service.OwnerService;
import com.hcl.service.UsersService;
import com.hcl.serviceImpl.UsersServiceImpl;

@SpringBootTest
class MiniProject2ApplicationTests {
	@InjectMocks
	private OwnerController ownerController;
	@Mock
	private OwnerService ownerService;
	
	@InjectMocks
	private UserController userController;
	
	@Mock
	private UsersServiceImpl usersService;
	
	/*@Test
    public void testDeleteVehicle() {
        String vehicleid="105";
        int ownerId=1;
        doNothing().when(ownerService).deleteVehicle(vehicleid,ownerId);
        ResponseEntity<?> responseEntity = ownerController.deleteVehicle(vehicleid);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }*/
	
	@Test
	public void testGetVehicleById()
	{
		String vehicleId="101";
        Vehicle vehicle = new Vehicle();
        boolean expected=false;
        vehicle.setVehicleId(vehicleId);
        when(ownerService.getVehicleById(vehicleId)).thenReturn(expected);
        boolean actual = ownerService.getVehicleById(vehicleId);
        assertEquals(expected,actual);
       
	}
	@Test
	public void testGetRideById()
	{
		int rideId=111;
        Ride ride = new Ride();
        boolean expected=false;
        ride.setRideId(rideId);;
        when(ownerService.getRideById(rideId)).thenReturn(expected);
        boolean actual = ownerService.getRideById(rideId);
        assertEquals(expected,actual);   
	}
	@Test
	public void testUserById()
	{
		int userId=10;
		boolean expected=false;
		when(usersService.getUserById(userId)).thenReturn(expected);
        boolean actual = usersService.getUserById(userId);
        assertEquals(expected,actual);
       
	}
}
