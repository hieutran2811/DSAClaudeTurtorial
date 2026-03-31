package phase6_advanced.math;

import java.util.*;

/**
 * PHASE 6.3 -- MATH & BIT MANIPULATION
 *
 * Topics covered:
 *  #99  Bit Tricks              -- AND/OR/XOR, set/clear/toggle, count bits
 *  #100 Power Set using Bits    -- Enumerate all subsets via bitmask
 *  #101 Sieve of Eratosthenes   -- All primes up to N in O(n log log n)
 *  #102 GCD / LCM               -- Euclidean algorithm, extended GCD
 *  #103 Modular Arithmetic      -- ModPow, ModInverse, Fermat's little theorem
 */
public class MathAndBits {

    public static void main(String[] args) {
        System.out.println("=== PHASE 6.3: MATH & BIT MANIPULATION ===\n");
        demoBitTricks();
        demoPowerSet();
        demoSieve();
        demoGcdLcm();
        demoModularArithmetic();
    }

    // =========================================================================
    // TOPIC #99: BIT TRICKS
    // =========================================================================
    // Bit operations run in O(1) and are extremely fast.
    //
    // FUNDAMENTALS:
    //   n & 1          -- check if n is odd (last bit)
    //   n >> 1         -- divide by 2 (arithmetic right shift)
    //   n << 1         -- multiply by 2
    //   n & (n-1)      -- clear the lowest set bit  (KEY TRICK)
    //   n & (-n)       -- isolate the lowest set bit (used in Fenwick Tree)
    //   n ^ n  = 0     -- XOR with self = 0
    //   n ^ 0  = n     -- XOR with 0 = n
    //   ~n             -- flip all bits
    //
    // BIT MANIPULATION PATTERNS:
    //   Set bit k:     n | (1 << k)
    //   Clear bit k:   n & ~(1 << k)
    //   Toggle bit k:  n ^ (1 << k)
    //   Check bit k:   (n >> k) & 1
    //
    // LeetCode:
    //   #191 Number of 1 Bits (Hamming Weight)
    //   #190 Reverse Bits
    //   #231 Power of Two
    //   #268 Missing Number
    //   #136 Single Number
    //   #137 Single Number II
    //   #260 Single Number III
    //   #338 Counting Bits
    //   #371 Sum of Two Integers (no + operator)

    // #191 -- Number of 1 bits (Hamming weight)
    // Using n & (n-1) trick: each operation removes one set bit => O(k) where k = popcount
    static int hammingWeight(int n) {
        int count = 0;
        while (n != 0) { n &= (n - 1); count++; }
        return count;
    }

