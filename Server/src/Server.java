/**
 * Created by Андрюха on 13.12.2018.
 */
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Random;

public class Server {

    public static List<User> userList = new ArrayList<>();
    static BigInteger k = new BigInteger("3", 10);
    static BigInteger g = new BigInteger("2", 10);
    static BigInteger mod_N = new BigInteger("115b8b692e0e045692cf280b436735c77a5a9e8a9e7ed56c965f87db5b2a2ece3", 16);

    public static BigInteger getRandom()
    {
        return new BigInteger(128, new Random());
    }

    static public Long hashFunc(String... args) {

        long res = 0;

        for (String arg : args) {

            for (char string : arg.toCharArray()) {

                res += string;
            }

        }

        res >>= 4;
        return res;

    }


    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(8030)) { // Вызов конструктора и присваивание порта.
            while (true) {
                Socket client = server.accept(); // Устанавливаем соединение с клиен- том.
                        System.out.println("Соединение установленно с " + client.getPort());
                try (PrintWriter output = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
                BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream())))
                {
                    while(true)
                    {
                        String cmd = input.readLine();



                        if("REGISTRATION".equals(cmd)){ //РЕГИСТРАЦИЯ ПОЛЬЗОВАТЕЛЯ
                            System.out.println(cmd);
                        String login = input.readLine();
                        String salt = input.readLine();
                        String v = input.readLine();
                        System.out.println(login);
                        System.out.println(salt);
                        System.out.println(v);
                            boolean userIsReg = false;
                            for (User user : userList)
                            {
                                if(login.equals(user.getLogin()))
                                {
                                    userIsReg = true;
                                }
                            }
                            if(!userIsReg){
                                userList.add(new User(login, salt, v));
                                output.println("Пользователь \"" + login + "\" успешно зарегистрирован");
                                output.flush();
                            } else {
                                output.println("Пользователь \"" + login + "\"уже зарегистрирован!");
                                output.flush();
                            }
                        }

                        if("AUTH".equals(cmd)) {
                            User currentUser;
                            BigInteger u;
                            BigInteger v;
                            BigInteger salt;
                            System.out.println(cmd);
                            String login = input.readLine();
                            String A = input.readLine();
                            int iter = 0;
                            boolean check = false;
                            for (User user : userList)                            //Проверка
                            {                                                     //Существует ли пользователь
                                if (user.getLogin().equals(login)) {              //c таким идентификатором
                                    check = true;
                                    iter = userList.indexOf(user);
                                }
                            }
                            if (check) {
                                output.println("Пользователь найден ");
                                output.flush();

                                //Вычисление b - приватного и B - закрытого ключей
                                BigInteger b = getRandom();
                                BigInteger B1 = new BigInteger((k.multiply(userList.get(iter).getV()).toString()));
                                BigInteger B2 = new BigInteger(g.modPow(b, mod_N).toString());
                                BigInteger B = new BigInteger(B1.add(B2).toString());
                                //BigInteger B = B3.mod(mod_N);

                                output.println(B.toString());
                                output.flush();                                         //Отправка пользователю
                                output.println(userList.get(iter).getSalt().toString());// соли и публичного ключа
                                output.flush();                                         // B

                                u = new BigInteger(hashFunc(A.toString(), B.toString()).toString());

                                //S = ((A*(v^u % N)) ^ b) % N
                                BigInteger Svun = userList.get(iter).getV().modPow(u, mod_N);
                                BigInteger _A = new BigInteger(A);
                                BigInteger S1 = _A.multiply(Svun);
                                BigInteger S = S1.modPow(b, mod_N);

                                //K = H(S)
                                BigInteger K = new BigInteger(hashFunc(S.toString()).toString());

                                //M = H( H(N) XOR H(g), H(I), s, A, B, K)
                                BigInteger hN = new BigInteger(hashFunc(mod_N.toString()).toString());
                                BigInteger hG = new BigInteger(hashFunc(g.toString()).toString());
                                BigInteger xor = hN.xor(hG);
                                BigInteger M = new BigInteger(hashFunc(xor.toString(), hashFunc(login).toString(),
                                        userList.get(iter).getSalt().toString(), A.toString(), B.toString(),
                                        K.toString()).toString());
                                System.out.println(M);
                                BigInteger clientM = new BigInteger(input.readLine());

                                if(clientM.equals(M))
                                {
                                    BigInteger R = new BigInteger(hashFunc(A.toString(), M.toString(),
                                            K.toString()).toString());
                                    System.out.println(R);
                                    output.println(R.toString());
                                    output.flush();
                                }
                                else
                                {
                                    output.println("Неверный пароль!");
                                    output.flush();
                                    continue;
                                }

                            } else
                            {
                               output.println("Пользователь не найден!");
                               output.flush();
                               continue;
                            }
                        }
                    }

                } catch (SocketException ex) {
                    for (User user : userList)
                    {
                        System.out.println(user);
                    }
                    System.out.println("Соединение с " + client.getInetAddress() +
                            ":" + client.getPort() + "разорвано");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}

