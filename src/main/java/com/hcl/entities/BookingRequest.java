package com.hcl.entities;

public class BookingRequest {
	
	private String bookingDate;
	private String bookingTime;
	private String pickupLocation;
	private String dropLocation;
	public BookingRequest(String bookingDate, String bookingTime, String pickupLocation, String dropLocation) {
		super();
		this.bookingDate = bookingDate;
		this.bookingTime = bookingTime;
		this.pickupLocation = pickupLocation;
		this.dropLocation = dropLocation;
	}
	public String getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}
	public String getBookingTime() {
		return bookingTime;
	}
	public void setBookingTime(String bookingTime) {
		this.bookingTime = bookingTime;
	}
	public String getPickupLocation() {
		return pickupLocation;
	}
	public void setPickupLocation(String pickupLocation) {
		this.pickupLocation = pickupLocation;
	}
	public String getDropLocation() {
		return dropLocation;
	}
	public void setDropLocation(String dropLocation) {
		this.dropLocation = dropLocation;
	}
	@Override
	public String toString() {
		return "BookingRequest [bookingDate=" + bookingDate + ", bookingTime=" + bookingTime + ", pickupLocation="
				+ pickupLocation + ", dropLocation=" + dropLocation + "]";
	}
	
	
	
	

}
