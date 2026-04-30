package com.Auction.Common.Exceptions;

public class AuctionClosedException extends RuntimeException {
  public AuctionClosedException(String message) {
    super(message);
  }
}
