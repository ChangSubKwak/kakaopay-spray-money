package com.kakaopay.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ttoken")
public class Token {
	@Id
	@Column(name="token", nullable = false)
	private String token;
	
	@Column(name="amt",  nullable = false)
	private long amt;
	//private Long amt;
	
	@Column(name="person_num",  nullable = false)
	private int personNum;
	//private Integer personNum;
	
	@Column(name="reg_dt",  nullable = false)
	private String regDt;
	
	@Column(name="reg_id",  nullable = false)
	private String regId;
	
	@Column(name="reg_room_id",  nullable = false)
	private String regRoomId;
}
