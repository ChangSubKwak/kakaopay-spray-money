package com.kakaopay.model;

import javax.persistence.*;
import lombok.*;

@Builder
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tmoney")
public class Money {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="seq", nullable = false)
	private Integer seq;
	
	@Column(name="token",  nullable = false)
	private String token;
	
	@Column(name="part_amt",  nullable = false)
	private long partAmt;
	
	@Setter
	@Column(name="rcv_id",  nullable = true)
	private String rcvId;
}
