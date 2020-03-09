package com.online.auction.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.online.auction.model.Bid;
import com.online.auction.model.Item;
import com.online.auction.model.User;

import exception.InvalidBidException;

public class BidTrackerImplTest {
	private BidTrackerImpl bidTracker;
	private final User user1 = new User("u1", "Nicolas Bentayou");
	private final User user2 = new User("u2", "Randolph Carter");
	private final User user3 = new User("u3", "Herbert West");
	private final Item item1 = new Item("i1", "item1", "Brilliant!");
	private final Item item2 = new Item("i2", "item2", "Brilliant!");
	private final Item item3 = new Item("i3", "item3", "Brilliant!");
	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Before
	public void initAuctionRoom() {
		bidTracker = new BidTrackerImpl();
	}

	@Test
	public void recordUserBidOnItem_shouldAddAFirstBidToItem_whenBidIsValid() throws InvalidBidException {
		Bid bid = new Bid(user1, 10);

		bidTracker.recordUserBidOnItem(bid, item1);

		List<Bid> actualBidListOnItem1 = bidTracker.getCurrentAuctionBoardCopy().get(item1);
		List<Bid> expectedBidListOnItem1 = Collections.singletonList(bid);
		assertEquals(expectedBidListOnItem1, actualBidListOnItem1);
	}

	@Test
	public void recordUserBidOnItem_shouldAddSeveralBidsToItem_whenBidsAreValid() throws InvalidBidException {
		bidTracker.recordUserBidOnItem(new Bid(user1, 10), item1);
		bidTracker.recordUserBidOnItem(new Bid(user2, 20), item1);
		bidTracker.recordUserBidOnItem(new Bid(user1, 30), item1);

		List<Bid> actualBidsListOnItem1 = bidTracker.getCurrentAuctionBoardCopy().get(item1);
		List<Bid> expectedBidsListOnItem1 = Arrays.asList(new Bid(user1, 10), new Bid(user2, 20), new Bid(user1, 30));

		for (int i = 0; i < actualBidsListOnItem1.size(); i++) {
			assertEquals(expectedBidsListOnItem1.get(i).getAutoIncrementAmt(),
					actualBidsListOnItem1.get(i).getAutoIncrementAmt());
			assertEquals(expectedBidsListOnItem1.get(i).getBiddr(), actualBidsListOnItem1.get(i).getBiddr());
		}

	}

	@Test
	public void recordUserBidOnItem_shouldThrowIllegalArgumentException_whenItemIsNull() throws InvalidBidException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Item is null");

