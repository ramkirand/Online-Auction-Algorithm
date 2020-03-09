package com.online.auction.system;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.online.auction.model.Bid;
import com.online.auction.model.Item;
import com.online.auction.model.User;

import exception.InvalidBidException;

public interface BidTracker {

	void recordUserBidOnItem(Bid bid, Item item) throws InvalidBidException;

	Optional<Bid> currentWinningBidForItem(Item item);

	List<Bid> getAllBidsForItem(Item item);

	Set<Item> getAllItemsWithBidFromuser(User user);
}
