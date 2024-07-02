package gui;

import constants.CommonConstants;
import service.CalcService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class CalcGui extends JFrame implements ActionListener {

    private final SpringLayout springLayout = new SpringLayout();
    private final CalcService calcService;

    private final JTextField displayField;
    private final JButton[] buttons;

    private boolean pressedOperator = false;
    private boolean pressedEquals = false;

    public CalcGui() {
        super(CommonConstants.APP_NAME);
        setSize(CommonConstants.APP_SIZE[0], CommonConstants.APP_SIZE[1]);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(springLayout);

        calcService = new CalcService();

        displayField = createDisplayField();
        buttons = createButtons();

        addComponentsToFrame();
    }

    private void addComponentsToFrame() {
        addDisplayFieldGui();
        addButtonComponents();
    }

    private JTextField createDisplayField() {
        JTextField textField = new JTextField(CommonConstants.TEXTFIELD_LENGTH);
        textField.setFont(new Font("Dialog", Font.PLAIN, CommonConstants.TEXTFIELD_FONTSIZE));
        textField.setEditable(false);
        textField.setText("0");
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        return textField;
    }

    private JButton[] createButtons() {
        JButton[] buttons = new JButton[CommonConstants.BUTTON_COUNT];
        for (int i = 0; i < CommonConstants.BUTTON_COUNT; i++) {
            JButton button = new JButton(getButtonLabel(i));
            button.setFont(new Font("Dialog", Font.PLAIN, CommonConstants.BUTTON_FONTSIZE));
            button.addActionListener(this);
            buttons[i] = button;
        }
        return buttons;
    }

    private void addDisplayFieldGui() {
        JPanel displayFieldPanel = new JPanel();
        displayFieldPanel.add(displayField);
        this.getContentPane().add(displayFieldPanel);
        springLayout.putConstraint(SpringLayout.NORTH, displayFieldPanel, CommonConstants.TEXTFIELD_SPRINGLAYOUT_NORTHPAD, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, displayFieldPanel, CommonConstants.TEXTFIELD_SPRINGLAYOUT_WESTPAD, SpringLayout.WEST, this);
    }

    private void addButtonComponents() {
        GridLayout gridLayout = new GridLayout(CommonConstants.BUTTON_ROWCOUNT, CommonConstants.BUTTON_COLUMNCOUNT);
        gridLayout.setHgap(CommonConstants.BUTTON_HGAP);
        gridLayout.setVgap(CommonConstants.BUTTON_VGAP);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(gridLayout);

        for (JButton button : buttons) {
            buttonPanel.add(button);
        }

        this.getContentPane().add(buttonPanel);
        springLayout.putConstraint(SpringLayout.NORTH, buttonPanel, CommonConstants.BUTTON_SPRINGLAYOUT_NORTHPAD, SpringLayout.NORTH, this);
        springLayout.putConstraint(SpringLayout.WEST, buttonPanel, CommonConstants.BUTTON_SPRINGLAYOUT_WESTPAD, SpringLayout.WEST, this);
    }

    private String getButtonLabel(int buttonIndex) {
        Map<Integer, String> buttonLabels = new HashMap<>();
        buttonLabels.put(0, "7");
        buttonLabels.put(1, "8");
        buttonLabels.put(2, "9");
        buttonLabels.put(3, "/");
        buttonLabels.put(4, "4");
        buttonLabels.put(5, "5");
        buttonLabels.put(6, "6");
        buttonLabels.put(7, "x");
        buttonLabels.put(8, "1");
        buttonLabels.put(9, "2");
        buttonLabels.put(10, "3");
        buttonLabels.put(11, "-");
        buttonLabels.put(12, "0");
        buttonLabels.put(13, ".");
        buttonLabels.put(14, "+");
        buttonLabels.put(15, "=");

        return buttonLabels.getOrDefault(buttonIndex, "");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonCommand = e.getActionCommand();
        if (isNumeric(buttonCommand)) {
            handleNumberInput(buttonCommand);
        } else if (buttonCommand.equals("=")) {
            handleEqualsInput();
        } else if (buttonCommand.equals(".")) {
            handleDecimalInput();
        } else {
            handleOperatorInput(buttonCommand.charAt(0));
        }
    }

    private boolean isNumeric(String str) {
        return str.matches("[0-9]");
    }

    private void handleNumberInput(String number) {
        if (displayField.getText().equals("0") || pressedOperator || pressedEquals) {
            displayField.setText(number);
            pressedOperator = false;
            pressedEquals = false;
        } else {
            displayField.setText(displayField.getText() + number);
        }
    }

    private void handleEqualsInput() {
        calcService.setNum2(Double.parseDouble(displayField.getText()));
        double result = calculateResult(calcService.getMathSymbol());
        result = Math.round(result * 1000000.0) / 1000000.0; // limit result to 6 decimal places
        displayField.setText(Double.toString(result));

        pressedEquals = true;
        pressedOperator = false;
    }

    private double calculateResult(char operator) {
        switch (operator) {
            case '+':
                return calcService.add();
            case '-':
                return calcService.subtract();
            case 'x':
                return calcService.multiply();
            case '/':
                return calcService.divide();
            default:
                return 0;
        }
    }

    private void handleDecimalInput() {
        if (!displayField.getText().contains(".")) {
            displayField.setText(displayField.getText() + ".");
        }
    }

    private void handleOperatorInput(char operator) {
        calcService.setNum1(Double.parseDouble(displayField.getText()));
        calcService.setMathSymbol(operator);

        pressedOperator = true;
        pressedEquals = false;
    }
}
