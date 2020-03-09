package com.online.auction.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.online.auction.model.Bid;
import com.online.auction.model.Item;
import com.online.auction.model.User;

import exception.InvalidBidException;

public class BidTrackerImpl implements BidTracker {

	private final Map<Item, List<Bid>> auctionBoard;

	public BidTrackerImpl() {
		auctionBoard = new ConcurrentHashMap<Item, List<Bid>>();
	}

	public Map<Item, List<Bid>> getCurrentAuctionBoardCopy() {
		return new HashMap<>(auctionBoard);
	}

	

	public void recordUserBidOnItem(Bid bid, Item item) throws InvalidBidException {
		checkForNullBid(bid);
		checkForNullItem(item);
		recordUserBidOnItemSync(bid, item);

	}

	@Override
	public Optional<Bid> currentWinningBidForItem(Item item) {
		LinkedList<Bid> bids = new LinkedList<>(getAllBidsForItem(item));
		return bids.isEmpty() ? Optional.empty() : Optional.of(bids.getLast());
	}

	@Override
	public List<Bid> getAllBidsForItem(Item item) {
		checkForNullItem(item);
		return auctionBoard.getOrDefault(item, new ArrayList<>());
	}

	@Override
	public Set<Item> getAllItemsWithBidFromuser(User user) {
		return auctionBoard.entrySet().stream().filter(entry -> containsBidFromUser(entry.getValue(), user))
				.map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	public Optional<Bid> currentWinningBidForItem1(Item item) {
		LinkedList<Bid> bids = new LinkedList<Bid>(getAllBidsForItem1(item));
		return (Optional<Bid>) (bids.isEmpty() ? Optional.empty() : Optional.of(bids.getLast()));
	}

	public List<Bid> getAllBidsForItem1(Item item) {
		checkForNullItem(item);
		return auctionBoard.getOrDefault(item, new ArrayList<Bid>());
	}

	private void checkForNullItem(Item item) {
		if (item == null)
			throw new IllegalArgumentException("Item is null");

	}

	public Set<Item> getAllItemsWithBidFromuser1(User user) {
		return auctionBoard.entrySet().stream().filter(entry -> containsBidFromUser(entry.getValue(), user))
				.map(Map.Entry::getKey).collect(Collectors.toSet());
	}

	private boolean containsBidFromUser(List<Bid> bidsList, User user) {
		return bidsList.stream().anyMatch(bid -> bid.isFromUser(user));
	}

	public void recordUserBidOnItem1(Bid bid, Item item) throws InvalidBidException {
		checkForNullBid(bid);
		checkForNullItem(item);
		recordUserBidOnItemSync(bid, item);

	}

	private void recordUserBidOnItemSync(Bid bid, Item item) throws InvalidBidException {
		checkBidIsHighEnough(bid, item);
		addBidOnItem(item, bid);

	}

	private void checkBidIsHighEnough(Bid bid, Item item) throws InvalidBidException {
		Optional<Bid> currentWinningBid = currentWinningBidForItem1(item);
		if (currentWinningBid.isPresent()
				&& bid.getAutoIncrementAmt() <= currentWinningBid.get().getAutoIncrementAmt()) {
			throw new InvalidBidException(String.format(
					"A bid of £%s on item %s is too low. It should be more than the current winning bid: £%s)",
					bid.getAutoIncrementAmt(), item, currentWinningBid.get().getAutoIncrementAmt()));
		}

	}

	private void addBidOnItem(Item item, Bid bid) {
		List<Bid> bidsOnItem = auctionBoard.getOrDefault(item, new ArrayList<>());
		bidsOnItem.add(bid);
		auctionBoard.put(item, bidsOnItem);

	}

	private void checkForNullBid(Bid bid) {
		if (bid == null)
			throw new IllegalArgumentException("Bid can't be null");
	}

}