		bidTracker.recordUserBidOnItem(new Bid(user1, 10), null);
	}

	@Test
	public void recordUserBidOnItem_shouldThrowIllegalArgumentException_whenBidIsNull() throws InvalidBidException {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Bid can't be null");

		bidTracker.recordUserBidOnItem(null, item1);
	}

	@Test
	public void recordUserBidOnItem_shouldThrowIllegalArgumentException_whenUserIsNull() throws InvalidBidException {
		bidTracker.recordUserBidOnItem(new Bid(null, 10), item1);
	}

	// @Test
	// public void
	// recordUserBidOnItem_shouldThrowInvalidBidException_whenBidIsLowerThanCurrentlyWinningBid()
	// throws InvalidBidException {
	// thrown.expect(InvalidBidException.class);
	// thrown.expectMessage(
	// "A bid of £5 on item { id: i1, name: item1, description: Brilliant! } is too
	// low. It should be more than the current winning bid: £10)");
	//
	// bidTracker.recordUserBidOnItem(new Bid(user1, 10), item1);
	//
	// Bid lowBid = new Bid(user2, 5);
	// bidTracker.recordUserBidOnItem(lowBid, item1);
	// }

	@Test
	public void recordUserBidOnItem_shouldThrowInvalidBidException_whenBidIsSameAsCurrentlyWinningBid()
			throws InvalidBidException {
		thrown.expect(InvalidBidException.class);
		bidTracker.recordUserBidOnItem(new Bid(user1, 10), item1);

		Bid sameBid = new Bid(user2, 10);
		bidTracker.recordUserBidOnItem(sameBid, item1);
	}

	@Test
	public void recordUserBidOnItem_shouldAddSeveralBidsToItem_whenSomeBidsAreInvalid() throws InvalidBidException {
		bidTracker.recordUserBidOnItem(new Bid(user1, 10), item1);
		bidTracker.recordUserBidOnItem(new Bid(user2, 20), item1);
		try { // invalid bid
			bidTracker.recordUserBidOnItem(new Bid(user3, 15), item1);
		} catch (InvalidBidException e) {
			/* Silencing the exception as it is irrelevant for this test */ }
		bidTracker.recordUserBidOnItem(new Bid(user1, 30), item1);

		List<Bid> bidsListOnItem1 = bidTracker.getCurrentAuctionBoardCopy().get(item1);

		List<Bid> expectedBidsOnItem1 = Arrays.asList(new Bid(user1, 10), new Bid(user2, 20), new Bid(user1, 30));

		for (int i = 0; i < expectedBidsOnItem1.size(); i++) {
			assertEquals(expectedBidsOnItem1.get(i).getAutoIncrementAmt(),
					bidsListOnItem1.get(i).getAutoIncrementAmt());
			assertEquals(expectedBidsOnItem1.get(i).getBiddr(), bidsListOnItem1.get(i).getBiddr());
		}
	}

	// @Test
	// public void
	// recordUserBidOnItem_shouldOnlyRecordValidBids_inAMultithreadedEnvironment() {
	// AtomicInteger invalidBidsCount = new AtomicInteger(0);
	//
	// // Make 10000 bids on 4 different threads.
	// int totalNbBids = 10000;
	// ExecutorService executor = Executors.newFixedThreadPool(4);
	// IntStream.range(0, totalNbBids).forEach(i -> executor.submit(() -> {
	// try {
	// bidTracker.recordUserBidOnItem(new Bid(user1, i), item1);
	// } catch (InvalidBidException e) {
	// invalidBidsCount.incrementAndGet();
	// }
	// }));
	// shutDownExecutor(executor);
	//
	// List<Bid> actualBidsMade =
	// bidTracker.getCurrentAuctionBoardCopy().get(item1);
	//
	// // asserting that all bids were processed
	// assertEquals(totalNbBids, actualBidsMade.size() + invalidBidsCount.get());
	// // asserting that the accepted bids for the item are all ordered by
	// increasing
	// // value
	// assertEquals(actualBidsMade, sortBidListByValue(actualBidsMade));
	// // asserting that the last bid is for 9999
	// Bid lastBidMade = actualBidsMade.get(actualBidsMade.size() - 1);
	// assertEquals(totalNbBids - 1, lastBidMade.getAutoIncrementAmt());
	// }

	private void shutDownExecutor(ExecutorService executor) {
		try {
			executor.awaitTermination(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			executor.shutdownNow();
		}
	}

	private List<Bid> sortBidListByValue(List<Bid> bidList) {
		return bidList.stream().sorted(Comparator.comparing(Bid::getAutoIncrementAmt)).collect(Collectors.toList());
	}

	// @Test
	// public void
	// getCurrentWinningBidForItem_shouldThrowIllegalArgumentException_whenItemIsNull()
	// {
	// thrown.expect(IllegalArgumentException.class);
	// thrown.expectMessage("Item can't be null");
	//
	// bidTracker.getAllItemsWithBidFromuser(null);
	// }

	// @Test
	// public void
	// getCurrentWinningBidForItem_shouldReturnEmptyOptional_whenItemHasNoBid() {
	// Optional<Bid> bid = bidTracker.getAllItemsWithBidFromuser(item1);
	// assertEquals(Optional.empty(), bid);
	// }

	@Test
	public void getCurrentWinningBidForItem_shouldReturnOptionalWithAValue_whenItemHasBids()
			throws InvalidBidException {
		bidTracker.recordUserBidOnItem(new Bid(user1, 10), item1);
		bidTracker.recordUserBidOnItem(new Bid(user2, 20), item1);

		Optional<Bid> bid = bidTracker.currentWinningBidForItem(item1);

		assertTrue(bid.isPresent());
		// assertEquals(bid.get(), new Bid(user2, 20));
	}

	@Test
	public void getAllBidsForItem_shouldThrowIllegalArgumentException_whenItemIsNull() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("Item is null");

		bidTracker.getAllBidsForItem(null);
	}

	@Test
	public void getAllBidsForItem_shouldReturnEmptyList_whenItemHasNoBid() {
		List<Bid> bids = bidTracker.getAllBidsForItem(item1);
		assertTrue(bids.isEmpty());
	}

	@Test
	public void getAllBidsForItem_shouldReturnTheCorrectListOfBids_whenItemHasBids() throws InvalidBidException {
		bidTracker.recordUserBidOnItem(new Bid(user1, 10), item1);
		bidTracker.recordUserBidOnItem(new Bid(user2, 20), item1);

		List<Bid> actualBids = bidTracker.getAllBidsForItem(item1);
		List<Bid> expectedBids = Arrays.asList(new Bid(user1, 10), new Bid(user2, 20));
		for (int i = 0; i < actualBids.size(); i++) {
			assertEquals(expectedBids.get(i).getAutoIncrementAmt(), actualBids.get(i).getAutoIncrementAmt());
			assertEquals(expectedBids.get(i).getBiddr(), actualBids.get(i).getBiddr());
			assertEquals(expectedBids.get(i).getMaxBid(), actualBids.get(i).getMaxBid());
		}

	}

	@Test
	public void getAllItemsWithBidFromUser_shouldReturnEmptySet_whenUserIsNull() {
		Set<Item> items = bidTracker.getAllItemsWithBidFromuser(null);
		assertTrue(items.isEmpty());
	}

	@Test
	public void getAllItemsWithBidFromUser_shouldReturnEmptySet_whenUserHasNoBid() {
		Set<Item> items = bidTracker.getAllItemsWithBidFromuser(user1);
		assertTrue(items.isEmpty());
	}

	@Test
	public void getAllItemsWithBidFromUser_shouldReturnCorrectItemSet_whenUserHasBids() throws InvalidBidException {
		bidTracker.recordUserBidOnItem(new Bid(user1, 10), item1); // bid on item1
		bidTracker.recordUserBidOnItem(new Bid(user2, 20), item1);
		bidTracker.recordUserBidOnItem(new Bid(user1, 30), item1); // second bid on item1
		bidTracker.recordUserBidOnItem(new Bid(user2, 10), item2);
		bidTracker.recordUserBidOnItem(new Bid(user3, 20), item2);
		bidTracker.recordUserBidOnItem(new Bid(user3, 10), item3);
		bidTracker.recordUserBidOnItem(new Bid(user1, 20), item3); // bid on item3

		Set<Item> itemList = bidTracker.getAllItemsWithBidFromuser(user1);

		Set<Item> expectedItemList = new HashSet<>(Arrays.asList(item1, item3));
		assertEquals(expectedItemList, itemList);
	}

}
