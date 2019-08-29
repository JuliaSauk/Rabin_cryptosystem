import java.math.BigInteger;
import java.util.Random;

public class Rabin {
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger b = BigInteger.valueOf(100);
    private int bitLength = 128;

    public BigInteger getN() {
        return n;
    }

    public BigInteger getB() {
        return b;
    }

    public BigInteger numberBlum(int bitLen) {
        while(true) {
            BigInteger number = BigInteger.probablePrime(bitLen, new Random());
            BigInteger temp = number.subtract(BigInteger.valueOf(3)).mod(BigInteger.valueOf(4));
            if (temp.compareTo(BigInteger.ZERO) == 0)
                return number;
        }
    }

    public void GenerateKeyPair() {
        do {
            this.p = numberBlum(this.bitLength);
            this.q = numberBlum(this.bitLength);
            this.n = this.p.multiply(this.q);
        } while (n.bitLength() != this.bitLength*2);

        System.out.println("Q: " + this.q);
        System.out.println("P: " + this.p);
        System.out.println("N: " + this.n);
    }

    public static BigInteger toFormat(BigInteger m, BigInteger n) {
        int l = n.bitLength();
        BigInteger r = BigInteger.probablePrime(64, new Random());
        if((l-80) >= m.bitLength()) {
            //System.out.println(l);
            BigInteger format = BigInteger.valueOf(255).multiply(BigInteger.valueOf(2).pow((l-64))).add(m.multiply(BigInteger.valueOf(2).pow(64))).add(r);
            // System.out.println(format.bitLength());
            // System.out.println(BigInteger.valueOf(255).multiply(BigInteger.valueOf(2).pow(8*(l-8))).bitLength());
            return format;
        }
        return null;
    }

    public static BigInteger reFormat(BigInteger mFormat) {
        BigInteger temp = BigInteger.ZERO.setBit(mFormat.bitLength()-1);
        if (mFormat.compareTo(temp) == 1){
            BigInteger m = mFormat.mod(BigInteger.ZERO.setBit(mFormat.bitLength()-9)).shiftRight(64);
            return m;
        }
        return null;
    }

    public static BigInteger[] Encrypt(BigInteger m, BigInteger b, BigInteger n) {
        BigInteger x = toFormat(m, n);
        BigInteger[] crypt = new BigInteger[3];
        if (b.compareTo(n.subtract(BigInteger.ONE)) != 1){
            BigInteger y = x.multiply(x.add(b)).mod(n);
            BigInteger c1 = C1(x, b, n);
            BigInteger c2 = C2(x, b, n);
            crypt[0] = y;
            crypt[1] = c1;
            crypt[2] = c2;
            return crypt;
        }
        return null;
    }

    private static BigInteger C1(BigInteger x, BigInteger b, BigInteger n) {
        return ((x.add(b.multiply(BigInteger.valueOf(2).modInverse(n)))).mod(n)).mod(BigInteger.valueOf(2));
    }

    private static BigInteger C2(BigInteger x, BigInteger b, BigInteger n) {
        if(jakobi(x.add(b.multiply(BigInteger.valueOf(2).modInverse(n))), n) == 1)
            return BigInteger.ONE;
        else return BigInteger.ZERO;
    }


    public BigInteger Decrypt(BigInteger[] crypt, BigInteger b, BigInteger n) {
        BigInteger squareBlum = crypt[0].add(b.pow(2).multiply(BigInteger.valueOf(4).modInverse(n)));
        BigInteger[] squareRootBlum = sqrBlum(squareBlum, this.p, this.q);
        BigInteger x;
        BigInteger xC1;
        BigInteger xC2;
        for(int i = 0; i < squareRootBlum.length; i++) {
            x = (b.multiply(BigInteger.valueOf(2).modInverse(n)).negate().add(squareRootBlum[i])).mod(n);
            //System.out.println(x.toString(16));
            xC1 = C1(x, b, n);
            xC2 = C2(x, b, n);
            /*System.out.println("c1 " + xC1);
            System.out.println("c2 " + xC2);*/

            if (xC1.equals(crypt[1]) && xC2.equals(crypt[2])) return reFormat(x);
        }
        return null;
    }

    public static BigInteger[] sqrBlum(BigInteger y, BigInteger p, BigInteger q) {
        BigInteger[] X = new BigInteger[4];
        BigInteger s1 = y.modPow(p.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), p);//.multiply(BigInteger.valueOf(4).modInverse(p)), p);
        BigInteger s2 = y.modPow(q.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), q);//multiply(BigInteger.valueOf(4).modInverse(q)), q);
        X[0] = (s1.multiply(q).multiply(q.modInverse(p))).add(s2.multiply(p).multiply(p.modInverse(q)));
        //System.out.println(X[0]);
        X[1] = (s1.multiply(q).multiply(q.modInverse(p))).subtract(s2.multiply(p).multiply(p.modInverse(q)));
        //System.out.println(X[1]);
        X[2] = ((s1.multiply(q).multiply(q.modInverse(p))).negate()).add(s2.multiply(p).multiply(p.modInverse(q)));
        //System.out.println(X[2]);
        X[3] = ((s1.multiply(q).multiply(q.modInverse(p))).negate()).subtract(s2.multiply(p).multiply(p.modInverse(q)));
        //System.out.println(X[3]);
        return X;
    }

    private static int jakobi(BigInteger a, BigInteger b){
        if (b.compareTo(BigInteger.ONE) <= 0 || b.mod(BigInteger.valueOf(2)).compareTo(BigInteger.ZERO) == 0 )
            throw new IllegalArgumentException();

        if (!a.gcd(b).equals(BigInteger.ONE)) {
            return 0;
        }

        BigInteger r = BigInteger.ONE;
        if (a.compareTo(BigInteger.ZERO) != -1) {
        } else {
            a = a.negate();
            if (b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
                r = r.negate();
        }

        BigInteger t = BigInteger.ZERO;
        while (a.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
            t = t.add(BigInteger.ONE);
            a = a.divide(BigInteger.valueOf(2));
        }
        if (!t.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)){
            if (b.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(3))) {
                r = r.negate();
            } else if (b.mod(BigInteger.valueOf(8)).equals(BigInteger.valueOf(5))) {
                r = r.negate();
            }
        }

        if (a.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3)))
            if (b.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
                r = r.negate();
            }
        BigInteger c = a;
        a = b.mod(c);
        b = c;

        if (!a.equals(BigInteger.ZERO)) return r.multiply(BigInteger.valueOf(jakobi(a, b))).intValue();

        return r.intValue();
    }

}

