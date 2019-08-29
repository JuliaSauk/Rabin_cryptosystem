import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Rabin A = new Rabin();
        A.GenerateKeyPair();
        BigInteger m = BigInteger.valueOf(432);
        System.out.println("Открытое сообщение: " + m);
        System.out.println("\nШифротекст");
        BigInteger[] text = Rabin.Encrypt(m, A.getB(), A.getN());
        System.out.println("Y: " + text[0].toString(16));
        System.out.println("C1: " + text[1] + ", C2: " + text[2]);
        System.out.println("Расшифрованное сообщение: " + (A.Decrypt(text, A.getB(), A.getN())));
    }
}
