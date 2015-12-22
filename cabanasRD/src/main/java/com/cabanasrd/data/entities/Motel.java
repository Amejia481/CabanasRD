package com.cabanasrd.data.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import com.cabanasrd.config.CabannasrdApp;


public class Motel  implements  Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private Long id = null;
    private String name = null;
    private double latitude = 0;
    private double longitude = 0;
	private boolean isManagedByTheOwner = false;
    private ArrayList<MotelService> motelServices;
    private ArrayList<String> phones = null;
    private boolean takeCredictCards = false;
    private int ranking  = 0;
    private ArrayList<String>  images ;
    private ArrayList<Image>  images2 ;
    private State state = null;
    private Rating rating = null;
    private int type =0;
    private ArrayList<CreditCard> creditCards = null;
    
    public ArrayList<Image> getImages2() {
		return images2;
	}


	public void setImages2(ArrayList<Image> images2) {
		this.images2 = images2;
	}


	public ArrayList<CreditCard> getCreditCards() {
		return creditCards;
	}


	public void setCreditCards(ArrayList<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isManagedByTheOwner() {
        return isManagedByTheOwner;
    }

    public void setIsManagedByTheOwner(boolean isManagedByTheOwner) {
        this.isManagedByTheOwner = isManagedByTheOwner;
    }

    public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public Rating getRating() {
		return rating;
	}


	public void setRating(Rating rating) {
		this.rating = rating;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String description = null;
    
    public State getState() {
		return state;
	}


	public void setState(State state) {
		this.state = state;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public ArrayList<String> getPhones() {
		return phones;
	}


	public void setPhones(ArrayList<String> phones) {
		this.phones = phones;
	}


	public boolean isTakeCredictCards() {
		return takeCredictCards;
	}


	public void setTakeCredictCards(boolean takeCredictCards) {
		this.takeCredictCards = takeCredictCards;
	}


	public int getRanking() {
		return ranking;
	}


	public void setRanking(int ranking) {
		this.ranking = ranking;
	}


	public ArrayList<String> getImages() {
		return  images;
	}


	public void setImages(ArrayList<String> images) {
			this.images = images;
	}


	public ArrayList<MotelService> getMotelServices() {
		return motelServices;
	}


	public void setMotelServices(ArrayList<MotelService> motelServices) {
		this.motelServices = motelServices;
	}

	
    

    public Motel() {
    	
    	motelServices = new ArrayList<MotelService>();
    	
    }

    
   	public Motel(Long id, String name, double latitude, double longitude,
			ArrayList<String> phones, String description,
			boolean takeCredictCards, int ranking, ArrayList<String> images,
			State state, ArrayList<MotelService> motelServices) {
		super();
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.phones = phones;
		this.description = description;
		this.takeCredictCards = takeCredictCards;
		this.ranking = ranking;
		this.images = images;
		this.state = state;
		this.motelServices = motelServices;

	}


	public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

   
    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();        
        if(motelServices!= null){
	        for(int i =0; i < motelServices.size() ; i++){
	        	MotelService motelService1 = motelServices.get(i);
	        	
	        	if(motelService1 !=null && motelService1.getType() == CabannasrdApp.MOTEL_TYPE_HOUSING ){

		        	sb.append( motelService1.getService());
		        	sb.append(": ");
		        	sb.append(String.format("$%,3.2f",motelService1.getPrice()));
		        	sb.append( (( (i+1) != motelServices.size())  ? " \n" : ""));
	        	}
	        }
        }
    	return  sb.toString();
    }
    @Override
    public boolean equals(Object o) {
    	
    	return (o!=null &&  ((Motel) o).getId().equals(id)  ) ? true: false;
    }
    
}

	
	 