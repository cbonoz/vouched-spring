package com.vouched.service;

import com.vouched.error.SoftException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

  private final static int MAX_INVITES_PER_DAY = 50;
  private final Bucket requestBucket;

  public RateLimiterService() {
    Bandwidth limit = Bandwidth.builder().capacity(MAX_INVITES_PER_DAY)
        .refillIntervally(MAX_INVITES_PER_DAY, Duration.ofDays(1))
        .build();
    this.requestBucket = Bucket.builder().addLimit(limit).build();
  }

  public boolean recordUserRequest() {
    boolean result = requestBucket.tryConsume(1);
    if (!result) {
      throw new SoftException("User requests exceeded for today, come back again later");
    }
    return true;
  }
}