    // #190 -- Reverse 32 bits
    static int reverseBits(int n) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            result = (result << 1) | (n & 1);
            n >>= 1;
        }
        return result;
    }

    // #231 -- Power of Two: n > 0 and exactly one bit set
    static boolean isPowerOfTwo(int n) { return n > 0 && (n & (n - 1)) == 0; }

    // #268 -- Missing Number in [0..n]: XOR all indices with all values
    // KEY: a ^ a = 0, a ^ 0 = a => duplicates cancel out
    static int missingNumber(int[] nums) {
        int xor = nums.length; // start with n
        for (int i = 0; i < nums.length; i++) xor ^= i ^ nums[i];
        return xor;
    }

    // #136 -- Single Number: every element appears twice except one
    static int singleNumber(int[] nums) {
        int xor = 0;
        for (int n : nums) xor ^= n;
        return xor;
    }

    // #137 -- Single Number II: every element appears 3x except one
    // KEY: use two bitmasks (ones, twos) to count bits mod 3
    static int singleNumberII(int[] nums) {
        int ones = 0, twos = 0;
        for (int n : nums) {
            ones = (ones ^ n) & ~twos;
            twos = (twos ^ n) & ~ones;
        }
        return ones;
    }

    // #260 -- Single Number III: two numbers appear once, rest appear twice
    // Step 1: XOR all => xor = a ^ b
    // Step 2: find any set bit in xor (use lowest set bit: diff & -diff)
    // Step 3: partition nums into two groups by that bit, XOR each group
    static int[] singleNumberIII(int[] nums) {
        int xor = 0;
        for (int n : nums) xor ^= n;
        int diff = xor & (-xor); // isolate lowest differing bit
        int a = 0;
        for (int n : nums) if ((n & diff) != 0) a ^= n;
        return new int[]{a, xor ^ a};
    }

    // #338 -- Counting Bits: count[i] = number of 1s in binary of i
    // DP: count[i] = count[i >> 1] + (i & 1)  [i >> 1 is i/2, already computed]
    static int[] countBits(int n) {
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) dp[i] = dp[i >> 1] + (i & 1);
        return dp;
    }

    // #371 -- Sum of Two Integers without + operator
    // KEY: a + b = (a XOR b) + carry, carry = (a AND b) << 1
    //      repeat until no carry
    static int getSum(int a, int b) {
        while (b != 0) {
            int carry = (a & b) << 1;
            a = a ^ b;
            b = carry;
        }
        return a;
    }

    // Utility: print binary representation (32-bit)
    static String toBin(int n) { return String.format("%32s", Integer.toBinaryString(n)).replace(' ', '0'); }

    static void demoBitTricks() {
        System.out.println("--- TOPIC #99: Bit Tricks ---");
        int n = 0b10110100; // 180
        System.out.printf("n = %d  (%s)%n", n, Integer.toBinaryString(n));
        System.out.printf("n & 1       = %d  (odd check)%n",  n & 1);
        System.out.printf("n & (n-1)   = %s  (clear lowest set bit)%n", Integer.toBinaryString(n & (n-1)));
        System.out.printf("n & (-n)    = %s  (isolate lowest set bit)%n", Integer.toBinaryString(n & (-n)));
        System.out.printf("Set bit 1   = %s%n", Integer.toBinaryString(n | (1 << 1)));
        System.out.printf("Clear bit 4 = %s%n", Integer.toBinaryString(n & ~(1 << 4)));
        System.out.printf("Toggle bit 2= %s%n", Integer.toBinaryString(n ^ (1 << 2)));
        System.out.printf("Check bit 5 = %d%n",  (n >> 5) & 1);

        System.out.println("\nLeetCode:");
        System.out.println("  #191 hammingWeight(11)     = " + hammingWeight(11));        // 3
        System.out.println("  #231 isPowerOfTwo(16)      = " + isPowerOfTwo(16));         // true
        System.out.println("  #231 isPowerOfTwo(6)       = " + isPowerOfTwo(6));          // false
        System.out.println("  #268 missingNumber([3,0,1])= " + missingNumber(new int[]{3,0,1})); // 2
        System.out.println("  #136 singleNumber([2,2,1]) = " + singleNumber(new int[]{2,2,1})); // 1
        System.out.println("  #137 singleNumberII([2,2,3,2]) = " + singleNumberII(new int[]{2,2,3,2})); // 3
        System.out.println("  #260 singleNumberIII([1,2,1,3,2,5]) = " + Arrays.toString(singleNumberIII(new int[]{1,2,1,3,2,5}))); // [3,5]
        System.out.println("  #338 countBits(5)          = " + Arrays.toString(countBits(5))); // [0,1,1,2,1,2]
        System.out.println("  #371 getSum(3,5)           = " + getSum(3, 5)); // 8
        System.out.println();
    }

    // =========================================================================
    // TOPIC #100: POWER SET USING BITS
    // =========================================================================
    // A set of n elements has 2^n subsets (including empty set).
    // Each subset can be represented as an n-bit integer (bitmask).
    //   bit k = 1 => element k is IN the subset
    //   bit k = 0 => element k is NOT in the subset
    //
    //   {a, b, c}  =>  0=000 (empty), 1=001 ({c}), 2=010 ({b}), ..., 7=111 ({a,b,c})
    //
    // Enumerate all subsets: loop mask from 0 to (1<<n)-1
    // Enumerate elements in mask: loop bit from 0 to n-1, check (mask >> bit) & 1
    //
    // TRICKS:
    //   Enumerate all subsets of a mask:  for (int s = mask; s > 0; s = (s-1) & mask)
    //   Check if mask is subset of full:  (mask & full) == mask
    //   Next permutation of bits (same popcount):  see Gosper's hack
    //
    // LeetCode:
    //   #78  Subsets
    //   #90  Subsets II (with duplicates)
    //   #1286 Iterator for Combination
    //   Bitmask DP problems (already covered in Phase 5.4)

    // #78 -- Subsets (no duplicates)
    static List<List<Integer>> subsets(int[] nums) {
        int n = nums.length;
        List<List<Integer>> result = new ArrayList<>();
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> sub = new ArrayList<>();
            for (int bit = 0; bit < n; bit++)
                if ((mask >> bit & 1) == 1) sub.add(nums[bit]);
            result.add(sub);
        }
        return result;
    }

    // Enumerate all SUBsets of a given mask (sum over subsets -- SOS)
    // Complexity: O(3^n) because each element is in one of 3 states
    //   (in mask but not subset, in mask and in subset, not in mask)
    static void enumerateSubmasksOf(int mask, int[] arr) {
        System.out.print("  Subsets of mask " + Integer.toBinaryString(mask) + ": ");
        for (int s = mask; s > 0; s = (s - 1) & mask) {
            List<Integer> sub = new ArrayList<>();
            for (int bit = 0; bit < arr.length; bit++)
                if ((s >> bit & 1) == 1) sub.add(arr[bit]);
            System.out.print(sub + " ");
        }
        System.out.println("[]"); // empty subset
    }

    // Gosper's Hack: next integer with same number of 1 bits
    // Useful to iterate over all masks with exactly k bits set
    static int gosperHack(int mask) {
        int c = mask & -mask;          // lowest set bit
        int r = mask + c;              // set next group
        return (((r ^ mask) >> 2) / c) | r;
    }

    // Enumerate all size-k subsets of {0..n-1} using Gosper's hack
    static List<Integer> allMasksWithKBits(int n, int k) {
        List<Integer> result = new ArrayList<>();
        int mask = (1 << k) - 1; // smallest k-bit mask
        while (mask < (1 << n)) {
            result.add(mask);
            mask = gosperHack(mask);
        }
        return result;
    }

    static void demoPowerSet() {
        System.out.println("--- TOPIC #100: Power Set using Bits ---");
        int[] nums = {1, 2, 3};
        System.out.println("nums = " + Arrays.toString(nums));
        System.out.println("#78 All subsets:");
        for (List<Integer> s : subsets(nums)) System.out.print("  " + s);
        System.out.println();

        System.out.println("\nEnumerate sub-masks:");
        enumerateSubmasksOf(0b110, nums); // subsets of {2,3}

        System.out.println("\nAll size-2 masks from {0..3} (Gosper's hack):");
        List<Integer> masks = allMasksWithKBits(4, 2);
        for (int m : masks)
            System.out.printf("  mask=%d (%s)%n", m, Integer.toBinaryString(m));

        // XOR of all subset sums -- classic bitmask DP
        System.out.println("\nBitmask DP -- max XOR subset:");
        int[] a = {3, 5, 6};
        int maxXor = 0;
        for (int mask = 1; mask < (1 << a.length); mask++) {
            int xorSum = 0;
            for (int bit = 0; bit < a.length; bit++)
                if ((mask >> bit & 1) == 1) xorSum ^= a[bit];
            maxXor = Math.max(maxXor, xorSum);
        }
        System.out.println("  Max XOR of any subset of " + Arrays.toString(a) + " = " + maxXor);
        System.out.println();
    }

    // =========================================================================
    // TOPIC #101: SIEVE OF ERATOSTHENES
    // =========================================================================
    // Find all primes up to N in O(n log log n) time, O(n) space.
    //
    // ALGORITHM:
    //   1. Create boolean array isPrime[0..n], set all true.
    //   2. Set isPrime[0] = isPrime[1] = false.
    //   3. For p from 2 to sqrt(n):
    //        if isPrime[p]: mark all multiples p^2, p^2+p, p^2+2p, ... as false
    //   (Start from p^2 because smaller multiples already marked by smaller primes)
    //
    // VARIANTS:
    //   Linear Sieve          -- O(n) but complex, each composite marked exactly once
    //   Segmented Sieve       -- for very large ranges using small memory
    //   Sieve of Euler        -- O(n), finds Euler's totient function simultaneously
    //
    // LeetCode:
    //   #204 Count Primes
    //   #279 Perfect Squares (BFS, primes help)
    //   #1175 Prime Arrangements
    //   #2523 Closest Prime Numbers in Range

    static boolean[] sieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int p = 2; (long) p * p <= n; p++) {
            if (isPrime[p]) {
                for (int multiple = p * p; multiple <= n; multiple += p)
                    isPrime[multiple] = false;
            }
        }
        return isPrime;
    }

    // #204 -- Count Primes less than n
    static int countPrimes(int n) {
        if (n < 2) return 0;
        boolean[] isPrime = sieve(n - 1);
        int count = 0;
        for (boolean p : isPrime) if (p) count++;
        return count;
    }

    // Smallest prime factor sieve -- useful for fast factorization
    // spf[i] = smallest prime factor of i
    static int[] smallestPrimeFactor(int n) {
        int[] spf = new int[n + 1];
        for (int i = 0; i <= n; i++) spf[i] = i; // initialize: spf[i] = i
        for (int p = 2; (long) p * p <= n; p++) {
            if (spf[p] == p) { // p is prime
                for (int multiple = p * p; multiple <= n; multiple += p)
                    if (spf[multiple] == multiple) spf[multiple] = p;
            }
        }
        return spf;
    }

    // Factorize n in O(log n) using SPF sieve
    static List<Integer> factorize(int n, int[] spf) {
        List<Integer> factors = new ArrayList<>();
        while (n > 1) { factors.add(spf[n]); n /= spf[n]; }
        return factors;
    }

    // Euler's Totient Function: phi(n) = count of k in [1,n] with gcd(k,n)=1
    // Sieve-based computation of phi for all numbers up to n
    static int[] eulerTotient(int n) {
        int[] phi = new int[n + 1];
        for (int i = 0; i <= n; i++) phi[i] = i;
        for (int p = 2; p <= n; p++) {
            if (phi[p] == p) { // p is prime
                for (int multiple = p; multiple <= n; multiple += p)
                    phi[multiple] -= phi[multiple] / p;
            }
        }
        return phi;
    }

    // #2523 -- Closest Prime Numbers in Range [left, right]
    static int[] closestPrimes(int left, int right) {
        boolean[] isPrime = sieve(right);
        int p1 = -1, p2 = -1, minGap = Integer.MAX_VALUE;
        int prev = -1;
        for (int i = left; i <= right; i++) {
            if (isPrime[i]) {
                if (prev != -1 && i - prev < minGap) {
                    minGap = i - prev; p1 = prev; p2 = i;
                }
                prev = i;
            }
        }
        return new int[]{p1, p2};
    }

    static void demoSieve() {
        System.out.println("--- TOPIC #101: Sieve of Eratosthenes ---");
        boolean[] isPrime = sieve(50);
        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= 50; i++) if (isPrime[i]) primes.add(i);
        System.out.println("Primes up to 50: " + primes);

        System.out.println("#204 countPrimes(10) = " + countPrimes(10)); // 4 (2,3,5,7)
        System.out.println("#204 countPrimes(20) = " + countPrimes(20)); // 8

        int[] spf = smallestPrimeFactor(400);
        System.out.println("\nSmallest Prime Factor sieve:");
        System.out.println("  spf[12]=" + spf[12] + ", spf[30]=" + spf[30] + ", spf[97]=" + spf[97]);
        System.out.println("  Factorize 360: " + factorize(360, spf));
        System.out.println("  Factorize 84:  " + factorize(84, spf));

        int[] phi = eulerTotient(12);
        System.out.println("\nEuler Totient phi[1..12]: " + Arrays.toString(Arrays.copyOfRange(phi, 1, 13)));

        System.out.println("\n#2523 closestPrimes(10,19)   = " + Arrays.toString(closestPrimes(10, 19)));
        System.out.println("#2523 closestPrimes(4,6)     = " + Arrays.toString(closestPrimes(4, 6)));
        System.out.println();
    }

    // =========================================================================
    // TOPIC #102: GCD / LCM
    // =========================================================================
    // GCD (Greatest Common Divisor) -- Euclidean Algorithm:
    //   gcd(a, b) = gcd(b, a % b)   base case: gcd(a, 0) = a
    //   Time: O(log(min(a,b)))
    //
    // LCM (Least Common Multiple):
    //   lcm(a, b) = a / gcd(a, b) * b   (divide first to avoid overflow)
    //
    // EXTENDED EUCLIDEAN ALGORITHM:
    //   Finds x, y such that: a*x + b*y = gcd(a, b)
    //   Used to compute modular inverse when gcd(a, m) = 1
    //
    // KEY PROPERTIES:
    //   gcd(a, b) = gcd(b, a)              -- commutative
    //   gcd(a, 0) = a
    //   gcd(a, a) = a
    //   gcd(a, b) = gcd(a-b, b)            -- subtraction form
    //   a * b = gcd(a,b) * lcm(a,b)
    //
    // LeetCode:
    //   #1071 Greatest Common Divisor of Strings
    //   #1979 Find Greatest Common Divisor of Array
    //   #2447 Number of Subarrays With GCD Equal to K
    //   #858  Mirror Reflection (LCM)

    static int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }

    static long gcd(long a, long b) { return b == 0 ? a : gcd(b, a % b); }

    static long lcm(long a, long b) { return a / gcd(a, b) * b; } // divide first!

    // GCD of an array
    static int gcdArray(int[] arr) {
        int result = arr[0];
        for (int x : arr) result = gcd(result, x);
        return result;
    }

    // Extended Euclidean: returns gcd, and sets x,y such that a*x + b*y = gcd
    static long[] extGcd(long a, long b) {
        if (b == 0) return new long[]{a, 1, 0}; // gcd=a, x=1, y=0
        long[] r = extGcd(b, a % b);
        long g = r[0], x = r[2], y = r[1] - (a / b) * r[2];
        return new long[]{g, x, y};
    }

    // #1071 -- GCD of Strings
    // "ABCABC" and "ABC" => "ABC"
    // KEY: str1 + str2 == str2 + str1 iff they share a common divisor string
    static String gcdOfStrings(String str1, String str2) {
        if (!(str1 + str2).equals(str2 + str1)) return "";
        return str1.substring(0, gcd(str1.length(), str2.length()));
    }

    static void demoGcdLcm() {
        System.out.println("--- TOPIC #102: GCD / LCM ---");
        System.out.println("gcd(48, 18)      = " + gcd(48, 18));   // 6
        System.out.println("gcd(100, 75)     = " + gcd(100, 75));  // 25
        System.out.println("lcm(4, 6)        = " + lcm(4, 6));     // 12
        System.out.println("lcm(12, 18)      = " + lcm(12, 18));   // 36

        System.out.println("\nGCD of array [12, 8, 6, 4]: " + gcdArray(new int[]{12, 8, 6, 4})); // 2

        long[] ext = extGcd(35, 15);
        System.out.printf("\nExtended GCD(35,15): gcd=%d, x=%d, y=%d%n", ext[0], ext[1], ext[2]);
        System.out.printf("  Verify: 35*%d + 15*%d = %d%n", ext[1], ext[2], 35*ext[1] + 15*ext[2]);

        System.out.println("\n#1071 gcdOfStrings:");
        System.out.println("  (\"ABCABC\", \"ABC\")   => \"" + gcdOfStrings("ABCABC", "ABC") + "\"");   // ABC
        System.out.println("  (\"ABABAB\", \"ABAB\")  => \"" + gcdOfStrings("ABABAB", "ABAB") + "\"");  // AB
        System.out.println("  (\"LEET\", \"CODE\")    => \"" + gcdOfStrings("LEET", "CODE") + "\"");    // ""
        System.out.println();
    }

    // =========================================================================
    // TOPIC #103: MODULAR ARITHMETIC
    // =========================================================================
    // WHY: numbers in combinatorics problems grow astronomically.
    //   The answer is always given "modulo 10^9+7" (a prime).
    //
    // PROPERTIES:
    //   (a + b) % m = ((a % m) + (b % m)) % m
    //   (a - b) % m = ((a % m) - (b % m) + m) % m  <-- +m to avoid negative!
    //   (a * b) % m = ((a % m) * (b % m)) % m
    //   (a / b) % m = (a * modInverse(b, m)) % m    <-- division needs inverse!
    //
    // MODULAR EXPONENTIATION (Fast Power):
    //   a^n % m in O(log n) using binary exponentiation (exponentiation by squaring)
    //   a^n = (a^(n/2))^2        if n even
    //   a^n = a * (a^(n/2))^2   if n odd
    //
    // MODULAR INVERSE:
    //   a^(-1) mod m = a^(m-2) mod m    when m is PRIME (Fermat's little theorem)
    //   General case: use Extended Euclidean (works when gcd(a,m) = 1)
    //
    // COMBINATION (nCr) mod p:
    //   C(n,r) = n! / (r! * (n-r)!)  mod p
    //   Precompute factorials and inverse factorials.
    //
    // LeetCode:
    //   #50  Pow(x, n)
    //   #372 Super Pow
    //   #509 Fibonacci Number (matrix exponentiation)
    //   #1922 Count Good Numbers
    //   #2338 Count the Number of Ideal Arrays

    static final long MOD = 1_000_000_007L;

    // Fast modular exponentiation: base^exp % mod in O(log exp)
    static long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;
        while (exp > 0) {
            if ((exp & 1) == 1) result = result * base % mod; // odd exp
            base = base * base % mod;
            exp >>= 1;
        }
        return result;
    }

    // Modular inverse using Fermat's little theorem (mod must be prime!)
    // a^(-1) mod p = a^(p-2) mod p
    static long modInverse(long a, long mod) {
        return modPow(a, mod - 2, mod);
    }

    // Precompute factorials and inverse factorials for nCr mod p
    static long[] fact, invFact;

    static void precomputeFactorials(int maxN) {
        fact    = new long[maxN + 1];
        invFact = new long[maxN + 1];
        fact[0] = 1;
        for (int i = 1; i <= maxN; i++) fact[i] = fact[i-1] * i % MOD;
        invFact[maxN] = modInverse(fact[maxN], MOD);
        for (int i = maxN - 1; i >= 0; i--) invFact[i] = invFact[i+1] * (i+1) % MOD;
    }

    // nCr mod p in O(1) after O(n) precomputation
    static long nCr(int n, int r) {
        if (r < 0 || r > n) return 0;
        return fact[n] % MOD * invFact[r] % MOD * invFact[n-r] % MOD;
    }

    // #50 -- Pow(x, n): handle negative n, use modPow idea
    static double myPow(double x, int n) {
        long exp = n;
        if (exp < 0) { x = 1.0 / x; exp = -exp; }
        double result = 1.0;
        while (exp > 0) {
            if ((exp & 1) == 1) result *= x;
            x *= x;
            exp >>= 1;
        }
        return result;
    }

    // Matrix multiplication mod p -- for matrix exponentiation
    static long[][] matMul(long[][] A, long[][] B, long mod) {
        int n = A.length;
        long[][] C = new long[n][n];
        for (int i = 0; i < n; i++)
            for (int k = 0; k < n; k++) if (A[i][k] != 0)
                for (int j = 0; j < n; j++)
                    C[i][j] = (C[i][j] + A[i][k] * B[k][j]) % mod;
        return C;
    }

    static long[][] matPow(long[][] M, long exp, long mod) {
        int n = M.length;
        long[][] result = new long[n][n];
        for (int i = 0; i < n; i++) result[i][i] = 1; // identity matrix
        while (exp > 0) {
            if ((exp & 1) == 1) result = matMul(result, M, mod);
            M = matMul(M, M, mod);
            exp >>= 1;
        }
        return result;
    }

    // Fibonacci in O(log n) using matrix exponentiation:
    // [F(n+1)] = [1 1]^n * [1]
    // [F(n)  ]   [1 0]     [0]
    static long fibMatrix(int n) {
        if (n <= 1) return n;
        long[][] M = {{1, 1}, {1, 0}};
        long[][] R = matPow(M, n - 1, MOD);
        return R[0][0]; // F(n)
    }

    // #1922 -- Count Good Numbers
    // Position even: 5 choices (0,2,4,6,8), odd: 4 choices (2,3,5,7)
    static long countGoodNumbers(long n) {
        long evenPositions = (n + 1) / 2;
        long oddPositions  = n / 2;
        return modPow(5, evenPositions, MOD) * modPow(4, oddPositions, MOD) % MOD;
    }

    static void demoModularArithmetic() {
        System.out.println("--- TOPIC #103: Modular Arithmetic ---");
        System.out.println("modPow(2, 10, 1e9+7)  = " + modPow(2, 10, MOD));   // 1024
        System.out.println("modPow(2, 100, 1e9+7) = " + modPow(2, 100, MOD));  // large
        System.out.println("modInverse(3, 1e9+7)  = " + modInverse(3, MOD));
        System.out.println("  Verify: 3 * " + modInverse(3, MOD) + " % MOD = "
            + (3L * modInverse(3, MOD) % MOD)); // should be 1

        System.out.println("\nBinomial Coefficients nCr (with precomputed factorials):");
        precomputeFactorials(100);
        System.out.println("  C(10, 3) = " + nCr(10, 3));   // 120
        System.out.println("  C(20, 5) = " + nCr(20, 5));   // 15504
        System.out.println("  C(50,25) = " + nCr(50, 25));  // large, mod applied

        System.out.println("\n#50 Pow(x,n):");
        System.out.println("  myPow(2.0, 10) = " + myPow(2.0, 10));   // 1024.0
        System.out.println("  myPow(2.0, -2) = " + myPow(2.0, -2));   // 0.25

        System.out.println("\nFibonacci via Matrix Exponentiation O(log n):");
        System.out.println("  fib(10) = " + fibMatrix(10));  // 55
        System.out.println("  fib(50) = " + fibMatrix(50));  // 12586269025

        System.out.println("\n#1922 countGoodNumbers:");
        System.out.println("  n=1  => " + countGoodNumbers(1));  // 5
        System.out.println("  n=4  => " + countGoodNumbers(4));  // 400
        System.out.println("  n=50 => " + countGoodNumbers(50));

        System.out.println();
        System.out.println("SUMMARY -- Math & Bits Cheat Sheet:");
        System.out.println("  n & (n-1)        : clear lowest set bit / check power of 2");
        System.out.println("  n & (-n)         : isolate lowest set bit (Fenwick Tree)");
        System.out.println("  a ^ b            : XOR -- cancel duplicates, find difference");
        System.out.println("  mask 0..(1<<n)-1 : enumerate all 2^n subsets");
        System.out.println("  s=(s-1)&mask     : enumerate sub-masks of mask");
        System.out.println("  Sieve O(n log logn): all primes + SPF factorization");
        System.out.println("  gcd Euclidean    : O(log min) -- basis of modular inverse");
        System.out.println("  modPow O(log n)  : fast exponentiation, required for large n");
        System.out.println("  nCr mod p        : precompute fact[] + invFact[] => O(1)");
    }
}
