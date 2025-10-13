package app;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculatorFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;

    // stare internă
    private String current = "0";          // ce apare pe ecran
    private double firstOperand = 0;       // primul operand
    private String operator = null;        // +, -, *, /
    private boolean startNewNumber = true; // dacă începem un număr nou

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            CalculatorFrame frame = new CalculatorFrame();
            frame.setVisible(true);
        });
    }

    public CalculatorFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 446, 348);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Afișaj
        textField = new JTextField();
        textField.setText("0");
        textField.setFont(new Font("Tahoma", Font.PLAIN, 28));
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setEditable(false);
        textField.setBounds(10, 17, 414, 40);
        contentPane.add(textField);
        textField.setColumns(10);

        // Butoane
        JButton btn7 = new JButton("7");
        btn7.setBounds(10, 115, 89, 40);
        contentPane.add(btn7);

        JButton btn8 = new JButton("8");
        btn8.setBounds(118, 115, 89, 40);
        contentPane.add(btn8);

        JButton btn9 = new JButton("9");
        btn9.setBounds(228, 115, 89, 40);
        contentPane.add(btn9);

        JButton btnPlus = new JButton("+");
        btnPlus.setBounds(335, 115, 89, 40);
        contentPane.add(btnPlus);

        JButton btn4 = new JButton("4");
        btn4.setBounds(10, 162, 89, 40);
        contentPane.add(btn4);

        JButton btn5 = new JButton("5");
        btn5.setBounds(118, 162, 89, 40);
        contentPane.add(btn5);

        JButton btn6 = new JButton("6");
        btn6.setBounds(228, 162, 89, 40);
        contentPane.add(btn6);

        JButton btnMinus = new JButton("-");
        btnMinus.setBounds(335, 162, 89, 40);
        contentPane.add(btnMinus);

        JButton btn1 = new JButton("1");
        btn1.setBounds(10, 209, 89, 40);
        contentPane.add(btn1);

        JButton btn2 = new JButton("2");
        btn2.setBounds(118, 209, 89, 40);
        contentPane.add(btn2);

        JButton btn3 = new JButton("3");
        btn3.setBounds(228, 209, 89, 40);
        contentPane.add(btn3);

        JButton btnInmultire = new JButton("*");
        btnInmultire.setBounds(335, 209, 89, 40);
        contentPane.add(btnInmultire);

        JButton btnDelete = new JButton("DEL");
        btnDelete.setBounds(10, 64, 89, 40);
        contentPane.add(btnDelete);

        JButton btn0 = new JButton("0");
        btn0.setBounds(118, 256, 89, 40);
        contentPane.add(btn0);

        JButton btnVirgula = new JButton(",");
        btnVirgula.setBounds(228, 256, 89, 40);
        contentPane.add(btnVirgula);

        JButton btnImpartire = new JButton("/");
        btnImpartire.setBounds(335, 256, 89, 40);
        contentPane.add(btnImpartire);

        JButton btnEgal = new JButton("=");
        btnEgal.setBounds(335, 68, 89, 40);
        contentPane.add(btnEgal);

        JButton btnC = new JButton("C");
        btnC.setBounds(10, 256, 89, 40);
        contentPane.add(btnC);

        // === Legare acțiuni ===

        // Cifre
        btn0.addActionListener(e -> handleDigit("0"));
        btn1.addActionListener(e -> handleDigit("1"));
        btn2.addActionListener(e -> handleDigit("2"));
        btn3.addActionListener(e -> handleDigit("3"));
        btn4.addActionListener(e -> handleDigit("4"));
        btn5.addActionListener(e -> handleDigit("5"));
        btn6.addActionListener(e -> handleDigit("6"));
        btn7.addActionListener(e -> handleDigit("7"));
        btn8.addActionListener(e -> handleDigit("8"));
        btn9.addActionListener(e -> handleDigit("9"));

        // Operații
        btnPlus.addActionListener(e -> handleOperator("+"));
        btnMinus.addActionListener(e -> handleOperator("-"));
        btnInmultire.addActionListener(e -> handleOperator("*"));
        btnImpartire.addActionListener(e -> handleOperator("/"));

        // Speciale
        btnVirgula.addActionListener((ActionEvent e) -> handleComma());
        btnC.addActionListener(e -> handleClear());
        btnDelete.addActionListener(e -> handleDelete());
        btnEgal.addActionListener(e -> handleEquals());

        setLocationRelativeTo(null); // centrează fereastra
        updateDisplay();
    }

    // ===== Logica =====

    private void updateDisplay() {
        textField.setText(current);
    }

    private boolean isError() {
        return current.startsWith("Eroare");
    }

    private void handleDigit(String digit) {
        if (isError() || startNewNumber) {
            current = digit;
            startNewNumber = false;
        } else {
            if (current.equals("0")) current = digit;
            else current += digit;
        }
        updateDisplay();
    }

    private void handleComma() {
        if (isError() || startNewNumber) {
            current = "0,";
            startNewNumber = false;
        } else if (!current.contains(",")) {
            current += ",";
        }
        updateDisplay();
    }

    private void handleClear() {
        current = "0";
        firstOperand = 0;
        operator = null;
        startNewNumber = true;
        updateDisplay();
    }

    private void handleDelete() {
        if (isError()) return;
        if (startNewNumber) return;
        if (current.length() <= 1) {
            current = "0";
            startNewNumber = true;
        } else {
            current = current.substring(0, current.length() - 1);
            if (current.equals("-")) {
                current = "0";
                startNewNumber = true;
            }
        }
        updateDisplay();
    }

    private void handleOperator(String op) {
        if (isError()) return;
        firstOperand = parseNumber(current);
        operator = op;
        startNewNumber = true;
    }

    private void handleEquals() {
        if (isError() || operator == null) return;

        double secondOperand = parseNumber(current);
        double result = 0;

        switch (operator) {
            case "+": result = firstOperand + secondOperand; break;
            case "-": result = firstOperand - secondOperand; break;
            case "*": result = firstOperand * secondOperand; break;
            case "/":
                if (secondOperand == 0) {
                    current = "Eroare /0";
                    operator = null;
                    startNewNumber = true;
                    updateDisplay();
                    return;
                }
                result = firstOperand / secondOperand;
                break;
        }

        current = formatResult(result);
        operator = null;
        startNewNumber = true;
        updateDisplay();
    }

    private double parseNumber(String s) {
        return Double.parseDouble(s.replace(',', '.'));
    }

    private String formatResult(double value) {
        // Elimină zerourile inutile și afișează cu virgulă
        BigDecimal bd = BigDecimal.valueOf(value)
                                   .stripTrailingZeros();
        String plain = bd.toPlainString();
        return plain.replace('.', ',');
    }
}
