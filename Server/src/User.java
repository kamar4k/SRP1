import java.math.BigInteger;

/**
 * Created by Андрюха on 15.12.2018.
 */
public class User {

    private String login;
    private BigInteger salt;
    private BigInteger v;

    public User(String login, String salt, String v)
    {
        this.login = login;
        this.salt = new BigInteger(salt, 10);
        this.v = new BigInteger(v, 10);
    }

    public String getLogin()
    {
        return login;
    }

    public BigInteger getSalt()
    {
        return salt;
    }

    public BigInteger getV()
    {
        return v;
    }

    @Override
    public String toString()
    {
        return "User: " + login +"\nSalt: " + salt.toString() + "\nv: " + v.toString();
    }

}
