package com.blue.getout.event;

import com.blue.getout.user.UserDTO;

import java.time.ZonedDateTime;
import java.util.Set;

public record EventDTO (String id,String title,String location,LatLng latLng,ZonedDateTime time,ZonedDateTime endTime,
                        Set<UserDTO> participants, int min, int max, String info,String recurring, String ownerId) {}