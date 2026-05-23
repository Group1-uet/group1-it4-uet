package com.Auction.Common.Exceptions;

public class InvalidBidException extends Exception {
    public InvalidBidException(String message) {
        super(message);
    }

    public InvalidBidException(String message, Throwable cause) {
        super(message, cause);
    }
}