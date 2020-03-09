package com.online.auction.model;

public class Bid {
	private User bidder;
	private int startingBid;
	private int maxBid;
	private int amount;

	public Bid(User user, int value) {
		this.bidder = user;
		this.amount = value;
	}

	public User getBiddr() {
		return bidder;
	}

	public boolean isFromUser(User user) {
		return this.bidder.equals(user);
	}

	public void setBiddr(User bidder) {
		this.bidder = bidder;
	}

	public int getStartingBid() {
		return startingBid;
	}

	public void setStartingBid(int startingBid) {
		this.startingBid = startingBid;
	}

	public int getMaxBid() {
		return maxBid;
	}

	public void setMaxBid(int maxBid) {
		this.maxBid = maxBid;
	}

	public int getAutoIncrementAmt() {
		return amount;
	}

	public void setAutoIncrementAmt(int autoIncrementAmt) {
		this.amount = autoIncrementAmt;
	}

}
