package com.desicoders.hardcode;

import com.google.appengine.api.datastore.Entity;

public class UserUtils {

	public static boolean addRating(Entity user, double rating) {
		double value = 0;
		if(user.hasProperty("Rating"))
			value = Double.parseDouble(user.getProperty("Rating").toString());
		
		long numberOfVoters = (long) 0;
		if(user.hasProperty("VotersNumber"))
			numberOfVoters = (Long)user.getProperty("VotersNumber");
		
		
		value = (value*numberOfVoters+rating)/(numberOfVoters+1);
		user.setProperty("Rating", value);
		user.setProperty("VotersNumber",numberOfVoters+1);
		Utils.put(user);
		return true;
		
	}

}
