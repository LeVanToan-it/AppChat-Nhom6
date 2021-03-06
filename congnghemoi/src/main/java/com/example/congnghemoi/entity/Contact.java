package com.example.congnghemoi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Contact extends BaseEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4806674609804322567L;

	
	
	@ManyToOne(fetch = FetchType.LAZY)
	//@JsonBackReference(value="account-contact-movement")
    private Account account;
	@Column(columnDefinition = "boolean default false")
	private boolean accept;
	private long friendId;

	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	public long getFriendId() {
        return friendId;
    }

    public void setFriendId(long friend) {
        this.friendId = friend;
    }

}
