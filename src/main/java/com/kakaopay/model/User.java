package com.kakaopay.model;

import javax.persistence.*;
import lombok.*;

@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tuser")
public class User {
	@Id
	@Column(name="user_id", nullable = false)
	private String userId;
	
	@Column(name="room_id",  nullable = false)
	private String roomId;
}
