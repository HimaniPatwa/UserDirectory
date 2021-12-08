package com.CISCOproject.model;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "phone")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Phone {

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "phoneid")
	@Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "phoneid", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")   
	private UUID phoneId;
	@Column(name = "phonename")
	private String phoneName;
	@Column(name = "phonemodel")
	private String phoneModel;
	@Column(name = "phonenumber")
	private String phoneNumber;
	@SuppressWarnings("deprecation")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "preferredphonenumber")
	private boolean isPreferredPhoneNumber;
	// Foreign key constraint from User table
	@ManyToOne
	@JoinColumn(name = "userid", nullable = false)
	private User user;

}
