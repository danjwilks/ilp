package uk.ac.ed.inf.aqmaps;

public class SensorReading {
	
	private final double longitude;
	private final double latitude;
	private final String location;
	private final String rgbString;
	private final String markerColor;
	private final String markerSymbol;
	
	private SensorReading(SensorReadingBuilder builder) {
		
		this.longitude = builder.longitude;
		this.latitude = builder.latitude;
        this.location = builder.location;
        this.rgbString = builder.rgbString;
        this.markerColor = builder.markerColor;
        this.markerSymbol = builder.markerSymbol;
    }
	
	public double getLongitude() {
		return longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public String getLocation() {
		return location;
	}
	public String getRgbString() {
		return rgbString;
	}
	public String getMarkerColor() {
		return markerColor;
	}
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	
	public static class SensorReadingBuilder {
		
		private double longitude;
		private double latitude;
		private String location;
        private String rgbString;
        private String markerColor;
        private String markerSymbol;
 
        public SensorReadingBuilder() {
        	
        }
        public SensorReadingBuilder setLongitude(double longitude) {
        	this.longitude = longitude;
        	return this;
        }
        public SensorReadingBuilder setLatitude(double latitude) {
        	this.latitude = latitude;
        	return this;
        } 
        public SensorReadingBuilder setLocation(String location) {
    		this.location = location;
    		return this;
    	}
        public SensorReadingBuilder setRgbString(String rgbString) {
			this.rgbString = rgbString;
    		return this;
		}
    	public SensorReadingBuilder setMarkerColor(String markerColor) {
    		this.markerColor = markerColor;
    		return this;
    	}
    	
    	public SensorReadingBuilder setMarkerSymbol(String markerSymbol) {
    		this.markerSymbol = markerSymbol;
    		return this;
    	}
    	
    	public SensorReading build() {
    		SensorReading sensorReading = new SensorReading(this);
    		return sensorReading;
    	}
		
	}

}

//public User build() {
//    User user =  new User(this);
//    validateUserObject(user);
//    return user;
//}


 
//    public static class UserBuilder 
//    {
//        private final String firstName;
//        private final String lastName;
//        private int age;
//        private String phone;
//        private String address;
// 
//        public UserBuilder(String firstName, String lastName) {
//            this.firstName = firstName;
//            this.lastName = lastName;
//        }
//        public UserBuilder age(int age) {
//            this.age = age;
//            return this;
//        }
//        public UserBuilder phone(String phone) {
//            this.phone = phone;
//            return this;
//        }
//        public UserBuilder address(String address) {
//            this.address = address;
//            return this;
//        }
//        //Return the finally consrcuted User object
//        public User build() {
//            User user =  new User(this);
//            validateUserObject(user);
//            return user;
//        }
//        private void validateUserObject(User user) {
//            //Do some basic validations to check 
//            //if user object does not break any assumption of system
//        }
//    }
//}

