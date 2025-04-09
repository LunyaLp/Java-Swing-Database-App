package org.example;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class Main extends JFrame {
    Main() {
        this.setLayout(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setBounds(100, 100, 1000, 600);
        this.setTitle("Database");

        jlfio = new JLabel("fio:");
        this.add(jlfio);
        jlfio.setBounds(10, 40, 30, 25);

        jtFio = new JTextField();
        this.add(jtFio);
        jtFio.setBounds(70, 40, 260, 25);

        jlAge = new JLabel("age:");
        this.add(jlAge);
        jlAge.setBounds(10, 80, 30, 25);

        jsAge = new JSlider(0, 100);
        jsAge.setPaintLabels(true);
        jsAge.setMajorTickSpacing(10);
        this.add(jsAge);

        jsAge.setBounds(70, 80, 200, 25);

        label = new JLabel(String.valueOf(jsAge.getValue()));
        this.add(label);
        label.setBounds(300, 80, 200, 40);

        jsAge.addChangeListener(e -> label.setText(String.valueOf(jsAge.getValue())));

        jlHand = new JLabel("Right?");
        this.add(jlHand);
        jlHand.setBounds(10, 130, 50, 25);

        jlE = new JLabel("");
        this.add(jlE);
        jlE.setBounds(30, 330, 200, 25);
        jlE.setVisible(true);

        jbOk = new JButton("OK");
        this.add(jbOk);
        jbOk.setBounds(30,360,100,25);
        jbOk.setVisible(false);

        jbOk.addActionListener(e -> {
            jlE.setVisible(false);
            jbOk.setVisible(false);
        });

        jrbR = new JRadioButton("Right");
        this.add(jrbR);
        jrbR.setBounds(70, 130, 60, 30);
        jrbR.setSelected(true);

        jrbL = new JRadioButton("Left");
        this.add(jrbL);
        jrbL.setBounds(150, 130, 60, 30);

        bgOne = new ButtonGroup();
        bgOne.add(jrbR);
        bgOne.add(jrbL);

        jbW = new JButton("Записать");
        this.add(jbW);
        jbW.setBounds(30, 250, 100, 30);

        jbW.addActionListener(e -> {
            Connection con1 = null;
            String url1 = "jdbc:postgresql://127.0.01:5432/postgres";
            try {
                con1 = DriverManager.getConnection(url1, "введите пользователя", "введите пароль");
                con1.setAutoCommit(true);

                DatabaseMetaData metaData = con1.getMetaData();
                ResultSet tables = metaData.getTables(null, null,nameTab, null);
                if (!tables.next()) {
                    Statement statement = con1.createStatement();
                    String createTableQuery = "CREATE TABLE " + nameTab + " (id SERIAL PRIMARY KEY,fio TEXT,age INTEGER,rig BOOLEAN,dat DATE)";
                    statement.executeUpdate(createTableQuery);
                }
                String sql = "insert into public."+nameTab+" (id,fio,age,rig,dat) VALUES (?, ?, ?, ?, ?)";
                Statement  TempStat = con1.createStatement();
                ResultSet resultSet = TempStat.executeQuery("SELECT MAX(id) FROM "+nameTab);
                int id = 1;
                if (resultSet.next()) {
                    id = resultSet.getInt(1) + 1;
                }
                PreparedStatement preparedStatement = con1.prepareStatement(sql);
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, jtFio.getText());
                preparedStatement.setInt(3, jsAge.getValue());
                preparedStatement.setBoolean(4, jrbR.isSelected());
                preparedStatement.setDate(5, Date.valueOf(LocalDate.now()));
                preparedStatement.executeUpdate();
                con1.close();
            } catch (SQLException ex) {
                jlE.setVisible(true);
                jlE.setText("Произошла ошибка, попробуйте еще раз");
                jbOk.setVisible(true);
            }
        });

        jlR = new JLabel("Введите id:");
        this.add(jlR);
        jlR.setBounds(10, 180, 80, 30);

        jtR = new JTextField();
        this.add(jtR);
        jtR.setBounds(90, 185, 40, 25);

        jbC = new JButton("Изменить");
        this.add(jbC);
        jbC.setBounds(150, 250, 100, 30);

        jbC.addActionListener(e -> {
            Connection con1 = null;
            String url1 = "jdbc:postgresql://127.0.01:5432/postgres";
            try {
                con1 = DriverManager.getConnection(url1, "введите пользователя", "введите пароль");
                con1.setAutoCommit(true);

                DatabaseMetaData metaData = con1.getMetaData();
                ResultSet tables = metaData.getTables(null, null,nameTab, null);
                if (tables.next()) {

                    String sql1 = "SELECT * FROM " + nameTab + " WHERE id = ?";
                    PreparedStatement statement = con1.prepareStatement(sql1);
                    statement.setInt(1, Integer.parseInt(jtR.getText()));
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        String sql = "UPDATE public." + nameTab + " SET fio = ?,age = ?,rig = ?,dat = ? WHERE id = ? ";
                        PreparedStatement statem = con1.prepareStatement(sql);
                        statem.setString(1, jtFio.getText());
                        statem.setInt(2, jsAge.getValue());
                        statem.setBoolean(3, jrbR.isSelected());
                        statem.setDate(4, Date.valueOf(LocalDate.now()));
                        statem.setInt(5, Integer.parseInt(jtR.getText()));
                        statem.executeUpdate();
                        con1.close();
                    } else {
                        jtaO.setText("id не найден");
                    }
                }
                else {
                    jtaO.setText("Таблица не найдена");
                }
            } catch (SQLException ex) {
                jlE.setVisible(true);
                jlE.setText("Произошла ошибка, попробуйте еще раз");
                jbOk.setVisible(true);
            }
            jtR.setText("");

        });


        jbO = new JButton("Вывести базу данных");
        this.add(jbO);
        jbO.setBounds(40, 290, 200, 30);

        jtaO = new JTextArea();
        this.add(jtaO);
        jtaO.setBounds(400, 30, 500, 500);

        jbO.addActionListener(e -> {
            Connection con1 = null;
            Statement st1 = null;
            String url1 = "jdbc:postgresql://127.0.01:5432/postgres";
            StringBuilder text= new StringBuilder();
            try {
                con1 = DriverManager.getConnection(url1, "введите пользователя", "введите пароль");
                DatabaseMetaData metaData = con1.getMetaData();
                ResultSet tables = metaData.getTables(null, null,nameTab, null);
                if (tables.next()) {

                    st1 = con1.createStatement();
                    ResultSet rs1 = st1.executeQuery("SELECT * FROM public." + nameTab + " ORDER BY id;");
                    while (rs1.next()) {
                        String stRes = rs1.getString("id") + " " + rs1.getString(2) + " " + rs1.getString(3) + " " + rs1.getString(4) + " " + rs1.getString(5);
                        text.append(stRes).append("\n");
                    }
                    jtaO.setText(text.toString());
                    st1.close();
                    con1.close();
                }
                else {
                    jtaO.setText("Таблица не найдена");
                }
            } catch (SQLException ex) {
                jlE.setVisible(true);
                jlE.setText("Ошибка SQL запроса");
                jbOk.setVisible(true);
            }
        });
    }

    public static void main(String[] args)  {
        new Main();

    }
    String nameTab ="my_table";
    JTextField jtFio, jtR;
    JSlider jsAge;
    JRadioButton jrbR, jrbL;
    JTextArea jtaO;
    ButtonGroup bgOne;
    JLabel label, jlfio, jlAge, jlHand, jlR, jlE;
    JButton jbW, jbC, jbO, jbOk;
}