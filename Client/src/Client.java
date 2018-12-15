import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by Андрюха on 12.12.2018.
 */
public class Client extends JFrame{

    BigInteger g = new BigInteger("2", 10);
    BigInteger k = new BigInteger("3", 10);
    BigInteger mod_N = new BigInteger("115b8b692e0e045692cf280b436735c77a5a9e8a9e7ed56c965f87db5b2a2ece3", 16);


    Socket client;
    PrintWriter output;
    BufferedReader input;

    JPanel connectPanel;
    JPanel mainPanel;
    JPanel registrationPanel;
    JPanel authorizationPanel;

    JButton connectButton = new JButton("Подключиться");
    JButton exitButton = new JButton("Выход");
    JLabel registerLabel = new JLabel("Регистрация");
    JLabel authirizedLabel = new JLabel("Авторизация");
    JLabel loginRegLabel = new JLabel("Имя пользователя:");
    JLabel loginAuthLabel = new JLabel("Имя пользователя:");
    JTextField loginRegField = new JTextField();
    JTextField loginAuthField = new JTextField();
    JLabel passwordRegLabel = new JLabel("Пароль:");
    JLabel passwordAuthLabel = new JLabel("Пароль:");
    JTextField passwordRegField = new JTextField();
    JTextField passwordAuthField = new JTextField();
    JButton registerButton = new JButton("Зарегистрироваться");
    JButton authorizedButton = new JButton("Авторизация");

    JTextArea textArea2 = new JTextArea();

    boolean isConnect = true;

    public Client()
    {
        super("Клиент");
        createGUI();
    }

//Создание Фрейма_______________________________________________________
//______________________________________________________________________

    public void createGUI()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 300);
        setResizable(false);
        setLocation(400,200);

        setLayout(new BorderLayout());

        textArea2.setEditable(false);

        authirizedLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        registerLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));

        connectPanel = new JPanel();
        connectPanel.setLayout(new GridLayout(1,2));
        connectPanel.setBackground(Color.red);

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1,2));

        registrationPanel = new JPanel();
        registrationPanel.setLayout(new GridLayout(6,1));
        registrationPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        authorizationPanel = new JPanel();
        authorizationPanel.setLayout(new GridLayout(6,1));
        authorizationPanel.setBorder(BorderFactory.createLineBorder(Color.gray, 1));

        connectPanel.add(connectButton);
        connectPanel.add(exitButton);

        authorizationPanel.add(authirizedLabel);
        authorizationPanel.add(loginAuthLabel);
        authorizationPanel.add(loginAuthField);
        authorizationPanel.add(passwordAuthLabel);
        authorizationPanel.add(passwordAuthField);
        authorizationPanel.add(authorizedButton);

        registrationPanel.add(registerLabel);
        registrationPanel.add(loginRegLabel);
        registrationPanel.add(loginRegField);
        registrationPanel.add(passwordRegLabel);
        registrationPanel.add(passwordRegField);
        registrationPanel.add(registerButton);

        mainPanel.add(authorizationPanel);
        mainPanel.add(registrationPanel);

        add(connectPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(textArea2, BorderLayout.SOUTH);

        buttonInit();

        setVisible(true);
    }

    public void buttonInit()
    {
        connectButton.addActionListener(new ActionConnect());

        registerButton.addActionListener(new ActionRegister());

        authorizedButton.addActionListener(new ActionAuthorization());
    }
//________________________________________________________________________________________
//________________________________________________________________________________________\

    public BigInteger getSalt() //Метод генерации соли
    {
        int bits = 128;
        return new BigInteger(bits, new Random());

    }

    public BigInteger getRandom()
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



    public static void main(String [] args)
    {
        Client client = new Client();
    }


