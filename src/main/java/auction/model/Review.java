package auction.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Review {
    private String reviewId;
    private String auctionId;
    private String reviewerId;
    private String revieweeId;
    private int rating; // 1-5
    private String comment;
    private LocalDateTime timestamp;
    private String reviewType; // SELLER_REVIEW or BIDDER_REVIEW

    public Review(String reviewId, String auctionId, String reviewerId, String revieweeId,
                  int rating, String comment, String reviewType) {
        this.reviewId = reviewId;
        this.auctionId = auctionId;
        this.reviewerId = reviewerId;
        this.revieweeId = revieweeId;
        this.rating = rating;
        this.comment = comment;
        this.reviewType = reviewType;
        this.timestamp = LocalDateTime.now();
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getRevieweeId() {
        return revieweeId;
    }

    public void setRevieweeId(String revieweeId) {
        this.revieweeId = revieweeId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        if (!reviewType.matches("SELLER_REVIEW|BIDDER_REVIEW")) {
            throw new IllegalArgumentException("Invalid review type");
        }
        this.reviewType = reviewType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(reviewId, review.reviewId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reviewId);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", reviewerId='" + reviewerId + '\'' +
                ", revieweeId='" + revieweeId + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", timestamp=" + timestamp +
                ", reviewType='" + reviewType + '\'' +
                '}';
    }
}

