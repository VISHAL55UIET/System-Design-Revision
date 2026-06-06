import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimiterDemo {

    // ==========================
    // CONFIG
    // ==========================
    static class RateLimitConfig {
        private final int capacity;
        private final int refillRate;

        public RateLimitConfig(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getRefillRate() {
            return refillRate;
        }
    }

    // ==========================
    // RATE LIMITER INTERFACE
    // ==========================
    interface RateLimiter {
        boolean allowRequest(String clientId);
    }

    // ==========================
    // TOKEN BUCKET
    // ==========================
    static class TokenBucket {

        private final int capacity;
        private final int refillRate;

        private double currentTokens;
        private long lastRefillTime;

        private final ReentrantLock lock = new ReentrantLock();

        public TokenBucket(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.currentTokens = capacity;
            this.lastRefillTime = System.currentTimeMillis();
        }

        public boolean consume() {

            lock.lock();

            try {
                refill();

                if (currentTokens >= 1) {
                    currentTokens--;
                    return true;
                }

                return false;

            } finally {
                lock.unlock();
            }
        }

        private void refill() {

            long currentTime = System.currentTimeMillis();

            double tokensToAdd =
                    ((currentTime - lastRefillTime) / 1000.0)
                            * refillRate;

            currentTokens =
                    Math.min(capacity,
                            currentTokens + tokensToAdd);

            lastRefillTime = currentTime;
        }

        public double getCurrentTokens() {
            return currentTokens;
        }
    }

    // ==========================
    // IN MEMORY RATE LIMITER
    // ==========================
    static class InMemoryRateLimiter
            implements RateLimiter {

        private final RateLimitConfig config;

        private final ConcurrentHashMap<String, TokenBucket>
                bucketMap;

        public InMemoryRateLimiter(
                RateLimitConfig config) {

            this.config = config;
            this.bucketMap =
                    new ConcurrentHashMap<>();
        }

        @Override
        public boolean allowRequest(
                String clientId) {

            TokenBucket bucket =
                    bucketMap.computeIfAbsent(
                            clientId,
                            k -> new TokenBucket(
                                    config.getCapacity(),
                                    config.getRefillRate()
                            )
                    );

            return bucket.consume();
        }

        public TokenBucket getBucket(
                String clientId) {

            return bucketMap.get(clientId);
        }
    }

    // ==========================
    // DRIVER
    // ==========================
    public static void main(String[] args)
            throws InterruptedException {

        RateLimitConfig config =
                new RateLimitConfig(
                        5,      // bucket capacity
                        1       // 1 token/sec refill
                );

        InMemoryRateLimiter limiter =
                new InMemoryRateLimiter(config);

        String user = "user123";

        System.out.println(
                "===== FIRST BURST =====");

        for (int i = 1; i <= 10; i++) {

            boolean allowed =
                    limiter.allowRequest(user);

            System.out.println(
                    "Request " + i + " -> "
                            + (allowed
                            ? "ALLOWED"
                            : "BLOCKED")
            );

            Thread.sleep(200);
        }

        System.out.println(
                "\nWaiting 5 seconds...\n");

        Thread.sleep(5000);

        System.out.println(
                "===== AFTER REFILL =====");

        for (int i = 11; i <= 20; i++) {

            boolean allowed =
                    limiter.allowRequest(user);

            System.out.println(
                    "Request " + i + " -> "
                            + (allowed
                            ? "ALLOWED"
                            : "BLOCKED")
            );

            Thread.sleep(200);
        }
    }
}