//Действия для кнопок__________________________________________________________________________
//_____________________________________________________________________________________________

    public class ActionConnect implements ActionListener
    {
        public void actionPerformed(ActionEvent ev)
        {
            if (isConnect) { // Флаг изменения кнопки
                try {
                    isConnect = false;
                    client = new Socket(InetAddress.getLocalHost(), 8030);
//Установка соединения с сервером
                    textArea2.setText(null);
                    textArea2.setText("Подключение к серверу прошло успешно!\n");
                    output = new PrintWriter(new
                            OutputStreamWriter(client.getOutputStream())); // Создаём входящий поток
                    input = new BufferedReader(new
                            InputStreamReader(client.getInputStream())); // Создаём исходящий поток
                    connectButton.setText("Отключится"); // Меняем кнопку
                } catch (UnknownHostException e) {
                    isConnect = true;
                    textArea2.setText(null);
                    textArea2.setText("Ошибка подключения!");
                    connectButton.setText("Подключится");
                } catch (IOException e) {
                    isConnect = true;
                    textArea2.setText(null);
                    textArea2.setText("Ошибка подключения!");
                    connectButton.setText("Подключится");
                }
            } else {
                isConnect = true;
                client=null;
                textArea2.setText("Соединение с сервером разорвано!");
                connectButton.setText("Подключится");
            }
        }
        }

        public class ActionRegister implements ActionListener
        {
            public void actionPerformed(ActionEvent ev) //РЕГИСТРАЦИЯ НОВОГО ПОЛЬЗОВАТЕЛЯ
            {
                String loginStr = loginRegField.getText();
                String passwordStr = passwordRegField.getText();
                if (loginStr.length() != 0 && passwordStr.length() != 0)
                {
                    try{
                    output.println("REGISTRATION"); //Отправка серверу команды для регистрации нового пользователя
                    output.flush();

                        BigInteger salt = getSalt(); //Соль

                        BigInteger x = new BigInteger(hashFunc(salt.toString(), passwordStr).toString()); // x = H(s, p)
                        BigInteger v = g.pow(x.intValue()); //v = g^x

                        System.out.println(loginStr);
                        System.out.println(salt.toString());
                        System.out.println(v.toString());
                        //Отправка на сервер login, salt, v
                        output.println(loginStr);
                        output.flush();
                        output.println(salt);
                        output.flush();
                        output.println(v);
                        output.flush();

                        String serv = input.readLine();
                        textArea2.setText("Сервер: " + serv);

                    } catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }

                } else
                {
                    textArea2.setText(null);
                    textArea2.setText("Не введены все данные для регистрации!");
                }
            }
        }

        public class ActionAuthorization implements ActionListener
        {

            BigInteger a; //приватный ключ
            BigInteger A; //публичный ключ

            public void actionPerformed(ActionEvent ev)
            {
                String login = loginAuthField.getText();
                String password = passwordAuthField.getText();
                if (login.length() != 0 && password.length() != 0)
                {
                    try{
                        output.println("AUTH");
                        output.flush();
                        a = getRandom();
                        A = g.modPow(a, mod_N);

                        output.println(login);              //Отправляем на сервер идентификатор пользователя
                        output.flush();                     //и публичный ключ A
                        output.println(A.toString());       //
                        output.flush();

                        String serv1 = input.readLine();
                        if(!serv1.equals("Пользователь не найден!"))
                        {
                            textArea2.setText(serv1);
                            String strB = input.readLine();
                            String strSaltServer = input.readLine();

                            BigInteger B = new BigInteger(strB);
                            BigInteger saltServer = new BigInteger(strSaltServer);

                            BigInteger u = new BigInteger(hashFunc(A.toString(), B.toString()).toString());

                            BigInteger x = new BigInteger(hashFunc(saltServer.toString(), password).toString());

                            //Вычисление сессионного ключа S = (B - ( (g^x % N) *k)) ^ (a + u*x) (% N)
                            BigInteger S1 = new BigInteger(B.subtract(k.multiply(g.modPow(x, mod_N))).toString());
                            BigInteger S2 = new BigInteger(a.add(u.pow(x.intValue())).toString());
                            BigInteger S = S1.modPow(S2, mod_N);

                            //K = H(S)
                            BigInteger K = new BigInteger(hashFunc(S.toString()).toString());
                            System.out.println(K);

                            //M = H( H(N) XOR H(g), H(I), s, A, B, K)
                            BigInteger hN = new BigInteger(hashFunc(mod_N.toString()).toString());
                            BigInteger hG = new BigInteger(hashFunc(g.toString()).toString());
                            BigInteger xor = hN.xor(hG);
                            BigInteger M = new BigInteger(hashFunc(xor.toString(), hashFunc(login).toString(), saltServer.toString(),
                                    A.toString(), B.toString(), K.toString()).toString());

                            //Проверка по M
                            System.out.println(M);
                            output.println(M.toString());
                            output.flush();

                            String serv2 = input.readLine();
                            textArea2.append(M.toString());
                            if(!serv2.equals("Неверный пароль!"))
                            {
                                BigInteger Rserv = new BigInteger(serv2);
                                System.out.println(Rserv);
                                BigInteger R = new BigInteger(hashFunc(A.toString(), M.toString(),
                                        K.toString()).toString());
                                textArea2.append(" " + R.toString());
                                if(Rserv.equals(R))
                                {
                                    textArea2.append(" Вход успешно выполнен!");
                                }
                            }
                            else
                            {
                                textArea2.append(" " + serv2);
                            }
                        }else
                        {
                            textArea2.setText(serv1);
                        }
                    } catch (Exception e1)
                    {

                    }
                }
            }
        }
    }
