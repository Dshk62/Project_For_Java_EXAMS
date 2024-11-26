package org.example;

import java.io.*;
import java.util.Stack;

public class ArithmeticProcessor {
    public static void main(String[] args) {
        String inputFile = "input.txt";   // Входной файл
        String outputFile = "output.txt"; // Выходной файл

        try {
            // Чтение текста из файла
            String inputData = readFile(inputFile);

            // Обработка текста (нахождение и замена выражений)
            String processedData = processArithmeticExpressions(inputData);

            // Запись результата в выходной файл
            writeFile(outputFile, processedData);

            System.out.println("Обработка завершена. Результат записан в " + outputFile);
        } catch (Exception e) {
            System.err.println("Произошла ошибка: " + e.getMessage());
        }
    }

    // Метод для чтения файла
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    // Метод для обработки текста (нахождение и замена арифметических выражений)
    private static String processArithmeticExpressions(String data) {
        StringBuilder result = new StringBuilder();
        StringBuilder expression = new StringBuilder();
        boolean inBrackets = false;

        for (char c : data.toCharArray()) {
            if (c == '(') {
                // Начало выражения в скобках
                if (inBrackets) {
                    expression.append(c);
                } else {
                    inBrackets = true;
                    expression.setLength(0); // Очищаем временное хранилище для выражения
                }
            } else if (c == ')') {
                // Конец выражения в скобках
                if (inBrackets) {
                    if (isValidArithmeticExpression(expression.toString())) {
                        result.append(evaluateExpression(expression.toString()));
                    } else {
                        result.append('(').append(expression).append(')');
                    }
                    expression.setLength(0);
                    inBrackets = false;
                } else {
                    result.append(c);
                }
            } else if (inBrackets) {
                expression.append(c);
            } else if (Character.isDigit(c) || "+-*/+".indexOf(c) != -1) {
                expression.append(c); // Собираем арифметическое выражение вне скобок
            } else {
                if (expression.length() > 0) {
                    // Вычисляем значение выражения вне скобок
                    if (isValidArithmeticExpression(expression.toString())) {
                        result.append(evaluateExpression(expression.toString()));
                    } else {
                        result.append(expression); // Если это не выражение, добавляем как есть
                    }
                    expression.setLength(0); // Очищаем временное выражение
                }
                result.append(c); // Добавляем текущий символ
            }
        }

        // Проверяем последнее выражение вне скобок
        if (expression.length() > 0) {
            if (isValidArithmeticExpression(expression.toString())) {
                result.append(evaluateExpression(expression.toString()));
            } else {
                result.append(expression);
            }
        }

        return result.toString();
    }

    // Проверка, является ли строка валидным арифметическим выражением
    private static boolean isValidArithmeticExpression(String expression) {
        return expression.matches("[0-9+\\-*+/\\s]+");
    }

    // Метод для вычисления арифметического выражения
    private static String evaluateExpression(String expression) {
        try {
            // Убираем пробелы из выражения
            expression = expression.replace(" ", "");

            Stack<Integer> numbers = new Stack<>();
            Stack<Character> operators = new Stack<>();
            int num = 0;
            char sign = '+';

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);

                if (Character.isDigit(c)) {
                    num = num * 10 + (c - '0'); // Собираем число
                }

                // Если встретили оператор или конец строки, обрабатываем текущий знак
                if ("+-*/".indexOf(c) != -1 || i == expression.length() - 1) {
                    switch (sign) {
                        case '+': numbers.push(num); break;
                        case '-': numbers.push(-num); break;
                        case '*': numbers.push(numbers.pop() * num); break;
                        case '/': numbers.push(numbers.pop() / num); break;
                    }
                    sign = c; // Обновляем текущий знак
                    num = 0;  // Сбрасываем число
                }
            }

            // Вычисляем итоговый результат
            int result = 0;
            for (int n : numbers) {
                result += n;
            }

            return String.valueOf(result);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректное выражение: " + expression);
        }
    }

    // Метод для записи текста в файл
    private static void writeFile(String filePath, String data) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(data);
        }
    }
}
